package com.kitty.blog.application.controller.post;

import com.kitty.blog.domain.service.post.postAnalysis.author.AuthorAnalyticsService;
import com.kitty.blog.domain.service.post.postAnalysis.author.AuthorAnalyticsService.AuthorAnalyticsReport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post/analytics")
@Tag(name = "作者分析", description = "获取作者的文章分析报告")
@CrossOrigin
public class AuthorAnalyticsController {

    @Autowired
    private AuthorAnalyticsService authorAnalyticsService;

    /**
     * 获取作者分析报告
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "获取作者分析报告", description = "根据作者ID获取作者的文章分析报告")
    @GetMapping("/public/author/{authorId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功获取作者分析报告"),
            @ApiResponse(responseCode = "401", description = "未登录或登录过期"),
            @ApiResponse(responseCode = "403", description = "没有权限访问"),
            @ApiResponse(responseCode = "404", description = "作者不存在")
    })
    public ResponseEntity<AuthorAnalyticsReport> getAuthorAnalytics(
            @PathVariable Integer authorId) {
        AuthorAnalyticsReport report = authorAnalyticsService.generateReport(authorId);
        return ResponseEntity.ok(report);
    }
}