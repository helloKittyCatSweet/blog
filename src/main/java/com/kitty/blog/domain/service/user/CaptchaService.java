package com.kitty.blog.application.service.user;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CaptchaService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;

    @PostConstruct
    public void init() {
        try {
            String testKey = "test_redis_connection";
            redisTemplate.opsForValue().set(testKey, "test", 1, TimeUnit.SECONDS);
            String testValue = redisTemplate.opsForValue().get(testKey);
            log.info("Redis连接测试成功: {}", testValue);
        } catch (Exception e) {
            log.error("Redis连接测试失败: {}", e.getMessage(), e);
        }
    }

    public BufferedImage generateCaptcha(String sessionId) {
        // 创建验证码图片
        BufferedImage captchaImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = captchaImage.createGraphics();

        // 设置背景色
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, WIDTH, HEIGHT);

        // 绘制随机验证码字符
        graphics.setFont(new Font("Arial", Font.BOLD, 24));
        String captchaCode = generateCaptchaCode(5);
        graphics.setColor(Color.BLACK);
        graphics.drawString(captchaCode, 20, 30);

        log.info("captchaCode:{}", captchaCode);

        // 将验证码存储到 Redis 中，并设置过期时间为 5 分钟
        String redisKey = "CAPTCHA_" + sessionId;
        redisTemplate.opsForValue().set(redisKey, captchaCode, 5, TimeUnit.MINUTES);

        // 关闭图形上下文
        graphics.dispose();

        return captchaImage;
    }

    private String generateCaptchaCode(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder captcha = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            captcha.append(characters.charAt(random.nextInt(characters.length())));
        }
        return captcha.toString();
    }

    public boolean verifyCaptcha(String sessionId, String inputCode) {
        if (sessionId == null || inputCode == null) {
            log.error("验证码验证失败：参数为空 sessionId:{}, inputCode:{}", sessionId, inputCode);
            throw new RuntimeException("验证码参数错误");
        }

        // 从 Redis 中获取验证码
        String redisKey = "CAPTCHA_" + sessionId;
        String storedCode = null;
        try {
            storedCode = redisTemplate.opsForValue().get(redisKey);
            log.info("从Redis获取验证码 key:{}, storedCode:{}", redisKey, storedCode);
        } catch (Exception e) {
            log.error("从Redis获取验证码失败：{}", e.getMessage(), e);
            throw new RuntimeException("验证码验证失败，请稍后重试");
        }

        if (storedCode == null) {
            log.warn("验证码不存在或已过期 sessionId:{}, redisKey:{}", sessionId, redisKey);
            throw new RuntimeException("验证码已过期，请重新获取");
        }

        // 验证完成后，删除Redis中的验证码，防止重复使用
        boolean isValid = storedCode.equalsIgnoreCase(inputCode);
        try {
            if (isValid) {
                redisTemplate.delete(redisKey);
                log.info("验证码验证成功，已删除Redis缓存 key:{}", redisKey);
            } else {
                log.warn("验证码不匹配 sessionId:{}, inputCode:{}, storedCode:{}",
                        sessionId, inputCode, storedCode);
            }
        } catch (Exception e) {
            log.error("删除Redis验证码失败：{}", e.getMessage(), e);
        }

        return isValid;
    }
}