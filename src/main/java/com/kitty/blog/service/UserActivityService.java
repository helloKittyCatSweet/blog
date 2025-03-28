package com.kitty.blog.service;

import com.kitty.blog.model.userActivity.UserActivity;
import com.kitty.blog.repository.PostRepository;
import com.kitty.blog.repository.UserActivityRepository;
import com.kitty.blog.repository.UserRepository;
import com.kitty.blog.utils.UpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "userActivity")
public class UserActivityService {

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ResponseEntity<Boolean> create(UserActivity userActivity) {
        if (!postRepository.existsById(userActivity.getPostId()) ||
                !userRepository.existsById(userActivity.getUserId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        userActivityRepository.save(userActivity);
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> update(UserActivity updatedActivity) {
        if (!userActivityRepository.existsById(updatedActivity.getActivityId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        UserActivity existingActivity = (UserActivity)
                userActivityRepository.findById(updatedActivity.getActivityId()).orElseThrow();
        try {
            UpdateUtil.updateNotNullProperties(updatedActivity, existingActivity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        userActivityRepository.save(existingActivity);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }


    @Transactional
    @Cacheable(key = "#postId")
    public ResponseEntity<List<UserActivity>> findByPostId(Integer postId) {
        if (!postRepository.existsById(postId)){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(
                    userActivityRepository.findByPostId(postId).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#userId")
    public ResponseEntity<List<UserActivity>> findByUserId(Integer userId) {
        if (!userRepository.existsById(userId)){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(
                    userActivityRepository.findByUserId(userId).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#activityType")
    public ResponseEntity<List<UserActivity>> findByActivityType(String activityType) {
        return new ResponseEntity<>(
                userActivityRepository.findByActivityType(activityType).orElse(new ArrayList<>()),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#userId + #postId + #activityType")
    public ResponseEntity<UserActivity> findExplicit(Integer userId, Integer postId,
                                                     String activityType){
        return new ResponseEntity<>(
                userActivityRepository.findExplicit
                        (userId, postId, activityType).orElse(new UserActivity()),
                HttpStatus.OK);
    }

    /**
     * Auto-generated methods
     */

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<UserActivity> save(UserActivity userActivity) {
        return new ResponseEntity<>(
                (UserActivity) userActivityRepository.save(userActivity),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#activityId")
    public ResponseEntity<UserActivity> findById(Integer activityId) {
        return new ResponseEntity<>(
                (UserActivity) userActivityRepository.findById(activityId)
                        .orElse(new UserActivity()),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#activityId")
    public ResponseEntity<List<UserActivity>> findAll() {
        if (userActivityRepository.count() == 0){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(
                (List<UserActivity>) userActivityRepository.findAll(),
                HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> deleteById(Integer activityId) {
        if (!existsById(activityId).getBody()){
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        userActivityRepository.deleteById(activityId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(
                userActivityRepository.count(),
                HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> existsById(Integer activityId) {
        return new ResponseEntity<>(
                userActivityRepository.existsById(activityId),
                HttpStatus.OK);
    }
}
