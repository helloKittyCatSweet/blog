package com.kitty.blog.common.aspect;

import com.kitty.blog.common.constant.LogConstants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class BackendLoggingAspect {

    @Before("execution(* com.kitty.blog.application..*.*(..))" +
            " || execution(* com.kitty.blog.interfaces..*.*(..))" +
            " && !execution(* com.kitty.blog.infrastructure.security.filter..*.*(..))")
    public void before(JoinPoint joinPoint) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("@timestamp", LocalDateTime.now().toInstant(ZoneOffset.UTC).toString());
        logData.put("log_type", LogConstants.LogType.API_METRICS);
        logData.put("application", LogConstants.APPLICATION_NAME);
        logData.put("phase", "method_start");
        logData.put("class", joinPoint.getTarget().getClass().getName());
        logData.put("method", joinPoint.getSignature().getName());
        logData.put("args", Arrays.toString(joinPoint.getArgs()));

        // 添加标签
        try {
            logData.put("host", java.net.InetAddress.getLocalHost().getHostName());
            logData.put("service", LogConstants.APPLICATION_NAME);
            logData.put("environment", System.getProperty("spring.profiles.active", "dev"));
        } catch (Exception e) {
            log.warn("Failed to add tags to log data", e);
        }

        log.info("Method Execution: {}", net.logstash.logback.argument.StructuredArguments.entries(logData));
    }

    @After("execution(* com.kitty.blog.application..*.*(..))" +
            " || execution(* com.kitty.blog.interfaces..*.*(..))" +
            " && !execution(* com.kitty.blog.infrastructure.security.filter..*.*(..))")
    public void after(JoinPoint joinPoint) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("@timestamp", LocalDateTime.now().toInstant(ZoneOffset.UTC).toString());
        logData.put("log_type", LogConstants.LogType.API_METRICS);
        logData.put("application", LogConstants.APPLICATION_NAME);
        logData.put("phase", "method_end");
        logData.put("class", joinPoint.getTarget().getClass().getName());
        logData.put("method", joinPoint.getSignature().getName());

        // 添加标签
        try {
            logData.put("host", java.net.InetAddress.getLocalHost().getHostName());
            logData.put("service", LogConstants.APPLICATION_NAME);
            logData.put("environment", System.getProperty("spring.profiles.active", "dev"));
        } catch (Exception e) {
            log.warn("Failed to add tags to log data", e);
        }

        log.info("Method Execution: {}", net.logstash.logback.argument.StructuredArguments.entries(logData));
    }

    @AfterThrowing(pointcut = "execution(* com.kitty.blog.application..*.*(..))" +
            " || execution(* com.kitty.blog.interfaces..*.*(..))" +
            " && !execution(* com.kitty.blog.infrastructure.security.filter..*.*(..))", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("@timestamp", LocalDateTime.now().toInstant(ZoneOffset.UTC).toString());
        logData.put("log_type", LogConstants.LogType.ERROR);
        logData.put("application", LogConstants.APPLICATION_NAME);
        logData.put("phase", "method_error");
        logData.put("class", joinPoint.getTarget().getClass().getName());
        logData.put("method", joinPoint.getSignature().getName());
        logData.put("error_message", ex.getMessage());
        logData.put("stack_trace", Arrays.toString(ex.getStackTrace()));

        // 添加标签
        try {
            logData.put("host", java.net.InetAddress.getLocalHost().getHostName());
            logData.put("service", LogConstants.APPLICATION_NAME);
            logData.put("environment", System.getProperty("spring.profiles.active", "dev"));
        } catch (Exception e) {
            log.warn("Failed to add tags to log data", e);
        }

        log.error("Method Execution Error: {}", net.logstash.logback.argument.StructuredArguments.entries(logData));
    }
}