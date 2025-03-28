package com.kitty.blog.controller.user;

import com.kitty.blog.model.Permission;
import com.kitty.blog.service.PermissionService;
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

@Tag(name = "权限管理")
@RestController
@RequestMapping("/api/user/permission")
@CrossOrigin
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_PERMISSION_MANAGER)" +
            " or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据名称查询权限")
    @GetMapping("/admin/find/name/{name}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "权限不存在")
    })
    public ResponseEntity<Response<Permission>> findByName(@PathVariable String name) {
        ResponseEntity<Permission> response = permissionService.findByName(name);
        return Response.ok(response.getBody());
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_PERMISSION_MANAGER)" +
            " or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "模糊查询权限")
    @GetMapping("/admin/find/name/like/{name}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "权限不存在")
    })
    public ResponseEntity<Response<List<Permission>>> findByNameLike(@PathVariable String name) {
        ResponseEntity<List<Permission>> response = permissionService.findByNameLike(name);
        return Response.ok(response.getBody());
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_PERMISSION_MANAGER)" +
            " or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "保存权限")
    @PostMapping("/admin/save")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "保存成功"),
            @ApiResponse(responseCode = "500", description = "保存失败")
    })
    public ResponseEntity<Response<Boolean>> save(@RequestBody Permission permission) {
        ResponseEntity<Boolean> response = permissionService.save(permission);
        return Response.ok(response.getBody());
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_PERMISSION_MANAGER)" +
            " or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据ID查询权限")
    @GetMapping("/admin/find/id/{permissionId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "权限不存在")
    })
    public ResponseEntity<Response<Permission>> findById(@PathVariable Integer permissionId) {
        ResponseEntity<Permission> response = permissionService.findById(permissionId);
        return Response.ok(response.getBody());
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_PERMISSION_MANAGER)" +
            " or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询所有权限")
    @GetMapping("/admin/find/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    public ResponseEntity<Response<List<Permission>>> findAll() {
        ResponseEntity<List<Permission>> response = permissionService.findAll();
        return Response.ok(response.getBody());
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_PERMISSION_MANAGER)" +
            " or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "删除权限")
    @DeleteMapping("/admin/delete/id/{permissionId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "500", description = "删除失败")
    })
    public ResponseEntity<Response<Boolean>> deleteById(@PathVariable Integer permissionId) {
        ResponseEntity<Boolean> response = permissionService.deleteById(permissionId);
        return Response.ok(response.getBody());
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_PERMISSION_MANAGER)" +
            " or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "统计权限数量")
    @GetMapping("/admin/count")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功")
    })
    public ResponseEntity<Response<Long>> count() {
        ResponseEntity<Long> response = permissionService.count();
        return Response.ok(response.getBody());
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_PERMISSION_MANAGER)" +
            " or hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "判断权限是否存在")
    @GetMapping("/admin/exists/id/{permissionId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "权限不存在")
    })
    public ResponseEntity<Response<Boolean>> existsById(@PathVariable Integer permissionId) {
        ResponseEntity<Boolean> response = permissionService.existsById(permissionId);
        return Response.ok(response.getBody());
    }
}
