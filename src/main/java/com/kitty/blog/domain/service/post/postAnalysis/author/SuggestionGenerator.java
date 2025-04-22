package com.kitty.blog.domain.service.post.postAnalysis.author;

import com.kitty.blog.common.constant.ActivityType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuggestionGenerator {

    @Autowired
    private AnalyticsProperties properties;

    public List<String> generateWritingSuggestions(
            Map<Integer, AuthorAnalyticsService.PostPerformance> postPerformance,
            AuthorAnalyticsService.UserBehaviorAnalysis userBehavior) {

        List<String> suggestions = new ArrayList<>();

        // 分析最佳发布时间
        analyzeBestPostingTime(postPerformance, suggestions);

        // 分析内容受欢迎程度
        analyzeContentPopularity(postPerformance, suggestions);

        // 分析用户互动模式
        analyzeUserInteractionPatterns(userBehavior, suggestions);

        // 分析用户留存
        analyzeUserRetention(userBehavior, suggestions);

        // 分析文章长度
        analyzePostLengthEngagement(postPerformance, suggestions);

        // 提供具体改进建议
        provideImprovementSuggestions(postPerformance, userBehavior, suggestions);

        return suggestions;
    }

    private void analyzeContentPopularity(
            Map<Integer, AuthorAnalyticsService.PostPerformance> postPerformance,
            List<String> suggestions) {

        // 分析标题长度与互动率的关系
        analyzeTitleLength(postPerformance, suggestions);

        // 分析文章发布时间间隔
        analyzePostingFrequency(postPerformance, suggestions);

        // 现有的最佳表现分析
        Optional<AuthorAnalyticsService.PostPerformance> bestPerforming = postPerformance.values().stream()
                .max(Comparator.comparingDouble(AuthorAnalyticsService.PostPerformance::engagementRate));

        bestPerforming.ifPresent(performance -> {
            suggestions.add(String.format(
                    "文章《%s》的互动率最高（%.2f%%），建议参考其写作风格和主题",
                    performance.title(),
                    performance.engagementRate() * 100));
        });
    }

    private void analyzeTitleLength(
            Map<Integer, AuthorAnalyticsService.PostPerformance> postPerformance,
            List<String> suggestions) {
        // 分析标题长度与互动率的关系
        Map<Integer, Double> titleLengthEngagement = new HashMap<>();
        for (AuthorAnalyticsService.PostPerformance performance : postPerformance.values()) {
            int titleLength = performance.title().length();
            titleLengthEngagement.merge(titleLength, performance.engagementRate(), Double::sum);
        }

        OptionalDouble avgBestLength = titleLengthEngagement.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(3)
                .mapToInt(Map.Entry::getKey)
                .average();

        avgBestLength.ifPresent(length -> suggestions.add(String.format("建议标题长度保持在%d个字左右，这个长度的互动率较高",
                (int) length)));
    }

    private void analyzePostingFrequency(
            Map<Integer, AuthorAnalyticsService.PostPerformance> postPerformance,
            List<String> suggestions) {
        List<LocalDateTime> postDates = postPerformance.values().stream()
                .map(AuthorAnalyticsService.PostPerformance::createdAt)
                .sorted()
                .toList();

        if (postDates.size() >= 2) {
            long totalHours = ChronoUnit.HOURS.between(
                    postDates.get(0),
                    postDates.get(postDates.size() - 1));
            double avgDays = (double) totalHours / 24.0 / (postDates.size() - 1);

            suggestions.add(String.format("您平均每%.1f天发布一篇文章，保持稳定的更新频率有助于维持读者粘性", avgDays));
        }
    }

    /**
     * 分析用户留存
     */
    private void analyzeUserRetention(
            AuthorAnalyticsService.UserBehaviorAnalysis userBehavior,
            List<String> suggestions) {

        if (userBehavior.topEngagers().size() >= 5) {
            suggestions.add("您有一批忠实读者，建议加强与他们的互动，建立稳定的读者群");
        }
    }

    /**
     * 分析最佳发布时间
     */
    private void analyzeBestPostingTime(
            Map<Integer, AuthorAnalyticsService.PostPerformance> postPerformance,
            List<String> suggestions) {
        // 按小时统计互动率
        Map<Integer, List<Double>> hourlyEngagement = new HashMap<>();
        for (AuthorAnalyticsService.PostPerformance performance : postPerformance.values()) {
            int hour = performance.createdAt().getHour();
            hourlyEngagement.computeIfAbsent(hour, k -> new ArrayList<>())
                    .add(performance.engagementRate());
        }

        // 计算每个小时的平均互动率
        Map<Integer, Double> avgHourlyEngagement = hourlyEngagement.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0)));

        // 找出互动率最高的时间段
        Optional<Map.Entry<Integer, Double>> bestHour = avgHourlyEngagement.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        bestHour.ifPresent(entry -> {
            String timeRange = String.format("%02d:00-%02d:00",
                    entry.getKey(),
                    (entry.getKey() + 1) % 24);
            suggestions.add(String.format("建议在 %s 发布文章，这个时间段的平均互动率最高（%.2f%%）",
                    timeRange,
                    entry.getValue() * 100));
        });
    }

    /**
     * 分析用户互动模式
     */
    private void analyzeUserInteractionPatterns(
            AuthorAnalyticsService.UserBehaviorAnalysis userBehavior,
            List<String> suggestions) {

        Map<ActivityType, Long> distribution = userBehavior.activityDistribution();
        long totalActivities = distribution.values().stream().mapToLong(Long::longValue).sum();

        // 分析互动类型分布
        if (totalActivities > 0) {
            // 分析评论和回复
            long comments = distribution.getOrDefault(ActivityType.COMMENT, 0L);
            long replies = distribution.getOrDefault(ActivityType.REPLY, 0L);
            double commentRate = (double) (comments + replies) / totalActivities;

            if (commentRate < 0.1) {
                suggestions.add("评论互动率较低，建议：\n" +
                        "1. 在文章末尾添加互动引导\n" +
                        "2. 积极回复读者评论\n" +
                        "3. 在文章中设置开放性问题");
            }

            // 分析点赞情况
            long likes = distribution.getOrDefault(ActivityType.LIKE, 0L);
            long commentLikes = distribution.getOrDefault(ActivityType.LIKE_COMMENT, 0L);
            if (likes > commentLikes * 2) {
                suggestions.add("读者更倾向于点赞文章而非评论，建议增加文章的互动性，鼓励读者发表观点");
            }

            // 分析收藏率
            long favorites = distribution.getOrDefault(ActivityType.FAVORITE, 0L);
            double favoriteRate = (double) favorites / totalActivities;
            if (favoriteRate > 0.2) {
                suggestions.add("文章收藏率较高，说明内容具有较高的参考价值，建议：\n" +
                        "1. 继续深化专业性内容\n" +
                        "2. 考虑制作系列文章\n" +
                        "3. 适当增加实用性技巧");
            }
        }
    }

    /**
     * 分析文章长度与互动率的关系
     */
    private void analyzePostLengthEngagement(
            Map<Integer, AuthorAnalyticsService.PostPerformance> postPerformance,
            List<String> suggestions) {
        // 根据文章内容长度分组分析
        Map<String, List<Double>> lengthRangeEngagement = new HashMap<>();
        lengthRangeEngagement.put("短文(1000字以下)", new ArrayList<>());
        lengthRangeEngagement.put("中等(1000-3000字)", new ArrayList<>());
        lengthRangeEngagement.put("长文(3000字以上)", new ArrayList<>());

        // 统计每个长度范围的互动率
        for (AuthorAnalyticsService.PostPerformance performance : postPerformance.values()) {
            // 添加内容长度的空值检查
            String content = performance.content();
            if (content == null) {
                continue; // 跳过没有内容的文章
            }

            int contentLength = performance.content().length();
            double engagementRate = performance.engagementRate();

            if (contentLength < 1000) {
                lengthRangeEngagement.get("短文(1000字以下)").add(engagementRate);
            } else if (contentLength <= 3000) {
                lengthRangeEngagement.get("中等(1000-3000字)").add(engagementRate);
            } else {
                lengthRangeEngagement.get("长文(3000字以上)").add(engagementRate);
            }
        }

        // 计算每个长度范围的平均互动率
        Map<String, Double> avgEngagementByLength = new HashMap<>();
        lengthRangeEngagement.forEach((range, rates) -> {
            double avgRate = rates.stream()
                    .mapToDouble(Double::doubleValue)
                    .average()
                    .orElse(0.0);
            avgEngagementByLength.put(range, avgRate);
        });

        // 找出互动率最高的长度范围
        Optional<Map.Entry<String, Double>> bestRange = avgEngagementByLength.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        if (bestRange.isPresent()) {
            suggestions.add(String.format("数据显示%s的文章互动率最高（%.2f%%），建议：\n" +
                            "1. 技术教程建议2000-3000字，确保内容详实\n" +
                            "2. 心得体会控制在1000-2000字，保持精炼\n" +
                            "3. 新闻资讯800-1500字，突出重点\n" +
                            "4. 当前最受欢迎的是%s的文章，可以多尝试这个长度",
                    bestRange.get().getKey(),
                    bestRange.get().getValue() * 100,
                    bestRange.get().getKey()));
        } else {
            suggestions.add("建议根据主题选择合适的文章长度：\n" +
                    "1. 技术教程建议2000-3000字，确保内容详实\n" +
                    "2. 心得体会控制在1000-2000字，保持精炼\n" +
                    "3. 新闻资讯800-1500字，突出重点");
        }
    }

    private void provideImprovementSuggestions(
            Map<Integer, AuthorAnalyticsService.PostPerformance> postPerformance,
            AuthorAnalyticsService.UserBehaviorAnalysis userBehavior,
            List<String> suggestions) {

        // 分析互动高峰期
        Map<Integer, Long> hourlyDistribution = new HashMap<>();
        postPerformance.values().forEach(performance -> {
            int hour = performance.createdAt().getHour();
            hourlyDistribution.merge(hour, 1L, Long::sum);
        });

        // 找出最活跃的时间段
        Optional<Map.Entry<Integer, Long>> peakHour = hourlyDistribution.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        peakHour.ifPresent(hour ->
                suggestions.add(String.format("读者在%d:00-%d:00活跃度最高，建议在此时间段发布文章或与读者互动",
                hour.getKey(), (hour.getKey() + 1) % 24)));

        // 分析评论质量
        if (userBehavior.activityDistribution().containsKey(ActivityType.COMMENT)) {
            long commentCount = userBehavior.activityDistribution().get(ActivityType.COMMENT);
            if (commentCount > 0) {
                suggestions.add("建议及时回复读者评论，提高读者互动积极性");
            }
        }
    }
}