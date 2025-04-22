package com.kitty.blog.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class SystemCallLogAspect {

    private final ObjectMapper objectMapper;

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object logSystemCall(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 生成请求ID
        String requestId = UUID.randomUUID().toString();
        request.setAttribute("requestId", requestId);

        // 记录请求日志
        logRequest(request, requestId);

        Object result = null;
        try {
            result = joinPoint.proceed();
            // 记录响应日志
            logResponse(request, result, requestId, true, null);
            return result;
        } catch (Exception e) {
            // 记录错误响应日志
            logResponse(request, null, requestId, false, e.getMessage());
            throw e;
        }
    }

    private void logRequest(HttpServletRequest request, String requestId) {
        try {
            Map<String, Object> requestLog = new HashMap<>();
            requestLog.put("log_type", "system-api-request");
            requestLog.put("@timestamp", Instant.now().toString());
            requestLog.put("request_id", requestId);

            // 请求基本信息
            requestLog.put("http_method", request.getMethod());
            requestLog.put("endpoint", request.getRequestURI());
            requestLog.put("query_string", request.getQueryString());

            // 客户端信息
            requestLog.put("client_ip", request.getRemoteAddr());
            requestLog.put("user_agent", request.getHeader("User-Agent"));

            // 会话信息
            if (request.getSession(false) != null) {
                requestLog.put("session_id", request.getSession().getId());
            }

            // 请求头
            Map<String, String> headers = new HashMap<>();
            Collections.list(request.getHeaderNames())
                    .forEach(headerName -> headers.put(headerName, request.getHeader(headerName)));
            requestLog.put("headers", headers);

            // 请求参数
            requestLog.put("parameters", request.getParameterMap());

            // 如果是包装的请求，获取请求体
            if (request instanceof ContentCachingRequestWrapper wrapper) {
                String requestBody = new String(wrapper.getContentAsByteArray());
                if (!requestBody.isEmpty()) {
                    requestLog.put("body", requestBody);
                }
            }

            log.info("API Request: {}",
                    net.logstash.logback.argument.StructuredArguments.entries(requestLog));
        } catch (Exception e) {
            log.error("Failed to log request", e);
        }
    }

    private void logResponse(HttpServletRequest request, Object result, String requestId,
            boolean isSuccess, String errorMessage) {
        try {
            Map<String, Object> responseLog = new HashMap<>();
            responseLog.put("log_type", "system-api-response");
            responseLog.put("@timestamp", Instant.now().toString());
            responseLog.put("request_id", requestId);

            // 响应信息
            responseLog.put("success", isSuccess);
            if (result != null) {
                responseLog.put("response_body", objectMapper.writeValueAsString(result));
            }
            if (errorMessage != null) {
                responseLog.put("error_message", errorMessage);
            }

            // 请求上下文
            responseLog.put("endpoint", request.getRequestURI());
            responseLog.put("http_method", request.getMethod());

            // 如果是包装的响应，获取响应体
            if (result instanceof ContentCachingResponseWrapper wrapper) {
                String responseBody = new String(wrapper.getContentAsByteArray());
                if (!responseBody.isEmpty()) {
                    responseLog.put("response_body", responseBody);
                }
                wrapper.copyBodyToResponse(); // 重要：复制响应体以便后续处理
            }

            log.info("API Response: {}",
                    net.logstash.logback.argument.StructuredArguments.entries(responseLog));
        } catch (Exception e) {
            log.error("Failed to log response", e);
        }
    }
}