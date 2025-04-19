package com.kitty.blog.infrastructure.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * 定制 MeterRegistry，添加全局标签并设置过滤器
     * @return MeterRegistryCustomizer 对象
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> configureMetrics() {
        return registry -> {
            // 添加全局标签，这里添加应用名称作为标签
            registry.config().commonTags("application", applicationName);

            // 设置 MeterFilter，过滤掉不需要的指标
            registry.config().meterFilter(MeterFilter.deny(id ->
                    id.getName().startsWith("jvm") && id.getTag("region") == null
            ));
        };
    }
}