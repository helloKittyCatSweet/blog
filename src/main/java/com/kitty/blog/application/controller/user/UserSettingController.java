package com.kitty.blog.application.controller.user;

import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.domain.model.UserSetting;
import com.kitty.blog.domain.service.UserSettingService;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "用户设置模块")
@RestController
@RequestMapping("/api/user/setting")
@CrossOrigin
public class UserSettingController {

    @Autowired
    private UserSettingService userSettingService;

    /**
     * 根据用户ID查询用户设置
     *
     * @param userId
     * @param user
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)" +
            " and #userId == #user.id")
    @Operation(summary = "根据用户ID查询用户设置")
    @GetMapping("/public/find/user/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "用户设置不存在")
    })
    public ResponseEntity<Response<UserSetting>> findByUserId
            (@PathVariable(name = "userId") Integer userId,
             @AuthenticationPrincipal LoginResponseDto user) {
        log.info("Current user roles: {}", user.getAuthorities());
        log.info("Requested userId: {}, Authenticated user id: {}", userId, user.getId());

        UserSetting response = userSettingService.findByUserId(userId);
        return Response.createResponse(ResponseEntity.ok(response),
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND,"用户设置不存在");
    }

    /**
     * 保存用户设置
     * @param userSetting
     * @return
     */
    @PreAuthorize("(hasRole(T(com.kitty.blog.constant.Role).ROLE_USER) " +
            "and (#userSetting.userId == #user.id))")
    @Operation(summary = "保存用户设置")
    @PostMapping("/public/save")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "保存成功"),
            @ApiResponse(responseCode = "500", description = "保存失败")
    })
    public ResponseEntity<Response<UserSetting>> save(
            @RequestBody UserSetting userSetting,
            @AuthenticationPrincipal LoginResponseDto user
    ) {
        UserSetting response = userSettingService.save(userSetting);
        return Response.createResponse(ResponseEntity.ok(response),
                HttpStatus.OK, "保存成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "保存失败");
    }

    /**
     * 根据ID查询用户设置
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据ID查询用户设置")
    @GetMapping("/admin/find/id/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "用户设置不存在")
    })
    public ResponseEntity<Response<UserSetting>> findById(@PathVariable(name = "id") Integer id) {
        UserSetting response = userSettingService.findById(id);
        return Response.createResponse(ResponseEntity.ok(response),
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND, "用户设置不存在");
    }

    /**
     * 查询所有用户设置
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询所有用户设置")
    @GetMapping("/admin/find/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "查询失败")
    })
    public ResponseEntity<Response<List<UserSetting>>> findAll() {
        List<UserSetting> response = userSettingService.findAll();
        return Response.createResponse(ResponseEntity.ok(response),
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
    }

    /**
     * 根据ID删除用户设置
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据ID删除用户设置")
    @DeleteMapping("/admin/delete/id/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "用户设置不存在")
    })
    public ResponseEntity<Response<Boolean>> deleteById(@PathVariable(name = "id") Integer id) {
        Boolean response = userSettingService.deleteById(id);
        return Response.createResponse(ResponseEntity.ok(response),
                HttpStatus.OK, "删除成功",
                HttpStatus.NOT_FOUND, "用户设置不存在");
    }

    /**
     * 查询用户设置数量
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询用户设置数量")
    @GetMapping("/admin/count")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "查询失败")
    })
    public ResponseEntity<Response<Long>> count() {
        Long response = userSettingService.count();
        return Response.createResponse(ResponseEntity.ok(response),
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
    }

    /**
     * 根据ID判断用户设置是否存在
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据ID判断用户设置是否存在")
    @GetMapping("/admin/exist/id/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "判断成功"),
            @ApiResponse(responseCode = "404", description = "用户设置不存在")
    })
    public ResponseEntity<Response<Boolean>> existsById(@PathVariable(name = "id") Integer id) {
        boolean response = userSettingService.existsById(id);
        return Response.createResponse(ResponseEntity.ok(response),
                HttpStatus.OK, "判断成功",
                HttpStatus.NOT_FOUND, "用户设置不存在");
    }
}
