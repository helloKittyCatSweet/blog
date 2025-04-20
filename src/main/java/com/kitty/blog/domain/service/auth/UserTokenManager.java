package com.kitty.blog.domain.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserTokenManager {

    private static final String TOKEN_KEY_PREFIX = "user_token:";
    private static final long TOKEN_EXPIRE_HOURS = 24;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void saveUserToken(Integer userId, String token) {
        String key = TOKEN_KEY_PREFIX + userId;
        // 获取旧token
        String oldToken = redisTemplate.opsForValue().get(key);
        if (oldToken != null) {
            // 将旧token加入黑名单
            redisTemplate.opsForValue().set("token_blacklist:" + oldToken, "", TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);
        }
        // 保存新token
        redisTemplate.opsForValue().set(key, token, TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    public boolean isTokenValid(String token) {
        return !Boolean.TRUE.equals(redisTemplate.hasKey("token_blacklist:" + token));
    }
}