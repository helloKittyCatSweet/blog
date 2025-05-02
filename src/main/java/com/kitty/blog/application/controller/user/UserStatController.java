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
    @Operation(
            summary = "获取控制面板统计数据",
            description = "获取用户控制面板的统计数据，包括文章数量、" +
                    "分类数量、标签数量、评论数量、附件数量、最近发布的文章、互动数据统计等。",
            tags = {"用户控制台数据模块"}
    )
    @GetMapping("/public/dashboard/stats")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @io.swagger.v3.oas.annotations.media.Schema(
                                implementation = Response.class
                        ),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                        name = "成功示例",
                                        value = "{\n" +
                                                "  \"status\": 200,\n" +
                                                "  \"message\": \"获取成功\",\n" +
                                                "  \"data\": {\n" +
                                                "    \"postCount\": 10,\n" +
                                                "    \"categoryCount\": 5,\n" +
                                                "    \"tagCount\": 10,\n" +
                                                "    \"commentCount\": 100,\n" +
                                                "    \"attachmentCount\": 100,\n" +
                                                "    \"recentPosts\": [\n" +
                                                "      {\n" +
                                                "        \"id\": 1,\n" +
                                                "        \"title\": \"标题1\",\n" +
                                                "        \"summary\": \"摘要1\",\n" +
                                                "        \"createTime\": \"2021-01-01 00:00:00\",\n" +
                                                "        \"author\": \"作者1\",\n" +
                                                "        \"category\": {\n" +
                                                "          \"id\": 1,\n" +
                                                "          \"name\": \"分类1\"\n" +
                                                "        }\n" +
                                                "      },\n" +
                                                "      {\n" +
                                                "        \"id\": 2,\n" +
                                                "        \"title\": \"标题2\",\n" +
                                                "        \"summary\": \"摘要2\",\n" +
                                                "        \"createTime\": \"2021-01-02 00:00:00\",\n" +
                                                "        \"author\": \"作者2\",\n" +
                                                "        \"category\": {\n" +
                                                "          \"id\": 2,\n" +
                                                "          \"name\": \"分类2\"\n" +
                                                "        }\n" +
                                                "      }\n" +
                                                "    ],\n" +
                                                "    \"interaction\": {\n" +
                                                "      \"postViewCount\": 100,\n" +
                                                "      \"commentCount\": 100,\n" +
                                                "      \"likeCount\": 100\n" +
                                                "    }\n" +
                                                "  }\n" +
                                                "}"
                                    )
                            }
                    )
            ),
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

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(
            summary = "获取月度统计数据",
            description = "获取用户最近一月的统计数据，包括文章数量、分类数量、标签数量、评论数量、附件数量、" +
                    "最近发布的文章、互动数据统计等。",
            tags = {"用户控制台数据模块"}
    )
    @GetMapping("/public/dashboard/monthly-stats")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @io.swagger.v3.oas.annotations.media.Schema(
                                implementation = Response.class
                        ),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                        name = "成功示例",
                                        value = "{\n" +
                                                "  \"status\": 200,\n" +
                                                "  \"message\": \"获取成功\",\n" +
                                                "  \"data\": {\n" +
                                                "    \"postCount\": 10,\n" +
                                                "    \"categoryCount\": 5,\n" +
                                                "    \"tagCount\": 10,\n" +
                                                "    \"commentCount\": 100,\n" +
                                                "    \"attachmentCount\": 100,\n" +
                                                "    \"recentPosts\": [\n" +
                                                "      {\n" +
                                                "        \"id\": 1,\n" +
                                                "        \"title\": \"标题1\",\n" +
                                                "        \"summary\": \"摘要1\",\n" +
                                                "        \"createTime\": \"2021-01-01 00:00:00\",\n" +
                                                "        \"author\": \"作者1\",\n" +
                                                "        \"category\": {\n" +
                                                "          \"id\": 1,\n" +
                                                "          \"name\": \"分类1\"\n" +
                                                "        }\n" +
                                                "      },\n" +
                                                "      {\n" +
                                                "        \"id\": 2,\n" +
                                                "        \"title\": \"标题2\",\n" +
                                                "        \"summary\": \"摘要2\",\n" +
                                                "        \"createTime\": \"2021-01-02 00:00:00\",\n" +
                                                "        \"author\": \"作者2\",\n" +
                                                "        \"category\": {\n" +
                                                "          \"id\": 2,\n" +
                                                "          \"name\": \"分类2\"\n" +
                                                "        }\n" +
                                                "      }\n" +
                                                "    ],\n" +
                                                "    \"interaction\": {\n" +
                                                "      \"postViewCount\": 100,\n" +
                                                "      \"commentCount\": 100,\n" +
                                                "      \"likeCount\": 100\n" +
                                                "    }\n" +
                                                "  }\n" +
                                                "}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Map<String, Object>>> getMonthlyStats
            (@AuthenticationPrincipal LoginResponseDto user) {
        Map<String, Object> stats = userStatisticsService.getMonthlyStats(user.getId());
        return Response.createResponse(
                new ResponseEntity<>(stats, HttpStatus.OK),
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取失败"
        );
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(
            summary = "获取最近发布的文章",
            description = "获取用户最近发布的文章列表，包括文章标题、摘要、创建时间、作者、分类等信息。",
            tags = {"用户控制台数据模块"}
    )
    @GetMapping("/public/dashboard/recent-posts")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @io.swagger.v3.oas.annotations.media.Schema(
                                implementation = Response.class
                        ),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                        name = "成功示例",
                                        value = "{\n" +
                                                "  \"status\": 200,\n" +
                                                "  \"message\": \"获取成功\",\n" +
                                                "  \"data\": [\n" +
                                                "    {\n" +
                                                "      \"id\": 1,\n" +
                                                "      \"title\": \"标题1\",\n" +
                                                "      \"summary\": \"摘要1\",\n" +
                                                "      \"createTime\": \"2021-01-01 00:00:00\",\n" +
                                                "      \"author\": \"作者1\",\n" +
                                                "      \"category\": {\n" +
                                                "        \"id\": 1,\n" +
                                                "        \"name\": \"分类1\"\n" +
                                                "      }\n" +
                                                "    },\n" +
                                                "    {\n" +
                                                "      \"id\": 2,\n" +
                                                "      \"title\": \"标题2\",\n" +
                                                "      \"summary\": \"摘要2\",\n" +
                                                "      \"createTime\": \"2021-01-02 00:00:00\",\n" +
                                                "      \"author\": \"作者2\",\n" +
                                                "      \"category\": {\n" +
                                                "        \"id\": 2,\n" +
                                                "        \"name\": \"分类2\"\n" +
                                                "      }\n" +
                                                "    }\n" +
                                                "  ]\n" +
                                                "}"
                                    )
                            }
                        )
            ),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
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
    @Operation(
            summary = "获取互动数据统计",
            description = "获取用户最近一月的文章阅读量、评论数量、点赞数量等数据。",
            tags = {"用户控制台数据模块"}
    )
    @GetMapping("/public/dashboard/interaction-stats")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @io.swagger.v3.oas.annotations.media.Schema(
                                implementation = Response.class
                        ),
                            examples = {
                                    @io.swagger.v3.oas.annotations.media.ExampleObject(
                                        name = "成功示例",
                                        value = "{\n" +
                                                "  \"status\": 200,\n" +
                                                "  \"message\": \"获取成功\",\n" +
                                                "  \"data\": {\n" +
                                                "    \"postViewCount\": 100,\n" +
                                                "    \"commentCount\": 100,\n" +
                                                "    \"likeCount\": 100\n" +
                                                "  }\n" +
                                                "}"
                                    )
                            }
                        )
            ),
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

    @Operation(
            summary = "获取控制面板统计数据",
            description = "获取控制面板统计数据",
            tags = {"用户控制台数据模块"}
    )
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

    @Operation(
            summary = "获取月度统计数据",
            description = "获取月度统计数据",
            tags = {"用户控制台数据模块"}
    )
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

    @Operation(
            summary = "获取最近发布的文章",
            description = "获取最近发布的文章列表",
            tags = {"用户控制台数据模块"}
    )
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

    @Operation(
            summary = "获取互动数据统计",
            description = "获取互动数据统计",
            tags = {"用户控制台数据模块"}
    )
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
