package com.kitty.blog.infrastructure.security;

import java.security.SecureRandom;
import java.util.Base64;

public class EncryptKeyGenerator {

    public static String generateKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[16]; // AES-128 需要16字节密钥
        secureRandom.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    public static void main(String[] args) {
        // 生成一个新的密钥并打印
        String key = generateKey();
        System.out.println("生成的16位密钥: " + key);
    }
}