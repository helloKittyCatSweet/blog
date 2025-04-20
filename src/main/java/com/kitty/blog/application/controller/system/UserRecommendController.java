package com.kitty.blog.application.controller.system;

import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.domain.model.User;
import com.kitty.blog.domain.service.recommend.UserRecommendService;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/user/recommend")
@Tag(name = "用户推荐模块", description = "用户推荐模块")
public class UserRecommendController {

    @Autowired
    private UserRecommendService userRecommendService;

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "获取推荐用户列表")
    @GetMapping("/public/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取推荐用户列表成功"),
            @ApiResponse(responseCode = "400", description = "参数不合法")
    })
    public ResponseEntity<Response<List<User>>> getRecommendUsers(
            @AuthenticationPrincipal LoginResponseDto user,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) Integer limit) {

        // 参数验证由@Min和@Max注解处理

        List<User> recommendUsers = userRecommendService.recommendUsers(user, limit);
        return Response.ok(recommendUsers, "获取推荐用户列表成功");
    }

}
