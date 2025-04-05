package com.kitty.blog.application.service.tag;

import com.kitty.blog.model.tag.Tag;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class TagWeightService {

    private static final int BASE_WEIGHT = 10;  // 基础权重
    private static final double USE_WEIGHT = 1.0;  // 使用权重系数
    private static final double CLICK_WEIGHT = 0.5;  // 点击权重系数
    private static final double TIME_DECAY = 0.9;  // 时间衰减系数
    private static final int TIME_WEIGHT = 5;  // 时间权重

    public int calculateWeight(Tag tag) {
        double timeWeight = calculateTimeWeight(tag.getLastUsedAt());

        double weight = BASE_WEIGHT +
                (tag.getUseCount() * USE_WEIGHT) +
                (tag.getClickCount() * CLICK_WEIGHT) +
                (timeWeight * TIME_WEIGHT) +
                tag.getAdminWeight();

        // 确保权重在0-100之间
        return (int) Math.min(100, Math.max(0, weight));
    }

    private double calculateTimeWeight(LocalDateTime lastUsedAt) {
        if (lastUsedAt == null) {
            return 0;
        }

        long daysAgo = ChronoUnit.DAYS.between(lastUsedAt, LocalDateTime.now());
        return Math.pow(TIME_DECAY, daysAgo);
    }

    // 更新标签使用次数
    public void incrementUseCount(Tag tag) {
        tag.setUseCount(tag.getUseCount() + 1);
        tag.setLastUsedAt(LocalDateTime.now());
        updateWeight(tag);
    }

    // 更新标签点击次数
    public void incrementClickCount(Tag tag) {
        tag.setClickCount(tag.getClickCount() + 1);
        updateWeight(tag);
    }

    // 更新权重
    public void updateWeight(Tag tag) {
        tag.setWeight(calculateWeight(tag));
    }
}