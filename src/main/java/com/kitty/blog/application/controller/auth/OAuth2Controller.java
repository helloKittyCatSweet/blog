package com.kitty.blog.application.controller.auth;

import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.domain.service.auth.OAuth2Service;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Tag(name = "OAuth2认证", description = "OAuth2相关接口")
@RestController
@RequestMapping("/api/auth/oauth2")
@CrossOrigin
@Slf4j
public class OAuth2Controller {

    @Autowired
    private OAuth2Service oAuth2Service;

    @Autowired
    private Environment environment;

    @Operation(summary = "获取GitHub登录URL", description = "获取GitHub OAuth2登录地址")
    @GetMapping("/github/url")
    public ResponseEntity<Response<String>> getGithubLoginUrl() {
        String clientId = environment.getProperty("spring.security.oauth2.client.registration.github.client-id");
        // 设置回调地址为前端的回调路由
        String redirectUri = "http://47.94.57.175/auth/github/callback";
        String scope = "user:email read:user";
        String state = UUID.randomUUID().toString();

        // 构建GitHub授权URL，添加参数确保显示账号选择
        String githubAuthUrl = String.format(
                "https://github.com/login/oauth/authorize?" +
                        "client_id=%s&" +
                        "redirect_uri=%s&" +
                        "scope=%s&" +
                        "state=%s&" +
                        "allow_signup=true&" + // 允许注册新账号
                        "show_dialog=true", // 总是显示授权对话框
                clientId,
                URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
                URLEncoder.encode(scope, StandardCharsets.UTF_8),
                state);

        return Response.ok(githubAuthUrl);
    }

    @Operation(summary = "GitHub回调", description = "处理GitHub OAuth2回调，检查用户是否存在")
    @GetMapping("/github/callback")
    public ResponseEntity<Response<LoginResponseDto>> githubCallback(@RequestParam("code") String code) {
        try {
            if (code == null) {
                return Response.error(HttpStatus.BAD_REQUEST, "Missing authorization code");
            }

            // 检查用户是否已注册
            LoginResponseDto loginResponse = oAuth2Service.checkGithubUser(code);
            return Response.ok(loginResponse);

        } catch (Exception e) {
            log.error("GitHub callback processing failed", e);
            return Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "GitHub登录失败: " + e.getMessage());
        }
    }

    @Operation(summary = "完成GitHub注册", description = "完成GitHub用户注册，设置密码和邮箱")
    @PostMapping("/github/complete-registration")
    public ResponseEntity<Response<LoginResponseDto>> completeGithubRegistration(
            @RequestBody Map<String, String> request) {
        try {
            // 添加请求参数日志
            log.debug("Received registration request: {}", request);

            String password = request.get("password");
            String email = request.get("email");
            String state = request.get("state");

            log.debug("Extracted parameters - password: {}, email: {}",
                    password != null ? "PROVIDED" : "NULL",
                    email);

            // 更详细的参数验证
            if (password == null || password.trim().isEmpty()) {
                log.warn("Registration failed: password is empty or null");
                return Response.error(HttpStatus.BAD_REQUEST, "密码不能为空");
            }
            if (email == null || email.trim().isEmpty()) {
                log.warn("Registration failed: email is empty or null");
                return Response.error(HttpStatus.BAD_REQUEST, "邮箱不能为空");
            }

            LoginResponseDto loginResponse = oAuth2Service.processGithubLogin(password, email, state);
            log.debug("Registration successful for email: {}", email);
            return Response.ok(loginResponse);

        } catch (RuntimeException e) {
            // 对于已知的运行时异常，返回400错误
            log.error("Complete GitHub registration failed with validation error: {}", e.getMessage());
            return Response.error(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            // 对于其他未知异常，返回500错误
            log.error("Complete GitHub registration failed with unexpected error", e);
            return Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "注册过程中发生错误，请稍后重试");
        }
    }
}