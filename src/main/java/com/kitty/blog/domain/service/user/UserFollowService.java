package com.kitty.blog.domain.service.user;

import com.kitty.blog.common.constant.MessageStatus;
import com.kitty.blog.domain.model.Message;
import com.kitty.blog.domain.model.User;
import com.kitty.blog.domain.model.UserFollow;
import com.kitty.blog.domain.repository.UserFollowRepository;
import com.kitty.blog.domain.service.MessageService;
import com.kitty.blog.domain.service.WebSocketService;
import com.kitty.blog.infrastructure.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class UserFollowService {

    @Autowired
    private UserFollowRepository userFollowRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Transactional
    public void follow(Integer followerId, Integer followingId) {
        if (userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            return;
        }

        UserFollow userFollow = new UserFollow();
        userFollow.setFollowerId(followerId);
        userFollow.setFollowingId(followingId);
        userFollow.setCreateTime(LocalDateTime.now());
        userFollowRepository.save(userFollow);
    }

    @Transactional
    public void unfollow(Integer followerId, Integer followingId) {
        userFollowRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .ifPresent(userFollowRepository::delete);
    }

    public Page<User> getFollowers(Integer userId, Integer page, Integer size, String[] sort) {
        PageRequest pageRequest = PageUtil.createPageRequest(page, size, sort);
        Page<UserFollow> byFollowingId = userFollowRepository.findByFollowingId(userId, pageRequest);
        return mapUserFollowToUser(byFollowingId, true);
    }

    public List<User> getFollowing(Integer userId) {
        List<UserFollow> byFollowerId = userFollowRepository.findByFollowerId(userId);
        return mapUserFollowToUser(byFollowerId, false);
    }

    public List<UserFollow> getFollowerIds(Integer userId) {
        PageRequest pageRequest = PageRequest.of(0, Integer.MAX_VALUE);
        return userFollowRepository.findByFollowingId(userId, pageRequest).getContent();
    }

    public void notifyFollowers(Integer authorId, String postTitle, Integer postId) {
        try {
            List<UserFollow> followers = getFollowerIds(authorId);
            if (followers.isEmpty()) {
                return;
            }

            String message = String.format("您关注的作者发布了新文章：%s", postTitle);
            for (UserFollow follow : followers) {
                try {
                    // 发送WebSocket通知
                    webSocketService.sendUserNotification(
                            follow.getFollowerId(),
                            message,
                            "POST_NOTIFICATION",
                            postId.toString());

                    Message parentMessage = messageService.findLastMessageBetweenUsers(authorId, follow.getFollowerId())
                            .getBody();
                    // 保存到消息表
                    Message newMessage = new Message();
                    newMessage.setSenderId(authorId);
                    newMessage.setReceiverId(follow.getFollowerId());
                    newMessage.setContent(message);
                    newMessage.setIsRead(false);
                    newMessage.setStatus(MessageStatus.SENT.name());
                    newMessage.setParentId(parentMessage.getMessageId());

                    messageService.save(newMessage);

                } catch (Exception e) {
                    log.error("Failed to send notification to user {}: {}", follow.getFollowerId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to notify followers for post {}: {}", postId, e.getMessage());
        }
    }

    /**
     * 从UserFollow对象映射到User对象
     */
    private List<User> mapUserFollowToUser(List<UserFollow> userFollows, boolean isFollower) {
        return userFollows.stream()
                .map(follow -> {
                    Integer userId = isFollower ? follow.getFollowerId() : follow.getFollowingId();
                    return userService.findById(userId).getBody();
                })
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private Page<User> mapUserFollowToUser(Page<UserFollow> userFollows, boolean isFollower) {
        List<User> users = userFollows.getContent().stream()
                .map(follow -> {
                    Integer userId = isFollower ? follow.getFollowerId() : follow.getFollowingId();
                    return userService.findById(userId).getBody();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new PageImpl<>(
                users,
                userFollows.getPageable(),
                userFollows.getTotalElements());
    }

    public boolean checkFollow(Integer followerId, Integer followingId) {
        return userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }
}