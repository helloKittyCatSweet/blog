package com.kitty.blog.domain.service.user;

import com.kitty.blog.application.dto.common.FileDto;
import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.application.dto.userRole.WholeUserInfo;
import com.kitty.blog.domain.model.Role;
import com.kitty.blog.domain.model.User;
import com.kitty.blog.domain.model.userRole.UserRole;
import com.kitty.blog.domain.model.userRole.UserRoleId;
import com.kitty.blog.domain.repository.RoleRepository;
import com.kitty.blog.domain.repository.UserRepository;
import com.kitty.blog.domain.repository.UserRoleRepository;
import com.kitty.blog.domain.service.UserRoleService;
import com.kitty.blog.domain.service.contentReview.BaiduContentService;
import com.kitty.blog.infrastructure.utils.AliyunOSSUploader;
import com.kitty.blog.infrastructure.security.JwtTokenUtil;
import com.kitty.blog.infrastructure.utils.IpUtil;
import com.kitty.blog.infrastructure.utils.RequestUtil;
import com.kitty.blog.infrastructure.utils.UpdateUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "user")
@Slf4j
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

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public ResponseEntity<Boolean> register(User user) {
        User ifExists = userRepository.findByUsername(user.getUsername()).orElse(null);
        if ((ifExists!= null) &&!ifExists.isDeleted()){
            ifExists.setUsername("");
            save(ifExists);
        }
        if (userRepository.existsByEmail(user.getEmail())){
            return new ResponseEntity<>(false, HttpStatus.CONFLICT);
        }

        String s = baiduContentService.checkText(user.getUsername());
        if (!s.equals("合规")) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        // 密码加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);
        user.setGender(2); // 默认保密
        user.setDeleted(false);

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
        if (!s.equals("合规")) {
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
        if (user.get().isDeleted()){
            return new ResponseEntity<>(new LoginResponseDto(), HttpStatus.UNAUTHORIZED);
        }
//        System.out.println(password);
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            return new ResponseEntity<>(new LoginResponseDto(), HttpStatus.UNAUTHORIZED);
        }
        if (!user.get().getIsActive()) {
            return new ResponseEntity<>(new LoginResponseDto(), HttpStatus.FORBIDDEN);
        }

        // 获取请求对象并记录 IP
        HttpServletRequest request = RequestUtil.getRequest();
        if (request != null) {
            String ip = IpUtil.getIpAddress(request);
            String location = IpUtil.getIpLocation(ip);
            user.get().setLastLoginIp(ip);
            user.get().setLastLoginLocation(location);
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
        user.get().setLastLoginTime(LocalDate.now());
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
    public ResponseEntity<Boolean> verifyPassword(Integer userId, String password) {
        if (!userRepository.existsById(userId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        } else {
            User user = userRepository.findById(userId).get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
            }
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
        } else {
            return new ResponseEntity<>(new User(), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    @Cacheable(key = "#isActive")
    public ResponseEntity<List<WholeUserInfo>> findByActivated(boolean isActive) {
        List<User> users = userRepository.findByActivated(isActive).orElse(new ArrayList<User>());
        List<WholeUserInfo> wholeUserInfos = new ArrayList<>();
        for (User user : users) {
            List<Role> roles = userRoleService.findByUserId(user.getUserId()).getBody();
            wholeUserInfos.add(new WholeUserInfo(user, roles));
        }
        return new ResponseEntity<>(wholeUserInfos, HttpStatus.OK);
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
    public ResponseEntity<List<WholeUserInfo>> findAll() {
        if (userRepository.count() == 0) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        List<User> users = userRepository.findAll();
        List<WholeUserInfo> wholeUserInfos = new ArrayList<>();
        for (User user : users) {
            List<Role> roles = userRoleService.findByUserId(user.getUserId()).getBody();
            wholeUserInfos.add(new WholeUserInfo(user, roles));
        }
//        log.info("findAll: " + wholeUserInfos.toString());
        return new ResponseEntity<>(wholeUserInfos, HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> deleteById(Integer userId) {
        if (!existsById(userId).getBody()) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        User user = userRepository.findById(userId).orElse(new User());
        user.setDeleted(true);
        user.setIsActive(false);
        save(user);
//        userRepository.deleteById(userId);
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

    @Transactional
    public ResponseEntity<List<WholeUserInfo>> findUserByUsernameAndEmail(String keyword) {
        List<User> users = userRepository.findUserByUsernameAndEmail(keyword).orElse(new ArrayList<User>());
        if (users.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        List<WholeUserInfo> wholeUserInfos = new ArrayList<>();
        for (User user : users) {
            List<Role> roles = userRoleService.findByUserId(user.getUserId()).getBody();
            wholeUserInfos.add(new WholeUserInfo(user, roles));
        }
        return new ResponseEntity<>(wholeUserInfos, HttpStatus.OK);
    }

    /**
     * 通过excel导入创建用户
     */
    @Data
    @AllArgsConstructor
    public static class ImportError {
        private int row;
        private String message;
    }

    @Transactional
    public ResponseEntity<List<ImportError>> importUsers(MultipartFile file) {
        List<ImportError> errors = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    String username = getCellValueAsString(row.getCell(1));
                    String nickname = getCellValueAsString(row.getCell(2));
                    String email = getCellValueAsString(row.getCell(3));
                    String status = getCellValueAsString(row.getCell(4));
                    String roles = getCellValueAsString(row.getCell(9));

                    log.info("读取第{}行: username={}, nickname={}, email={}, status={}, roles={}",
                            i + 1, username, nickname, email, status, roles);

                    // Basic validation
                    if (StringUtils.isEmpty(username) || StringUtils.isEmpty(email)) {
                        errors.add(new ImportError(i + 1, "用户名和邮箱为必填项"));
                        continue;
                    }

                    // Handle existing username
                    Optional<User> existingUser = userRepository.findByUsername(username);
                    if (existingUser.isPresent()) {
                        User user = existingUser.get();
                        if (user.isDeleted()) {
                            user.setUsername("");
                            userRepository.save(user);
                            log.info("已清空被删除用户的用户名: {}", username);
                        } else {
                            log.info("用户名已存在且处于活跃状态，跳过: {}", username);
                            continue;
                        }
                    }

                    if (userRepository.existsByEmail(email)){
                        continue;
                    }

                    // Create new user
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setNickname(nickname);
                    newUser.setEmail(email);
                    newUser.setIsActive(true);
                    newUser.setPassword(passwordEncoder.encode("123456")); // Default password
                    newUser.setGender(2);
                    newUser.setDeleted(false);
                    newUser.setCreatedAt(LocalDate.now());
                    newUser.setUpdatedAt(LocalDate.now());

                    try {
                        newUser = userRepository.save(newUser);
                        log.info("成功创建用户: {}", username);
                        handleUserRoles(newUser, roles, i + 1, errors);
                    } catch (Exception e) {
                        log.error("保存用户失败: {}", username, e);
                        errors.add(new ImportError(i + 1, "保存用户失败: " + e.getMessage()));
                    }

                } catch (Exception e) {
                    log.error("处理第{}行时发生错误", i + 1, e);
                    errors.add(new ImportError(i + 1, "处理失败: " + e.getMessage()));
                }
            }
        } catch (Exception e) {
            log.error("读取Excel文件失败", e);
            errors.add(new ImportError(0, "文件处理失败: " + e.getMessage()));
        }

        return new ResponseEntity<>(errors, errors.isEmpty() ?
                HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @Transactional(noRollbackFor = Exception.class)
    private void handleUserRoles(User user, String roles, int rowNum, List<ImportError> errors) {
        if (!StringUtils.isEmpty(roles)) {
            String[] roleList = roles.split("[,，]");
            for (String roleName : roleList) {
                roleName = roleName.trim();
                try {
                    Optional<Role> role = roleRepository.findByRoleName(roleName);
                    if (role.isPresent()) {
                        UserRole userRole = new UserRole();
                        userRole.setId(new UserRoleId(user.getUserId(), role.get().getRoleId()));
                        userRoleService.save(userRole);
                        log.info("用户{}添加角色: {}", user.getUsername(), roleName);
                    } else {
                        errors.add(new ImportError(rowNum, "角色不存在：" + roleName));
                    }
                } catch (Exception e) {
                    log.error("添加角色失败: {}", roleName, e);
                    errors.add(new ImportError(rowNum, "添加角色失败: " + e.getMessage()));
                }
            }
        } else {
            // Add default role
            try {
                userRoleService.save(new UserRole(new UserRoleId(user.getUserId(), 1)));
                log.info("用户{}添加默认角色", user.getUsername());
            } catch (Exception e) {
                log.error("添加默认角色失败", e);
                errors.add(new ImportError(rowNum, "添加默认角色失败: " + e.getMessage()));
            }
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        try {
            return switch (cell.getCellType()) {
                case STRING -> cell.getStringCellValue().trim();
                case NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        yield cell.getLocalDateTimeCellValue().toString();
                    }
                    yield String.format("%.0f", cell.getNumericCellValue());
                }
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                default -> "";
            };
        } catch (Exception e) {
            log.error("单元格读取失败", e);
            return "";
        }
    }
}