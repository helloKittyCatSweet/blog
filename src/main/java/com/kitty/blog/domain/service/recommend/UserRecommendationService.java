package com.kitty.blog.domain.service.recommend;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.kitty.blog.domain.model.User;
import com.kitty.blog.domain.model.UserFollow;
import com.kitty.blog.domain.model.Favorite;
import com.kitty.blog.domain.model.tag.Tag;
import com.kitty.blog.domain.repository.FavoriteRepository;
import com.kitty.blog.domain.repository.UserFollowRepository;
import com.kitty.blog.domain.repository.tag.TagRepository;
import com.kitty.blog.domain.service.post.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserRecommendationService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private UserFollowRepository userFollowRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private TagRepository tagRepository;

    public List<User> recommendUsers(User currentUser, int limit) {
        // 构建多维度评分
        Map<Integer, Double> userScores = new HashMap<>();

        // 1. 邮箱后缀相似度评分 (权重: 0.2)
        String currentUserEmailDomain = currentUser.getEmail().split("@")[1];
        searchSimilarEmailDomainUsers(currentUser.getUserId(), currentUserEmailDomain)
                .forEach((userId, score) -> userScores.merge(userId, score * 0.2, Double::sum));

        // 2. IP地理位置相似度评分 (权重: 0.3)
        String currentUserLocation = currentUser.getLastLoginLocation();
        searchNearbyUsers(currentUser.getUserId(), currentUserLocation)
                .forEach((userId, score) -> userScores.merge(userId, score * 0.3, Double::sum));

        // 3. 关注关系重叠度评分 (权重: 0.25)
        calculateFollowOverlapScore(currentUser.getUserId())
                .forEach((userId, score) -> userScores.merge(userId, score * 0.25, Double::sum));

        // 4. 收藏内容相似度评分 (权重: 0.25)
        calculateFavoritesSimilarityScore(currentUser.getUserId())
                .forEach((userId, score) -> userScores.merge(userId, score * 0.25, Double::sum));

        // 排除当前用户和已关注的用户
        List<Integer> followingIds = userFollowRepository.findByFollowerId(currentUser.getUserId())
                .stream()
                .map(UserFollow::getFollowingId)
                .toList();

        // 根据评分排序并返回推荐用户
        return userScores.entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                // 过滤掉已关注的用户
                .filter(entry -> !followingIds.contains(entry.getKey()))
                // 过滤掉当前用户
                .filter(entry -> !entry.getKey().equals(currentUser.getUserId()))
                .limit(limit)
                .map(entry -> searchUserById(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    private Map<Integer, Double> searchSimilarEmailDomainUsers(Integer currentUserId, String emailDomain) {
        try {
            SearchResponse<User> response = elasticsearchClient.search(s -> s
                            .index("users")
                            .query(q -> q
                                    .wildcard(w -> w
                                            .field("email")
                                            .wildcard("*@" + emailDomain)
                                    )
                            ),
                    User.class
            );

            Map<Integer, Double> scores = new HashMap<>();
            response.hits().hits().forEach(hit -> {
                Integer userId = hit.source().getUserId();
                if (!userId.equals(currentUserId)) {
                    scores.put(userId, hit.score());
                }
            });
            return scores;
        } catch (Exception e) {
            log.error("搜索相似邮箱域名用户失败", e);
            return Collections.emptyMap();
        }
    }

    private Map<Integer, Double> searchNearbyUsers(Integer currentUserId, String location) {
        try {
            SearchResponse<User> response = elasticsearchClient.search(s -> s
                            .index("users")
                            .query(q -> q
                                    .match(m -> m
                                            .field("lastLoginLocation")
                                            .query(location)
                                    )
                            ),
                    User.class
            );

            Map<Integer, Double> scores = new HashMap<>();
            response.hits().hits().forEach(hit -> {
                Integer userId = hit.source().getUserId();
                if (!userId.equals(currentUserId)) {
                    scores.put(userId, hit.score());
                }
            });
            return scores;
        } catch (Exception e) {
            log.error("搜索附近用户失败", e);
            return Collections.emptyMap();
        }
    }

    private Map<Integer, Double> calculateFollowOverlapScore(Integer currentUserId) {
        // 获取当前用户的关注列表
        Set<Integer> currentUserFollowing = userFollowRepository.findByFollowerId(currentUserId)
                .stream()
                .map(UserFollow::getFollowingId)
                .collect(Collectors.toSet());

        // 获取所有用户的关注列表并计算重叠度
        Map<Integer, Double> overlapScores = new HashMap<>();
        userFollowRepository.findAll().stream()
                .collect(Collectors.groupingBy(UserFollow::getFollowerId))
                .forEach((userId, follows) -> {
                    if (!userId.equals(currentUserId)) {
                        Set<Integer> userFollowing = follows.stream()
                                .map(UserFollow::getFollowingId)
                                .collect(Collectors.toSet());

                        // 计算Jaccard相似度
                        Set<Integer> intersection = new HashSet<>(currentUserFollowing);
                        intersection.retainAll(userFollowing);
                        Set<Integer> union = new HashSet<>(currentUserFollowing);
                        union.addAll(userFollowing);

                        double similarity = union.isEmpty() ? 0 : (double) intersection.size() / union.size();
                        overlapScores.put(userId, similarity);
                    }
                });
        return overlapScores;
    }

    private Map<Integer, Double> calculateFavoritesSimilarityScore(Integer currentUserId) {
        // 获取当前用户的收藏
        List<Favorite> currentUserFavorites = favoriteRepository.
                findByUserId(currentUserId).orElse(new ArrayList<>());
        Set<String> currentUserTags = currentUserFavorites.stream()
                .map(favorite -> tagRepository.findByPostId(favorite.getPostId()).get())
                .flatMap(List::stream)
                .map(Tag::getName)
                .collect(Collectors.toSet());

        // 计算与其他用户的收藏相似度
        Map<Integer, Double> similarityScores = new HashMap<>();
        favoriteRepository.findAll().stream()
                .collect(Collectors.groupingBy(Favorite::getUserId))
                .forEach((userId, favorites) -> {
                    if (!userId.equals(currentUserId)) {
                        Set<String> userTags = favorites.stream()
                                .map(favorite -> tagRepository.findByPostId(favorite.getPostId()).get())
                                .flatMap(List::stream)
                                .map(Tag::getName)
                                .collect(Collectors.toSet());
                        // 计算标签的余弦相似度
                        double similarity = calculateCosineSimilarity(currentUserTags, userTags);
                        similarityScores.put(userId, similarity);
                    }
                });
        return similarityScores;
    }

    private double calculateCosineSimilarity(Set<String> tags1, Set<String> tags2) {
        Set<String> intersection = new HashSet<>(tags1);
        intersection.retainAll(tags2);
        double numerator = intersection.size();
        double denominator = Math.sqrt(tags1.size()) * Math.sqrt(tags2.size());
        return denominator == 0 ? 0 : numerator / denominator;
    }

    private User searchUserById(Integer userId) {
        try {
            SearchResponse<User> response = elasticsearchClient.search(s -> s
                            .index("users")
                            .query(q -> q
                                    .term(t -> t
                                            .field("userId")
                                            .value(userId)
                                    )
                            ),
                    User.class
            );

            return response.hits().hits().stream()
                    .findFirst()
                    .map(Hit::source)
                    .orElse(null);
        } catch (Exception e) {
            log.error("搜索用户失败: {}", userId, e);
            return null;
        }
    }
}