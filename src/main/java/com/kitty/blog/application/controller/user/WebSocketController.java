package com.kitty.blog.application.controller.user;

import com.kitty.blog.application.dto.message.MessageStatusUpdate;
import com.kitty.blog.application.dto.message.SystemMessageDto;
import com.kitty.blog.application.dto.message.SystemMessageRequest;
import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.domain.model.Message;
import com.kitty.blog.domain.service.MessageService;
import com.kitty.blog.domain.service.WebSocketService;
import com.kitty.blog.domain.service.auth.MyUserDetailService;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@Tag(name = "WebSocket", description = "WebSocket相关接口")
@CrossOrigin
@RestController
@RequestMapping("/ws")
public class WebSocketController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private WebSocketService webSocketService;

    @Autowired
    private MyUserDetailService userService;

    @Operation(summary = "发送WebSocket消息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功发送WebSocket消息")
    })
    @MessageMapping("/chat")
    @SendToUser("/queue/messages")
    public ResponseEntity<Response<Message>> handleChatMessage(@Payload Message message) {
        Message savedMessage = messageService.save(message).getBody();
        return Response.ok(savedMessage);
    }

    @Operation(summary = "更新WebSocket消息状态")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功更新WebSocket消息状态")
    })
    @MessageMapping("/message-status")
    public void handleMessageStatus(@Payload MessageStatusUpdate statusUpdate) {
        messageService.updateMessageStatus(statusUpdate.getMessageId(), statusUpdate.getStatus());
    }

    @Operation(summary = "发送WebSocket私人消息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功发送WebSocket私人消息")
    })
    @MessageMapping("/chat/topic")
    @SendTo("/topic/chat/{userId}")
    public ResponseEntity<Response<Message>> handleTopicMessage(
            @Payload Message message,
            @PathVariable Integer userId) {
        message.setSenderId(userId);
        Message savedMessage = messageService.sendTopicMessage(message).getBody();
        return Response.ok(savedMessage);
    }

    @Operation(summary = "发送WebSocket私人消息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功发送WebSocket私人消息")
    })
    @MessageMapping("/chat/private")
    @SendToUser("/queue/messages")
    public Message handlePrivateMessage(@Payload Message message,
            StompHeaderAccessor headerAccessor) {
        LoginResponseDto user = getCurrentUser(headerAccessor);
        message.setSenderId(user.getId());
        Message savedMessage = messageService.save(message).getBody();
        return savedMessage;
    }

    @Operation(summary = "获取WebSocket未读消息数量")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取WebSocket未读消息数量")
    })
    @MessageMapping("/message/read")
    public void handleMessageRead(@Payload Integer messageId,
            StompHeaderAccessor headerAccessor) {
        LoginResponseDto user = getCurrentUser(headerAccessor);
        messageService.markMessageAsRead(messageId, user.getId());
    }

    @Operation(summary = "获取WebSocket未读消息数量")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取WebSocket未读消息数量")
    })
    @MessageMapping("/message-status/update")
    @SendTo("/user/{userId}/queue/message-status")
    public MessageStatusUpdate handleMessageStatusUpdate(
            @Payload MessageStatusUpdate statusUpdate,
            @PathVariable Integer userId) {
        messageService.updateMessageStatus(statusUpdate.getMessageId(), statusUpdate.getStatus());
        return statusUpdate;
    }

    @Operation(summary = "获取WebSocket未读消息数量")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取WebSocket未读消息数量")
    })
    @SubscribeMapping("/user/{userId}/queue/messages")
    public void subscribeToPrivateMessages(StompHeaderAccessor headerAccessor) {
        webSocketService.subscribeToPrivateMessages(getCurrentUser(headerAccessor));
    }

    @Operation(summary = "获取WebSocket未读消息数量")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取WebSocket未读消息数量")
    })
    @SubscribeMapping("/topic/chat/{userId}")
    public void subscribeToTopicMessages(StompHeaderAccessor headerAccessor) {
        LoginResponseDto user = getCurrentUser(headerAccessor);
        // 可以在这里记录用户订阅的主题，或者进行其他初始化操作
        log.info("User {} subscribed to topic chat/{}", user.getUsername(), user.getId());
    }

    @MessageMapping("/system-message")
    public void sendSystemMessage(@Payload SystemMessageRequest request,
            StompHeaderAccessor headerAccessor) {
        webSocketService.sendSystemMessage(request, getCurrentUser(headerAccessor));
    }

    private LoginResponseDto getCurrentUser(StompHeaderAccessor headerAccessor) {
        if (headerAccessor != null && headerAccessor.getSessionAttributes() != null) {
            // For WebSocket connections
            return (LoginResponseDto) headerAccessor.getSessionAttributes().get("USER_DETAILS");
        } else {
            // For HTTP requests
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof LoginResponseDto) {
                return (LoginResponseDto) principal;
            } else if (principal instanceof String && !"anonymousUser".equals(principal)) {
                return (LoginResponseDto) userService.loadUserByUsername((String) principal);
            }
        }
        return null;
    }
}