package com.kitty.blog.application.controller.post;

import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.domain.service.post.postAnalysis.author.AuthorAnalyticsService;
import com.kitty.blog.domain.service.post.postAnalysis.author.AuthorAnalyticsService.AuthorAnalyticsReport;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @Operation(
            summary = "获取作者写作建议",
            description = "根据作者ID获取作者的文章分析报告",
            tags = {"作者分析"}
    )
    @GetMapping("/public/author")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "成功获取作者分析报告",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthorAnalyticsReport.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "作者分析报告",
                                                    value = """
                        {
                            "authorId": 1,
                            "userBehavior": {
                                "postCount": 10,
                                "commentCount": 20,
                                "likeCount": 30,
                                "shareCount": 40,
                                "readTime": 50,
                                "postPerformance": [
                                    {
                                        "postId": 1,
                                        "title": "文章标题1",
                                        "readTime": 10,
                                        "commentCount": 1,
                                        "likeCount": 2,
                                        "shareCount": 3
                                    },
                                    {
                                        "postId": 2,
                                        "title": "文章标题2",
                                        "readTime": 20,
                                        "commentCount": 2,
                                        "likeCount": 4,
                                        "shareCount": 6
                                    }
                                ]
                            }
                        }
                        """
                                            )
                                    }
                            )
                    }
            ),
            @ApiResponse(responseCode = "401", description = "未登录或登录过期"),
            @ApiResponse(responseCode = "403", description = "没有权限访问"),
            @ApiResponse(responseCode = "404", description = "作者不存在")
    })
    public ResponseEntity<Response<AuthorAnalyticsReport>> getAuthorAnalytics(
            @AuthenticationPrincipal LoginResponseDto user) {
        AuthorAnalyticsReport report = authorAnalyticsService.generateReport(user.getId());
        return Response.ok(report);
    }
}