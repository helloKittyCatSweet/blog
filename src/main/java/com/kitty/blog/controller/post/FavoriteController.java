package com.kitty.blog.controller.post;

import com.kitty.blog.dto.user.LoginResponseDto;
import com.kitty.blog.model.Favorite;
import com.kitty.blog.model.Post;
import com.kitty.blog.service.FavoriteService;
import com.kitty.blog.utils.Response;

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

@Tag(name = "收藏管理", description = "收藏管理相关接口")
@RestController
@RequestMapping("/api/post/favorite")
@CrossOrigin
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "创建收藏", description = "创建收藏")
    @PostMapping("/public/create")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "创建成功"),
                            @ApiResponse(responseCode = "400", description = "参数错误"),
                            @ApiResponse(responseCode = "404", description = "用户或帖子不存在"),
                            @ApiResponse(responseCode = "500", description = "服务器繁忙") })
    public ResponseEntity<Response<Boolean>> create
            (@RequestBody Favorite favorite) {
        ResponseEntity<Boolean> responseEntity = favoriteService.create(favorite);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "创建成功",
                HttpStatus.BAD_REQUEST,  "参数错误");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "更新收藏", description = "更新收藏")
    @PutMapping("/public/update")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "更新成功"),
                            @ApiResponse(responseCode = "400", description = "参数错误"),
                            @ApiResponse(responseCode = "404", description = "收藏不存在"),
                            @ApiResponse(responseCode = "500", description = "服务器繁忙") })
    public ResponseEntity<Response<Boolean>> update
            (@RequestBody Favorite favorite) {
        ResponseEntity<Boolean> responseEntity = favoriteService.update(favorite);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "更新成功",
                HttpStatus.BAD_REQUEST,  "参数错误");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER) " +
            " and @favoriteService.IsUser(#userId, #user.id)")
    @Operation(summary = "根据用户id查询所有收藏", description = "根据用户id查询所有收藏")
    @GetMapping("/public/find/{userId}/list")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功"),
                            @ApiResponse(responseCode = "404", description = "用户不存在") })
    public ResponseEntity<Response<List<Post>>> findByUserId
            (@RequestParam(value = "userId", required = true)
             @PathVariable Integer userId,
             @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<List<Post>> response = favoriteService.findByUserId(userId);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND,  "用户不存在");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER) " +
            " and @favoriteService.IsUser(#userId, #user.id)")
    @Operation(summary = "查询用户收藏数量", description = "查询用户收藏数量")
    @GetMapping("/public/find/{userId}/count")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功"),
                            @ApiResponse(responseCode = "404", description = "用户不存在") })
    public ResponseEntity<Response<Integer>> countByUserId
            (@RequestParam(value = "userId", required = true)
             @PathVariable Integer userId,
             @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<Integer> response = favoriteService.countByUserId(userId);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND,  "用户不存在");
    }

//    @GetMapping("/listByPostId")
//    public ResponseEntity<Response<Favorite>> findByPostId
//            (@RequestParam(value = "postId", required = true)
//             @PathVariable Integer postId) {
//        ResponseEntity<Favorite> responseEntity = favoriteService.findByPostId(postId);
//        if (responseEntity.getStatusCode() == HttpStatus.OK) {
//            return ResponseEntity.ok(new Response<>
//                    (HttpStatus.OK.value(), "查询成功",
//                            responseEntity.getBody()));
//        } else {
//            return ResponseEntity.status(responseEntity.getStatusCode()).
//                    body(new Response<>(responseEntity.getStatusCode().value()
//                            , "查询失败", null));
//        }
//    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "查询文章收藏数量", description = "查询文章收藏数量")
    @GetMapping("/public/find/{postId}/count")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功"),
                            @ApiResponse(responseCode = "404", description = "帖子不存在") })
    public ResponseEntity<Response<Integer>> countByPostId
            (@RequestParam(value = "postId", required = true)
             @PathVariable Integer postId) {
        ResponseEntity<Integer> response = favoriteService.countByPostId(postId);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND,  "帖子不存在");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
    @Operation(summary = "查询收藏", description = "查询收藏")
    @GetMapping("/public/find/{userId}/{postId}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功"),
                            @ApiResponse(responseCode = "404", description = "收藏不存在") })
    public ResponseEntity<Response<Favorite>> findByUserIdAndPostId
            (@RequestParam(value = "userId", required = true)
             @PathVariable Integer userId,
             @RequestParam(value = "postId", required = true)
             @PathVariable Integer postId) {
        ResponseEntity<Favorite> response = favoriteService.findByUserIdAndPostId(userId, postId);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND,  "收藏不存在");
    }

//    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_USER)")
//    @Operation(summary = "保存收藏", description = "保存收藏")
//    @PostMapping("/public/save")
//    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "保存成功"),
//                            @ApiResponse(responseCode = "500", description = "服务器繁忙") })
//    public ResponseEntity<Response<Favorite>> save
//            (@RequestBody Favorite favorite) {
//        ResponseEntity<Favorite> responseEntity = favoriteService.save(favorite);
//        return Response.createResponse(responseEntity,
//                HttpStatus.OK, "保存成功",
//                HttpStatus.INTERNAL_SERVER_ERROR,  "服务器繁忙");
//    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询收藏", description = "查询收藏")
    @GetMapping("/admin/find/id/{id}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功"),
                            @ApiResponse(responseCode = "404", description = "收藏不存在") })
    public ResponseEntity<Response<Post>> findById
            (@PathVariable Integer id) {
        ResponseEntity<Post> response = favoriteService.findById(id);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND,  "收藏不存在");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询所有收藏", description = "查询所有收藏")
    @GetMapping("/admin/find/all")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功") ,
                             @ApiResponse(responseCode = "404", description = "收藏不存在")} )
    public ResponseEntity<Response<List<Post>>> findAll() {
        ResponseEntity<List<Post>> response = favoriteService.findAll();
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND,  "收藏不存在");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "删除收藏", description = "删除收藏")
    @DeleteMapping("/admin/delete/id/{id}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "删除成功"),
                            @ApiResponse(responseCode = "404", description = "收藏不存在") })
    public ResponseEntity<Response<Boolean>> deleteById
            (@PathVariable Integer id) {
        ResponseEntity<Boolean> response = favoriteService.deleteById(id);
        return Response.createResponse(response,
                HttpStatus.OK, "删除成功",
                HttpStatus.NOT_FOUND,  "收藏不存在");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询收藏数量", description = "查询收藏数量")
    @GetMapping("/admin/count")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功") ,
                             @ApiResponse(responseCode = "404", description = "收藏不存在")})
    public ResponseEntity<Response<Long>> count() {
        ResponseEntity<Long> response = favoriteService.count();
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND,  "收藏不存在");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.controller.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询收藏是否存在", description = "查询收藏是否存在")
    @GetMapping("/admin/exists/id/{id}")
    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "查询成功"),
                            @ApiResponse(responseCode = "404", description = "收藏不存在") })
    public ResponseEntity<Response<Boolean>> existsById
            (@PathVariable Integer id) {
        ResponseEntity<Boolean> response = favoriteService.existsById(id);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND,  "收藏不存在");
    }
}
