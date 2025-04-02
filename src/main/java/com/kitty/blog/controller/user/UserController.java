package com.kitty.blog.controller.user;

import com.kitty.blog.dto.common.FileDto;
import com.kitty.blog.dto.user.LoginDto;
import com.kitty.blog.dto.user.LoginResponseDto;
import com.kitty.blog.dto.user.RegisterDto;
import com.kitty.blog.model.User;
import com.kitty.blog.service.UserService;
import com.kitty.blog.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "用户模块")
@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 注册
     *
     * @param registerDto 注册信息
     * @return 注册结果
     */
    // 注册不需要权限
    @Operation(summary = "注册")
    @PostMapping("/auth/register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "注册成功"),
            @ApiResponse(responseCode = "400", description = "用户名不合规"),
            @ApiResponse(responseCode = "409", description = "用户名已被注册或邮箱已被注册"),
            @ApiResponse(responseCode = "500", description = "注册失败")
    })
    public ResponseEntity<Response<Boolean>> register(@RequestBody RegisterDto registerDto) {
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setPassword(registerDto.getPassword());
        user.setEmail(registerDto.getEmail());
        ResponseEntity<Boolean> responseEntity = userService.register(user);
        return switch (responseEntity.getStatusCode().value()) {
            case 200 -> Response.ok(true);
            case 400 -> Response.error(HttpStatus.BAD_REQUEST, "用户名不合规");
            case 409 -> Response.error(HttpStatus.CONFLICT, "用户名已被注册或邮箱已被注册");
            default -> Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "注册失败");
        };
    }

    /**
     * 更新用户信息
     *
     * @param user
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "更新用户信息")
    @PutMapping("/public/update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "更新失败"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")
    })
    public ResponseEntity<Response<Boolean>> update(@RequestBody User user) {
        ResponseEntity<Boolean> responseEntity = userService.update(user);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "更新成功",
                HttpStatus.BAD_REQUEST, "更新失败");
    }

    /**
     * 激活用户
     *
     * @param userId
     * @param isActive
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER_USER_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)" +
            " or userId == principal.id)")
    @Operation(summary = "激活用户")
    @PutMapping("/admin/activate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "激活成功"),
            @ApiResponse(responseCode = "500", description = "激活失败")
    })
    public ResponseEntity<Response<Boolean>> activateUser
    (@RequestParam("userId") Integer userId,
     @RequestParam("isActive") Boolean isActive) {
        ResponseEntity<Boolean> responseEntity = userService.activateUser(userId, isActive);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, isActive ? "激活成功" : "取消激活成功",
                HttpStatus.INTERNAL_SERVER_ERROR, isActive ? "激活失败" : "取消激活失败");
    }

    /**
     * 根据用户名查询用户
     *
     * @param username
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "根据用户名查询用户")
    @GetMapping("/public/find/username/{username}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<Response<User>> findByUsername
    (@PathVariable("username") String username) {
        ResponseEntity<User> responseEntity = userService.findByUsername(username);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND, "用户不存在");
    }

    /**
     * 根据邮箱（模糊搜索）查询用户
     *
     * @param email
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "根据邮箱（模糊搜索）查询用户")
    @GetMapping("/public/find/email/{email}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<Response<User>> findByEmail(@PathVariable("email") String email) {
        ResponseEntity<User> responseEntity = userService.findByEmail(email);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND, "用户不存在");
    }

    /**
     * 根据邮箱后缀查询用户
     *
     * @param emailSuffix
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "根据邮箱后缀查询用户")
    @GetMapping("/public/find/email/suffix/{emailSuffix}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<Response<List<User>>> findByEmailSuffix
    (@PathVariable("emailSuffix") String emailSuffix) {
        ResponseEntity<List<User>> responseEntity = userService.findByEmailSuffix(emailSuffix);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND, "用户不存在");
    }

    /**
     * 查询激活用户
     *
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER_USER_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询激活用户")
    @GetMapping("/admin/find/{isActive}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "没有激活用户")
    })
    public ResponseEntity<Response<List<User>>> findByActivated
    (@PathVariable("isActive") boolean isActive) {
        ResponseEntity<List<User>> responseEntity = userService.findByActivated(isActive);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND, "没有激活用户");
    }

    /**
     * 登录
     * 登录成功返回token
     * 登录失败返回用户名或密码错误
     *
     * @param loginDto 登录信息
     * @return 登录结果
     */
    // 登录不需要权限
    @Operation(summary = "登录")
    @PostMapping("/auth/login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误"),
            @ApiResponse(responseCode = "403", description = "用户被禁用"),
            @ApiResponse(responseCode = "500", description = "登录失败")
    })
    public ResponseEntity<Response<LoginResponseDto>> login(@RequestBody LoginDto loginDto) {
        ResponseEntity<LoginResponseDto> responseEntity =
                userService.login(loginDto.getUsername(),
                        loginDto.getPassword());
        return switch (responseEntity.getStatusCode().value()) {
            case 200 -> Response.ok(responseEntity.getBody());
            case 401 -> Response.error(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
            case 403 -> Response.error(HttpStatus.FORBIDDEN, "用户被禁用");
            default -> Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "登录失败");
        };
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "验证token")
    @PostMapping("/public/validate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "验证成功"),
            @ApiResponse(responseCode = "401", description = "token失效")
    })
    public ResponseEntity<Response<String>> validateToken(@RequestParam String token) {
        // 移除可能的引号
        token = token.trim().replace("\"", "");
        ResponseEntity<String> responseEntity = userService.validateToken(token);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "验证成功",
                HttpStatus.UNAUTHORIZED, "token失效");
    }

    // 不需要权限
    @Operation(summary = "重置密码")
    @PutMapping("/auth/password/reset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "密码重置成功"),
            @ApiResponse(responseCode = "500", description = "密码重置失败")})
    public ResponseEntity<Response<Boolean>> resetPassword
            (@RequestBody Map<String,Object> params) {
        Integer userId = (Integer) params.get("userId");
        String password = (String) params.get("password");
        ResponseEntity<Boolean> responseEntity = userService.resetPassword(userId, password);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "密码重置成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "密码重置失败");
    }

    @Operation(summary = "验证密码")
    @PostMapping("/auth/password/verify")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "密码验证成功"),
            @ApiResponse(responseCode = "500", description = "密码验证失败")
    })
    public ResponseEntity<Response<Boolean>> verifyPassword(@RequestBody Map<String,Object> params){
        Integer userId = (Integer) params.get("userId");
        String password = (String) params.get("password");
        ResponseEntity<Boolean> responseEntity = userService.verifyPassword(userId, password);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "密码验证成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "密码验证失败");
    }


    @Operation(summary = "判断邮箱是否已被注册")
    @GetMapping("/auth/exist/email/{email}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "邮箱已被注册"),
            @ApiResponse(responseCode = "404", description = "邮箱未被注册")
    })
    public ResponseEntity<Response<Boolean>> existsByEmail(@PathVariable("email") String email) {
        ResponseEntity<Boolean> responseEntity = userService.existsByEmail(email);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "邮箱已被注册",
                HttpStatus.NOT_FOUND, "邮箱未被注册");
    }

    @Operation(summary = "根据邮箱查询用户")
    @GetMapping("/auth/find/email/{email}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<Response<User>> findUserByEmail(@PathVariable("email") String email) {
        ResponseEntity<User> responseEntity = userService.findUserByEmail(email);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND, "用户不存在");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "上传头像", description = "上传用户头像图片，支持jpg、png、jpeg格式")
    @PostMapping(value = "/public/upload/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "上传成功"),
            @ApiResponse(responseCode = "400", description = "文件格式不正确"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "500", description = "上传失败")
    })
    public ResponseEntity<Response<Boolean>> uploadAvatar(
            @Parameter(description = "头像文件", required = true)
            @RequestPart(value = "file") MultipartFile file,
            @Parameter(description = "用户ID", required = true)
            @RequestParam Integer userId) {
        // 检查文件是否为空
        if (file.isEmpty()) {
            return Response.createResponse(new ResponseEntity<>(HttpStatus.NO_CONTENT),
                    HttpStatus.BAD_REQUEST,
                    "文件为空", null, null);
        }
        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Response.createResponse(new ResponseEntity<>(HttpStatus.NO_CONTENT),
                    HttpStatus.BAD_REQUEST,
                    "文件格式不正确", null, null);
        }
        // 检查文件大小（限制为5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            return Response.createResponse(new ResponseEntity<>(HttpStatus.NO_CONTENT),
                    HttpStatus.BAD_REQUEST,
                    "文件大小超出限制", null, null);
        }
        try {
            // 创建临时文件
            File tempFile = File.createTempFile("avatar-", "-" +
                    file.getOriginalFilename());
            file.transferTo(tempFile);
            // 构建文件传输对象
            FileDto fileDto = new FileDto();
            fileDto.setFile(tempFile);
            fileDto.setSomeId(userId);
            // 调用服务层处理上传
            ResponseEntity<Boolean> result = userService.uploadAvatar(fileDto);
            // 删除临时文件
            if (!tempFile.delete()) {
                log.warn("临时文件删除失败: {}", tempFile.getAbsolutePath());
            }
            // 处理响应
            if (result.getStatusCode() == HttpStatus.OK &&
                    Boolean.TRUE.equals(result.getBody())) {
                return Response.createResponse
                        (result, HttpStatus.OK,
                                "上传成功", null, null);
            } else if (result.getStatusCode() == HttpStatus.NOT_FOUND) {
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
     * 查询所有用户
     *
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER_USER_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询所有用户")
    @GetMapping("/admin/find/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "查询失败")
    })
    public ResponseEntity<Response<List<User>>> findAll() {
        ResponseEntity<List<User>> responseEntity = userService.findAll();
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
    }

    /**
     * 根据用户ID查询用户
     *
     * @param userId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "根据用户ID查询用户")
    @GetMapping("/public/find/id/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<Response<User>> findById(@PathVariable("userId") Integer userId) {
        ResponseEntity<User> responseEntity = userService.findById(userId);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "查询成功",
                HttpStatus.NOT_FOUND, "用户不存在");
    }

    /**
     * 删除用户
     *
     * @param userId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER_USER_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)" +
            " or userId == principal.id")
    @Operation(summary = "删除用户")
    @DeleteMapping("/admin/delete/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public ResponseEntity<Response<Boolean>> deleteById(@PathVariable("userId") Integer userId) {
        ResponseEntity<Boolean> responseEntity = userService.deleteById(userId);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "删除成功",
                HttpStatus.NOT_FOUND, "用户不存在");
    }

    /**
     * 查询用户数量
     *
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER_USER_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询用户数量")
    @GetMapping("/admin/count")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "查询失败")
    })
    public ResponseEntity<Response<Long>> count() {
        ResponseEntity<Long> responseEntity = userService.count();
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "查询成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "查询失败");
    }

    /**
     * 判断用户是否存在
     *
     * @param userId
     * @return
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER_USER_MANAGER)" +
            " or hasRole(T(com.kitty.blog.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "判断用户是否存在")
    @GetMapping("/admin/exist/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "存在"),
            @ApiResponse(responseCode = "404", description = "不存在")
    })
    public ResponseEntity<Response<Boolean>> existsById(@PathVariable("userId") Integer userId) {
        ResponseEntity<Boolean> responseEntity = userService.existsById(userId);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "存在",
                HttpStatus.NOT_FOUND, "不存在");
    }
}
