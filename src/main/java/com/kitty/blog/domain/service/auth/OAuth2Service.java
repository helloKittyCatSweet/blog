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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class OAuth2Service {

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

    @Transactional
    public LoginResponseDto processGithubLogin(String code, String password, String email) {
        // 1. 使用 code 获取 access token
        String accessToken = getGithubAccessToken(code);

        // 2. 使用 access token 获取用户信息
        Map<String, Object> githubUser = getGithubUserInfo(accessToken);

        // 3. 处理用户信息
        String githubId = String.valueOf(githubUser.get("id"));
        String username = (String) githubUser.get("login");
        String avatarUrl = (String) githubUser.get("avatar_url");

        // 4. 查找现有用户
        Optional<User> existingUser = userRepository.findByUsername(username);
        log.info("Found existing user: " + existingUser.isPresent()
                + ", username: " + username + ", githubId: " + githubId);

        if (existingUser.isPresent()) {
            // 已存在用户，只更新头像
            User user = existingUser.get();
            user.setAvatar(avatarUrl);
            user = userRepository.save(user);

            // 生成 JWT token
            UserDetails userDetails = myUserDetailService.loadUserByUsername(user.getUsername());
            String token = jwtTokenUtil.generateToken(userDetails);

            return LoginResponseDto.builder()
                    .id(user.getUserId())
                    .username(user.getUsername())
                    .token(token)
                    .roles(userDetails.getAuthorities())
                    .isNewUser(false)
                    .build();
        } else {
            // 创建新用户
            if (password == null || email == null) {
                // 如果是新用户但没有提供密码和邮箱，返回需要设置密码的标志
                return LoginResponseDto.builder()
                        .username(username)
                        .isNewUser(true)
                        .build();
            }

            // 创建新用户
            User newUser = User.builder()
                    .username(username)
                    .password(password)
                    .email(email)
                    .avatar(avatarUrl)
                    .build();
            userService.register(newUser);

            // 保存 GitHub 账号关联信息
            UserSetting userSetting = UserSetting.builder()
                    .userId(newUser.getUserId())
                    .githubAccount(githubId)
                    .build();
            userSettingRepository.save(userSetting);

            // 生成 JWT token
            UserDetails userDetails = myUserDetailService.loadUserByUsername(newUser.getUsername());
            String token = jwtTokenUtil.generateToken(userDetails);

            return LoginResponseDto.builder()
                    .id(newUser.getUserId())
                    .username(newUser.getUsername())
                    .token(token)
                    .roles(userDetails.getAuthorities())
                    .isNewUser(false)
                    .build();
        }
    }

    private String getGithubAccessToken(String code) {
        String tokenUrl = "https://github.com/login/oauth/access_token";
        String clientId = environment.getProperty("spring.security.oauth2.client.registration.github.client-id");
        String clientSecret = environment.getProperty("spring.security.oauth2.client.registration.github.client-secret");

        // 设置请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // 设置请求参数
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);

        // 发送请求
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

        // 解析响应
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.get("access_token").asText();
        } catch (Exception e) {
            log.error("Failed to parse GitHub access token response", e);
            throw new RuntimeException("Failed to get GitHub access token");
        }
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
                Map.class
        );

        return response.getBody();
    }
}