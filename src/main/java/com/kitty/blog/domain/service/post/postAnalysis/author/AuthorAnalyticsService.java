package com.kitty.blog.application.service.post.postAnalysis.author;

import com.kitty.blog.model.Post;
import com.kitty.blog.constant.ActivityType;
import com.kitty.blog.model.UserActivity;
import com.kitty.blog.repository.PostRepository;
import com.kitty.blog.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthorAnalyticsService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserActivityRepository userActivityRepository;

    /**
     * 生成作者分析报告
     */
    public AuthorAnalyticsReport generateReport(Integer authorId) {
        // 1. 获取作者的所有文章
        List<Post> authorPosts = postRepository.findByUserId(authorId).orElse(new ArrayList<>());

        // 2. 获取这些文章的所有用户活动
        List<UserActivity> activities = new ArrayList<>();
        for (Post post : authorPosts) {
            activities.addAll(userActivityRepository.findByPostId(post.getPostId())
                    .orElse(new ArrayList<>()));
        }

        // 3. 分析文章表现
        Map<Integer, PostPerformance> postPerformance = analyzePostPerformance(authorPosts, activities);

        // 4. 分析用户行为
        UserBehaviorAnalysis userBehavior = analyzeUserBehavior(activities);

        // 5. 生成写作建议
        List<String> writingSuggestions = generateWritingSuggestions(postPerformance, userBehavior);

        return new AuthorAnalyticsReport(
                authorId,
                postPerformance,
                userBehavior,
                writingSuggestions);
    }

    /**
     * 分析文章表现
     */
    private Map<Integer, PostPerformance> analyzePostPerformance(
            List<Post> posts,
            List<UserActivity> activities) {

        Map<Integer, PostPerformance> performanceMap = new HashMap<>();

        for (Post post : posts) {
            List<UserActivity> postActivities = activities.stream()
                    .filter(a -> a.getPostId().equals(post.getPostId()))
                    .collect(Collectors.toList());

            // 计算互动指标
            long likes = countActivities(postActivities, ActivityType.LIKE);
            long favorites = countActivities(postActivities, ActivityType.FAVORITE);
            long comments = countActivities(postActivities, ActivityType.COMMENT);
            long shares = countActivities(postActivities, ActivityType.SHARE);

            // 计算参与度
            double engagementRate = calculateEngagementRate(post, postActivities);

            performanceMap.put(post.getPostId(), new PostPerformance(
                    post.getTitle(),
                    post.getCreatedAt().atStartOfDay(),
                    likes,
                    favorites,
                    comments,
                    shares,
                    engagementRate));
        }

        return performanceMap;
    }

    /**
     * 分析用户行为
     */
    private UserBehaviorAnalysis analyzeUserBehavior(List<UserActivity> activities) {
        // 统计活动类型分布
        Map<ActivityType, Long> activityDistribution = activities.stream()
                .collect(Collectors.groupingBy(
                        activity -> ActivityType.valueOf(activity.getActivityType().toUpperCase()),
                        Collectors.counting()));

        // 计算平均互动时间
        double avgInteractionTime = calculateAverageInteractionTime(activities);

        // 识别最活跃的用户
        List<Integer> topEngagers = identifyTopEngagers(activities);

        return new UserBehaviorAnalysis(
                activityDistribution,
                avgInteractionTime,
                topEngagers);
    }

    /**
     * 生成写作建议
     */
    private List<String> generateWritingSuggestions(
            Map<Integer, PostPerformance> postPerformance,
            UserBehaviorAnalysis userBehavior) {

        List<String> suggestions = new ArrayList<>();

        // 1. 分析最佳发布时间
        analyzeBestPostingTime(postPerformance, suggestions);

        // 2. 分析内容受欢迎程度
        analyzeContentPopularity(postPerformance, suggestions);

        // 3. 分析用户互动模式
        analyzeUserInteractionPatterns(userBehavior, suggestions);

        // 4. 提供具体改进建议
        provideImprovementSuggestions(postPerformance, userBehavior, suggestions);

        return suggestions;
    }

    /**
     * 分析最佳发布时间
     */
    private void analyzeBestPostingTime(
            Map<Integer, PostPerformance> postPerformance,
            List<String> suggestions) {

        Map<Integer, Double> hourEngagement = new HashMap<>();
        for (PostPerformance performance : postPerformance.values()) {
            int hour = performance.createdAt().getHour();
            hourEngagement.merge(hour, performance.engagementRate(), Double::sum);
        }

        // 找出互动率最高的时间段
        Optional<Map.Entry<Integer, Double>> bestHour = hourEngagement.entrySet().stream()
                .max(Map.Entry.comparingByValue());

        bestHour.ifPresent(entry -> suggestions.add(
                String.format("建议在 %d:00 发布文章，这个时间段的互动率最高", entry.getKey())));
    }

    /**
     * 分析内容受欢迎程度
     */
    private void analyzeContentPopularity(
            Map<Integer, PostPerformance> postPerformance,
            List<String> suggestions) {

        // 找出最受欢迎的文章
        Optional<PostPerformance> bestPerforming = postPerformance.values().stream()
                .max(Comparator.comparingDouble(PostPerformance::engagementRate));

        bestPerforming.ifPresent(performance -> {
            suggestions.add(String.format(
                    "文章《%s》的互动率最高，建议参考其写作风格和主题",
                    performance.title()));
        });
    }

    /**
     * 分析用户互动模式
     */
    private void analyzeUserInteractionPatterns(
            UserBehaviorAnalysis userBehavior,
            List<String> suggestions) {

        Map<ActivityType, Long> distribution = userBehavior.activityDistribution();
        long totalActivities = distribution.values().stream().mapToLong(Long::longValue).sum();

        // 分析互动类型分布
        if (totalActivities > 0) {
            double commentRate = (double) distribution.getOrDefault(ActivityType.COMMENT, 0L) / totalActivities;
            if (commentRate < 0.1) {
                suggestions.add("评论互动率较低，建议在文章末尾添加互动引导，鼓励读者评论");
            }
        }
    }

    /**
     * 提供具体改进建议
     */
    private void provideImprovementSuggestions(
            Map<Integer, PostPerformance> postPerformance,
            UserBehaviorAnalysis userBehavior,
            List<String> suggestions) {

        // 分析文章长度与互动率的关系
        analyzePostLengthEngagement(postPerformance, suggestions);

        // 分析用户留存
        analyzeUserRetention(userBehavior, suggestions);
    }

    /**
     * 分析文章长度与互动率的关系
     */
    private void analyzePostLengthEngagement(
            Map<Integer, PostPerformance> postPerformance,
            List<String> suggestions) {

        // 这里可以添加文章长度分析逻辑
        suggestions.add("建议保持文章长度在1000-2000字之间，这个长度最受读者欢迎");
    }

    /**
     * 分析用户留存
     */
    private void analyzeUserRetention(
            UserBehaviorAnalysis userBehavior,
            List<String> suggestions) {

        if (userBehavior.topEngagers().size() >= 5) {
            suggestions.add("您有一批忠实读者，建议加强与他们的互动，建立稳定的读者群");
        }
    }

    /**
     * 计算互动率
     */
    private double calculateEngagementRate(Post post, List<UserActivity> activities) {
        if (post.getViews() == null || post.getViews() == 0) {
            return 0.0;
        }

        long totalInteractions = activities.size();
        return (double) totalInteractions / post.getViews();
    }

    /**
     * 计算平均互动时间
     */
    private double calculateAverageInteractionTime(List<UserActivity> activities) {
        return activities.stream()
                .mapToLong(activity -> java.time.temporal.ChronoUnit.HOURS.between(
                        activity.getPost().getCreatedAt(),
                        activity.getCreatedAt()))
                .average()
                .orElse(0.0);
    }

    /**
     * 识别最活跃的用户
     */
    private List<Integer> identifyTopEngagers(List<UserActivity> activities) {
        return activities.stream()
                .collect(Collectors.groupingBy(
                        UserActivity::getUserId,
                        Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 统计特定类型的活动数量
     */
    private long countActivities(List<UserActivity> activities, ActivityType type) {
        return activities.stream()
                .filter(a -> ActivityType.valueOf(a.getActivityType().toUpperCase()) == type)
                .count();
    }

    /**
     * 作者分析报告数据类
     *
     * @param authorId Getters
     */
        public record AuthorAnalyticsReport(Integer authorId, Map<Integer, PostPerformance> postPerformance,
                                            UserBehaviorAnalysis userBehavior, List<String> writingSuggestions) {

    }

    /**
     * 文章表现数据类
     *
     * @param title Getters
     */
        public record PostPerformance(String title, LocalDateTime createdAt, long likes, long favorites, long comments,
                                      long shares, double engagementRate) {

    }

    /**
     * 用户行为分析数据类
     *
     * @param activityDistribution Getters
     */
        public record UserBehaviorAnalysis(Map<ActivityType, Long> activityDistribution, double avgInteractionTime,
                                           List<Integer> topEngagers) {

    }
}