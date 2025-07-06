package com.kitty.blog.infrastructure.config.system;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "xunfei")
public class XunfeiConfig {
    private String hostUrl;
    private String appId;
    private String apiKey;
    private String apiSecret;
    private String patchId;
    private String domain;
}