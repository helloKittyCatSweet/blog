package com.kitty.blog.domain.service;

import com.kitty.blog.domain.model.UserSetting;
import com.kitty.blog.domain.repository.UserRepository;
import com.kitty.blog.domain.repository.UserSettingRepository;
import com.kitty.blog.infrastructure.utils.UpdateUtil;
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
    public UserSetting findByUserId(Integer userId) {
        return userSettingRepository.findByUserId(userId).orElse(new UserSetting());
    }

    /**
     * Auto-generated methods
     */
    @Transactional
    @CacheEvict(value = "userSetting", key = "#userSetting.userId")
    public UserSetting save(UserSetting userSetting) {
        // 如果这个userId已经存在，则更新，否则新增
        UserSetting response = findByUserId(userSetting.getUserId());
        if (response.getUserId() == null) {
            userSettingRepository.save(userSetting);
        } else {
            try {
                UpdateUtil.updateNotNullProperties(userSetting, response);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            userSettingRepository.save(response);
        }
        return response;
    }

    @Transactional
    @Cacheable(value = "userSetting", key = "#id")
    public UserSetting findById(Integer id) {
        return (UserSetting) userSettingRepository.findById(id).orElse(null);
    }

    @Transactional
    @Cacheable(value = "userSetting", key = "'all'")
    public List<UserSetting> findAll() {
        if (userSettingRepository.count() == 0) {
            return new ArrayList<>();
        }
        return userSettingRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = "userSetting", key = "#userSetting.userId")
    public Boolean deleteById(Integer id) {
        if (!existsById(id)) {
            return false;
        }
        userSettingRepository.deleteById(id);
        return true;
    }

    @Transactional
    public Long count() {
        return userSettingRepository.count();
    }

    @Transactional
    public boolean existsById(Integer id) {
        return userSettingRepository.existsById(id);
    }
}
