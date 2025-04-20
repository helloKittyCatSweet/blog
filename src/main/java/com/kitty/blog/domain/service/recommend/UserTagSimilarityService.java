package com.kitty.blog.domain.service.recommend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;

@Slf4j
@Service
public class UserTagSimilarityService {

    private static final double SIMILARITY_THRESHOLD = 0.7;

    public double calculateSimilarity(String tag1, String tag2) {
        if (tag1 == null || tag2 == null) {
            return 0.0;
        }

        tag1 = tag1.toLowerCase();
        tag2 = tag2.toLowerCase();

        int[][] dp = new int[tag1.length() + 1][tag2.length() + 1];

        // 初始化
        for (int i = 0; i <= tag1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= tag2.length(); j++) {
            dp[0][j] = j;
        }

        // 计算编辑距离
        for (int i = 1; i <= tag1.length(); i++) {
            for (int j = 1; j <= tag2.length(); j++) {
                if (tag1.charAt(i-1) == tag2.charAt(j-1)) {
                    dp[i][j] = dp[i-1][j-1];
                } else {
                    dp[i][j] = 1 + Math.min(
                            dp[i-1][j-1],  // 替换
                            Math.min(
                                    dp[i-1][j],    // 删除
                                    dp[i][j-1]     // 插入
                            )
                    );
                }
            }
        }

        // 归一化得分
        int maxLength = Math.max(tag1.length(), tag2.length());
        return 1.0 - (double) dp[tag1.length()][tag2.length()] / maxLength;
    }

    public double calculateTagSetSimilarity(Set<String> tags1, Set<String> tags2) {
        if (tags1 == null || tags2 == null || tags1.isEmpty() || tags2.isEmpty()) {
            return 0.0;
        }

        double totalScore = 0.0;
        int validComparisons = 0;

        // 对每个标签找到最佳匹配
        for (String tag1 : tags1) {
            double maxScore = 0.0;
            for (String tag2 : tags2) {
                double score = calculateSimilarity(tag1, tag2);
                maxScore = Math.max(maxScore, score);
            }

            // 只计算相似度超过阈值的标签对
            if (maxScore >= SIMILARITY_THRESHOLD) {
                totalScore += maxScore;
                validComparisons++;
            }
        }

        // 返回平均相似度
        return validComparisons > 0 ? totalScore / validComparisons : 0.0;
    }
}