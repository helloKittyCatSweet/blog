package com.kitty.blog.infrastructure.config.system.monitor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitorConfig {

    @Bean("metricsIndex")
    public String metricsIndex() {
        return "blog-system-metrics";
    }
}