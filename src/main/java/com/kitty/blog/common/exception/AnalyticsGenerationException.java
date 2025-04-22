package com.kitty.blog.common.exception;

public class AnalyticsGenerationException extends RuntimeException {
    public AnalyticsGenerationException(String message) {
        super(message);
    }

    public AnalyticsGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}