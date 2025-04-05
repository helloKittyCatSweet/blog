package com.kitty.blog.application.controller.user;

import com.kitty.blog.application.dto.userRole.FindDto;
import com.kitty.blog.domain.model.Role;
import com.kitty.blog.domain.model.userRole.UserRole;
import com.kitty.blog.domain.model.userRole.UserRoleId;
import com.kitty.blog.domain.service.UserRoleService;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "用户角色管理")
@RestController
@RequestMapping(path = "/api/user/userRole", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin
public class UserRoleController {

    @Autowired
    private UserRoleService userRoleService;

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER_ROLE_MAPPING_MANAGER) " +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "删除角色")
    @DeleteMapping("/admin/delete/{userId}/{roleId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "500", description = "删除失败")
    })
    public ResponseEntity<Response<Boolean>> deleteRole
            (@PathVariable Integer userId,
             @PathVariable Integer roleId) {
        ResponseEntity<Boolean> response = userRoleService.deleteRole(userId, roleId);
        return Response.createResponse(response,
                HttpStatus.OK, "删除成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "删除失败");
    }

    /**
     * 保存角色
     * @param ur
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER_ROLE_MAPPING_MANAGER) " +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "保存角色")
    @PostMapping("/admin/save")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "保存成功"),
            @ApiResponse(responseCode = "500", description = "保存失败")
    })
    public ResponseEntity<Response<UserRole>> save(UserRoleId ur) {
        UserRole userRole = new UserRole(ur);
        ResponseEntity<UserRole> response = userRoleService.save(userRole);
        return Response.createResponse(response,
                HttpStatus.OK, "保存成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "保存失败");
    }

    /**
     * 根据用户ID查询角色
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER_ROLE_MAPPING_MANAGER) " +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR) " +
            " or #userId == authentication.principal.id")
    @Operation(summary = "查找一个用户都有什么角色")
    @GetMapping("/admin/find/user/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "查询失败")
    })
    public ResponseEntity<Response<List<Role>>> findByUserId
            (@PathVariable("userId") Integer id) {
        ResponseEntity<List<Role>> response = userRoleService.findByUserId(id);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
    }

    /**
     * 根据角色ID查询角色
     * @param id
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER_ROLE_MAPPING_MANAGER) " +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据角色ID查询角色")
    @GetMapping("/admin/find/role/{roleId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "查询失败")
    })
    public ResponseEntity<Response<List<UserRole>>> findByRoleId
            (@PathVariable("roleId") Integer id) {
        ResponseEntity<List<UserRole>> response = userRoleService.findByRoleId(id);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
    }

    /**
     * 查询所有角色
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER_ROLE_MAPPING_MANAGER) " +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询所有角色")
    @GetMapping("/admin/find/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "查询失败")
    })
    public ResponseEntity<Response<List<FindDto>>> findAll() {
        ResponseEntity<List<FindDto>> response = userRoleService.findAll();
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
    }

    /**
     * 查询用户角色关系数量
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER_ROLE_MAPPING_MANAGER) " +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询用户角色关系数量")
    @GetMapping("/admin/count")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "查询失败")
    })
    public ResponseEntity<Response<Long>> count() {
        ResponseEntity<Long> response = userRoleService.count();
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
    }

    /**
     * 判断角色是否存在
     * @param userId
     * @param roleId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER_ROLE_MAPPING_MANAGER) " +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "判断角色是否存在")
    @GetMapping("/admin/exist/id/{userId}/{roleId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "查询失败")
    })
    public ResponseEntity<Response<Boolean>> exist
            (@PathVariable("userId") Integer userId, @PathVariable("roleId") Integer roleId) {
        ResponseEntity<Boolean> response = userRoleService.exist(userId, roleId);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
    }
}
