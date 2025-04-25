package com.kitty.blog.domain.service.post.postAnalysis.author;

import com.kitty.blog.common.constant.ActivityType;
import com.kitty.blog.domain.model.User;
import com.kitty.blog.domain.model.UserActivity;
import com.kitty.blog.domain.repository.UserRepository;
import com.kitty.blog.domain.repository.post.PostRepository;
import com.kitty.blog.domain.service.post.postAnalysis.author.AuthorAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserBehaviorAnalyzer {

        @Autowired
        private UserRepository userRepository;
        @Autowired
        private PostRepository postRepository;

        /**
         * 分析用户行为
         */
        public AuthorAnalyticsService.UserBehaviorAnalysis analyzeUserBehavior(List<UserActivity> activities) {
                // 防止空指针
                if (activities == null || activities.isEmpty()) {
                        return new AuthorAnalyticsService.UserBehaviorAnalysis(
                                        new HashMap<>(),
                                        0.0,
                                        new ArrayList<>(),
                                        new ArrayList<>());
                }

                // 统计活动类型分布
                Map<ActivityType, Long> activityDistribution = activities.stream()
                                .collect(Collectors.groupingBy(
                                                activity -> ActivityType
                                                                .valueOf(activity.getActivityType().toUpperCase()),
                                                Collectors.counting()));

                // 计算平均互动时间
                double avgInteractionTime = calculateAverageInteractionTime(activities);

                // 识别最活跃的用户
                List<Integer> topEngagers = identifyTopEngagers(activities);

                return new AuthorAnalyticsService.UserBehaviorAnalysis(
                                activityDistribution,
                                avgInteractionTime,
                                topEngagers,
                                userRepository.findByUserIdIn(topEngagers).stream().map(User::getUsername).toList());
        }

        private double calculateAverageInteractionTime(List<UserActivity> activities) {
                double avgTime = activities.stream()
                                .mapToLong(activity -> {
                                        LocalDateTime postDate = postRepository.findById(activity.getPostId())
                                                        .map(post -> post.getCreatedAt().atStartOfDay())
                                                        .orElse(LocalDateTime.now());
                                        LocalDateTime activityDate = activity.getCreatedAt().atStartOfDay();
                                        return ChronoUnit.HOURS.between(postDate, activityDate);
                                })
                                .average()
                                .orElse(0.0);
                // 使用 Math.round 保留两位小数
                return Math.round(avgTime * 100.0) / 100.0;
        }

        private List<Integer> identifyTopEngagers(List<UserActivity> activities) {
                return activities.stream()
                                .collect(Collectors.groupingBy(
                                                UserActivity::getUserId,
                                                Collectors.counting()))
                                .entrySet().stream()
                                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                                .limit(3)
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toList());
        }
}