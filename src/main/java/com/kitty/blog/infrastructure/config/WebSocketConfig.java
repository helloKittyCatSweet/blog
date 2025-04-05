package com.kitty.blog.config;

import com.kitty.blog.dto.user.LoginResponseDto;
import com.kitty.blog.exception.WebSocketAuthenticationException;
import com.kitty.blog.security.JwtTokenUtil;
import com.kitty.blog.application.service.user.MyUserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Value("${frontend.url}")
    private String allowedOrigins;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(allowedOrigins)
                .withSockJS()
                .setInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(ServerHttpRequest request,
                                                   ServerHttpResponse response,
                                                   WebSocketHandler wsHandler,
                                                   Map<String, Object> attributes) throws Exception {
                        // 从URL参数中获取token
                        String query = request.getURI().getQuery();
                        String token = extractToken(query);

                        if (token == null) {
                            log.error("WebSocket连接失败：未提供token");
                            return false;
                        }

                        try {
                            // 验证token并检查用户权限
                            String username = jwtTokenUtil.extractUsername(token);
                            if (username != null && !jwtTokenUtil.isTokenExpired(token)) {
                                LoginResponseDto user = (LoginResponseDto)
                                        myUserDetailService.loadUserByUsername(username);

                                if (user != null) {
                                    // 检查用户是否具有ROLE_USER角色
                                    boolean hasUserRole = user.getAuthorities().stream()
                                            .anyMatch(auth ->
                                                    auth.getAuthority().equals("ROLE_USER"));

                                    if (!hasUserRole) {
                                        log.error("WebSocket连接失败：用户{}没有必要的权限", username);
                                        return false;
                                    }

                                    // 存储用户信息
                                    attributes.put("username", username);
                                    attributes.put("userId", user.getId());
                                    return true;
                                }
                            }
                        } catch (WebSocketAuthenticationException e) {
                            log.error("WebSocket认证失败", e);
                        }
                        return false;
                    }

                    private String extractToken(String query) {
                        if (query != null && query.contains("token=")) {
                            String[] params = query.split("&");
                            for (String param : params) {
                                if (param.startsWith("token=")) {
                                    return param.substring(6);
                                }
                            }
                        }
                        return null;
                    }

                    @Override
                    public void afterHandshake(ServerHttpRequest request,
                                               ServerHttpResponse response,
                                               WebSocketHandler wsHandler,
                                               Exception exception) {
                    }
                });
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
}
