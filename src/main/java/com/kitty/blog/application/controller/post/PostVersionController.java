package com.kitty.blog.application.controller.post;

import com.kitty.blog.domain.model.PostVersion;
import com.kitty.blog.domain.service.PostVersionService;
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

@Tag(name = "版本管理")
@RestController
@RequestMapping("/api/post/postVersion")
@CrossOrigin
public class PostVersionController {

    @Autowired
    private PostVersionService postVersionService;

    /**
     * 根据文章ID查询版本列表
     * @param postId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "根据文章ID查询版本列表")
    @GetMapping("/public/find/post/{postId}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
                           @ApiResponse(responseCode = "500", description = "查询失败")})
    public ResponseEntity<Response<List<PostVersion>>> findByPostId(@PathVariable Integer postId) {
        ResponseEntity<List<PostVersion>> entity = postVersionService.findByPostId(postId);
        return Response.createResponse(entity,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "激活版本")
    @PutMapping("/public/activate/{postId}/{versionId}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "激活成功"),
                           @ApiResponse(responseCode = "500", description = "激活失败")})
    public ResponseEntity<Response<Boolean>> activateVersion
            (@PathVariable Integer postId, @PathVariable Integer versionId) {
        ResponseEntity<Boolean> entity = postVersionService.activateVersion(postId, versionId);
        return Response.createResponse(entity,
                HttpStatus.OK, "激活成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "激活失败");
    }

    /**
     * 保存版本
     * @param postVersion
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "保存版本")
    @PostMapping("/public/save")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "保存成功"),
                           @ApiResponse(responseCode = "500", description = "保存失败")})
    public ResponseEntity<Response<PostVersion>> save(PostVersion postVersion) {
        ResponseEntity<PostVersion> entity = postVersionService.save(postVersion);
        return Response.createResponse(entity,
                HttpStatus.OK, "保存成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "保存失败");
    }

    /**
     * 根据版本ID查询版本
     * @param versionId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据版本ID查询版本")
    @GetMapping("/admin/find/id/{versionId}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "查询失败")})
    public ResponseEntity<Response<PostVersion>> findById(Integer versionId) {
        ResponseEntity<PostVersion> entity = postVersionService.findById(versionId);
        return Response.createResponse(entity,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
    }

    /**
     * 查询所有版本
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询所有版本")
    @GetMapping("/admin/find/all")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "查询失败")})
    public ResponseEntity<Response<List<PostVersion>>> findAll() {
        ResponseEntity<List<PostVersion>> entity = postVersionService.findAll();
        return Response.createResponse(entity,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
    }

    /**
     * 删除版本
     * @param versionId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "删除版本")
    @DeleteMapping("/public/delete/id/{versionId}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "500", description = "删除失败")})
    public ResponseEntity<Response<Boolean>> deleteById(@PathVariable("versionId") Integer versionId) {
        ResponseEntity<Boolean> entity = postVersionService.deleteById(versionId);
        return Response.createResponse(entity,
                HttpStatus.OK, "删除成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "删除失败");
    }

    /**
     * 统计版本数量
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "统计版本数量")
    @GetMapping("/admin/count")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "统计成功"),
            @ApiResponse(responseCode = "500", description = "统计失败")})
    public ResponseEntity<Response<Long>> count() {
        ResponseEntity<Long> response = postVersionService.count();
        return Response.createResponse(response,
                HttpStatus.OK, "统计成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "统计失败");
    }

    /**
     * 判断版本是否存在
     * @param versionId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "判断版本是否存在")
    @GetMapping("/admin/exist/id/{versionId}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "判断成功"),
            @ApiResponse(responseCode = "500", description = "判断失败")})
    public ResponseEntity<Response<Boolean>> existsById(Integer versionId) {
        ResponseEntity<Boolean> response = postVersionService.existsById(versionId);
        return Response.createResponse(response,
                HttpStatus.OK, "判断成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "判断失败");
    }
}
