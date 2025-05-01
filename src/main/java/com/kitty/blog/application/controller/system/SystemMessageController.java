package com.kitty.blog.application.controller.system;

import com.kitty.blog.application.dto.message.SystemMessageDto;
import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.domain.service.WebSocketService;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Tag(name = "系统消息", description = "系统消息相关接口")
@CrossOrigin
@RestController
@RequestMapping("/api/ws")
public class SystemMessageController {

    @Autowired
    private WebSocketService webSocketService;

    @Operation(summary = "获取WebSocket信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取WebSocket信息")
    })
    @GetMapping("/info")
    public ResponseEntity<Response<Map<String, Object>>> getWebSocketInfo
            (@AuthenticationPrincipal LoginResponseDto user) {
        if (user == null) {
            return Response.error(HttpStatus.UNAUTHORIZED, "请先登录");
        }

        Map<String, Object> info = new HashMap<>();
        info.put("websocket", true);
        info.put("cookie_needed", false);
        info.put("origins", Collections.singletonList("*"));
        info.put("entropy", new Random().nextInt()); // 随机数
        return Response.ok(info);
    }

    // 获取系统消息历史（管理员）
    @GetMapping("/admin/system-messages")
    public ResponseEntity<Response<Page<SystemMessageDto>>> getSystemMessages(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "targetRole", required = false) String targetRole,
            @RequestParam(value = "sort", required = false, defaultValue = "createdAt,desc") String[] sort) {
        return Response.ok(webSocketService.getSystemMessages(page, size, keyword, targetRole, sort));
    }

    // 获取用户系统消息
    @GetMapping("/system-messages")
    public ResponseEntity<Response<Page<SystemMessageDto>>> getUserSystemMessages(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "targetRole", required = false) String targetRole,
            @RequestParam(value = "sort", required = false, defaultValue = "createdAt,desc") String[] sort,
            @AuthenticationPrincipal LoginResponseDto user) {
        assert user != null;
        return Response.ok(webSocketService.getUserSystemMessages(user, page, size, keyword, targetRole, sort));
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @DeleteMapping("/system-message/{id}")
    @Operation(summary = "删除系统消息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功删除系统消息")
    })
    public ResponseEntity<Response<Boolean>> deleteById(@PathVariable Integer id) {
        webSocketService.deleteById(id);
        return Response.ok(true);
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @PutMapping("/system-message/{id}/read")
    @Operation(summary = "标记系统消息为已读")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功标记系统消息为已读")
    })
    public ResponseEntity<Response<Boolean>> markSystemMessageAsRead(
            @PathVariable Integer id,
            @AuthenticationPrincipal LoginResponseDto user
    ) {
        webSocketService.markSystemMessageAsRead(id, user.getId());
        return Response.ok(true);
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @PutMapping("/system-message/{id}/unread")
    @Operation(summary = "标记系统消息为未读")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功标记系统消息为未读")
    })
    public ResponseEntity<Response<Boolean>> markSystemMessageAsUnread(
            @PathVariable Integer id,
            @AuthenticationPrincipal LoginResponseDto user
    ) {
        webSocketService.markSystemMessageAsUnread(id, user.getId());
        return Response.ok(true);
    }
}
