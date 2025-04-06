package com.kitty.blog.infrastructure.utils;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
public class IpUtil {
    private static Searcher searcher;

    static {
        try {
            // 加载 ip2region.xdb 文件
            ClassPathResource resource = new ClassPathResource("ip2region.xdb");
            byte[] bytes = resource.getInputStream().readAllBytes();
            searcher = Searcher.newWithBuffer(bytes);
        } catch (IOException e) {
            log.error("IP地址库加载失败", e);
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getIpLocation(String ip) {
        try {
            String region = searcher.search(ip);
            return Arrays.stream(region.split("\\|"))
                    .filter(s -> !s.isEmpty() && !s.equals("0"))
                    .reduce((a, b) -> a + " " + b)
                    .orElse("未知");
        } catch (Exception e) {
            log.error("IP地址解析失败: {}", ip, e);
            return "未知";
        }
    }
}