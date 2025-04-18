package com.kitty.blog.infrastructure.utils;

import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Enumeration;

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
        log.info("获取客户端IP地址: {}", request.toString());

        // 打印所有请求头
        log.info("所有请求头信息：");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            log.info("请求头 {}: {}", headerName, headerValue);
        }

        String ip = null;

        // 如果没有从host获取到IP，尝试其他方式
        // 尝试从标准头部获取
        if (!isValidIp(ip)) {
            // 调整头部优先级，X-Real-IP 和 X-Forwarded-For 优先
            String[] headers = {
                    "X-Forwarded-For", // 代理链路IP
                    "Cpolar-Real-IP", // cpolar 真实IP
                    "X-Real-IP", // Nginx 设置的真实IP
                    "Proxy-Client-IP",
                    "WL-Proxy-Client-IP",
                    "HTTP_CLIENT_IP",
                    "HTTP_X_FORWARDED_FOR"
            };

            for (String header : headers) {
                ip = request.getHeader(header);
                if (isValidIp(ip)) {
                    // 如果是X-Forwarded-For，取第一个IP（最原始的客户端IP）
                    if (header.equals("X-Forwarded-For") && ip.contains(",")) {
                        ip = ip.split(",")[0].trim();
                    }
                    log.info("从头部{}获取到IP: {}", header, ip);
                    break;
                }
            }
        }

        // 如果仍然没有获取到，使用RemoteAddr
        if (!isValidIp(ip)) {
            String remoteAddr = request.getRemoteAddr();
            log.info("原始 RemoteAddr: {}", remoteAddr);

            // 如果是IPv6地址，尝试获取IPv4
            if (remoteAddr != null && remoteAddr.contains(":")) {
                try {
                    InetAddress inetAddress = InetAddress.getByName(remoteAddr);
                    if (inetAddress instanceof java.net.Inet6Address) {
                        // 尝试获取对应的IPv4地址
                        InetAddress[] addresses = InetAddress.getAllByName(inetAddress.getHostName());
                        for (InetAddress addr : addresses) {
                            if (addr instanceof java.net.Inet4Address) {
                                ip = addr.getHostAddress();
                                log.info("转换IPv6到IPv4: {}", ip);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("IP地址转换失败", e);
                }
            }

            // 如果还是没有获取到IPv4地址，使用原始地址
            if (!isValidIp(ip)) {
                ip = remoteAddr;
            }

            log.info("使用RemoteAddr获取到IP: {}", ip);
        }

        log.info("最终获取到的IP: {}", ip);
        return ip;
    }

    private static boolean isValidIp(String ip) {
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            return false;
        }
        // 正则表达式验证 IPv4 和 IPv6 地址
        String ipv4Pattern = "^(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)(\\.(25[0-5]|2[0-4]\\d|1\\d{2}|[1-9]?\\d)){3}$";
        String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4})$";
        return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern);
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