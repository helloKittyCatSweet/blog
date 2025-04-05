package com.kitty.blog.controller.user;

import com.kitty.blog.dto.message.MessageStatusUpdate;
import com.kitty.blog.dto.user.LoginResponseDto;
import com.kitty.blog.model.Message;
import com.kitty.blog.application.service.MessageService;
import com.kitty.blog.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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

    @Operation(summary = "获取WebSocket信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取WebSocket信息")
    })
    @GetMapping("/info")
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    public ResponseEntity<Response<Map<String, Object>>> getWebSocketInfo
            (@AuthenticationPrincipal LoginResponseDto user) {
        if (user == null){
            return Response.error(HttpStatus.UNAUTHORIZED, "请先登录");
        }

        Map<String, Object> info = new HashMap<>();
        info.put("websocket", true);
        info.put("cookie_needed", false);
        info.put("origins", Collections.singletonList("*"));
        info.put("entropy", new Random().nextInt()); // 随机数
        return Response.ok(info);
    }

    @Operation(summary = "发送WebSocket消息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功发送WebSocket消息")
    })
    @MessageMapping("/chat")
    @SendToUser("/queue/messages")
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    public ResponseEntity<Response<Message>> handleChatMessage(@Payload Message message) {
        Message savedMessage = messageService.save(message).getBody();
        return Response.ok(savedMessage);
    }

    @Operation(summary = "更新WebSocket消息状态")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功更新WebSocket消息状态")
    })
    @MessageMapping("/message-status")
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    public void handleMessageStatus(@Payload MessageStatusUpdate statusUpdate) {
        messageService.updateMessageStatus(statusUpdate.getMessageId(), statusUpdate.getStatus());
    }

    @Operation(summary = "发送WebSocket私人消息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功发送WebSocket私人消息")
    })
    @MessageMapping("/chat/topic")
    @SendTo("/topic/chat/{userId}")
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    public ResponseEntity<Response<Message>> handleTopicMessage(
            @Payload Message message,
            @AuthenticationPrincipal LoginResponseDto user) {
        message.setSenderId(user.getId());
        Message savedMessage = messageService.sendTopicMessage(message).getBody();
        return Response.ok(savedMessage);
    }

    @Operation(summary = "发送WebSocket私人消息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功发送WebSocket私人消息")
    })
    @MessageMapping("/chat/private")
    @SendToUser("/queue/messages")
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    public Message handlePrivateMessage(
            @Payload Message message,
            @AuthenticationPrincipal LoginResponseDto user) {
        message.setSenderId(user.getId());
        Message savedMessage = messageService.save(message).getBody();
        return savedMessage;
    }

    @Operation(summary = "获取WebSocket未读消息数量")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取WebSocket未读消息数量")
    })
    @MessageMapping("/message/read")
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    public void handleMessageRead(
            @Payload Integer messageId,
            @AuthenticationPrincipal LoginResponseDto user) {
        messageService.markMessageAsRead(messageId, user.getId());
    }

    @Operation(summary = "获取WebSocket未读消息数量")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取WebSocket未读消息数量")
    })
    @MessageMapping("/message-status/update")
    @SendTo("/user/{userId}/queue/message-status")
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    public MessageStatusUpdate handleMessageStatusUpdate(
            @Payload MessageStatusUpdate statusUpdate,
            @AuthenticationPrincipal LoginResponseDto user) {
        messageService.updateMessageStatus(statusUpdate.getMessageId(), statusUpdate.getStatus());
        return statusUpdate;
    }

    @Operation(summary = "获取WebSocket未读消息数量")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取WebSocket未读消息数量")
    })
    @SubscribeMapping("/user/{userId}/queue/messages")
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    public void subscribeToPrivateMessages(@AuthenticationPrincipal LoginResponseDto user) {
        // 当用户订阅私人消息时，发送未读消息数量
        Long count = messageService.countByReceiverIdAndIsReadFalse(user.getId()).getBody();
        if (count != null && count > 0) {
            // 发送未读消息数量
            simpMessagingTemplate.convertAndSendToUser(user.getId().toString(),
                    "/queue/unread-count", count);
        }
    }

    @Operation(summary = "获取WebSocket未读消息数量")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取WebSocket未读消息数量")
    })
    @SubscribeMapping("/topic/chat/{userId}")
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    public void subscribeToTopicMessages(@AuthenticationPrincipal LoginResponseDto user) {
        // 可以在这里记录用户订阅的主题，或者进行其他初始化操作
        log.info("User {} subscribed to topic chat/{}", user.getUsername(), user.getId());
    }
}