package com.kitty.blog.service;

import com.kitty.blog.dto.common.FileDto;
import com.kitty.blog.dto.user.LoginResponseDto;
import com.kitty.blog.model.User;
import com.kitty.blog.model.userRole.UserRole;
import com.kitty.blog.model.userRole.UserRoleId;
import com.kitty.blog.repository.UserRepository;
import com.kitty.blog.service.contentReview.BaiduContentService;
import com.kitty.blog.utils.AliyunOSSUploader;
import com.kitty.blog.security.JwtTokenUtil;
import com.kitty.blog.utils.UpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "user")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AliyunOSSUploader aliyunOSSUploader;

    @Autowired
    private BaiduContentService baiduContentService;

    @Autowired
    private UserRoleService userRoleService;

    @Transactional
    public ResponseEntity<Boolean> register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent() ||
                userRepository.existsByEmail(user.getEmail())) {
            return new ResponseEntity<>(false, HttpStatus.CONFLICT);
        }

        String s = baiduContentService.checkText(user.getUsername());
        if (!s.equals("合规")){
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);
        user.setGender(true); // 默认为男

        save(user); // 先保存以获取用户id
        user = userRepository.findByUsername(user.getUsername()).get();

        // 普通用户by default
        userRoleService.save(new UserRole(new UserRoleId(user.getUserId(), 1)));

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> update(User user) {
        if (!userRepository.existsById(user.getUserId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        String s = baiduContentService.checkText(user.getUsername());
        if (!s.equals("合规")){
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

//        user.setPassword(passwordEncoder.encode(user.getPassword()));

        try {
            UpdateUtil.updateNotNullProperties(user, userRepository.findById(user.getUserId()).get());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        save(user);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> uploadAvatar(FileDto fileDto) {
        if (!userRepository.existsById(fileDto.getSomeId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        fileDto.setIdType("userId");
        String image = aliyunOSSUploader.uploadImage(fileDto.getFile(), fileDto.getSomeId(), fileDto.getIdType());
        User user = (User) userRepository.findById(fileDto.getSomeId()).get();
        user.setAvatar(image);
        save(user);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> activateUser(Integer userId, boolean isActive) {
        if (!userRepository.existsById(userId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        userRepository.activateUser(userId, isActive);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#username")
    public ResponseEntity<User> findByUsername(String username) {
        return new ResponseEntity<>(
                userRepository.findByUsername(username).orElse(new User()),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#email")
    public ResponseEntity<User> findByEmail(String email) {
        return new ResponseEntity<>(
                userRepository.findByEmail(email).orElse(new User()),
                HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<LoginResponseDto> login(String username, String password) {
        // 验证密码
        Optional<User> user = userRepository.findByUsername(username);
//        System.out.println(password);
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            return new ResponseEntity<>(new LoginResponseDto(), HttpStatus.UNAUTHORIZED);
        }
        if (!user.get().getIsActive()){
            return new ResponseEntity<>(new LoginResponseDto(), HttpStatus.FORBIDDEN);
        }

        // 生成token
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String token = jwtTokenUtil.generateToken(userDetails);

        // 创建响应对象
        LoginResponseDto response = new LoginResponseDto();
        response.setUsername(user.get().getUsername());
        response.setToken(token);
        response.setRoles(userDetails.getAuthorities());
        response.setId(user.get().getUserId());

        user.get().setToken(token);
        save(user.get());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> validateToken(String token) {
        try {
            if (jwtTokenUtil.isTokenExpired(token)) {
                return new ResponseEntity<>("Token expired", HttpStatus.UNAUTHORIZED);
            }
            return new ResponseEntity<>("Token valid", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> resetPassword(Integer userId, String password) {
        if (!userRepository.existsById(userId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        } else {
            userRepository.resetPassword(userId, passwordEncoder.encode(password));
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<Boolean> existsByEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public ResponseEntity<User> findUserByEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            return new ResponseEntity<>(
                    userRepository.findUserByEmail(email).orElse(new User()),
                    HttpStatus.OK);
        }else {
            return new ResponseEntity<>(new User(), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @Cacheable(key = "#isActive")
    public ResponseEntity<List<User>> findByActivated(boolean isActive) {
        return new ResponseEntity<>(
                userRepository.findByActivated(isActive).orElse(new ArrayList<User>()),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#emailSuffix")
    public ResponseEntity<List<User>> findByEmailSuffix(String emailSuffix) {
        return new ResponseEntity<>(
                userRepository.findByEmailSuffix(emailSuffix).orElse(new ArrayList<User>()),
                HttpStatus.OK);
    }

    /*
     * Auto-generated methods
     */
    @Transactional
    // 主键存在则更新，不存在则新增
    public ResponseEntity<User> save(User user) {
        return new ResponseEntity<>((User) userRepository.save(user), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<User> findById(Integer userId) {
        return new ResponseEntity<>(
                (User) userRepository.findById(userId).orElse(null),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "'all'")
    public ResponseEntity<List<User>> findAll() {
        if (userRepository.count() == 0) {
            return new ResponseEntity<>(new ArrayList<User>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(userRepository.findAll(), HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> deleteById(Integer userId) {
        if (!existsById(userId).getBody()) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(userRepository.count(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> existsById(Integer userId) {
        if (userRepository.existsById(userId)) {
            return new ResponseEntity<>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }
}
