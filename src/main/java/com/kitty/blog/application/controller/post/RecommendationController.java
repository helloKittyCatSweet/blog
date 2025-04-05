package com.kitty.blog.application.controller.post;

import com.kitty.blog.domain.model.Post;
import com.kitty.blog.domain.service.post.postAnalysis.reader.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post/recommendations")
@Tag(name = "推荐", description = "推荐相关接口")
@CrossOrigin
public class RecommendationController {

    @Autowired
    private RecommendationService recommendationService;

    /**
     * 获取个性化推荐
     */
    @Operation(summary = "获取个性化推荐")
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @GetMapping("/personal/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    public ResponseEntity<List<Post>> getPersonalRecommendations(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<Post> recommendations = recommendationService.recommendPosts(userId, limit);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * 获取热门文章推荐
     */
    @Operation(summary = "获取热门文章推荐")
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @GetMapping("/hot")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功")
    })
    public ResponseEntity<List<Post>> getHotPosts(
            @RequestParam(defaultValue = "10") int limit) {
        List<Post> hotPosts = recommendationService.getHotPosts(limit);
        return ResponseEntity.ok(hotPosts);
    }
}