package com.kitty.blog.infrastructure.config;

import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.common.constant.Role;
import com.kitty.blog.domain.service.auth.MyUserDetailService;
import com.kitty.blog.infrastructure.security.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.security.core.Authentication;

import java.util.Objects;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private JwtTokenUtil jwtService;

    @Autowired
    private MyUserDetailService userDetailsService;

    @Bean
    AuthorizationEventPublisher authorizationEventPublisher() {
        return new SpringAuthorizationEventPublisher(applicationEventPublisher);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        AuthorizationManager<Message<?>> authorizationManager = MessageMatcherDelegatingAuthorizationManager
                .builder()
                // 首先放行所有基础连接相关的消息
                .simpTypeMatchers(SimpMessageType.CONNECT,
                        SimpMessageType.DISCONNECT,
                        SimpMessageType.HEARTBEAT,
                        SimpMessageType.SUBSCRIBE,
                        SimpMessageType.UNSUBSCRIBE)
                .permitAll()
                // 放行错误消息和系统消息
                .simpDestMatchers("/user/queue/errors", "/user/queue/reply").permitAll()
                // 允许系统消息的发送和订阅
                // 只有管理员可以发送系统消息，但所有认证用户都可以接收系统消息
                .simpMessageDestMatchers("/app/system-message").access(
                        (authentication, message) -> {
                            try {
                                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message.getMessage());
                                if (accessor.getSessionAttributes() != null &&
                                        accessor.getSessionAttributes().containsKey("SPRING_SECURITY_AUTHENTICATION")) {
                                    SecurityContext securityContext = (SecurityContext) accessor.getSessionAttributes()
                                            .get("SPRING_SECURITY_AUTHENTICATION");
                                    Authentication auth = securityContext.getAuthentication();

                                    boolean hasRole = auth.getAuthorities().stream()
                                            .anyMatch(authority -> authority.getAuthority()
                                                    .equals("ROLE_" + Role.ROLE_MESSAGE_MANAGER) ||
                                                    authority.getAuthority()
                                                            .equals("ROLE_" + Role.ROLE_SYSTEM_ADMINISTRATOR));
                                    System.out.println("系统消息权限验证结果: " + auth.getAuthorities());
                                    return new AuthorizationDecision(hasRole);
                                }
                            } catch (Exception e) {
                                System.out.println("权限验证发生异常: " + e.getMessage());
                                e.printStackTrace();
                            }
                            return new AuthorizationDecision(false);
                        })
                .simpDestMatchers("/topic/system-message/**").authenticated()
                // 订阅权限验证规则
                .simpSubscribeDestMatchers("/user/{userId}/**").access(
                        ((authentication, object) -> {
                            try {
                                String userId = object.getVariables().get("userId");
                                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                                if (auth != null && auth.getPrincipal() instanceof LoginResponseDto loginUser) {
                                    return new AuthorizationDecision(loginUser.getId().toString().equals(userId));
                                }
                            } catch (Exception e) {
                                // 记录错误但不中断连接
                                e.printStackTrace();
                            }
                            return new AuthorizationDecision(false);
                        }))
                .simpSubscribeDestMatchers("/topic/**").authenticated()
                .simpMessageDestMatchers("/app/**").authenticated()
                // 放行错误消息和系统消息
                .simpDestMatchers("/user/queue/errors", "/user/queue/reply", "/user/queue/notifications").permitAll()

                // 允许订阅个人通知
                .simpSubscribeDestMatchers("/user/queue/notifications").authenticated()
                // 默认允许已认证的消息
                .anyMessage().authenticated()
                .build();

        registration.interceptors(
                messageSecurityInterceptor(), // 自定义拦截器放在最前面
                new SecurityContextChannelInterceptor(),
                new AuthorizationChannelInterceptor(authorizationManager)).taskExecutor()
                .corePoolSize(4) // 核心线程数
                .maxPoolSize(20) // 最大线程数
                .queueCapacity(50) // 队列容量
                .keepAliveSeconds(60); // 线程存活时间
    }

    @Bean
    public ChannelInterceptor messageSecurityInterceptor() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                try {
                    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                    StompCommand command = accessor.getCommand();
                    String destination = accessor.getDestination();

                    // 处理 JWT 认证
                    String jwt = null;
                    if (StompCommand.CONNECT.equals(command)) {
                        // 连接时从头部获取 token
                        jwt = accessor.getFirstNativeHeader("Authorization");
                        // 验证 JWT 并设置认证信息
                        if (jwt != null && jwt.startsWith("Bearer ")) {
                            jwt = jwt.substring(7);
                            try {
                                String username = jwtService.extractUsername(jwt);
                                if (username != null) {
                                    LoginResponseDto userDetails = (LoginResponseDto) userDetailsService
                                            .loadUserByUsername(username);
                                    if (!jwtService.isTokenExpired(jwt)) {
                                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                                userDetails,
                                                null,
                                                userDetails.getAuthorities());
                                        SecurityContextHolder.getContext().setAuthentication(authToken);
                                        accessor.setUser(authToken);
                                        // 在会话属性中保存认证信息和用户详情
                                        Objects.requireNonNull(accessor.getSessionAttributes()).put(
                                                "SPRING_SECURITY_AUTHENTICATION", SecurityContextHolder.getContext());
                                        Objects.requireNonNull(accessor.getSessionAttributes()).put(
                                                "USER_DETAILS", userDetails);
                                    }
                                }
                            } catch (Exception e) {
                                System.out.println("Token验证失败: " + e.getMessage());
                            }
                        }
                    } else {
                        // 从会话属性中恢复认证信息
                        if (accessor.getSessionAttributes() != null &&
                                accessor.getSessionAttributes().containsKey("SPRING_SECURITY_AUTHENTICATION")) {
                            SecurityContextHolder.setContext(
                                    (SecurityContext) accessor.getSessionAttributes()
                                            .get("SPRING_SECURITY_AUTHENTICATION"));
                        }
                        // 恢复用户详情到认证对象中
                        if (accessor.getSessionAttributes() != null &&
                                accessor.getSessionAttributes().containsKey("USER_DETAILS")) {
                            LoginResponseDto userDetails = (LoginResponseDto) accessor.getSessionAttributes()
                                    .get("USER_DETAILS");
                            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());
                            SecurityContextHolder.getContext().setAuthentication(authToken);
                        }
                    }

                    // 打印调试信息
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    System.out.println("WebSocket消息拦截 - 命令类型: " + command);
                    System.out.println("WebSocket消息拦截 - 目标地址: " + destination);
                    if (authentication != null) {
                        System.out.println("WebSocket消息拦截 - 当前用户: " + authentication.getName());
                        System.out.println("WebSocket消息拦截 - 用户权限: " + authentication.getAuthorities());
                    } else {
                        System.out.println("WebSocket消息拦截 - 未认证用户");
                    }

                    // 控制命令（CONNECT, SUBSCRIBE等）直接放行
                    if (command != null && (StompCommand.CONNECT.equals(command) ||
                            StompCommand.DISCONNECT.equals(command) ||
                            StompCommand.SUBSCRIBE.equals(command) ||
                            StompCommand.UNSUBSCRIBE.equals(command))) {
                        System.out.println("WebSocket消息拦截 - 控制命令放行: " + command);
                        return message;
                    }

                    // 打印消息内容
                    System.out.println("WebSocket消息拦截 - 消息内容: " + message);
                } catch (Exception e) {
                    System.out.println("WebSocket消息拦截 - 发生异常: " + e.getMessage());
                    e.printStackTrace();
                }
                return message;
            }
        };
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

}