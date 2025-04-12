package com.kitty.blog.infrastructure.security.filter;

import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.infrastructure.security.JwtTokenUtil;
import com.kitty.blog.infrastructure.utils.AuthenticationUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenUtil jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwt = null;
        String username = null;

        // 先尝试从URL参数获取token
        jwt = request.getParameter("token");

        // 如果URL中没有token，则尝试从header中获取
        if (jwt == null) {
            final String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            }
        }

        // 如果获取到了token，进行验证
        if (jwt != null) {
            Authentication auth = AuthenticationUtils.authenticateToken(jwt, jwtService, userDetailsService);
            if (auth != null) {
                ((UsernamePasswordAuthenticationToken) auth)
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("User {} authenticated successfully", auth.getName());
            }
        }
        filterChain.doFilter(request, response);
    }
}