package com.kitty.blog.domain.service.post.postAnalysis.author;

import com.kitty.blog.common.constant.ActivityType;
import com.kitty.blog.domain.model.Post;
import com.kitty.blog.domain.model.UserActivity;
import com.kitty.blog.domain.repository.UserActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostAnalyzer {

    @Autowired
    private AnalyticsProperties properties;

    @Autowired
    private UserActivityRepository userActivityRepository;

    public Map<Integer, AuthorAnalyticsService.PostPerformance> analyzePostsPerformance(
            List<Post> posts,
            List<UserActivity> activities) {
        return posts.parallelStream()
                .collect(Collectors.toMap(
                        Post::getPostId,
                        post -> analyzePostPerformance(post,
                                activities)));
    }

    /**
     * 分析文章表现
     */
    private AuthorAnalyticsService.PostPerformance analyzePostPerformance(
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
        double engagementRate = calculateEngagementRate
                (views, likes, favorites, comments, replies, commentLikes);

        return new AuthorAnalyticsService.PostPerformance(
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
     * 统计特定类型的活动数量
     */
    private long countActivities(List<UserActivity> activities, ActivityType type) {
        return activities.stream()
                .filter(a ->
                        ActivityType.valueOf(a.getActivityType().toUpperCase()) == type)
                .count();
    }
}