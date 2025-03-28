package com.kitty.blog.service;

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

    @Async("emailTaskExecutor")
    public CompletableFuture<String> sendVerificationEmail(String toEmail) {
        // 检查是否已发送验证码
        String existingCode = redisTemplate.opsForValue().get("EMAIL_CODE_" + toEmail);
        if (existingCode != null) {
            return CompletableFuture.completedFuture("验证码已发送至 " + toEmail + "，请稍后再试。");
        }
        log.info("Sending verification email to {}", toEmail);

        // 随机生成验证码
        String code = generateCode();

        // 创建邮件内容
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Verification Code");
        message.setText("Thank you for signing up FreeShare! Your verification code is: " + code +
        ". It's valid for 5 minutes.");

        // 发送邮件
        mailSender.send(message);

        // 将验证码存储到 Redis，并设置过期时间为 5 分钟
        redisTemplate.opsForValue().set("EMAIL_CODE_" + toEmail, code, 5, TimeUnit.MINUTES);
        return CompletableFuture.completedFuture("验证码已发送至 " + toEmail + "，请注意查收。");
    }

    public boolean verifyCode(String email, String code) {
        // 从 Redis 中获取验证码
        String storedCode = redisTemplate.opsForValue().get("EMAIL_CODE_" + email);
        log.info("storedCode: " + storedCode);
        if (storedCode == null) {
            throw new RuntimeException("验证码已过期");
        }
        return storedCode.equals(code);
    }

    private String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6位数
        return String.valueOf(code);
    }
}