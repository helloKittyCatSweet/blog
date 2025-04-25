package com.kitty.blog.domain.service.post;

import com.kitty.blog.application.dto.post.PostDto;
import com.kitty.blog.domain.model.Post;
import com.kitty.blog.domain.repository.post.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserStatisticsService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    /**
     * 获取用户的仪表盘统计数据
     */
    @Transactional
    @Cacheable(cacheNames = "user_dashboard_stats", key = "#userId")
    public ResponseEntity<Map<String, Object>> getDashboardStats(Integer userId) {
        if (userId == null) {
            log.warn("获取仪表盘统计数据失败：userId 为空");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> stats = new HashMap<>();
        try {
            // 检查用户是否存在
            if (postRepository.hasNoPublishedPosts(userId)) {
                stats.put("totalPosts", 0);
                stats.put("totalViews", 0);
                stats.put("totalComments", 0);
                stats.put("totalLikes", 0);
                return new ResponseEntity<>(stats, HttpStatus.OK);
            }

            // 只获取当前用户的统计数据
            stats.put("totalPosts", postRepository.countByUserId(userId));
            stats.put("totalViews", postRepository.getTotalViewsByUserId(userId));
            stats.put("totalComments", postRepository.getTotalCommentsByUserId(userId));
            stats.put("totalLikes", postRepository.getTotalLikesByUserId(userId));
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            log.error("获取用户仪表盘统计数据失败，userId: {}", userId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取用户的月度统计数据
     */
    @Transactional
    @Cacheable(cacheNames = "user_monthly_stats", key = "#userId")
    public Map<String, Object> getMonthlyStats(Integer userId) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusMonths(5).withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());

        List<Object[]> postCounts = postRepository.getMonthlyPostCountByUserId(userId, startDate, endDate);
        List<Object[]> viewCounts = postRepository.getMonthlyViewCountByUserId(userId, startDate, endDate);

        Map<Integer, Integer> postMap = new HashMap<>();
        Map<Integer, Integer> viewMap = new HashMap<>();

        // 初始化最近6个月的数据为0
        for (int i = 0; i < 6; i++) {
            int month = now.minusMonths(i).getMonthValue();
            postMap.put(month, 0);
            viewMap.put(month, 0);
        }

        // 填充实际数据
        for (Object[] row : postCounts) {
            int month = ((Number) row[0]).intValue();
            int count = ((Number) row[1]).intValue();
            postMap.put(month, count);
        }

        for (Object[] row : viewCounts) {
            int month = ((Number) row[0]).intValue();
            int views = row[1] == null ? 0 : ((Number) row[1]).intValue();
            viewMap.put(month, views);
        }

        List<String> months = postMap.keySet().stream()
                .sorted((a, b) -> Integer.compare(a, b)) // 明确指定升序排序
                .map(month -> month + "月")
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("months", months);
        result.put("posts", months.stream()
                .map(month -> postMap.get(Integer.parseInt(month.replace("月", ""))))
                .collect(Collectors.toList()));
        result.put("views", months.stream()
                .map(month -> viewMap.get(Integer.parseInt(month.replace("月", ""))))
                .collect(Collectors.toList()));

        return result;
    }

    /**
     * 获取用户的互动统计数据
     */
    @Transactional
    @Cacheable(cacheNames = "user_interaction_stats", key = "#userId")
    public ResponseEntity<Map<String, Integer>> getInteractionStats(Integer userId) {
        if (userId == null) {
            log.warn("获取互动统计数据失败：userId 为空");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Map<String, Integer> stats = new HashMap<>();
        try {
            // 检查用户是否存在
            if (postRepository.hasNoPublishedPosts(userId)) {
                stats.put("likes", 0);
                stats.put("comments", 0);
                stats.put("favorites", 0);
                return new ResponseEntity<>(stats, HttpStatus.OK);
            }

            stats.put("likes", postRepository.getTotalLikesByUserId(userId));
            stats.put("comments", postRepository.getTotalCommentsByUserId(userId));
            stats.put("favorites", postRepository.getTotalFavoritesByUserId(userId));
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            log.error("获取用户互动统计数据失败，userId: {}", userId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取用户的最近文章
     */
    @Transactional
    @Cacheable(cacheNames = "user_recent_posts", key = "#userId")
    public ResponseEntity<List<PostDto>> getRecentPosts(Integer userId) {
        if (userId == null) {
            log.warn("获取最近文章失败：userId 为空");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            List<Post> posts = postRepository.findTop5ByUserIdOrderByCreatedAtDesc(userId);
            // 如果没有文章，返回空列表
            if (posts.isEmpty()) {
                return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
            }

            List<PostDto> postDtos = posts.stream()
                    .map(postService::convertToPostDto)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(postDtos, HttpStatus.OK);
        } catch (Exception e) {
            log.error("获取用户最近文章失败，userId: {}", userId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 控制台数据首页数据渲染
     *
     * @return
     */

    @Transactional
    @Cacheable(key = "'dashboard_stats'")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("totalPosts", postRepository.count());
            stats.put("totalViews", postRepository.getTotalViews());
            stats.put("totalComments", postRepository.getTotalComments());
            stats.put("totalLikes", postRepository.getTotalLikes());
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            log.error("获取仪表盘统计数据失败", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Map<String, Object> getMonthlyStats() {
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusMonths(5).withDayOfMonth(1); // 获取6个月前的第一天
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth()); // 当月最后一天

        List<Object[]> postCounts = postRepository.getMonthlyPostCount(startDate, endDate);
        List<Object[]> viewCounts = postRepository.getMonthlyViewCount(startDate, endDate);

        Map<Integer, Integer> postMap = new HashMap<>();
        Map<Integer, Integer> viewMap = new HashMap<>();

        // 初始化最近6个月的数据为0
        for (int i = 0; i < 6; i++) {
            int month = now.minusMonths(i).getMonthValue();
            postMap.put(month, 0);
            viewMap.put(month, 0);
        }

        // 填充实际数据
        for (Object[] row : postCounts) {
            int month = ((Number) row[0]).intValue();
            int count = ((Number) row[1]).intValue();
            postMap.put(month, count);
        }

        for (Object[] row : viewCounts) {
            int month = ((Number) row[0]).intValue();
            int views = row[1] == null ? 0 : ((Number) row[1]).intValue();
            viewMap.put(month, views);
        }

        List<String> months = postMap.keySet().stream()
                .sorted()
                .map(month -> month + "月")
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("months", months);
        result.put("posts", new ArrayList<>(postMap.values()));
        result.put("views", new ArrayList<>(viewMap.values()));

        return result;
    }

    @Transactional
    @Cacheable(key = "'interaction_stats'")
    public ResponseEntity<Map<String, Integer>> getInteractionStats() {
        Map<String, Integer> stats = new HashMap<>();
        try {
            stats.put("likes", postRepository.getTotalLikes());
            stats.put("comments", postRepository.getTotalComments());
            stats.put("favorites", postRepository.getTotalFavorites());
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            log.error("获取互动统计数据失败", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<List<PostDto>> getRecentPosts() {
        List<Post> posts = postRepository.findTop5ByOrderByCreatedAtDesc();
        List<PostDto> postDtos = posts.stream()
                .map(postService::convertToPostDto)
                .collect(Collectors.toList());

        return new ResponseEntity<>(postDtos, HttpStatus.OK);
    }
}