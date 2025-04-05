package com.kitty.blog.application.service.post.postAnalysis.reader;

import com.kitty.blog.model.Post;

import com.kitty.blog.constant.ActivityType;
import com.kitty.blog.model.UserActivity;
import com.kitty.blog.repository.CategoryRepository;
import com.kitty.blog.repository.PostRepository;
import com.kitty.blog.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * 基于用户行为推荐文章
     * 
     * @param userId 用户ID
     * @param limit  推荐数量
     * @return 推荐的文章列表
     */
    public List<Post> recommendPosts(Integer userId, int limit) {
        // 1. 获取用户的历史行为
        List<UserActivity> userActivities = userActivityRepository.findByUserId(userId).orElse(new ArrayList<>());

        // 2. 分析用户兴趣
        Map<Integer, Double> categoryScores = analyzeUserInterests(userActivities);

        // 3. 获取用户已读文章
        Set<Integer> readPostIds = userActivities.stream()
                .map(UserActivity::getPostId)
                .collect(Collectors.toSet());

        // 4. 根据兴趣分数推荐文章
        return recommendPostsByInterests(categoryScores, readPostIds, limit);
    }

    /**
     * 分析用户兴趣
     */
    private Map<Integer, Double> analyzeUserInterests(List<UserActivity> activities) {
        Map<Integer, Double> categoryScores = new HashMap<>();

        for (UserActivity activity : activities) {
            if (postRepository.existsById(activity.getPostId())){
                Post post = postRepository.findById(activity.getPostId()).orElse(null);
                assert post != null;
                Integer categoryId = categoryRepository.findByPostId(post.getPostId()).orElse(0);
                double score = calculateActivityScore(activity);
                categoryScores.merge(categoryId, score, Double::sum);

            }
        }

        return categoryScores;
    }

    /**
     * 计算活动权重分数
     */
    private double calculateActivityScore(UserActivity activity) {
        // 如果字符串是小写的，转换为大写
        String activityType = activity.getActivityType().toUpperCase();
        switch (ActivityType.valueOf(activityType)) {
            case LIKE:
                return 1.0;
            case FAVORITE:
                return 2.0;
            case COMMENT:
                return 1.5;
            case SHARE:
                return 1.8;
            default:
                return 0.5;
        }
    }

    /**
     * 根据兴趣分数推荐文章
     */
    private List<Post> recommendPostsByInterests(
            Map<Integer, Double> categoryScores,
            Set<Integer> readPostIds,
            int limit) {

        // 1. 获取所有文章
        List<Post> allPosts = postRepository.findAll();

        // 2. 计算每篇文章的推荐分数
        Map<Post, Double> postScores = new HashMap<>();
        for (Post post : allPosts) {
            Integer categoryId = categoryRepository.findByPostId(post.getPostId()).orElse(0);
            if (!readPostIds.contains(post.getPostId()) && categoryId!= 0) {
                Double categoryScore = categoryScores.getOrDefault(categoryId, 0.0);

                // 考虑文章的其他因素
                double finalScore = calculatePostScore(post, categoryScore);
                postScores.put(post, finalScore);
            }
        }

        // 3. 按分数排序并返回推荐结果
        return postScores.entrySet().stream()
                .sorted(Map.Entry.<Post, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 计算文章推荐分数
     */
    private double calculatePostScore(Post post, double categoryScore) {
        // 基础分数：类别匹配度
        double score = categoryScore;

        // 考虑文章的其他因素
        if (post.getLikes() != null) {
            score += post.getLikes() * 0.1; // 点赞数权重
        }
        if (post.getViews() != null) {
            score += post.getViews() * 0.05; // 浏览量权重
        }
        if (post.getCreatedAt() != null) {
            // 时间衰减因子
            long daysOld = java.time.temporal.ChronoUnit.DAYS.between(
                    post.getCreatedAt(), java.time.LocalDate.now());
            score *= Math.exp(-daysOld / 30.0); // 30天半衰期
        }

        return score;
    }

    /**
     * 获取热门文章推荐
     */
    public List<Post> getHotPosts(int limit) {
        return postRepository.findAll().stream()
                .sorted(Comparator
                        .comparing(Post::getLikes, Comparator.nullsLast(Integer::compareTo))
                        .thenComparing(Post::getViews, Comparator.nullsLast(Integer::compareTo))
                        .reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}