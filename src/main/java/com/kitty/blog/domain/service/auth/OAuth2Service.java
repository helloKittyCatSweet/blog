package com.kitty.blog.domain.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.common.constant.Role;
import com.kitty.blog.domain.model.User;
import com.kitty.blog.domain.model.UserSetting;
import com.kitty.blog.domain.repository.UserRepository;
import com.kitty.blog.domain.repository.UserSettingRepository;
import com.kitty.blog.domain.service.user.UserService;
import com.kitty.blog.infrastructure.security.JwtTokenUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class OAuth2Service {

    private final ThreadLocal<String> avatarUrlLocal = new ThreadLocal<>();

    private final ThreadLocal<String> usernameLocal = new ThreadLocal<>();

    @Autowired
    private Environment environment;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserSettingRepository userSettingRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MyUserDetailService myUserDetailService;

    /**
     * 检查GitHub用户是否已在系统中注册
     * 
     * @param code GitHub授权码
     * @return 包含用户信息的LoginResponseDto，如果isNewUser为true则表示需要注册
     */
    public LoginResponseDto checkGithubUser(String code) {
        try {
        // 1. 使用 code 获取 access token
            String token = getGithubAccessToken(code);

        // 2. 使用 access token 获取用户信息
            Map<String, Object> githubUser = getGithubUserInfo(token);

            // 3. 获取GitHub用户信息
        String username = (String) githubUser.get("login");
            usernameLocal.set(username);

            // 保存GitHub用户信息到ThreadLocal
            avatarUrlLocal.set((String) githubUser.get("avatar_url"));

            // 4. 检查用户是否存在
        Optional<User> existingUser = userRepository.findByUsername(username);

        if (existingUser.isPresent()) {
                // 已存在用户，直接返回登录信息
            User user = existingUser.get();
            UserDetails userDetails = myUserDetailService.loadUserByUsername(user.getUsername());
                String jwtToken = jwtTokenUtil.generateToken(userDetails);

                // 清理ThreadLocal
                cleanupThreadLocals();

            return LoginResponseDto.builder()
                    .id(user.getUserId())
                    .username(user.getUsername())
                        .token(jwtToken)
                    .roles(userDetails.getAuthorities())
                    .isNewUser(false)
                    .build();
        } else {
                // 新用户，返回需要注册的标志
                return LoginResponseDto.builder()
                        .username(username)
                        .isNewUser(true)
                        .build();
            }
        } catch (Exception e) {
            // 发生异常时清理ThreadLocal
            cleanupThreadLocals();
            throw e;
        }
    }

    @Transactional
    public LoginResponseDto processGithubLogin(String password, String email) {
        try {
            // 检查必要的注册信息
            if (password == null || email == null || usernameLocal.get() == null) {
                return LoginResponseDto.builder()
                        .username(usernameLocal.get())
                        .isNewUser(true)
                        .build();
            }

            // 获取头像URL（如果有的话）
            String avatar = avatarUrlLocal.get();

            // 创建新用户
            User newUser = User.builder()
                    .username(usernameLocal.get())
                    .password(password)
                    .email(email)
                    .avatar(avatar) // 可能为null，这是可以接受的
                    .build();
            userService.register(newUser);

            // 保存 GitHub 账号关联信息
            UserSetting userSetting = userSettingRepository.findByUserId
                    (newUser.getUserId()).orElse(new UserSetting());
            userSetting.setGithubAccount(usernameLocal.get());
            userSettingRepository.save(userSetting);

            // 生成 JWT token
            UserDetails userDetails = myUserDetailService.loadUserByUsername(newUser.getUsername());
            String jwtToken = jwtTokenUtil.generateToken(userDetails);

            return LoginResponseDto.builder()
                    .id(newUser.getUserId())
                    .username(newUser.getUsername())
                    .token(jwtToken)
                    .roles(userDetails.getAuthorities())
                    .isNewUser(false)
                    .build();
        } finally {
            // 确保在方法结束时清理ThreadLocal
            cleanupThreadLocals();
        }
    }

    private void cleanupThreadLocals() {
        avatarUrlLocal.remove();
        usernameLocal.remove();
    }

    private String getGithubAccessToken(String code) {
        log.info("Starting GitHub access token request for code: {}",
                code.substring(0, Math.min(code.length(), 6)) + "...");

        String tokenUrl = environment.getProperty("spring.security.oauth2.client.provider.github.token-uri");
        String clientId = environment.getProperty("spring.security.oauth2.client.registration.github.client-id");
        String clientSecret = environment
                .getProperty("spring.security.oauth2.client.registration.github.client-secret");

        if (tokenUrl == null || clientId == null || clientSecret == null) {
            log.error("GitHub OAuth configuration is incomplete");
            throw new RuntimeException("GitHub OAuth configuration is missing required properties");
        }

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Accept", "application/json");

        // 设置请求参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);

        // 设置请求实体
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // 设置RestTemplate超时
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5秒连接超时
        factory.setReadTimeout(5000); // 5秒读取超时
        restTemplate.setRequestFactory(factory);

        int maxRetries = 3;
        int retryCount = 0;
        Exception lastException = null;

        while (retryCount < maxRetries) {
            try {
                log.debug("Attempting to get GitHub access token (attempt {}/{})", retryCount + 1, maxRetries);
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

                // 检查响应状态
                if (!response.getStatusCode().is2xxSuccessful()) {
                    log.warn("GitHub API returned non-200 status: {}", response.getStatusCode());
                    throw new RuntimeException("GitHub API error: " + response.getStatusCode());
                }

                if (response.getBody() == null) {
                    log.error("GitHub API returned empty response body");
                    throw new RuntimeException("Empty response from GitHub API");
                }

        // 解析响应
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
                JsonNode accessToken = root.get("access_token");
                JsonNode error = root.get("error");
                JsonNode errorDescription = root.get("error_description");

                if (accessToken != null) {
                    log.info("Successfully obtained GitHub access token");
                    return accessToken.asText();
                } else if (error != null) {
                    String errorMessage = String.format("GitHub OAuth error: %s - %s",
                            error.asText(),
                            errorDescription != null ? errorDescription.asText() : "No error description");
                    log.error(errorMessage);
                    throw new RuntimeException(errorMessage);
                } else {
                    log.error("Invalid GitHub response: access_token not found");
                    throw new RuntimeException("Invalid GitHub response: access_token not found");
                }

            } catch (RestClientException e) {
                lastException = e;
                log.warn("Failed to get GitHub access token (attempt {}/{}): {}",
                        retryCount + 1, maxRetries, e.getMessage());

                if (e instanceof HttpClientErrorException) {
                    HttpClientErrorException httpError = (HttpClientErrorException) e;
                    if (httpError.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        log.error("GitHub API authentication failed. Please check client credentials");
                        throw new RuntimeException("GitHub API authentication failed", e);
                    } else if (httpError.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                        log.error("GitHub API rate limit exceeded");
                        throw new RuntimeException("GitHub API rate limit exceeded", e);
                    }
                }

                retryCount++;
                if (retryCount < maxRetries) {
                    try {
                        Thread.sleep(1000 * retryCount); // 指数退避重试
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Retry interrupted", ie);
                    }
                }
        } catch (Exception e) {
                log.error("Unexpected error while getting GitHub access token: {}", e.getMessage());
                throw new RuntimeException("Failed to get GitHub access token", e);
            }
        }

        log.error("Failed to get GitHub access token after {} retries", maxRetries);
        throw new RuntimeException("Failed to get GitHub access token after " + maxRetries + " retries",
                lastException);
    }

    private Map<String, Object> getGithubUserInfo(String accessToken) {
        String userInfoUrl = "https://api.github.com/user";

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // 发送请求
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                userInfoUrl,
                HttpMethod.GET,
                request,
                Map.class);

        return response.getBody();
    }
}