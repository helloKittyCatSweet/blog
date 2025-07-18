package com.kitty.blog.common.aspect;

import com.kitty.blog.common.annotation.LogPostMetrics;
import com.kitty.blog.common.constant.LogConstants;
import com.kitty.blog.domain.model.Post;
import com.kitty.blog.domain.repository.post.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
public class PostMetricsAspect {

    @Autowired
    private PostRepository postRepository;

    @Around("@annotation(com.kitty.blog.common.annotation.LogPostMetrics)")
    public Object logPostMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String operation = getOperationType(joinPoint);
        Long postId = getPostId(joinPoint);

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;
            recordPostMetrics(operation, postId, true, null, duration);
            return result;
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            recordPostMetrics(operation, postId, false, e.getMessage(), duration);
            throw e;
        }
    }

    private String getOperationType(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        LogPostMetrics annotation = signature.getMethod().getAnnotation(LogPostMetrics.class);
        return annotation.value();
    }

    private Long getPostId(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            } else if (arg instanceof Integer) {
                return ((Integer) arg).longValue();
            } else if (arg instanceof String) {
                try {
                    return Long.parseLong((String) arg);
                } catch (NumberFormatException e) {
                    log.warn("Failed to parse post id from string: {}", arg);
                }
            }
        }
        return null;
    }

    private void recordPostMetrics(String operation, Long postId, boolean success, String errorMessage, long duration) {
        try {
            Map<String, Object> metrics = new HashMap<>();

            // 基础字段
            metrics.put("@timestamp", LocalDateTime.now().toInstant(ZoneOffset.UTC).toString());
            metrics.put("log_type", LogConstants.LogType.POST_METRICS);
            metrics.put("service", LogConstants.APPLICATION_NAME);
            metrics.put("level", "INFO");

            // 操作信息
            metrics.put("operation", operation);
            metrics.put("post_id", postId);
            if (postId != null) {
                try {
                    Post post = postRepository.findById(postId.intValue())
                            .orElse(null);
                    if (post != null) {
                        metrics.put("post_title", post.getTitle());
                        metrics.put("user_id", post.getUserId());
                        metrics.put("view_count", post.getViews());
                    } else {
                        metrics.put("post_title", "未找到文章");
                        metrics.put("user_id", "unknown");
                        log.error("文章不存在: {}", postId);
                    }
                } catch (Exception e) {
                    log.error("获取文章详情失败: {}", e.getMessage(), e);
                    metrics.put("post_title", "获取文章信息失败");
                    metrics.put("user_id", "unknown");
                }
            } else {
                metrics.put("post_title", "无文章ID");
                metrics.put("user_id", "unknown");
                log.error("文章ID为空");
            }
            metrics.put("success", success);
            metrics.put("duration", duration);

            if (errorMessage != null) {
                metrics.put("error_message", errorMessage);
                log.error("操作失败: {}", errorMessage);
            }

            // 添加标签
            metrics.put("host", java.net.InetAddress.getLocalHost().getHostName());
            metrics.put("environment", System.getProperty("spring.profiles.active", "dev"));
            log.info("Post Metrics: {}",
                    net.logstash.logback.argument.StructuredArguments.entries(metrics));
        } catch (Exception e) {
            log.error("记录文章指标失败", e);
        }
    }
}