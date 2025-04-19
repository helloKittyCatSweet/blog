package com.kitty.blog.common.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class BackendLoggingAspect {

    private final Logger log = LoggerFactory.getLogger(BackendLoggingAspect.class);

    @Before("execution(* com.kitty.blog.application..*.*(..))" +
            " || execution(* com.kitty.blog.interfaces..*.*(..))" +
            " && !execution(* com.kitty.blog.infrastructure.security.filter..*.*(..))")
    public void before(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        log.info("[{}] Before executing method: {}", className, methodName);
    }

    @After("execution(* com.kitty.blog.application..*.*(..))" +
            " || execution(* com.kitty.blog.interfaces..*.*(..))" +
            " && !execution(* com.kitty.blog.infrastructure.security.filter..*.*(..))")
    public void after(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        log.info("[{}] After executing method: {}", className, methodName);
    }
}