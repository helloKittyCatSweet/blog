package com.kitty.blog.domain.service.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class GithubUserInfoService {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private static final String GITHUB_USER_INFO_PREFIX = "github:user:";
    private static final long EXPIRE_TIME = 10; // 10分钟过期
    
    public void saveUserInfo(String state, Map<String, String> userInfo) {
        String key = GITHUB_USER_INFO_PREFIX + state;
        redisTemplate.opsForHash().putAll(key, new HashMap<>(userInfo));
        redisTemplate.expire(key, EXPIRE_TIME, TimeUnit.MINUTES);
    }
    
    public Map<String, String> getUserInfo(String state) {
        String key = GITHUB_USER_INFO_PREFIX + state;
        return redisTemplate.opsForHash().entries(key).entrySet().stream()
                .collect(HashMap::new,
                        (m, e) -> m.put(e.getKey().toString(), e.getValue().toString()),
                        HashMap::putAll);
    }
}