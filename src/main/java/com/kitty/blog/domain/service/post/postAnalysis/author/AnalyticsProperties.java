package com.kitty.blog.domain.service.post.postAnalysis.author;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "analytics")
@Data
@Component
public class AnalyticsProperties {
    private int cacheTtl = 3600;
    private int timeoutSeconds = 5;
    private EngagementWeights engagementWeights = new EngagementWeights();

    @Data
    public static class EngagementWeights {
        private double like = 1.0;
        private double favorite = 2.0;
        private double comment = 3.0;
        private double reply = 2.0;
        private double commentLike = 0.5;
    }
}