package com.kitty.blog.infrastructure.config.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "templates.mail")
public class MailTemplateConfig {

    private String adminNotification;

    private String autoReply;
}