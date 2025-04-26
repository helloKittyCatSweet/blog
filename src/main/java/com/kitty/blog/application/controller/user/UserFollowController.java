package com.kitty.blog.application.controller.user;

import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.common.annotation.LogUserActivity;
import com.kitty.blog.domain.model.User;
import com.kitty.blog.domain.model.UserFollow;
import com.kitty.blog.domain.service.user.UserFollowService;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户关注")
@RestController
@RequestMapping("/api/user/follow")
@CrossOrigin
public class UserFollowController {

    @Autowired
    private UserFollowService userFollowService;

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "关注用户")
    @PostMapping("/public/{followingId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "关注成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @LogUserActivity("关注用户")
    public ResponseEntity<Response<String>> follow(
            @PathVariable Integer followingId,
            @AuthenticationPrincipal LoginResponseDto user) {
        try {
            userFollowService.follow(user.getId(), followingId);
            return Response.ok("关注成功");
        } catch (Exception e) {
            return Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "关注失败");
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "取消关注")
    @DeleteMapping("/public/{followingId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "取消关注成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @LogUserActivity("取消关注")
    public ResponseEntity<Response<String>> unfollow(
            @PathVariable Integer followingId,
            @AuthenticationPrincipal LoginResponseDto user) {
        try {
            userFollowService.unfollow(user.getId(), followingId);
            return Response.ok("取消关注成功");
        } catch (Exception e) {
            return Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "取消关注失败");
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "获取用户的关注列表")
    @GetMapping("/public/following/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<List<User>>> getFollowing(@PathVariable Integer userId) {
        try {
            List<User> following = userFollowService.getFollowing(userId);
            return Response.ok(following);
        } catch (Exception e) {
            return Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "查询关注列表失败");
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "获取用户的粉丝列表")
    @GetMapping("/public/followers/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Page<User>>> getFollowers(
            @PathVariable Integer userId,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String[] sorts
            ) {
        try {
            Page<User> followers = userFollowService.getFollowers(userId, page, size, sorts);
            return Response.ok(followers);
        } catch (Exception e) {
            return Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "查询粉丝列表失败");
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "检查是否关注")
    @GetMapping("/public/check/{followerId}/{followingId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Boolean>> checkFollow(
            @PathVariable Integer followerId,
            @PathVariable Integer followingId) {
            return Response.ok(userFollowService.checkFollow(followerId, followingId));
    }
}