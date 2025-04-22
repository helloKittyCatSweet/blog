package com.kitty.blog.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.time.Instant;
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
        logData.put("type", "method-execution");
        logData.put("phase", "before");
        logData.put("class", joinPoint.getTarget().getClass().getName());
        logData.put("method", joinPoint.getSignature().getName());
        logData.put("args", Arrays.toString(joinPoint.getArgs()));
        logData.put("@timestamp", Instant.now().toString());

        log.info("Method Execution: {}", net.logstash.logback.argument.StructuredArguments.entries(logData));
    }

    @After("execution(* com.kitty.blog.application..*.*(..))" +
            " || execution(* com.kitty.blog.interfaces..*.*(..))" +
            " && !execution(* com.kitty.blog.infrastructure.security.filter..*.*(..))")
    public void after(JoinPoint joinPoint) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("type", "method-execution");
        logData.put("phase", "after");
        logData.put("class", joinPoint.getTarget().getClass().getName());
        logData.put("method", joinPoint.getSignature().getName());
        logData.put("@timestamp", Instant.now().toString());

        log.info("Method Execution: {}", net.logstash.logback.argument.StructuredArguments.entries(logData));
    }

    @AfterThrowing(pointcut = "execution(* com.kitty.blog.application..*.*(..))" +
            " || execution(* com.kitty.blog.interfaces..*.*(..))" +
            " && !execution(* com.kitty.blog.infrastructure.security.filter..*.*(..))", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Throwable ex) {
        Map<String, Object> logData = new HashMap<>();
        logData.put("type", "method-execution");
        logData.put("phase", "error");
        logData.put("class", joinPoint.getTarget().getClass().getName());
        logData.put("method", joinPoint.getSignature().getName());
        logData.put("error", ex.getMessage());
        logData.put("stack_trace", Arrays.toString(ex.getStackTrace()));
        logData.put("@timestamp", Instant.now().toString());

        log.error("Method Execution Error: {}", net.logstash.logback.argument.StructuredArguments.entries(logData));
    }
}