package com.kitty.blog.application.controller.user;

import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.application.dto.userActivity.UserActivityDto;
import com.kitty.blog.domain.model.UserActivity;
import com.kitty.blog.domain.service.PostService;
import com.kitty.blog.domain.service.UserActivityService;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户动态管理")
@RestController
@RequestMapping("/api/user/activity")
@CrossOrigin
public class UserActivityController {

    @Autowired
    private UserActivityService userActivityService;

    /**
     * 创建用户动态
     * @param userActivity
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "创建用户动态")
    @PostMapping("/public/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    public ResponseEntity<Response<Boolean>> create
            (@RequestBody UserActivity userActivity) {
        ResponseEntity<Boolean> response = userActivityService.create(userActivity);
        return Response.createResponse(response,
                HttpStatus.OK, "创建成功",
                HttpStatus.BAD_REQUEST, "参数错误");
    }

    /**
     * 更新用户动态
     * @param updatedActivity
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER_ACTIVITY_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "更新用户动态")
    @PutMapping("/admin/update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "参数错误"),
            @ApiResponse(responseCode = "404", description = "动态不存在")
    })
    public ResponseEntity<Response<Boolean>> update
            (@RequestBody UserActivity updatedActivity) {
        ResponseEntity<Boolean> response = userActivityService.update(updatedActivity);
        return Response.createResponse(response,
                HttpStatus.OK, "更新成功",
                HttpStatus.BAD_REQUEST, "参数错误");
    }

    /**
     * 根据文章ID获取用户动态列表
     * @param postId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER_ACTIVITY_MANAGER) " +
            "or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR) or " +
            "@postService.isAuthorOfOpenPost(#postId, #user.id)")
    @Operation(summary = "根据文章ID获取用户动态列表")
    @GetMapping("/admin/find/post/{postId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "帖子不存在")
    })
    public ResponseEntity<Response<List<UserActivity>>> findByPostId
            (@PathVariable(name = "postId") Integer postId,
             @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<List<UserActivity>> response = userActivityService.findByPostId(postId);
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.NOT_FOUND, "帖子不存在");
    }

    /**
     * 根据用户ID获取用户动态列表
     * @param userId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER_ACTIVITY_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据用户ID获取用户动态列表")
    @GetMapping("/admin/find/user/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<Response<List<UserActivity>>> findByUserId
            (@PathVariable(name = "userId") Integer userId) {
        ResponseEntity<List<UserActivity>> response = userActivityService.findByUserId(userId);
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.NOT_FOUND, "用户不存在");
    }

    /**
     * 根据互动类型获取用户动态列表
     * @param activityType
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "根据互动类型获取用户动态列表")
    @GetMapping("/public/find/type/{activityType}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "获取失败")
    })
    public ResponseEntity<Response<List<UserActivityDto>>> findByActivityType
            (@PathVariable(name = "activityType") String activityType,
             @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<List<UserActivityDto>> response =
                userActivityService.findByActivityType(user.getId(), activityType);
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取失败");
    }

    /**
     * 根据用户ID、文章ID、互动类型获取用户动态
     * @param userId
     * @param postId
     * @param activityType
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "根据用户ID、文章ID、互动类型获取用户动态")
    @GetMapping("/public/find/explicit/{userId}/{postId}/{activityType}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "动态不存在")
    })
    public ResponseEntity<Response<UserActivity>> findExplicit
            (@PathVariable(name = "userId") Integer userId,
             @PathVariable(name = "postId") Integer postId,
             @PathVariable(name = "activityType") String activityType) {
        ResponseEntity<UserActivity> response =
                userActivityService.findExplicit(userId, postId, activityType);
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.NOT_FOUND, "动态不存在");
    }

    /**
     * 保存用户动态
     * @param userActivity
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER_ACTIVITY_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "保存用户动态")
    @PostMapping("/admin/save")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "保存成功"),
            @ApiResponse(responseCode = "400", description = "参数错误")
    })
    public ResponseEntity<Response<UserActivity>> save
            (@RequestBody UserActivity userActivity) {
        ResponseEntity<UserActivity> response = userActivityService.save(userActivity);
        return Response.createResponse(response,
                HttpStatus.OK, "保存成功",
                HttpStatus.BAD_REQUEST, "参数错误");
    }

    /**
     * 获取用户动态详情
     * @param activityId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER_ACTIVITY_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "获取用户动态详情")
    @GetMapping("/admin/find/id/{activityId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "动态不存在")
    })
    public ResponseEntity<Response<UserActivity>> findById
            (@PathVariable(name = "activityId") Integer activityId) {
        ResponseEntity<UserActivity> response = userActivityService.findById(activityId);
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.NOT_FOUND, "动态不存在");
    }

    /**
     * 获取用户动态列表
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER_ACTIVITY_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "获取用户动态列表")
    @GetMapping("/admin/find/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "获取失败")
    })
    public ResponseEntity<Response<List<UserActivity>>> findAll() {
        ResponseEntity<List<UserActivity>> response = userActivityService.findAll();
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取失败");
    }

    /**
     * 删除用户动态
     * @param activityId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "删除用户动态")
    @DeleteMapping("/public/delete/id/{activityId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "动态不存在")
    })
    public ResponseEntity<Response<Boolean>> deleteById
            (@PathVariable(name = "activityId") Integer activityId,
             @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<Boolean> response = userActivityService.deleteById(activityId, user.getId());
        return Response.createResponse(response,
                HttpStatus.OK, "删除成功",
                HttpStatus.NOT_FOUND, "动态不存在");
    }

    /**
     * 获取用户动态数量
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER_ACTIVITY_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "获取用户动态数量")
    @GetMapping("/admin/count")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "获取失败")
    })
    public ResponseEntity<Response<Long>> count() {
        ResponseEntity<Long> count = userActivityService.count();
        return Response.createResponse(count,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取失败");
    }

    /**
     * 判断用户动态是否存在
     * @param activityId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER_ACTIVITY_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "判断用户动态是否存在")
    @GetMapping("/admin/exists/{activityId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "存在"),
            @ApiResponse(responseCode = "404", description = "不存在")
    })
    public ResponseEntity<Response<Boolean>> existsById
            (@PathVariable(name = "activityId") Integer activityId) {
        ResponseEntity<Boolean> response = userActivityService.existsById(activityId);
        return Response.createResponse(response,
                HttpStatus.OK, "存在",
                HttpStatus.NOT_FOUND, "不存在");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "获取文章互动记录")
    @GetMapping("/public/interactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "500", description = "服务器错误")
    })
    public ResponseEntity<Response<List<UserActivityDto>>> getPostInteractions(
            @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<List<UserActivityDto>> response =
                userActivityService.findPostInteractions(user.getId());
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.NOT_FOUND, "用户不存在");
    }
}
