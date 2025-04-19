package com.kitty.blog.infrastructure.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsEndpointAutoConfiguration;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActuatorConfig {

    @Bean
    public MetricsEndpoint metricsEndpoint(MeterRegistry meterRegistry) {
        return new MetricsEndpoint(meterRegistry);
    }
}