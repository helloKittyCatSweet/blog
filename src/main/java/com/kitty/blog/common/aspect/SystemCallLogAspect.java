package com.kitty.blog.common.aspect;

import com.kitty.blog.infrastructure.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class SystemCallLogAspect {

    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || ")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();

        // 构建日志信息
        Map<String, Object> logInfo = new HashMap<>();
        logInfo.put("method", joinPoint.getSignature().getName());
        logInfo.put("path", ((MethodSignature) joinPoint.getSignature()).getMethod().getAnnotation(RequestMapping.class));
        logInfo.put("timestamp", new Date());

        try {
            Object result = joinPoint.proceed();
            // 记录响应时间
            logInfo.put("duration", System.currentTimeMillis() - startTime);
            logInfo.put("status", "success");
            log.info("API访问: {}", JsonUtil.toJson(logInfo));
            return result;
        } catch (Exception e) {
            logInfo.put("status", "error");
            logInfo.put("error", e.getMessage());
            log.error("API异常: {}", JsonUtil.toJson(logInfo));
            throw e;
        }
    }
}