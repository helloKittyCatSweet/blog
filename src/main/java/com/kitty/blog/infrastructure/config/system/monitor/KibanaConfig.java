package com.kitty.blog.infrastructure.config.system.monitor;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@EnableRetry
@ConfigurationProperties(prefix = "blog.monitor.kibana") // Updated prefix
public class KibanaConfig {

    private final KibanaMonitoringConfig monitoringConfig;

    @Value("${blog.monitor.kibana.password:kibana123}") // Updated path
    private String password;

    @Value("${blog.monitor.kibana.username:kibana_system}") // Updated path
    private String username;

    @Value("${blog.monitor.kibana.url:http://localhost:5601}") // Updated path
    private String url;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(monitoringConfig.getKibana().getRetry().getDelayMillis());
        backOffPolicy.setMaxInterval(monitoringConfig.getKibana().getRetry().getMaxDelayMillis());
        backOffPolicy.setMultiplier(2.0);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(monitoringConfig.getKibana().getRetry().getMaxAttempts());

        retryTemplate.setBackOffPolicy(backOffPolicy);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

    @Bean
    public KibanaProperties kibanaProperties() {
        return new KibanaProperties(url, username, password);
    }
}