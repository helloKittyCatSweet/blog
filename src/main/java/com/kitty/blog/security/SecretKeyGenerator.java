package com.kitty.blog.security;

import java.util.Base64;
import io.jsonwebtoken.security.Keys; // 导入Keys类所在的包

public class SecretKeyGenerator {
    public static void main(String[] args) {
        byte[] secretKeyBytes = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256).getEncoded();
        String base64EncodedKey = Base64.getEncoder().encodeToString(secretKeyBytes);
        System.out.println("Generated SECRET_KEY: " + base64EncodedKey);
    }
}
