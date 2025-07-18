package com.kitty.blog.common.aspect;

import com.kitty.blog.common.constant.LogConstants;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Aspect
@Component("monitoringMetricsAspect")
@Slf4j
public class MetricsAspect {

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object logMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        boolean isSuccess = true;
        String errorMessage = null;

        try {
            return joinPoint.proceed();
        } catch (Exception e) {
            isSuccess = false;
            errorMessage = e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            recordMetrics(joinPoint, duration, isSuccess, errorMessage);
        }
    }

    private void recordMetrics(ProceedingJoinPoint joinPoint, long duration, boolean isSuccess, String errorMessage) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            // 构建指标数据
            Map<String, Object> metrics = new HashMap<>();

            // 基础字段
            metrics.put("@timestamp", LocalDateTime.now().toInstant(ZoneOffset.UTC).toString());
            metrics.put("log_type", LogConstants.LogType.API_METRICS);
            metrics.put("application", LogConstants.APPLICATION_NAME);

            // 性能指标
            metrics.put("response_time", duration);
            metrics.put("success", isSuccess);

            // 请求信息
            metrics.put("endpoint", request.getRequestURI());
            metrics.put("http_method", request.getMethod());

            // 上下文信息
            metrics.put("class_name", joinPoint.getTarget().getClass().getName());
            metrics.put("method_name", joinPoint.getSignature().getName());
            metrics.put("client_ip", request.getRemoteAddr());

            // 请求标识
            metrics.put("request_id", request.getAttribute("requestId"));
            metrics.put("session_id", Optional.ofNullable(request.getSession(false))
                    .map(session -> session.getId())
                    .orElse(null));

            // 错误信息（如果有）
            if (!isSuccess) {
                metrics.put("error_message", errorMessage);
            }

            // 添加标签
            metrics.put("host", java.net.InetAddress.getLocalHost().getHostName());
            metrics.put("service", LogConstants.APPLICATION_NAME);
            metrics.put("environment", System.getProperty("spring.profiles.active", "dev"));

            // 写入日志文件
            log.info("API Performance Metrics: {}",
                    net.logstash.logback.argument.StructuredArguments.entries(metrics));

        } catch (Exception e) {
            log.error("Failed to record metrics", e);
        }
    }
}