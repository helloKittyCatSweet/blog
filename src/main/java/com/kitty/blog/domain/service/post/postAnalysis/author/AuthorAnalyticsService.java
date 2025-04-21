package com.kitty.blog.domain.service.post.postAnalysis.author;

import com.kitty.blog.domain.model.Post;
import com.kitty.blog.common.constant.ActivityType;
import com.kitty.blog.domain.model.User;
import com.kitty.blog.domain.model.UserActivity;
import com.kitty.blog.domain.repository.UserRepository;
import com.kitty.blog.domain.repository.post.PostRepository;
import com.kitty.blog.domain.repository.UserActivityRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private UserRepository userRepository;

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
            // 检查作者是否有文章
            if (authorPosts.isEmpty()) {
                return AuthorAnalyticsReport.createEmptyReport(authorId);
            }

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
                                    activitiesByPost.getOrDefault(post.getPostId(), Collections.emptyList()))));

            // 4. 分析用户行为 - 修复这里的空指针问题
            List<UserActivity> allActivities = activitiesByPost.values().stream()
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            UserBehaviorAnalysis userBehavior = analyzeUserBehavior(allActivities);

            // 5. 生成写作建议
            List<String> writingSuggestions = generateWritingSuggestions(postPerformance, userBehavior);

            return new AuthorAnalyticsReport(
                    authorId,
                    userBehavior,
                    writingSuggestions);
        } catch (Exception e) {
            log.error("生成作者{}的分析报告失败: {}", authorId, e.getMessage(), e);
            return AuthorAnalyticsReport.createEmptyReport(authorId);
        }
    }

    /**
     * 分析文章表现
     */
    private PostPerformance analyzePostPerformance(
            Post post,
            List<UserActivity> activities) {

        // 过滤出当前文章的活动数据
        List<UserActivity> postActivities = activities.stream()
                .filter(a -> a.getPostId().equals(post.getPostId()))
                .toList();

        // 使用过滤后的活动数据计算互动指标
        long views = post.getViews();
        long likes = post.getLikes();
        long favorites = post.getFavorites();
        long comments = countActivities(postActivities, ActivityType.COMMENT);
        long replies = countActivities(postActivities, ActivityType.REPLY);
        long commentLikes = countActivities(postActivities, ActivityType.LIKE_COMMENT);

        // 计算综合参与度
        double engagementRate = calculateEngagementRate(views, likes, favorites, comments, replies, commentLikes);

        return new PostPerformance(
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt().atStartOfDay(),
                views,
                likes,
                favorites,
                comments,
                replies,
                commentLikes,
                engagementRate);
    }

    private double calculateEngagementRate(long views, long likes, long favorites,
            long comments, long replies, long commentLikes) {
        if (views == 0)
            return 0.0;

        // 设置不同互动类型的权重
        double weightedInteractions = likes * 1.0 +
                favorites * 2.0 +
                comments * 3.0 +
                replies * 2.0 +
                commentLikes * 0.5;

        return weightedInteractions / views;
    }

    /**
     * 分析用户行为
     */
    private UserBehaviorAnalysis analyzeUserBehavior(List<UserActivity> activities) {
        // 防止空指针
        if (activities == null || activities.isEmpty()) {
            return new UserBehaviorAnalysis(
                    new HashMap<>(),
                    0.0,
                    new ArrayList<>(),
                    new ArrayList<>());
        }

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
                topEngagers,
                userRepository.findByUserIdIn(topEngagers).stream().map(User::getUsername).toList());
    }

    /**
     * 生成写作建议
     */
    private List<String> generateWritingSuggestions(
            Map<Integer, PostPerformance> postPerformance,
            UserBehaviorAnalysis userBehavior) {

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

    /**
     * 分析最佳发布时间
     */
    private void analyzeBestPostingTime(
            Map<Integer, PostPerformance> postPerformance,
            List<String> suggestions) {
        // 按小时统计互动率
        Map<Integer, List<Double>> hourlyEngagement = new HashMap<>();
        for (PostPerformance performance : postPerformance.values()) {
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
            UserBehaviorAnalysis userBehavior,
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
            Map<Integer, PostPerformance> postPerformance,
            List<String> suggestions) {
        // 根据文章内容长度分组分析
        Map<String, List<Double>> lengthRangeEngagement = new HashMap<>();
        lengthRangeEngagement.put("短文(1000字以下)", new ArrayList<>());
        lengthRangeEngagement.put("中等(1000-3000字)", new ArrayList<>());
        lengthRangeEngagement.put("长文(3000字以上)", new ArrayList<>());

        // 统计每个长度范围的互动率
        for (PostPerformance performance : postPerformance.values()) {
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
     * 计算平均互动时间
     */
    private double calculateAverageInteractionTime(List<UserActivity> activities) {
        return activities.stream()
                .mapToLong(activity -> {
                    LocalDateTime postDate = postRepository.findById(activity.getPostId())
                            .get()
                            .getCreatedAt()
                            .atStartOfDay();
                    LocalDateTime activityDate = activity.getCreatedAt().atStartOfDay();
                    // 计算时间差（小时）
                    return ChronoUnit.HOURS.between(postDate, activityDate);
                })
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
    public record AuthorAnalyticsReport(
            Integer authorId,
            UserBehaviorAnalysis userBehavior,
            List<String> writingSuggestions) {
        // 提供一个静态工厂方法用于创建空报告
        public static AuthorAnalyticsReport createEmptyReport(Integer authorId) {
            return new AuthorAnalyticsReport(authorId,
                    new UserBehaviorAnalysis(
                            new HashMap<>(),
                            0.0,
                            new ArrayList<>(),
                            new ArrayList<>()),
                    Collections.singletonList("您还没有发表任何文章，开始创作第一篇文章吧!"));
        }

    }

    /**
     * 文章表现数据类
     *
     * @param title Getters
     */
    public record PostPerformance(
            String title,
            String content,
            LocalDateTime createdAt,
            long views,
            long likes,
            long favorites,
            long comments,
            long replies,
            long commentLikes,
            double engagementRate) {
    }

    /**
     * 用户行为分析数据类
     *
     * @param activityDistribution Getters
     */
    public record UserBehaviorAnalysis(
            Map<ActivityType, Long> activityDistribution,
            double avgInteractionTime,
            List<Integer> topEngagers,
            List<String> topEngagerNames) {

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

        avgBestLength.ifPresent(length -> suggestions.add(String.format("建议标题长度保持在%d个字左右，这个长度的互动率较高",
                (int) length)));
    }

    private void analyzePostingFrequency(
            Map<Integer, PostPerformance> postPerformance,
            List<String> suggestions) {
        List<LocalDateTime> postDates = postPerformance.values().stream()
                .map(PostPerformance::createdAt)
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

        peakHour.ifPresent(hour -> suggestions.add(String.format("读者在%d:00-%d:00活跃度最高，建议在此时间段发布文章或与读者互动",
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