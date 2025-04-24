package com.kitty.blog.common.aspect;

import com.kitty.blog.common.constant.LogConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class UserActivityAspect {

    @Around("@annotation(com.kitty.blog.common.annotation.LogUserActivity)")
    public Object logUserActivity(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String activityType = joinPoint.getSignature().getName();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        try {
            Object result = joinPoint.proceed();
            recordUserActivity(authentication, request, activityType, true, null,
                    System.currentTimeMillis() - startTime);
            return result;
        } catch (Exception e) {
            recordUserActivity(authentication, request, activityType, false, e.getMessage(),
                    System.currentTimeMillis() - startTime);
            throw e;
        }
    }

    private void recordUserActivity(Authentication authentication, HttpServletRequest request,
            String activityType, boolean success, String errorMessage, long duration) {
        try {
            Map<String, Object> activity = new HashMap<>();

            // 基础字段
            activity.put("@timestamp", LocalDateTime.now().toInstant(ZoneOffset.UTC).toString());
            activity.put("log_type", "user-activity");
            activity.put("service_name", LogConstants.APPLICATION_NAME);
            activity.put("level", "INFO");

            // 用户信息
            activity.put("user_id", authentication != null ? authentication.getName() : "anonymous");
            activity.put("activity_type", activityType);
            activity.put("success", success);
            activity.put("duration", duration);

            // 请求信息
            activity.put("endpoint", request.getRequestURI());
            activity.put("method", request.getMethod());
            activity.put("client_ip", request.getRemoteAddr());
            activity.put("user_agent", request.getHeader("User-Agent"));

            if (errorMessage != null) {
                activity.put("error_message", errorMessage);
            }

            // 添加标签
            activity.put("host", java.net.InetAddress.getLocalHost().getHostName());
            activity.put("environment", System.getProperty("spring.profiles.active", "dev"));

            log.info("User Activity: {}",
                    net.logstash.logback.argument.StructuredArguments.entries(activity));
        } catch (Exception e) {
            log.error("Failed to record user activity", e);
        }
    }
}