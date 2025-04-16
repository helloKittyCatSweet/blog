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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @PostMapping("/github/callback")
    public ResponseEntity<Response<LoginResponseDto>> githubCallback
            (@RequestBody Map<String, String> request) {
        try {
            String code = request.get("code");
            if (code == null) {
                return Response.error(HttpStatus.BAD_REQUEST, "Missing authorization code");
            }

            // 先检查是否是已注册用户
            LoginResponseDto loginResponse = oAuth2Service.processGithubLogin(code, null, null);

            // 如果是新用户，需要密码和邮箱
            if (loginResponse.isNewUser()) {
                String password = request.get("password");
                String email = request.get("email");

                if (password == null || password.trim().isEmpty()) {
                    return Response.error(HttpStatus.BAD_REQUEST, "密码不能为空");
                }
                if (email == null || email.trim().isEmpty()) {
                    return Response.error(HttpStatus.BAD_REQUEST, "邮箱不能为空");
                }

                // 处理新用户注册
                loginResponse = oAuth2Service.processGithubLogin(code, password, email);
            }

            return Response.createResponse(
                    new ResponseEntity<>(loginResponse, HttpStatus.OK),
                    HttpStatus.OK, "GitHub登录成功",
                    HttpStatus.INTERNAL_SERVER_ERROR, "GitHub登录失败");
        } catch (Exception e) {
            log.error("GitHub登录失败", e);
            return Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "GitHub登录失败");
        }
    }
    // 添加获取 GitHub 登录 URL 的接口
    @Operation(summary = "获取GitHub登录URL", description = "获取GitHub OAuth2登录地址")
    @GetMapping("/github/url")
    public ResponseEntity<Response<String>> getGithubLoginUrl() {
        String clientId = environment.getProperty("spring.security.oauth2.client.registration.github.client-id");
        String redirectUri = environment.getProperty("spring.security.oauth2.client.registration.github.redirect-uri");

        String githubAuthUrl = String.format(
                "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s",
                clientId,
                redirectUri);

        return Response.ok(githubAuthUrl);
    }
}