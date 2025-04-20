package com.kitty.blog.domain.service.post.postAnalysis.author;

import com.kitty.blog.domain.model.Post;
import com.kitty.blog.common.constant.ActivityType;
import com.kitty.blog.domain.model.UserActivity;
import com.kitty.blog.domain.repository.post.PostRepository;
import com.kitty.blog.domain.repository.UserActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthorAnalyticsService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private CacheManager cacheManager;

    @Value("${analytics.cache.ttl:3600}")
    private int cacheTTL;

    /**
     * 生成作者分析报告
     */
    @Cacheable(value = "authorAnalytics", key = "#authorId")
    public AuthorAnalyticsReport generateReport(Integer authorId) {
        try {
            // 1. 批量获取作者文章及其活动数据
            List<Post> authorPosts = postRepository.findByUserId(authorId).orElse(Collections.emptyList());
            List<Integer> postIds = authorPosts.stream().map(Post::getPostId).toList();

            // 2. 单次查询所有相关活动
            Map<Integer, List<UserActivity>> activitiesByPost = userActivityRepository
                    .findByPostIdIn(postIds)
                    .stream()
                    .collect(Collectors.groupingBy(UserActivity::getPostId));

            // 3. 并行处理文章分析
            Map<Integer, PostPerformance> postPerformance = authorPosts.parallelStream()
                    .collect(Collectors.toMap(
                            Post::getPostId,
                            post -> analyzePostPerformance(
                                    post,
                                    activitiesByPost.getOrDefault(post.getPostId(), Collections.emptyList())
                            )));

            // 4. 分析用户行为
            UserBehaviorAnalysis userBehavior = analyzeUserBehavior(activitiesByPost.get(postIds));

            // 5. 生成写作建议
            List<String> writingSuggestions = generateWritingSuggestions(postPerformance, userBehavior);

            return new AuthorAnalyticsReport(
                    authorId,
                    postPerformance,
                    userBehavior,
                    writingSuggestions);
        } catch (Exception e) {
            log.error("生成作者{}的分析报告失败: {}", authorId, e.getMessage(), e);
            return new AuthorAnalyticsReport(authorId, new HashMap<>(),
                    new UserBehaviorAnalysis(new HashMap<>(), 0.0, new ArrayList<>()),
                    Collections.singletonList("生成分析报告时发生错误，请稍后重试"));
        }
    }

    /**
     * 分析文章表现
     */
    private PostPerformance analyzePostPerformance(
            Post post,
            List<UserActivity> activities) {

        List<UserActivity> postActivities = activities.stream()
                .filter(a -> a.getPostId().equals(post.getPostId()))
                .collect(Collectors.toList());

        // 计算互动指标
        long likes = countActivities(postActivities, ActivityType.LIKE);
        long favorites = countActivities(postActivities, ActivityType.FAVORITE);
        long comments = countActivities(postActivities, ActivityType.COMMENT);
        long likeComments = countActivities(postActivities, ActivityType.LIKE_COMMENT);

        // 计算参与度
        double engagementRate = calculateEngagementRate(post, postActivities);

        return new PostPerformance(
                post.getTitle(),
                post.getCreatedAt().atStartOfDay(),
                likes,
                favorites,
                comments,
                likeComments,
                engagementRate);


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

    private void analyzeContentPopularity(
            Map<Integer, PostPerformance> postPerformance,
            List<String> suggestions) {

        // 分析标题长度与互动率的关系
        analyzeTitleLength(postPerformance, suggestions);

        // 分析文章发布时间间隔
        analyzePostingFrequency(postPerformance, suggestions);

        // 现有的最佳表现分析
        Optional<PostPerformance> bestPerforming = postPerformance.values().stream()
                .max(Comparator.comparingDouble(PostPerformance::engagementRate));

        bestPerforming.ifPresent(performance -> {
            suggestions.add(String.format(
                    "文章《%s》的互动率最高（%.2f%%），建议参考其写作风格和主题",
                    performance.title(),
                    performance.engagementRate() * 100));
        });
    }

    private void analyzeTitleLength(
            Map<Integer, PostPerformance> postPerformance,
            List<String> suggestions) {
        // 分析标题长度与互动率的关系
        Map<Integer, Double> titleLengthEngagement = new HashMap<>();
        for (PostPerformance performance : postPerformance.values()) {
            int titleLength = performance.title().length();
            titleLengthEngagement.merge(titleLength, performance.engagementRate(), Double::sum);
        }

        OptionalDouble avgBestLength = titleLengthEngagement.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(3)
                .mapToInt(Map.Entry::getKey)
                .average();

        avgBestLength.ifPresent(length ->
                suggestions.add(String.format("建议标题长度保持在%d个字左右，这个长度的互动率较高", (int) length)));
    }

    private void analyzePostingFrequency(
            Map<Integer, PostPerformance> postPerformance,
            List<String> suggestions) {
        // 分析发文频率
        List<LocalDateTime> postDates = postPerformance.values().stream()
                .map(PostPerformance::createdAt)
                .sorted()
                .toList();

        if (postDates.size() >= 2) {
            long totalDays = ChronoUnit.DAYS.between(
                    postDates.get(0),
                    postDates.get(postDates.size() - 1));
            double avgDays = (double) totalDays / (postDates.size() - 1);

            suggestions.add(String.format("您平均每%.1f天发布一篇文章，保持稳定的更新频率有助于维持读者粘性", avgDays));
        }
    }

    private void provideImprovementSuggestions(
            Map<Integer, PostPerformance> postPerformance,
            UserBehaviorAnalysis userBehavior,
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