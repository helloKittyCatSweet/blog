package com.kitty.blog.application.service;

import com.kitty.blog.constant.ActivityType;
import com.kitty.blog.dto.userActivity.UserActivityDto;
import com.kitty.blog.model.Post;
import com.kitty.blog.model.User;
import com.kitty.blog.model.UserActivity;
import com.kitty.blog.repository.PostRepository;
import com.kitty.blog.repository.UserActivityRepository;
import com.kitty.blog.repository.UserRepository;
import com.kitty.blog.utils.UpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@CacheConfig(cacheNames = "userActivity")
public class UserActivityService {

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public ResponseEntity<Boolean> create(UserActivity userActivity) {
        if (!postRepository.existsById(userActivity.getPostId()) ||
                !userRepository.existsById(userActivity.getUserId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        // 默认假删字段为false
        userActivity.setDeleted(false);
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
    public ResponseEntity<List<UserActivityDto>> findByActivityType(Integer userId, String activityType) {
        if (!userRepository.existsById(userId)){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }

        // 验证活动类型
        if (activityType != null && !ActivityType.isValidInteractType(activityType)){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
        }
        try{
            List<UserActivity> interactions = userActivityRepository.findByActivityType(userId,
                    activityType != null ? activityType.toLowerCase() : null).orElse(new ArrayList<>());
            List<UserActivityDto> dtoList = new ArrayList<>();
            for (UserActivity interaction : interactions) {
                String userName = userRepository.findById(interaction.getUserId()).
                        orElse(new User()).getUsername();
                String postTitle = postRepository.findById(interaction.getPostId()).
                        orElse(new Post()).getTitle();
                dtoList.add(new UserActivityDto().builder()
                        .activityId(interaction.getActivityId())
                        .username(userName)
                        .postTitle(postTitle)
                        .postId(interaction.getPostId())
                        .activityDetail(interaction.getActivityDetail())
                        .activityType(interaction.getActivityType())
                        .createdAt(interaction.getCreatedAt()).build());
            }
            return new ResponseEntity<>(dtoList, HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
    public ResponseEntity<Boolean> deleteById(Integer activityId, Integer userId) {
        try {
            // 1. 先获取完整的实体
            UserActivity userActivity = userActivityRepository.findById(activityId)
                    .orElse(null);
            if (userActivity == null) {
                return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
            }

            if(!Objects.equals(userActivity.getUserId(), userId)){
                // 文章作者修改的，假删就行了
                userActivity.setDeleted(true);
                userActivityRepository.save(userActivity);
            }else {
                // 操作执行的本人执行的，直接删除
                userActivityRepository.deleteById(activityId);
            }

            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    // 获取用户文章的互动记录
    public ResponseEntity<List<UserActivityDto>> findPostInteractions(Integer authorId) {
        if (!userRepository.existsById(authorId)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        try {
            List<UserActivity> interactions = userActivityRepository.findPostInteractions(authorId);
            List<UserActivityDto> dtoList = new ArrayList<>();
            for (UserActivity interaction : interactions) {
                String userName = userRepository.findById(interaction.getUserId()).
                        orElse(new User()).getUsername();
                String postTitle = postRepository.findById(interaction.getPostId()).
                        orElse(new Post()).getTitle();
                dtoList.add(new UserActivityDto().builder()
                        .activityId(interaction.getActivityId())
                        .username(userName)
                        .postTitle(postTitle)
                        .postId(interaction.getPostId())
                        .activityDetail(interaction.getActivityDetail())
                        .activityType(interaction.getActivityType())
                        .createdAt(interaction.getCreatedAt()).build());
            }
            return new ResponseEntity<>(dtoList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
