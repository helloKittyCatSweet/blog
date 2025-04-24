package com.kitty.blog.common.constant;

/**
 * Elasticsearch 相关常量
 */
public final class ElasticsearchConstants {

    private ElasticsearchConstants() {
        // 私有构造函数防止实例化
    }

    // 索引别名
    public static final String API_METRICS_INDEX_ALIAS = "blog-api-metrics-alias";
    public static final String ERROR_LOGS_INDEX_ALIAS = "blog-error-logs-alias";
    public static final String USER_ACTIVITY_INDEX_ALIAS = "blog-user-activity-alias";
    public static final String POST_METRICS_INDEX_ALIAS = "blog-post-metrics-alias";
    public static final String SYSTEM_METRICS_INDEX_ALIAS = "blog-system-metrics-alias";

    // 索引名称模式（用于创建索引模板）
    public static final String API_METRICS_INDEX_PATTERN = "blog-api-metrics-*";
    public static final String ERROR_LOGS_INDEX_PATTERN = "blog-error-logs-*";
    public static final String USER_ACTIVITY_INDEX_PATTERN = "blog-user-activity-*";
    public static final String POST_METRICS_INDEX_PATTERN = "blog-post-metrics-*";
    public static final String SYSTEM_METRICS_INDEX_PATTERN = "blog-system-metrics-*";

    // 生命周期策略名称
    public static final String LIFECYCLE_POLICY_NAME = "blog-policy";

    // 字段名称
    public static final class Fields {
        public static final String TIMESTAMP = "@timestamp";
        public static final String METRICS_NAME = "metrics_name";
        public static final String CPU_USAGE = "cpu_usage";
        public static final String MEMORY_USED = "memory_used";
        public static final String TOTAL_MEMORY = "total_memory";
        public static final String FREE_MEMORY = "free_memory";

        // 标签字段
        public static final String TAGS = "tags";
        public static final String HOST = "host";
        public static final String SERVICE = "service";
        public static final String ENVIRONMENT = "environment";
    }

    // 服务名称
    public static final class Services {
        public static final String BLOG_SYSTEM = "blog-system";
    }

    // 环境
    public static final class Environments {
        public static final String PRODUCTION = "production";
        public static final String DEVELOPMENT = "development";
        public static final String TEST = "test";
    }
}