package com.kitty.blog.infrastructure.utils;

import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.infrastructure.security.JwtTokenUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

public class AuthenticationUtils {

    public static Authentication authenticateToken(String jwt,
            JwtTokenUtil jwtService,
            UserDetailsService userDetailsService) {
        try {
            String username = jwtService.extractUsername(jwt);
            if (username != null) {
                LoginResponseDto userDetails = (LoginResponseDto) userDetailsService.loadUserByUsername(username);
                if (!jwtService.isTokenExpired(jwt)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
                    return authToken;
                }
            }
        } catch (Exception e) {
            System.out.println("Token验证失败: " + e.getMessage());
        }
        return null;
    }
}