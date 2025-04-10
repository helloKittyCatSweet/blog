package com.kitty.blog.application.controller.post;

import com.kitty.blog.application.dto.comment.CommentDto;
import com.kitty.blog.application.dto.comment.TreeDto;
import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.domain.model.Comment;
import com.kitty.blog.domain.service.CommentService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "评论模块", description = "评论相关接口")
@RestController
@RequestMapping("/api/comment")
@CrossOrigin
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 新增评论
     * @param comment
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "新增评论", description = "新增评论")
    @PostMapping("/public/create")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "新增成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Boolean>> create(
            @RequestBody Comment comment) {
        ResponseEntity<Boolean> response = commentService.create(comment);
        if (Boolean.FALSE.equals(response.getBody())){
            if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
                return Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
            }else if (response.getStatusCode() == HttpStatus.BAD_REQUEST){
                return Response.error(HttpStatus.BAD_REQUEST, "含有敏感信息，请谨慎发言");
            }
        }
        return Response.ok(false,"创建成功");
    }

    /**
     * 修改评论
     *
     * @param comment
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_COMMENT_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)" +
            " or #comment.userId == principal.id)")
    @Operation(summary = "修改评论")
    @PutMapping("/admin/update")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Boolean>> update(
            @RequestBody Comment comment) {
        ResponseEntity<Boolean> response = commentService.update(comment);
        return Response.createResponse(response,
                HttpStatus.OK, "修改成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "修改失败");
    }

    /**
     * 点赞评论
     *
     * @param commentId
     * @param count
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "点赞", description = "点赞评论")
    @PutMapping("/public/add/{commentId}/like")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "点赞成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Boolean>> incrementLikes(
            @PathVariable Integer commentId,
            @RequestParam int count) {
        ResponseEntity<Boolean> response = commentService.incrementLikes(commentId, count);
        return Response.createResponse(response,
                HttpStatus.OK, count > 0 ? "点赞成功" : "取消点赞成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 获取评论列表
     *
     * @param postId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "获取评论列表")
    @GetMapping("/public/find/{postId}/list")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "获取失败")})
    public ResponseEntity<Response<List<TreeDto>>> findByPostId(
            @PathVariable Integer postId) {
        ResponseEntity<List<TreeDto>> response = commentService.findByPostId(postId);
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "根据用户ID获取评论")
    @GetMapping("/public/find/user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<Response<List<CommentDto>>> getCommentsByUser(
            @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<List<CommentDto>> response = commentService.findByUserId(user.getId());
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }


    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "获取作者文章的所有评论")
    @GetMapping("/public/find/author")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "403", description = "无权限访问"),
            @ApiResponse(responseCode = "404", description = "作者不存在")
    })
    public ResponseEntity<Response<List<CommentDto>>> getAuthorPostComments
            (@AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<List<CommentDto>> response = commentService.findByPostAuthor(user.getId());
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 获取评论总数
     *
     * @param postId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "获取评论总数")
    @GetMapping("/public/count/{postId}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Integer>> getCommentCountByPostId(
            @PathVariable Integer postId) {
        ResponseEntity<Integer> response = commentService.getCommentCountByPostId(postId);
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 新增评论
     *
     * @param comment
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "新增评论", description = "新增评论")
    @PostMapping("/public/save")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "新增成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Comment>> save(
            @RequestBody Comment comment) {
        ResponseEntity<Comment> response = commentService.save(comment);
        return Response.createResponse(response,
                HttpStatus.OK, "新增成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "新增失败");
    }

    /**
     * 获取评论详情
     *
     * @param commentId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "获取评论详情", description = "根据评论ID获取评论详情")
    @GetMapping("/public/find/{commentId}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Comment>> findById(
            @PathVariable Integer commentId) {
        ResponseEntity<Comment> response = commentService.findById(commentId);
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 获取评论列表
     *
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_COMMENT_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "获取评论列表", description = "获取评论列表")
    @GetMapping("/admin/find/all")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<List<TreeDto>>> findAll() {
        ResponseEntity<List<TreeDto>> response = commentService.findAll();
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 删除评论
     *
     * @param commentId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_COMMENT_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)" +
            " or #comment.userId == principal.id " +
            " or @commentService.isUnderAuthorPost(commentId, principal.id)")
    @Operation(summary = "删除评论", description = "根据评论ID删除评论")
    @DeleteMapping("/public/delete/id/{commentId}/{userId}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "该评论不存在")})
    public ResponseEntity<Response<Boolean>> deleteById(
            @PathVariable Integer commentId) {
        ResponseEntity<Boolean> response = commentService.deleteById(commentId);
        return Response.createResponse(response,
                HttpStatus.OK, "删除成功",
                HttpStatus.NOT_FOUND, "该评论不存在");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_COMMENT_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)" +
            " or @commentService.isUnderAuthorPost(commentIds, principal.id)")
    @Operation(summary = "删除评论", description = "根据评论ID删除评论")
    @DeleteMapping("/public/delete/batch")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "该评论不存在")})
    public ResponseEntity<Response<Boolean>> deleteBatch(
            @RequestBody List<Integer> commentIds) {
        ResponseEntity<Boolean> response = commentService.deleteBatch(commentIds);
        return Response.createResponse(response,
                HttpStatus.OK, "删除成功",
                HttpStatus.NOT_FOUND, "该评论不存在");
    }

    /**
     * 获取评论总数
     *
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_COMMENT_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "获取评论总数", description = "获取评论总数")
    @GetMapping("/admin/count")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")})
    public ResponseEntity<Response<Long>> count() {
        ResponseEntity<Long> response = commentService.count();
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器繁忙");
    }

    /**
     * 判断评论是否存在
     *
     * @param commentId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_COMMENT_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "判断评论是否存在", description = "根据评论ID判断评论是否存在")
    @GetMapping("/admin/exists/{commentId}")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "判断成功"),
            @ApiResponse(responseCode = "404", description = "该评论不存在")})
    public ResponseEntity<Response<Boolean>> existsById(
            @PathVariable Integer commentId) {
        ResponseEntity<Boolean> response = commentService.existsById(commentId);
        return Response.createResponse(response,
                HttpStatus.OK, "判断成功",
                HttpStatus.NOT_FOUND, "该评论不存在");
    }
}
