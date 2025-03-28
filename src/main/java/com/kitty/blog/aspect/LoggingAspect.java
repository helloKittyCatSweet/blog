package com.kitty.blog.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // 定义切入点：拦截repository包下的所有方法
    @Before("execution(* com.kitty.blog.repository.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Before method: {} with arguments: {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @After("execution(* com.kitty.blog.repository.*.*(..))")
    public void logAfter(JoinPoint joinPoint) {
        logger.info("After method: {}", joinPoint.getSignature().getName());
    }
}
