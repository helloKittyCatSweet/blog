package com.kitty.blog.domain.service;

import com.kitty.blog.application.dto.message.MessageStatusUpdate;
import com.kitty.blog.application.dto.message.SystemMessageDto;
import com.kitty.blog.application.dto.message.SystemMessageRequest;
import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.common.constant.MessageStatus;
import com.kitty.blog.common.constant.Role;
import com.kitty.blog.domain.model.Message;
import com.kitty.blog.domain.model.SystemMessage;
import com.kitty.blog.domain.model.User;
import com.kitty.blog.domain.model.userRole.UserRole;
import com.kitty.blog.domain.repository.RoleRepository;
import com.kitty.blog.domain.repository.SystemMessageRepository;
import com.kitty.blog.domain.repository.UserRepository;
import com.kitty.blog.domain.repository.UserRoleRepository;
import com.kitty.blog.infrastructure.utils.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WebSocketService {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private SystemMessageRepository systemMessageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public Map<String, Object> getWebSocketInfo(LoginResponseDto user) {
        Map<String, Object> info = new HashMap<>();
        info.put("websocket", true);
        info.put("cookie_needed", false);
        info.put("origins", Collections.singletonList("*"));
        info.put("entropy", new Random().nextInt());
        return info;
    }

    @Transactional
    public Message handleChatMessage(Message message) {
        return messageService.save(message).getBody();
    }

    @Transactional
    public void handleMessageStatus(MessageStatusUpdate statusUpdate) {
        messageService.updateMessageStatus(statusUpdate.getMessageId(), statusUpdate.getStatus());
    }

    @Transactional
    public Message handleTopicMessage(Message message, Integer userId) {
        message.setSenderId(userId);
        return messageService.sendTopicMessage(message).getBody();
    }

    @Transactional
    public Message handlePrivateMessage(Message message, LoginResponseDto user) {
        message.setSenderId(user.getId());
        return messageService.save(message).getBody();
    }

    @Transactional
    public void handleMessageRead(Integer messageId, LoginResponseDto user) {
        messageService.markMessageAsRead(messageId, user.getId());
    }

    @Transactional
    public MessageStatusUpdate handleMessageStatusUpdate(MessageStatusUpdate statusUpdate, Integer userId) {
        messageService.updateMessageStatus(statusUpdate.getMessageId(), statusUpdate.getStatus());
        return statusUpdate;
    }

    @Transactional
    public void subscribeToPrivateMessages(LoginResponseDto user) {
        Long count = messageService.countByReceiverIdAndIsReadFalse(user.getId()).getBody();
        if (count != null && count > 0) {
            simpMessagingTemplate.convertAndSendToUser(
                    user.getId().toString(),
                    "/queue/unread-count",
                    count);
        }
    }

    @Transactional
    public void subscribeToTopicMessages(LoginResponseDto user) {
        log.info("User {} subscribed to topic chat/{}", user.getUsername(), user.getId());
    }

    @Transactional
    public void sendSystemMessage(SystemMessageRequest request, LoginResponseDto user) {
        try {
            String checkRole = request.getTargetRole().substring(5);
            log.info("用户 {} 发送系统消息: {}", user.getUsername(), request.getMessage());

            if (!isValidRole(checkRole)) {
                throw new IllegalArgumentException("无效的目标角色: " + request.getTargetRole());
            }

            SystemMessage systemMessage = SystemMessage.builder()
                    .message(request.getMessage())
                    .targetRole(request.getTargetRole())
                    .senderId(user.getId())
                    .status(MessageStatus.UNREAD)
                    .readUserIds("") // 初始化为空字符串而不是 null
                    .build();

            systemMessage = systemMessageRepository.save(systemMessage);

            simpMessagingTemplate.convertAndSend(
                    "/topic/system-message/" + request.getTargetRole(),
                    systemMessage);

            log.info("系统消息发送成功: {}", systemMessage);
        } catch (Exception e) {
            log.error("发送系统消息失败", e);
            throw new RuntimeException("发送系统消息失败: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Page<SystemMessageDto> getSystemMessages(Integer page, Integer size, String keyword, String targetRole,
            String[] sort) {
        PageRequest pageRequest = PageUtil.createPageRequest(page, size, sort);
        Page<SystemMessage> systemMessages;

        if (keyword != null && !keyword.trim().isEmpty() || targetRole != null && !targetRole.trim().isEmpty()) {
            systemMessages = systemMessageRepository.findByMessageContainingAndTargetRole(
                    keyword == null ? "" : keyword.trim(),
                    targetRole == null ? "" : targetRole.trim(),
                    pageRequest);
        } else {
            systemMessages = systemMessageRepository.findAllByOrderByCreatedAtDesc(pageRequest);
        }

        return systemMessages.map(this::convertToDtoForAdmin);
    }

    @Transactional
    public Page<SystemMessageDto> getUserSystemMessages(LoginResponseDto user,
            Integer page,
            Integer size,
            String keyword,
            String targetRole,
            String[] sort) {
        List<String> userRoles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        PageRequest pageRequest = PageUtil.createPageRequest(page, size, sort);
        Page<SystemMessage> systemMessages;

        if (keyword != null && !keyword.trim().isEmpty() || targetRole != null && !targetRole.trim().isEmpty()) {
            systemMessages = systemMessageRepository.findByTargetRoleInAndMessageContainingAndTargetRole(
                    userRoles,
                    keyword == null ? "" : keyword.trim(),
                    targetRole == null ? "" : targetRole.trim(),
                    pageRequest);
        } else {
            systemMessages = systemMessageRepository.findByTargetRoleInOrderByCreatedAtDesc(userRoles, pageRequest);
        }

        return systemMessages.map(message -> convertToDtoForUser(message, user.getId()));
    }

    @Transactional
    private boolean isValidRole(String role) {
        return Arrays.stream(Role.class.getDeclaredFields())
                .map(field -> {
                    try {
                        return (String) field.get(null);
                    } catch (IllegalAccessException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList()
                .contains(role);
    }

    @Transactional
    public void deleteById(Integer id) {
        systemMessageRepository.deleteById(id);
    }

    @Transactional
    public void markSystemMessageAsRead(Integer id, Integer userId) {
        SystemMessage systemMessage = systemMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("系统消息不存在"));

        Set<String> readUserIds = new HashSet<>();
        if (systemMessage.getReadUserIds() != null && !systemMessage.getReadUserIds().isEmpty()) {
            readUserIds.addAll(Arrays.asList(systemMessage.getReadUserIds().split(",")));
        }
        readUserIds.add(userId.toString());

        // 过滤掉空字符串，并用逗号连接
        String newReadUserIds = readUserIds.stream()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(","));

        systemMessage.setReadUserIds(newReadUserIds);
        systemMessageRepository.save(systemMessage);

        // 发送WebSocket消息通知前端更新状态
        simpMessagingTemplate.convertAndSend(
                "/topic/system-message/" + systemMessage.getTargetRole(),
                systemMessage);
    }

    @Transactional
    public void markSystemMessageAsUnread(Integer id, Integer userId) {
        SystemMessage message = systemMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("系统消息不存在"));

        Set<String> readUserIds = new HashSet<>();
        if (message.getReadUserIds() != null && !message.getReadUserIds().isEmpty()) {
            readUserIds.addAll(Arrays.asList(message.getReadUserIds().split(",")));
        }
        readUserIds.remove(userId.toString());

        String newReadUserIds = readUserIds.isEmpty() ? "" : String.join(",", readUserIds);
        message.setReadUserIds(newReadUserIds);
        systemMessageRepository.save(message);

        // 发送WebSocket消息通知前端更新状态
        simpMessagingTemplate.convertAndSend(
                "/topic/system-message/" + message.getTargetRole(),
                message);
    }

    @Transactional
    public void sendUserNotification(Integer userId, String message, String type, String data) {
        try {
            // 直接发送到用户的私人队列
            simpMessagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/notifications",
                    Map.of(
                            "message", message,
                            "type", type,
                            "data", data));
        } catch (Exception e) {
            log.error("Failed to send notification to user {}: {}", userId, e.getMessage());
        }
    }

    private SystemMessageDto convertToDtoForAdmin(SystemMessage systemMessage) {
        SystemMessageDto dto = new SystemMessageDto();
        dto.setId(systemMessage.getId());
        dto.setTargetRole(systemMessage.getTargetRole());
        dto.setMessage(systemMessage.getMessage());
        dto.setSenderId(systemMessage.getSenderId());

        User user = userRepository.findById(systemMessage.getSenderId()).orElse(null);
        dto.setSenderName(user != null ? user.getUsername() : null);

        List<UserRole> userRoles = userRoleRepository.findByUserId(systemMessage.getSenderId())
                .orElse(new ArrayList<>());
        if (userRoles.isEmpty()) {
            throw new IllegalArgumentException("用户角色为空");
        }
        List<String> administratorName = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            roleRepository.findById(userRole.getId().getRoleId())
                    .ifPresent(role -> administratorName.add(role.getAdministratorName()));
        }
        dto.setSenderRoles(administratorName);
        dto.setCreatedAt(systemMessage.getCreatedAt());
        return dto;
    }

    private SystemMessageDto convertToDtoForUser(SystemMessage systemMessage, Integer userId) {
            SystemMessageDto dto = new SystemMessageDto();
            dto.setId(systemMessage.getId());
            dto.setMessage(systemMessage.getMessage());
            dto.setTargetRole(systemMessage.getTargetRole());
            dto.setSenderId(systemMessage.getSenderId());

            User sender = userRepository.findById(systemMessage.getSenderId()).orElse(null);
            if (sender != null) {
                dto.setSenderName(sender.getUsername());
                List<UserRole> senderRoles = userRoleRepository.findByUserId(systemMessage.getSenderId())
                        .orElse(new ArrayList<>());
                if (!senderRoles.isEmpty()) {
                    List<String> administratorNames = senderRoles.stream()
                            .map(role -> roleRepository.findById(role.getId().getRoleId())
                                    .map(com.kitty.blog.domain.model.Role::getAdministratorName)
                                    .orElse(null))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    dto.setSenderRoles(administratorNames);
                }
            }
            dto.setCreatedAt(systemMessage.getCreatedAt());

            boolean isRead = false;
            if (systemMessage.getReadUserIds() != null && !systemMessage.getReadUserIds().isEmpty()) {
                isRead = Arrays.stream(systemMessage.getReadUserIds().split(","))
                        .filter(s -> !s.isEmpty())
                        .anyMatch(readUserId -> readUserId.equals(userId.toString()));
            }
            dto.setRead(isRead);
            return dto;
    }
}