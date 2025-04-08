package com.kitty.blog.application.controller.post;

import com.kitty.blog.application.dto.common.FileDto;
import com.kitty.blog.application.dto.post.PostDto;
import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.domain.model.*;
import com.kitty.blog.domain.model.category.PostCategory;
import com.kitty.blog.domain.model.tag.PostTag;
import com.kitty.blog.domain.service.PostService;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Tag(name = "文章模块")
@RestController
@RequestMapping("/api/post")
@CrossOrigin
@Slf4j
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 创建文章
     * @param postDto 文章实体类
     * @return 成功或失败的响应
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "创建文章")
    @PostMapping("/public/create")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<PostDto>> create(@RequestBody PostDto postDto) {
        ResponseEntity<PostDto> response = postService.create(postDto);
        return Response.createResponse(response,
                HttpStatus.OK, "创建成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 更新文章
     * @param postDto
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)" +
            " and (#postDto.post.userId == #user.id)")
    @Operation(summary = "更新文章")
    @PutMapping("/public/update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<PostDto>> update(
            @RequestBody PostDto postDto,
            @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<PostDto> response = postService.update(postDto);
        return Response.createResponse(response,
                HttpStatus.OK, "更新成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER) " +
            "and @postService.isAuthorOfOpenPost(#user.id, #postId)")
    @Operation(summary = "上传文件", description = "上传文件，支持图片、文件、压缩包格式")
    @PostMapping(value = "/public/upload/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "上传成功"),
            @ApiResponse(responseCode = "400", description = "文件格式不正确"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "500", description = "上传失败")
    })
    public ResponseEntity<Response<String>> uploadAttachment(
            @Parameter(description = "文件", required = true)
            @RequestPart(value = "file") @NotNull MultipartFile file,
            @Parameter(description = "博客ID", required = true)
            @RequestParam Integer postId,
            @AuthenticationPrincipal LoginResponseDto user) {
        // 检查文件是否为空
        if (file.isEmpty()) {
            return Response.createResponse(new ResponseEntity<>(HttpStatus.NO_CONTENT),
                    HttpStatus.BAD_REQUEST,
                    "文件为空", null, null);
        }
        // 检查文件大小（限制为5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return Response.createResponse(new ResponseEntity<>(HttpStatus.NO_CONTENT),
                    HttpStatus.BAD_REQUEST,
                    "文件大小超出限制", null, null);
        }
        try {
            // 创建临时文件
            File tempFile = File.createTempFile("tmp", "-" +
                    file.getOriginalFilename());
            file.transferTo(tempFile);
            // 构建文件传输对象
            FileDto fileDto = new FileDto();
            fileDto.setFile(tempFile);
            fileDto.setSomeId(postId);
            // 调用服务层处理上传
            ResponseEntity<String> result = postService.uploadAttachment(fileDto);
            // 删除临时文件
            if (!tempFile.delete()) {
                log.warn("临时文件删除失败: {}", tempFile.getAbsolutePath());
            }
            // 处理响应
            if (result.getStatusCode() == HttpStatus.OK) {
                result.getBody();
            }
            if (result.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Response.createResponse(
                        result, HttpStatus.NOT_FOUND,
                        "用户不存在", null, null);
            } else {
                return Response.createResponse(
                        result, HttpStatus.INTERNAL_SERVER_ERROR,
                        "上传失败", null, null);
            }

        } catch (IOException e) {
            log.error("文件处理失败", e);
            return Response.createResponse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR),
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null, null, "文件处理失败");
        } catch (Exception e) {
            log.error("上传失败", e);
            return Response.createResponse(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR)
                    , HttpStatus.INTERNAL_SERVER_ERROR,
                    null, null, "上传失败");
        }
    }

    /**
     * 添加文章分类
     * @param postCategory
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "添加文章分类")
    @PostMapping("/add/category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "添加成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Boolean>> addCategory
            (@RequestBody PostCategory postCategory) {
        ResponseEntity<Boolean> response =
                postService.addCategory(postCategory.getId().getPostId(),
                        postCategory.getId().getCategoryId());
        return Response.createResponse(response,
                HttpStatus.OK, "添加成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 添加文章分类
     * @param postTag
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "添加文章标签")
    @PostMapping("/add/tag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "添加成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Boolean>> addTag
            (@RequestBody PostTag postTag) {
        ResponseEntity<Boolean> response =
                postService.addTag(postTag.getId().getPostId(),
                        postTag.getId().getTagId());
        return Response.createResponse(response,
                HttpStatus.OK, "添加成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 根据文章ID查询文章详情
     * @param postVersion
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "添加文章版本")
    @PostMapping("/public/add/version")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "添加成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<PostVersion>> addVersion
            (@RequestBody PostVersion postVersion) {
        ResponseEntity<PostVersion> response = postService.addVersion
                (postVersion.getPostId(), postVersion.getContent(), postVersion.getUserId());
        return Response.createResponse(response,
                HttpStatus.OK, "添加成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "设置文章可见性")
    @PutMapping("/public/set/visibility")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "设置成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Boolean>> setVisibility(
            @Parameter(description = "文章ID", required = true)
            @RequestParam Integer postId,
            @Parameter(description = "可见性", required = true)
            @RequestParam String visibility) {
        ResponseEntity<Boolean> response = postService.setVisibility(postId, visibility);
        return Response.createResponse(response,
                HttpStatus.OK, "设置成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }


    /**
     * 根据文章ID查询文章详情
     * @param postCategory
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "删除文章分类")
    @DeleteMapping("/public/delete/category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Boolean>> deleteCategory
            (@RequestBody PostCategory postCategory) {
        ResponseEntity<Boolean> response =
                postService.deleteCategory(postCategory.getId().getPostId(),
                        postCategory.getId().getCategoryId());
        return Response.createResponse(response,
                HttpStatus.OK, "删除成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 删除文章标签
     * @param postTag
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USE)")
    @Operation(summary = "删除文章标签")
    @DeleteMapping("/public/delete/tag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Boolean>> deleteTag(@RequestBody PostTag postTag) {
        ResponseEntity<Boolean> response =
                postService.deleteTag(postTag.getId().getPostId(),
                        postTag.getId().getTagId());
        return Response.createResponse(response,
                HttpStatus.OK, "删除成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 删除文章版本
     * @param postVersion
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "删除文章版本")
    @DeleteMapping("/public/delete/version")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Boolean>> deleteVersion
            (@RequestBody PostVersion postVersion) {
        ResponseEntity<Boolean> response =
                postService.deleteVersion(postVersion.getPostId(),
                        postVersion.getVersionId());
        return Response.createResponse(response,
                HttpStatus.OK, "删除成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 根据用户名查询文章列表
     * @param username
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "根据用户名查询文章列表")
    @GetMapping("/public/find/user/{username}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<List<PostDto>>> findByUsername
            (@PathVariable String username) {
        ResponseEntity<List<PostDto>> response = postService.findByUsername(username);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 根据标题关键字查询文章列表
     * @param keyword
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "根据标题关键字查询文章列表")
    @GetMapping("/public/find/title/{keyword}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<List<Post>>> findByTitleContaining
            (@PathVariable String keyword) {
        ResponseEntity<List<Post>> response = postService.findByTitleContaining(keyword);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 根据内容关键字查询文章列表
     * @param keyword
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "根据内容关键字查询文章列表")
    @GetMapping("/public/find/content/{keyword}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<List<Post>>> findByContentContaining
            (@PathVariable String keyword) {
        ResponseEntity<List<Post>> response = postService.findByContentContaining(keyword);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 根据用户ID和发布状态查询文章列表
     * @param isPublished
     * @param username
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role)." +
            "ROLE_POST_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)" +
            " or @postService.isAuthorOfOpenPost(#username, #user.id)")
    @Operation(summary = "根据用户名和发布状态查询文章列表")
    @GetMapping("/admin/find/{username}/{isPublished}/published")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<List<Post>>> findByUsernameIsPublished
            (@PathVariable Boolean isPublished,
             @PathVariable String username,
             @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<List<Post>> response =
                postService.findByUsernameIsPublished(isPublished, username);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 根据分类ID查询文章列表
     * @param category
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "根据分类查询文章列表")
    @GetMapping("/public/find/category/{category}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<List<Post>>> findByCategory(@PathVariable String category) {
        ResponseEntity<List<Post>> response = postService.findByCategory(category);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 根据标签ID查询文章列表
     * @param tag
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "根据标签查询文章列表")
    @GetMapping("/public/find/tag/{tag}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<List<Post>>> findByTagId(@PathVariable String tag) {
        ResponseEntity<List<Post>> response = postService.findByTag(tag);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 根据文章ID查询文章详情
     * @param postId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "根据文章ID查询文章详情")
    @GetMapping("/public/find/id/{postId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<PostDto>> findById(@PathVariable Integer postId) {
        ResponseEntity<PostDto> response = postService.findById(postId);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 获取文章最新版本号
     * @param postId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role)." +
            " ROLE_POST_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)" +
            " or @postService.isAuthorOfOpenPost(#postId, #user.id)")
    @Operation(summary = "获取文章最新版本号")
    @GetMapping("/admin/getLatestVersion/{postId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Integer>> getLatestVersion(
            @PathVariable Integer postId,
            @AuthenticationPrincipal LoginResponseDto user
    ) {
        ResponseEntity<Integer> response = postService.getLatestVersion(postId);
        if (response.getBody() != null) {
            return ResponseEntity.ok(new Response<>(200, "查询成功", response.getBody()));
        } else {
            return ResponseEntity.ok(new Response<>(500, "服务器内部错误", null));
        }
    }

//    @Operation(summary = "置顶文章")
//    @PostMapping("/top")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "置顶成功"),
//            @ApiResponse(responseCode = "500", description = "服务器内部错误")
//    })
//    public ResponseEntity<Response<Boolean>> top(Integer postId) {
//        ResponseEntity<Boolean> response = postService.top(postId);
//        if (Boolean.TRUE.equals(response.getBody())){
//            return ResponseEntity.ok(new Response<>(200, "置顶成功", true));
//        }else {
//            return ResponseEntity.ok(new Response<>(500, "服务器内部错误", false));
//        }
//    }
//
//    @Operation(summary = "取消置顶文章")
//    @PostMapping("/cancelTop")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "取消置顶成功"),
//            @ApiResponse(responseCode = "500", description = "服务器内部错误")
//    })
//    public ResponseEntity<Response<Boolean>> cancelTop(Integer postId) {
//        ResponseEntity<Boolean> response = postService.cancelTop(postId);
//        if (Boolean.TRUE.equals(response.getBody())){
//            return ResponseEntity.ok(new Response<>(200, "取消置顶成功", true));
//        }else {
//            return ResponseEntity.ok(new Response<>(500, "服务器内部错误", false));
//        }
//    }

    /**
     * 点赞文章
     * @param postId
     * @param count
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "点赞文章")
    @PutMapping("/public/like")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "点赞成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Boolean>> like
            (@RequestParam Integer postId,
             @RequestParam Integer count) {
        ResponseEntity<Boolean> response = postService.increaseLikes
                (postId, count);
        return Response.createResponse(response,
                HttpStatus.OK, count > 0 ? "点赞成功" : "取消点赞成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 收藏文章
     * @param postId
     * @param count
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "收藏文章")
    @PutMapping("/public/collect")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "收藏成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Boolean>> increaseFavorites
            (@RequestParam Integer postId,
             @RequestParam Integer count,
             @AuthenticationPrincipal LoginResponseDto user){
        ResponseEntity<Boolean> response = postService.increaseFavorites
                (user.getId(), postId, count);
        return Response.createResponse(response,
                HttpStatus.OK, count > 0 ? "收藏成功" : "取消收藏成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 保存文章
     * @param post
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "保存文章")
    @PostMapping("/public/save")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "保存成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Post>> save(@RequestBody Post post) {
        ResponseEntity<Post> response = postService.save(post);
        return Response.createResponse(response,
                HttpStatus.OK, "保存成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "根据可见性查询文章列表")
    @GetMapping("/public/find/visibility/{visibility}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<List<Post>>> findByVisibility
            (@PathVariable String visibility,
             @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<List<Post>> response = postService.findByVisibility(visibility, user.getId());
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 查询所有文章
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE)")
    @Operation(summary = "查询所有文章")
    @GetMapping("/admin/find/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<List<Post>>> findAll(){
        ResponseEntity<List<Post>> response = postService.findAll();
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 根据ID删除文章
     * @param postId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_POST_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)" +
            " or @postService.isAuthorOfOpenPost(#postId, #user.id)")
    @Operation(summary = "根据ID删除文章")
    @DeleteMapping("/public/delete/id/{postId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Boolean>> deleteById(
            @PathVariable Integer postId,
            @AuthenticationPrincipal LoginResponseDto user) {
        ResponseEntity<Boolean> response = postService.deleteById(postId);
        return Response.createResponse(response,
                HttpStatus.OK, "删除成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 查询文章总数
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_POST_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询文章总数")
    @GetMapping("/admin/count")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Long>> count(){
        ResponseEntity<Long> response = postService.count();
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    /**
     * 根据ID查询文章是否存在
     * @param postId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_POST_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "根据ID查询文章是否存在")
    @GetMapping("/admin/exists/id/{postId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Boolean>> existsById(@PathVariable Integer postId) {
        ResponseEntity<Boolean> response = postService.existsById(postId);
        return Response.createResponse(response,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "获取控制面板统计数据")
    @GetMapping("/public/dashboard/stats")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Map<String, Object>>> getDashboardStats() {
        ResponseEntity<Map<String, Object>> response = postService.getDashboardStats();
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取统计数据失败");
    }

    @GetMapping("/public/dashboard/monthly-stats")
    public ResponseEntity<Response<Map<String, Object>>> getMonthlyStats() {
        Map<String, Object> stats = postService.getMonthlyStats();
        return Response.createResponse(
                new ResponseEntity<>(stats, HttpStatus.OK),
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取失败"
        );
    }

    @GetMapping("/public/dashboard/recent-posts")
    public ResponseEntity<Response<List<PostDto>>> getRecentPosts() {
        List<PostDto> posts = postService.getRecentPosts().getBody();
        return Response.createResponse(
                new ResponseEntity<>(posts, HttpStatus.OK),
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取失败"
        );
    }


    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "获取互动数据统计")
    @GetMapping("/public/dashboard/interaction-stats")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public ResponseEntity<Response<Map<String, Integer>>> getInteractionStats() {
        ResponseEntity<Map<String, Integer>> response = postService.getInteractionStats();
        return Response.createResponse(response,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取互动统计失败");
    }
}
