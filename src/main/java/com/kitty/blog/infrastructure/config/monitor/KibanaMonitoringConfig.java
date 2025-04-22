package com.kitty.blog.infrastructure.config.monitor;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "blog.monitor")
public class KibanaMonitoringConfig {

    private KibanaConfig kibana = new KibanaConfig();

    @Data
    public static class KibanaConfig {
        private String version = "8.11.1";
        private List<IndexPattern> indexPatterns;
        private List<DashboardConfig> dashboards;
        private RetryConfig retry = new RetryConfig();
        private HealthCheckConfig healthCheck = new HealthCheckConfig();
        private String url;
        private String username;
        private String password;
        private SecurityConfig security = new SecurityConfig();
    }

    @Data
    public static class SecurityConfig {
        private boolean enabled = true;
        private SslConfig ssl = new SslConfig();
        private AuthConfig authentication = new AuthConfig();
        private SessionConfig session = new SessionConfig();
        private boolean sandbox = false;
    }

    @Data
    public static class SslConfig {
        private boolean enabled = false;
    }

    @Data
    public static class AuthConfig {
        private String provider = "basic";
    }

    @Data
    public static class SessionConfig {
        private String timeout = "1h";
    }

    @Data
    public static class IndexPattern {
        private String title;
        private String name;
        private String timeFieldName = "@timestamp";
        private Map<String, FieldConfig> fields;
    }

    @Data
    public static class FieldConfig {
        private String type;
        private boolean searchable = true;
        private boolean aggregatable = true;
    }

    @Data
    public static class DashboardConfig {
        private String title;
        private String description;
        private List<VisualizationConfig> visualizations;
        private Map<String, Object> params;
    }

    @Data
    public static class VisualizationConfig {
        private String title;
        private String type;
        private String index;
        private Map<String, Object> params;
        private Map<String, Object> metrics;
        private Map<String, Object> buckets;
    }

    @Data
    public static class RetryConfig {
        private int maxAttempts = 3;
        private long delayMillis = 1000;
        private long maxDelayMillis = 5000;
    }

    @Data
    public static class HealthCheckConfig {
        private int timeoutSeconds = 30;
        private int intervalSeconds = 5;
        private int maxAttempts = 10;
    }
}