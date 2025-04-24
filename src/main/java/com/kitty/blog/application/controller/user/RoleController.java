package com.kitty.blog.application.controller.user;


import com.kitty.blog.common.annotation.LogUserActivity;
import com.kitty.blog.domain.model.Role;
import com.kitty.blog.domain.service.RoleService;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/user/role")
@CrossOrigin
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 创建角色
     * 切记不能传id参数
     * @param role
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "创建角色")
    @PostMapping("/admin/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "失败")
    })
    public ResponseEntity<Response<Boolean>> create
            (@RequestBody Role role) {
        ResponseEntity<Boolean> response = roleService.create(role);
        return Response.createResponse(response,
                HttpStatus.OK, "创建成功",
                HttpStatus.INTERNAL_SERVER_ERROR,"创建失败");
    }

    /**
     * 更新角色
     * @param updatedRole
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "更新角色")
    @PutMapping("/admin/update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "失败")
    })
    @LogUserActivity("更新角色")
    public ResponseEntity<Response<Boolean>> update
            (@RequestBody Role updatedRole) {
        ResponseEntity<Boolean> response = roleService.update(updatedRole);
        return Response.createResponse(response,
                HttpStatus.OK, "更新成功",
                HttpStatus.INTERNAL_SERVER_ERROR,"更新失败");
    }

    /**
     * 根据角色名称删除角色
     * @param roleName
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据角色名称删除角色")
    @DeleteMapping("/admin/delete/name/{roleName}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "失败")
    })
    @LogUserActivity("根据角色名称删除角色")
    public ResponseEntity<Response<Boolean>> deleteByRoleName
            (@PathVariable(value = "roleName") String roleName) {
        ResponseEntity<Boolean> response = roleService.deleteByRoleName(roleName);
        return Response.createResponse(response,
                HttpStatus.OK, "删除成功",
                HttpStatus.INTERNAL_SERVER_ERROR,"删除失败");
    }

    /**
     * 根据角色名称查询角色
     * @param roleName
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据角色名称查询角色")
    @GetMapping("/admin/find/name/{roleName}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "失败")
    })
    public ResponseEntity<Response<Role>> findByRoleName
            (@PathVariable(value = "roleName") String roleName) {
        ResponseEntity<Role> response = roleService.findByRoleName(roleName);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR,"查询失败");
    }

    /**
     * 根据角色名称模糊查询角色
     * @param roleName
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据角色名称模糊查询角色")
    @GetMapping("/admin/find/name/like/{roleName}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "失败")
    })
    public ResponseEntity<Response<List<Role>>> findByRoleNameLike(String roleName){
        ResponseEntity<List<Role>> roles = roleService.findByRoleNameLike(roleName);
        return Response.createResponse(roles,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR,"查询失败");
    }

    /**
     * 根据描述模糊查询角色
     * @param description
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据描述模糊查询角色")
    @GetMapping("/admin/find/des/{description}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "失败")
    })
    public ResponseEntity<Response<List<Role>>> findByDescriptionLike
            (@PathVariable(value = "description") String description) {
        ResponseEntity<List<Role>> response = roleService.findByDescriptionLike(description);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR,"查询失败");
    }

    /**
     * 保存角色
     * @param role
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "保存角色")
    @PostMapping("/admin/save")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "失败")
    })
    @LogUserActivity("保存角色")
    public ResponseEntity<Response<Role>> save
            (@RequestBody Role role) {
        ResponseEntity<Role> response = roleService.save(role);
        return Response.createResponse(response,
                HttpStatus.OK, "保存成功",
                HttpStatus.INTERNAL_SERVER_ERROR,"保存失败");
    }

    /**
     * 根据角色ID查询角色
     * @param roleId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据角色ID查询角色")
    @GetMapping("/admin/find/id/{roleId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "失败")
    })
    public ResponseEntity<Response<Role>> findById
            (@PathVariable(value = "roleId") Integer roleId) {
        ResponseEntity<Role> response = roleService.findById(roleId);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR,"查询失败");
    }

    /**
     * 查询所有角色
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询所有角色")
    @GetMapping("/admin/find/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "失败")
    })
    public ResponseEntity<Response<List<Role>>> findAll() {
        ResponseEntity<List<Role>> response = roleService.findAll();
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR,"查询失败");
    }

    /**
     * 根据角色ID删除角色
     * @param roleId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据角色ID删除角色")
    @DeleteMapping("/admin/delete/id/{roleId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "失败")
    })
    @LogUserActivity("根据角色ID删除角色")
    public ResponseEntity<Response<Boolean>> deleteById
            (@PathVariable(value = "roleId") Integer roleId) {
        ResponseEntity<Boolean> response = roleService.deleteById(roleId);
        return Response.createResponse(response,
                HttpStatus.OK, "删除成功",
                HttpStatus.INTERNAL_SERVER_ERROR,"删除失败");
    }

    /**
     * 查询角色数量
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询角色数量")
    @GetMapping("/admin/count")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "失败")
    })
    public ResponseEntity<Response<Long>> count() {
        ResponseEntity<Long> response = roleService.count();
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR,"查询失败");
    }

    /**
     * 根据角色ID判断角色是否存在
     * @param roleId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据角色ID判断角色是否存在")
    @GetMapping("/admin/exist/id/{roleId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "500", description = "失败")
    })
    public ResponseEntity<Response<Boolean>> existsById
            (@PathVariable(value = "roleId") Integer roleId) {
        ResponseEntity<Boolean> response = roleService.existsById(roleId);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR,"查询失败");
    }
}
