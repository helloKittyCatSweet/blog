package com.kitty.blog.domain.service.post.postAnalysis.author;

import com.kitty.blog.common.exception.AnalyticsGenerationException;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
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
    private PostAnalyzer postAnalyzer;

    @Autowired
    private UserBehaviorAnalyzer userBehaviorAnalyzer;

    @Autowired
    private SuggestionGenerator suggestionGenerator;

    @Autowired
    private AnalyticsProperties analyticsProperties;

    @Autowired
    private CacheManager cacheManager;

    @Value("${analytics.cache.ttl:3600}")
    private int cacheTTL;

    @Value("${analytics.timeout.seconds:5}")
    private int timeoutSeconds;

    @Autowired
    private ThreadPoolExecutor analyticsExecutor;

    /**
     * 生成作者分析报告
     */
    @Cacheable(value = "authorAnalytics", key = "#authorId")
    public AuthorAnalyticsReport generateReport(Integer authorId) {
        try {
            // 并行获取数据
            CompletableFuture<List<Post>> postsFuture = CompletableFuture.supplyAsync(
                    () -> postRepository.findByUserId(authorId)
                            .orElse(Collections.emptyList()),
                    analyticsExecutor);

            CompletableFuture<List<UserActivity>> activitiesFuture = CompletableFuture.supplyAsync(
                    () -> {
                        List<Post> posts = postRepository.findByUserId(authorId)
                                .orElse(Collections.emptyList());
                        List<Integer> postIds = posts.stream()
                                .map(Post::getPostId)
                                .collect(Collectors.toList());
                        return userActivityRepository.findByPostIdIn(postIds)
                                .orElse(Collections.emptyList());
                    },
                    analyticsExecutor);

            // 等待数据获取完成
            List<Post> authorPosts = getDataWithTimeout(postsFuture, "获取作者文章");
            if (authorPosts.isEmpty()) {
                return AuthorAnalyticsReport.createEmptyReport(authorId);
            }

            List<UserActivity> authorActivities = getDataWithTimeout(activitiesFuture, "获取用户活动");

            // 分析处理
            Map<Integer, PostPerformance> postPerformance = postAnalyzer.analyzePostsPerformance(
                    authorPosts,
                    authorActivities);

            UserBehaviorAnalysis userBehavior = userBehaviorAnalyzer.analyzeUserBehavior(
                    authorActivities);

            List<String> suggestions = suggestionGenerator.generateWritingSuggestions(
                    postPerformance,
                    userBehavior);

            return new AuthorAnalyticsReport(
                    authorId,
                    userBehavior,
                    suggestions);

        } catch (Exception e) {
            log.error("生成作者{}的分析报告失败", authorId, e);
            throw new AnalyticsGenerationException("生成分析报告失败", e);
        }
    }

    private <T> T getDataWithTimeout(CompletableFuture<T> future, String operation) {
        try {
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("{}超时或失败", operation, e);
            throw new AnalyticsGenerationException(operation + "失败", e);
        }
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
}