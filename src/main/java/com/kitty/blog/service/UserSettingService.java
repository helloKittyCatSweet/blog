package com.kitty.blog.service;

import com.kitty.blog.model.UserSetting;
import com.kitty.blog.repository.UserRepository;
import com.kitty.blog.repository.UserSettingRepository;
import com.kitty.blog.utils.UpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.print.DocFlavor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@CacheConfig(cacheNames = "userSetting")
public class UserSettingService {

    @Autowired
    private UserSettingRepository userSettingRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Cacheable(value = "userSetting", key = "#userId")
    public ResponseEntity<UserSetting> findByUserId(Integer userId) {
        if (!userRepository.existsById(userId)){
            return new ResponseEntity<>(new UserSetting(), HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(
                    userSettingRepository.findByUserId(userId).orElse(new UserSetting()),
                    HttpStatus.OK);
        }
    }

    /**
     * Auto-generated methods
     */
    @Transactional
    @CacheEvict(value = "userSetting", key = "#userSetting.userId")
    public ResponseEntity<UserSetting> save(UserSetting userSetting) {
        // 如果这个userId已经存在，则更新，否则新增
        ResponseEntity<UserSetting> response = findByUserId(userSetting.getUserId());
        if (Objects.requireNonNull(response.getBody()).getUserId() != null) {
            // 更新
            try {
                UpdateUtil.updateNotNullProperties(userSetting, response.getBody());
                return new ResponseEntity<>(
                        (UserSetting) userSettingRepository.save(response.getBody()),
                        HttpStatus.OK);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } finally {
                return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
            }
        }else {
            return new ResponseEntity<>(
                    (UserSetting) userSettingRepository.save(userSetting),
                    HttpStatus.OK);
        }
    }


    @Transactional
    @Cacheable(value = "userSetting", key = "#id")
    public ResponseEntity<UserSetting> findById(Integer id) {
        return new ResponseEntity<>(
                (UserSetting) userSettingRepository.findById(id).orElse(new UserSetting()),
                HttpStatus.OK);
    }


    @Transactional
    @Cacheable(value = "userSetting", key = "#username")
    public ResponseEntity<List<UserSetting>> findAll() {
        if (userSettingRepository.count() == 0){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(
                (List<UserSetting>) userSettingRepository.findAll(),
                HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(value = "userSetting", key = "#userSetting.userId")
    public ResponseEntity<Boolean> deleteById(Integer id) {
        if (!existsById(id).getBody()){
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        userSettingRepository.deleteById(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(
                userSettingRepository.count(),
                HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> existsById(Integer id) {
        return new ResponseEntity<>(
                userSettingRepository.existsById(id),
                HttpStatus.OK);
    }
}
