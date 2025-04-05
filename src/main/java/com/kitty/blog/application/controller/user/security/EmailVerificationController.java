package com.kitty.blog.controller.user.security;

import com.kitty.blog.dto.user.EmailVerificationDto;
import com.kitty.blog.application.service.user.EmailVerificationService;
import com.kitty.blog.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Tag(name = "邮件验证", description = "邮件验证相关接口")
@RestController
@RequestMapping("/api/user/")
@CrossOrigin
public class EmailVerificationController {

    @Autowired
    private EmailVerificationService emailVerificationService;

    // 发送验证码接口
    @Operation(summary = "发送验证码")
    @PostMapping("/auth/email/send")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "验证码发送成功"),
            @ApiResponse(responseCode = "401", description = "未登录或登录过期")
    })
    public ResponseEntity<Response<String>> sendEmail(@RequestBody String email) {
        CompletableFuture<String> result = emailVerificationService.sendVerificationEmail(email);
        return Response.ok(result.join()); // 等待异步任务完成并获取结果
    }

    // 验证接口
    @Operation(summary = "验证验证码")
    @PostMapping("/auth/email/verify")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "验证码验证成功"),
            @ApiResponse(responseCode = "401", description = "未登录或登录过期")
    })
    public boolean verifyCode(@RequestBody EmailVerificationDto emailVerificationDto) {
        return emailVerificationService.verifyCode
                (emailVerificationDto.getEmail(), emailVerificationDto.getCode());
    }
}
