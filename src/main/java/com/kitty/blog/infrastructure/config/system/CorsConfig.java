package com.kitty.blog.infrastructure.config.system;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许的域名，开发环境下允许多个域名
        config.addAllowedOrigin("http://localhost:5173"); // 本地开发
        config.addAllowedOrigin("http://47.94.57.175:5173"); // Vue开发服务器
        // 允许的头信息
        config.addAllowedHeader("*");
        // 允许的HTTP方法
        config.addAllowedMethod("*");
        // 允许发送认证信息
        config.setAllowCredentials(true);
        // 预检请求的有效期，单位为秒
        config.setMaxAge(3600L);
        // 允许的请求头
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Type");
        config.addExposedHeader("X-Requested-With");
        config.addExposedHeader("accept");
        config.addExposedHeader("Origin");
        config.addExposedHeader("Access-Control-Request-Method");
        config.addExposedHeader("Access-Control-Request-Headers");
        // 添加常用的响应头
        config.addExposedHeader("Cache-Control");
        config.addExposedHeader("Content-Language");
        config.addExposedHeader("Content-Length");
        config.addExposedHeader("Content-Type");
        config.addExposedHeader("Expires");
        config.addExposedHeader("Last-Modified");
        config.addExposedHeader("Pragma");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}