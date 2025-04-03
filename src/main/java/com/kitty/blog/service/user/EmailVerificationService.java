package com.kitty.blog.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Value("${spring.mail.username}")
    private String fromEmail;

    // 预定义邮件模板
    private static final String EMAIL_TEMPLATE = "Thank you for signing up FreeShare! Your verification code is: %s. It's valid for 5 minutes.";

    // 重试配置
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 1000;

    @Async("emailTaskExecutor")
    public CompletableFuture<String> sendVerificationEmail(String toEmail) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendEmailWithRetry(toEmail);
            } catch (Exception e) {
                log.error("Failed to send email after {} attempts", MAX_RETRY_ATTEMPTS, e);
                throw new RuntimeException("邮件发送失败，请稍后重试");
            }
        });
    }

    private String sendEmailWithRetry(String toEmail) {
        // 检查频率限制
        String rateLimitKey = "EMAIL_RATE_" + toEmail;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(rateLimitKey))) {
            return "请等待60秒后再次请求验证码";
        }

        // 生成验证码
        String code = generateCode();
        SimpleMailMessage message = createEmailMessage(toEmail, code);

        // 重试机制
        for (int attempt = 0; attempt < MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                mailSender.send(message);

                // 存储验证码
                redisTemplate.opsForValue().set("EMAIL_CODE_" + toEmail, code, 5, TimeUnit.MINUTES);
                // 设置发送频率限制
                redisTemplate.opsForValue().set(rateLimitKey, "1", 60, TimeUnit.SECONDS);

                return "验证码已发送至 " + toEmail + "，请注意查收。";
            } catch (Exception e) {
                log.warn("Email sending attempt {} failed", attempt + 1, e);
                if (attempt < MAX_RETRY_ATTEMPTS - 1) {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("邮件发送中断");
                    }
                }
            }
        }
        throw new RuntimeException("邮件发送失败");
    }

    private SimpleMailMessage createEmailMessage(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Verification Code");
        message.setText(String.format(EMAIL_TEMPLATE, code));
        return message;
    }

    public boolean verifyCode(String email, String code) {
        String storedCode = redisTemplate.opsForValue().get("EMAIL_CODE_" + email);
        if (storedCode == null) {
            throw new RuntimeException("验证码已过期");
        }
        boolean isValid = storedCode.equals(code);
        if (isValid) {
            // 验证成功后立即删除验证码
            redisTemplate.delete("EMAIL_CODE_" + email);
        }
        return isValid;
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}