package com.kitty.blog.infrastructure.config.monitor;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class MetricsAspect {

    private final ElasticsearchClient elasticsearchClient;

    @Qualifier("metricsIndex")
    private final String metricsIndex;

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
            if (attributes == null) {
                return;
            }

            HttpServletRequest request = attributes.getRequest();
            Map<String, Object> metrics = createMetricsMap(joinPoint, duration, isSuccess, errorMessage, request);

            IndexRequest<Map<String, Object>> indexRequest = IndexRequest.of(i -> i
                    .index(metricsIndex + "-" + getCurrentDateString())
                    .document(metrics));

            elasticsearchClient.index(indexRequest);
        } catch (Exception e) {
            log.error("Failed to record metrics", e);
        }
    }

    private Map<String, Object> createMetricsMap(ProceedingJoinPoint joinPoint, long duration,
            boolean isSuccess, String errorMessage, HttpServletRequest request) {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("@timestamp", Instant.now().toString());
        metrics.put("log_type", "api-metrics");
        metrics.put("response_time_ms", duration);
        metrics.put("success", isSuccess);
        metrics.put("endpoint", request.getRequestURI());
        metrics.put("http_method", request.getMethod());
        metrics.put("class_name", joinPoint.getTarget().getClass().getName());
        metrics.put("method_name", joinPoint.getSignature().getName());
        metrics.put("client_ip", request.getRemoteAddr());

        // 添加请求ID和会话ID（如果存在）
        Optional.ofNullable(request.getAttribute("requestId"))
                .ifPresent(requestId -> metrics.put("request_id", requestId));

        Optional.ofNullable(request.getSession(false))
                .ifPresent(session -> metrics.put("session_id", session.getId()));

        // 如果有错误，添加错误信息
        if (!isSuccess && errorMessage != null) {
            metrics.put("error_message", errorMessage);
        }

        return metrics;
    }

    private String getCurrentDateString() {
        return DateTimeFormatter.ofPattern("yyyy.MM.dd").format(LocalDate.now());
    }
}