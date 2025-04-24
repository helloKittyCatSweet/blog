package com.kitty.blog.common.constant;

/**
 * 日志相关常量
 */
public final class LogConstants {

    private LogConstants() {
        // 私有构造函数防止实例化
    }

    // 日志文件名（不含扩展名）
    public static final String API_METRICS = "blog-api-metrics";
    public static final String ERROR = "blog-error";
    public static final String SYSTEM_METRICS = "blog-system-metrics";
    public static final String USER_ACTIVITY = "blog-user-activity";
    public static final String POST_METRICS = "blog-post-metrics";

    // 日志类型
    public static final class LogType {
        public static final String API_METRICS = "api-metrics";
        public static final String ERROR = "error";
        public static final String SYSTEM_METRICS = "system-metrics";
        public static final String USER_ACTIVITY = "user-activity";
        public static final String POST_METRICS = "post-metrics";
    }

    // 应用名称
    public static final String APPLICATION_NAME = "blog-system";

    // MDC Keys
    public static final class MDC {
        public static final String REQUEST_ID = "requestId";
        public static final String USER_ID = "userId";
        public static final String API_PATH = "apiPath";
        public static final String METHOD = "method";
        public static final String STATUS = "status";
        public static final String DURATION = "duration";
        public static final String TIMESTAMP = "@timestamp";
    }

    // 日志字段名
    public static final class Fields {
        public static final String TIMESTAMP = "@timestamp";
        public static final String MESSAGE = "message";
        public static final String LOGGER = "logger_name";
        public static final String THREAD = "thread_name";
        public static final String LEVEL = "level";
        public static final String APPLICATION = "application";
        public static final String LOG_TYPE = "log_type";
    }

    // 日志文件扩展名
    public static final String LOG_EXTENSION = ".json";

    // 日志文件路径（相对于应用根目录）
    public static final String LOG_PATH = "./logs";
}