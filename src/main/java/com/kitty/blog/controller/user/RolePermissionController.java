package com.kitty.blog.controller.user;

import com.kitty.blog.model.rolePermission.RolePermission;
import com.kitty.blog.service.RolePermissionService;
import com.kitty.blog.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色权限管理")
@RestController
@RequestMapping("/api/user/rolePermission")
@CrossOrigin
public class RolePermissionController {

    @Autowired
    private RolePermissionService rolePermissionService;

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role)." +
            "ROLE_PERMISSION_MAPPING_MANAGER) " +
            "or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "判断角色是否存在指定权限")
    @GetMapping("/admin/exist/explicit/{roleId}/{permissionId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "角色存在指定权限"),
            @ApiResponse(responseCode = "500", description = "角色不存在指定权限")
    })
    public ResponseEntity<Response<Boolean>> existsByRoleIdAndPermissionId
            (@PathVariable Integer roleId,
             @PathVariable Integer permissionId) {
        ResponseEntity<Boolean> response =
                rolePermissionService.existsByRoleIdAndPermissionId(roleId, permissionId);
        if (Boolean.TRUE.equals(response.getBody())){
            return Response.ok(true);
        }else {
            return Response.error("角色不存在指定权限");
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role)." +
            "ROLE_PERMISSION_MAPPING_MANAGER) " +
            "or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据角色ID查询角色权限")
    @GetMapping("/admin/find/role/{roleId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "角色存在权限"),
            @ApiResponse(responseCode = "500", description = "角色不存在权限")
    })
    public ResponseEntity<Response<List<RolePermission>>> findByRoleId(@PathVariable Integer roleId) {
        ResponseEntity<List<RolePermission>> response = rolePermissionService.findByRoleId(roleId);
        if (response.getBody() != null){
            return Response.ok(response.getBody());
        }else {
            return Response.error("角色不存在权限");
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role)." +
            "ROLE_PERMISSION_MAPPING_MANAGER) " +
            "or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据权限ID查询角色权限")
    @GetMapping("/admin/find/permission/{permissionId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "权限存在角色"),
            @ApiResponse(responseCode = "500", description = "权限不存在角色")
    })
    public ResponseEntity<Response<List<RolePermission>>> findByPermissionId
            (@PathVariable Integer permissionId) {
        ResponseEntity<List<RolePermission>> response = rolePermissionService.
                findByPermissionId(permissionId);
        if (response.getBody() != null) {
            return Response.ok(response.getBody());
        } else {
            return Response.error("权限不存在角色");
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role)." +
            "ROLE_PERMISSION_MAPPING_MANAGER) " +
            "or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据角色ID和权限ID查询角色权限")
    @GetMapping("admin//find/explicit/{roleId}/{permissionId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "角色存在指定权限"),
            @ApiResponse(responseCode = "500", description = "角色不存在指定权限")
    })
    public ResponseEntity<Response<RolePermission>>
    findExplicit(@PathVariable Integer roleId, @PathVariable Integer permissionId) {
        ResponseEntity<RolePermission> response =
                rolePermissionService.findExplicit(roleId, permissionId);
        if (response.getBody() != null) {
            return Response.ok(response.getBody());
        } else {
            return Response.error("角色不存在指定权限");
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role)." +
            "ROLE_PERMISSION_MAPPING_MANAGER) " +
            "or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "保存角色权限")
    @PostMapping("/admin/save")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "保存成功"),
            @ApiResponse(responseCode = "500", description = "保存失败")
    })
    public ResponseEntity<Response<Boolean>> save(@RequestBody RolePermission rolePermission) {
        ResponseEntity<Boolean> response = rolePermissionService.save(rolePermission);
        if (response.getBody() != null) {
            return Response.ok(response.getBody());
        } else {
            return Response.error("保存失败");
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role)." +
            "ROLE_PERMISSION_MAPPING_MANAGER) " +
            "or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据ID查询角色权限")
    @GetMapping("/admin/find/id/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "角色权限存在"),
            @ApiResponse(responseCode = "500", description = "角色权限不存在")
    })
    public ResponseEntity<Response<RolePermission>> findById(@PathVariable Integer id) {
        ResponseEntity<RolePermission> response = rolePermissionService.findById(id);
        if (response.getBody() != null) {
            return Response.ok(response.getBody());
        } else {
            return Response.error("角色权限不存在");
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role)." +
            "ROLE_PERMISSION_MAPPING_MANAGER) " +
            "or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询所有角色权限")
    @GetMapping("/admin/find/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "角色权限存在"),
            @ApiResponse(responseCode = "500", description = "角色权限不存在")
    })
    public ResponseEntity<Response<List<RolePermission>>> findAll() {
        ResponseEntity<List<RolePermission>> response = rolePermissionService.findAll();
        if (response.getBody() != null) {
            return Response.ok(response.getBody());
        } else {
            return Response.error("角色权限不存在");
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role)." +
            "ROLE_PERMISSION_MAPPING_MANAGER) " +
            "or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据ID删除角色权限")
    @DeleteMapping("/admin/delete/id/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "500", description = "删除失败")
    })
    public ResponseEntity<Response<Boolean>> deleteById(@PathVariable Integer id) {
        ResponseEntity<Boolean> response = rolePermissionService.deleteById(id);
        if (response.getBody()){
            return Response.ok(true);
        }else {
            return Response.error("删除失败");
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role)." +
            "ROLE_PERMISSION_MAPPING_MANAGER) " +
            "or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询角色权限数量")
    @GetMapping("/admin/count")
    public ResponseEntity<Response<Long>> count() {
        Long count = rolePermissionService.count().getBody();
        return Response.ok(count);
    }
}
