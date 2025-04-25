package com.kitty.blog.application.controller.user;

import com.kitty.blog.application.dto.post.PostDto;
import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.domain.service.post.UserStatisticsService;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "用户控制台数据模块")
@RestController
@RequestMapping("/api/stat")
@CrossOrigin
public class UserStatController {

    @Autowired
    private UserStatisticsService userStatisticsService;

    /**
     * 普通用户
     */

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "获取控制面板统计数据")
    @GetMapping("/public/dashboard/stats")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Map<String, Object>>> getDashboardStats
            (@AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<Map<String, Object>> response = userStatisticsService
                .getDashboardStats(user.getId());
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取统计数据失败");
    }

    @Operation(summary = "获取月度统计数据")
    @GetMapping("/public/dashboard/monthly-stats")
    public ResponseEntity<Response<Map<String, Object>>> getMonthlyStats
            (@AuthenticationPrincipal LoginResponseDto user) {
        Map<String, Object> stats = userStatisticsService.getMonthlyStats(user.getId());
        return Response.createResponse(
                new ResponseEntity<>(stats, HttpStatus.OK),
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取失败"
        );
    }

    @Operation(summary = "获取最近发布的文章")
    @GetMapping("/public/dashboard/recent-posts")
    public ResponseEntity<Response<List<PostDto>>> getRecentPosts
            (@AuthenticationPrincipal LoginResponseDto user) {
        List<PostDto> posts = userStatisticsService.getRecentPosts(user.getId()).getBody();
        return Response.createResponse(
                new ResponseEntity<>(posts, HttpStatus.OK),
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取失败"
        );
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "获取互动数据统计")
    @GetMapping("/public/dashboard/interaction-stats")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Map<String, Integer>>> getInteractionStats
            (@AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<Map<String, Integer>> response = userStatisticsService.
                getInteractionStats(user.getId());
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取互动统计失败");
    }

    /**
     * 管理员
     */

    @Operation(summary = "获取控制面板统计数据")
    @GetMapping("/admin/dashboard/stats")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Map<String, Object>>> getDashboardStats() {
        ResponseEntity<Map<String, Object>> response = userStatisticsService.getDashboardStats();
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取统计数据失败");
    }

    @Operation(summary = "获取月度统计数据")
    @GetMapping("/admin/dashboard/monthly-stats")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Map<String, Object>>> getMonthlyStats() {
        Map<String, Object> stats = userStatisticsService.getMonthlyStats();
        return Response.createResponse(
                new ResponseEntity<>(stats, HttpStatus.OK),
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取失败"
        );
    }

    @Operation(summary = "获取最近发布的文章")
    @GetMapping("/admin/dashboard/recent-posts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<List<PostDto>>> getRecentPosts() {
        List<PostDto> posts = userStatisticsService.getRecentPosts().getBody();
        return Response.createResponse(
                new ResponseEntity<>(posts, HttpStatus.OK),
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取失败"
        );
    }

    @Operation(summary = "获取互动数据统计")
    @GetMapping("/admin/dashboard/interaction-stats")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Map<String, Integer>>> getInteractionStats() {
        ResponseEntity<Map<String, Integer>> response = userStatisticsService.
                getInteractionStats();
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取互动统计失败");
    }
}
