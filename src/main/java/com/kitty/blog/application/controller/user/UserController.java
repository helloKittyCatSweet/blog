package com.kitty.blog.application.controller.user;

import com.kitty.blog.application.dto.common.FileDto;
import com.kitty.blog.application.dto.user.LoginDto;
import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.application.dto.user.RegisterDto;
import com.kitty.blog.application.dto.userRole.WholeUserInfo;
import com.kitty.blog.common.annotation.LogUserActivity;
import com.kitty.blog.domain.model.User;
import com.kitty.blog.domain.service.user.UserService;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import java.util.stream.Collectors;

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
    @LogUserActivity("注册")
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
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "更新用户信息")
    @PutMapping("/public/update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "更新失败"),
            @ApiResponse(responseCode = "500", description = "服务器繁忙")
    })
    @LogUserActivity("更新用户信息")
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
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "激活用户")
    @PutMapping("/admin/activate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "激活成功"),
            @ApiResponse(responseCode = "500", description = "激活失败")
    })
    @LogUserActivity("激活用户")
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
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
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
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
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
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
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
     */
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询激活用户")
    @GetMapping("/admin/find/{isActive}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "404", description = "没有激活用户")
    })
    public ResponseEntity<Response<List<WholeUserInfo>>> findByActivated
    (@PathVariable("isActive") boolean isActive) {
        ResponseEntity<List<WholeUserInfo>> responseEntity = userService.findByActivated(isActive);
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
    @LogUserActivity("登录")
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

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "验证token")
    @PostMapping("/public/validate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "验证成功"),
            @ApiResponse(responseCode = "401", description = "token失效")
    })
    public ResponseEntity<Response<String>> validateToken(@RequestBody String token) {
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

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
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
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "查询所有用户")
    @GetMapping("/admin/find/all")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "查询失败")
    })
    public ResponseEntity<Response<Page<WholeUserInfo>>> findAll(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "sort", required = false, defaultValue = "id") String[] sort
    ) {
        ResponseEntity<Page<WholeUserInfo>> responseEntity = userService.findAll(page, size, sort);
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
    // 前台用户展示
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
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)" +
            " or userId == principal.id")
    @Operation(summary = "删除用户")
    @DeleteMapping("/admin/delete/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @LogUserActivity("删除用户")
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
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
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
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
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

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "搜索用户")
    @GetMapping("/admin/find/keyword")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "搜索成功"),
            @ApiResponse(responseCode = "404", description = "未找到匹配用户")
    })
    public ResponseEntity<Response<List<WholeUserInfo>>> searchUsers
            (@RequestParam("keyword") String keyword) {
        ResponseEntity<List<WholeUserInfo>> responseEntity =
                userService.findUserByUsernameAndEmail(keyword);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "搜索成功",
                HttpStatus.NOT_FOUND, "未找到匹配用户");
    }

    @PostMapping("/admin/import")
    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_ROLE_MANAGER)" +
            " or hasRole(T(com.kitty.blog.common.constant.Role).ROLE_SYSTEM_ADMINISTRATOR)")
    @Operation(summary = "导入用户")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "导入成功"),
            @ApiResponse(responseCode = "400", description = "文件格式不正确"),
            @ApiResponse(responseCode = "500", description = "导入失败")
    })
    @LogUserActivity("导入用户")
    public ResponseEntity<Response<String>> importUsers(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Response.error("请选择要上传的文件");
        }

        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!"xlsx".equals(fileExtension) && !"xls".equals(fileExtension)) {
            return Response.error("只支持 Excel 文件格式（.xlsx 或 .xls）");
        }

        ResponseEntity<List<UserService.ImportError>> response = userService.importUsers(file);

        if (response.getStatusCode() == HttpStatus.OK) {
            return Response.ok("导入成功");
        } else {
            List<UserService.ImportError> errors = response.getBody();
            String errorMessage = errors.stream()
                    .map(e -> String.format("第 %d 行：%s", e.getRow(), e.getMessage()))
                    .collect(Collectors.joining("\n"));
            return Response.error("导入失败：\n" + errorMessage);
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "获取用户签名URL")
    @GetMapping("/public/signature/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "500", description = "获取失败")
    })
    public ResponseEntity<Response<String>> getSignatureUrl(@PathVariable("userId") Integer userId) {
        ResponseEntity<String> responseEntity = userService.getSignatureUrl(userId);
        return Response.createResponse(responseEntity,
                HttpStatus.OK, "获取成功",
                HttpStatus.INTERNAL_SERVER_ERROR, "获取失败");
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "生成默认签名")
    @PostMapping("/public/signature/generate/{userId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "生成成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "500", description = "生成失败")
    })
    public ResponseEntity<Response<String>> generateDefaultSignature(@PathVariable Integer userId) {
        ResponseEntity<String> result = userService.generateDefaultSignature(userId);
        return switch (result.getStatusCode().value()) {
            case 200 -> Response.ok(result.getBody(), "默认签名生成成功");
            case 404 -> Response.error(HttpStatus.NOT_FOUND, "用户不存在");
            default -> Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "默认签名生成失败");
        };
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
    @Operation(summary = "上传签名", description = "上传用户签名图片，支持jpg、png、jpeg格式")
    @PostMapping(value = "/public/upload/signature", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "上传成功"),
            @ApiResponse(responseCode = "400", description = "文件格式不正确"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "500", description = "上传失败")
    })
    public ResponseEntity<Response<Boolean>> uploadSignature(
            @Parameter(description = "签名文件", required = true)
            @RequestPart(value = "file") MultipartFile file,
            @Parameter(description = "用户ID", required = true)
            @RequestParam Integer userId) {

        // 检查文件是否为空
        if (file.isEmpty()) {
            return Response.error(HttpStatus.BAD_REQUEST, "文件为空");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Response.error(HttpStatus.BAD_REQUEST, "文件格式不正确");
        }

        // 检查文件大小（限制为2MB）
        if (file.getSize() > 2 * 1024 * 1024) {
            return Response.error(HttpStatus.BAD_REQUEST, "文件大小超出限制");
        }

        try {
            // 创建临时文件
            File tempFile = File.createTempFile("signature-", "-" + file.getOriginalFilename());
            file.transferTo(tempFile);

            // 构建文件传输对象
            FileDto fileDto = new FileDto();
            fileDto.setFile(tempFile);
            fileDto.setSomeId(userId);
            fileDto.setIdType("signature");

            // 调用服务层处理上传
            ResponseEntity<Boolean> result = userService.uploadSignature(fileDto);

            // 删除临时文件
            if (!tempFile.delete()) {
                log.warn("临时文件删除失败: {}", tempFile.getAbsolutePath());
            }

            // 处理响应
            return switch (result.getStatusCode().value()) {
                case 200 -> Response.ok(true);
                case 404 -> Response.error(HttpStatus.NOT_FOUND, "用户不存在");
                default -> Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "签名上传失败");
            };

        } catch (IOException e) {
            log.error("文件处理失败", e);
            return Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "文件处理失败");
        } catch (Exception e) {
            log.error("上传失败", e);
            return Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "上传失败");
        }
    }
}
