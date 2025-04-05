package com.kitty.blog.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtTokenUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long jwtTokenExpiration; // token过期时间，单位为秒

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        logger.info("Initializing JWT secret key");
        try {
            if (secret == null || secret.trim().isEmpty()) {
                throw new IllegalStateException("JWT secret key cannot be null or empty");
            }
            byte[] keyBytes = Base64.getDecoder().decode(secret.trim());
            logger.debug("Secret key length: {} bytes", keyBytes.length);
            secretKey = Keys.hmacShaKeyFor(keyBytes);
            logger.info("JWT secret key initialized successfully");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Base64 encoded secret key", e);
            throw new IllegalStateException("Failed to decode JWT secret key", e);
        } catch (Exception e) {
            logger.error("Failed to initialize JWT secret key", e);
            throw new IllegalStateException("Failed to initialize JWT secret key", e);
        }
    }

    public String generateToken(UserDetails userDetails) {
        logger.debug("Generating token for user: {}", userDetails.getUsername());
        Map<String, Object> claims = new HashMap<>();
        claims.put("uuid", UUID.randomUUID().toString()); // 添加唯一标识符
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtTokenExpiration * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        logger.debug("Token generated successfully");
        return token;
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("Token cannot be null or empty");
            }
            // 移除可能存在的引号
            token = token.trim().replace("\"", "");

            logger.debug("Parsing JWT token");
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            logger.debug("JWT token parsed successfully");
            return claims;
        } catch (ExpiredJwtException e) {
            logger.debug("JWT token has expired");
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.debug("JWT token is unsupported");
            throw e;
        } catch (MalformedJwtException e) {
            logger.debug("JWT token is malformed");
            throw e;
        } catch (SignatureException e) {
            logger.debug("JWT signature validation failed");
            throw e;
        } catch (IllegalArgumentException e) {
            logger.debug("JWT token is invalid: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.debug("Failed to parse JWT token: {}", e.getMessage());
            throw e;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getUuidFromToken(String token) {
        return getClaimFromToken(token, claims -> (String) claims.get("uuid"));
    }

    public boolean isTokenExpired(String token) {
        try {
            final Date expiration = getClaimFromToken(token, Claims::getExpiration);
            return expiration.before(new Date());
        } catch (Exception e) {
            logger.debug("Token validation failed: {}", e.getMessage());
            return true;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = getUsernameFromToken(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            logger.debug("Token validation failed: {}", e.getMessage());
            return false;
        }
    }


    // 从 JWT 中提取用户名
    public String extractUsername(String jwt) {
        return extractClaim(jwt, Claims::getSubject);
    }

    // 提取 JWT 中的 Claims
    public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(jwt);
        return claimsResolver.apply(claims);
    }

    // 解析 JWT 并提取 Claims
    private Claims extractAllClaims(String jwt) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secret)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token has expired", e);
        } catch (MalformedJwtException e) {
            throw new RuntimeException("Invalid JWT token", e);
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("Unsupported JWT token", e);
        } catch (SignatureException e) {
            throw new RuntimeException("Invalid JWT token signature", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT token compact of handler are invalid", e);
        }
    }

}
