package com.kitty.blog.infrastructure.config;

import com.kitty.blog.common.constant.Role;
import com.kitty.blog.infrastructure.security.filter.JwtAuthenticationEntryPoint;
import com.kitty.blog.infrastructure.security.filter.JwtAuthenticationFilter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    /**
     * BCrypt是一种迭代的哈希算法，它的工作原理如下：
     * <p>
     * 加盐：BCrypt在哈希过程中会自动生成一个盐值（通常是128位），然后将盐值与用户密码组合在一起。
     * 迭代哈希：BCrypt使用一个称为“工作因子”的参数来决定哈希操作的迭代次数。这个参数越高，哈希计算的时间就越长，从而增加破解的难度。
     * 哈希生成：BCrypt将盐值和用户密码组合后，经过多次哈希运算生成最终的哈希值。
     * 哈希存储：生成的哈希值（包括盐值和工作因子）会被存储在数据库中。
     * 验证密码
     * 验证密码的过程是将用户输入的密码与存储的哈希值进行比较。BCrypt会自动从存储的哈希值中提取盐值和工作因子，然后使用这些参数对用户输入的密码进行哈希运算，最后将生成的哈希值与存储的哈希值进行比较。
     * <p>
     * 通过这种方式，BCrypt不仅能够保证密码安全，还能够防止彩虹表攻击，因为它为每个密码生成独特的哈希值。
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)// 禁用 CSRF (仅在开发阶段时使用); // 开启基本的 HTTP 认证 (如需要)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                      .authenticationEntryPoint(unauthorizedHandler)) // 认证失败处理器
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 禁用 session
                .authorizeHttpRequests(authorize -> authorize
                        /**
                         * Ant 风格的路径模式使用了一些特殊的字符来表示不同级别的路径匹配：
                         *
                         * ?：匹配任何单个字符（除了路径分隔符）。
                         * *：匹配任何字符的序列（除了路径分隔符），但不包括空字符串。
                         * **：匹配任何字符的序列，包括空字符串。至少匹配一个字符的序列，并且可以跨越路径分隔符。
                         * {}：表示一个通配符的选择，可以匹配多个逗号分隔的模式。例如，{,春夏秋冬} 可以匹配任何以春夏秋冬开头的字符串。
                         */

                        // 公开登录注册接口
                        .requestMatchers("/api/user/auth/**").permitAll()
                        // Swagger 开放访问
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-ui.html").permitAll()
                        // WebSocket 接口需要登录
                        .requestMatchers("/ws/**").authenticated()
                        // 所有用户都可以访问的接口
                        .requestMatchers("/api/*/public/**").hasRole(Role.ROLE_USER)
                        // 管理员接口
                        .requestMatchers("/api/*/admin/**").
                            hasAnyRole(Role.ROLE_CATEGORY_MANAGER, Role.ROLE_PERMISSION_MANAGER,
                                    Role.ROLE_PERMISSION_MAPPING_MANAGER, Role.ROLE_TAG_MANAGER,
                                    Role.ROLE_POST_MANAGER, Role.ROLE_USER_ACTIVITY_MANAGER,
                                    Role.ROLE_COMMENT_MANAGER,
                                    Role.ROLE_MESSAGE_MANAGER, Role.ROLE_REPORT_MANAGER,
                                    Role.ROLE_ROLE_MANAGER, Role.ROLE_SYSTEM_ADMINISTRATOR)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(webSocketAuthFilter(), ChannelProcessingFilter.class)
                .cors(Customizer.withDefaults())
                // 添加WebSocket支持
                .securityMatcher(new AndRequestMatcher(
                        new NegatedRequestMatcher(new AntPathRequestMatcher("/ws/**")),
                        new AntPathRequestMatcher("/**")
                ));
        // 添加WebSocket认证拦截器
        return http.build();
    }

    @Bean
    public Filter webSocketAuthFilter(){
        return new OncePerRequestFilter(){
            @Override
            protected void doFilterInternal(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {
                if (request.getRequestURI().startsWith("/ws/")){
                    // 复用 JWT 过滤器时需重置安全上下文
                    SecurityContextHolder.clearContext();

                    jwtAuthenticationFilter().doFilter(request, response, (req, res) -> {
                        if (SecurityContextHolder.getContext().getAuthentication() != null
                                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
                            log.debug("WebSocket 认证成功: {}",
                                    SecurityContextHolder.getContext().getAuthentication().getName());
                            filterChain.doFilter(request, response);
                        } else {
                            log.warn("WebSocket 认证失败");
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        }
                    });
                    return;
                }
                filterChain.doFilter(request, response);
            }
        };
    }



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }


}
