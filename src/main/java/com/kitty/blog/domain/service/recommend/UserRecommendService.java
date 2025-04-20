package com.kitty.blog.domain.service.recommend;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.domain.model.User;
import com.kitty.blog.domain.model.UserFollow;
import com.kitty.blog.domain.model.Favorite;
import com.kitty.blog.domain.model.tag.Tag;
import com.kitty.blog.domain.repository.FavoriteRepository;
import com.kitty.blog.domain.repository.UserFollowRepository;
import com.kitty.blog.domain.repository.UserRepository;
import com.kitty.blog.domain.repository.tag.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserRecommendService {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private UserFollowRepository userFollowRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserTagSimilarityService userTagSimilarityService;

    private double calculateJaccardSimilarity(Set<?> set1, Set<?> set2) {
        if (set1.isEmpty() && set2.isEmpty()) return 0.0;

        Set<Object> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);

        Set<Object> union = new HashSet<>(set1);
        union.addAll(new HashSet<>(set2));

        return (double) intersection.size() / union.size();
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
                .filter(follow -> !userRepository.findById(follow.getFollowerId())
                        .map(User::isDeleted)
                        .orElse(true))
                .collect(Collectors.groupingBy(UserFollow::getFollowerId))
                .forEach((userId, follows) -> {
                    if (!userId.equals(currentUserId)) {
                        Set<Integer> userFollowing = follows.stream()
                                .map(UserFollow::getFollowingId)
                                .collect(Collectors.toSet());

                        // 使用统一的 Jaccard 相似度计算方法
                        double similarity = calculateJaccardSimilarity(currentUserFollowing, userFollowing);
                        overlapScores.put(userId, similarity);
                    }
                });
        return overlapScores;
    }

    private Map<Integer, Double> calculateFavoritesSimilarityScore(Integer currentUserId) {
        Set<String> currentUserTags = getUserFavoriteTags(currentUserId);

        Map<Integer, Double> similarityScores = new HashMap<>();
        favoriteRepository.findAll().stream()
                .filter(favorite -> !userRepository.findById(favorite.getUserId())
                        .map(User::isDeleted)
                        .orElse(true))
                .collect(Collectors.groupingBy(Favorite::getUserId))
                .forEach((userId, favorites) -> {
                    if (!userId.equals(currentUserId)) {
                        Set<String> userTags = favorites.stream()
                                .map(favorite -> tagRepository.findByPostId(favorite.getPostId())
                                        .orElse(new ArrayList<>()))
                                .flatMap(List::stream)
                                .map(Tag::getName)
                                .collect(Collectors.toSet());

                        double similarity = userTagSimilarityService
                                .calculateTagSetSimilarity(currentUserTags, userTags);
                        similarityScores.put(userId, similarity);
                    }
                });
        return similarityScores;
    }

    public List<User> recommendUsers(LoginResponseDto loginInfo, int k) {
        User currentUser = convertLoginToUser(loginInfo);

        // 使用已有的搜索函数获取初始候选用户
        Map<Integer, Double> emailScores = searchSimilarEmailDomainUsers(
                currentUser.getUserId(),
                currentUser.getEmail().split("@")[1]
        );

        Map<Integer, Double> locationScores = searchNearbyUsers(
                currentUser.getUserId(),
                currentUser.getLastLoginLocation()
        );

        // 获取已关注用户ID列表
        List<Integer> followingIds = userFollowRepository.findByFollowerId(currentUser.getUserId())
                .stream()
                .map(UserFollow::getFollowingId)
                .toList();

        // 合并所有候选用户并计算最终得分
        Map<Integer, Double> finalScores = new HashMap<>();

        // 合并邮箱和位置得分
        emailScores.forEach((userId, score) ->
                finalScores.merge(userId, score * 0.2, Double::sum));

        locationScores.forEach((userId, score) ->
                finalScores.merge(userId, score * 0.3, Double::sum));

        // 添加关注和收藏相似度得分
        calculateFollowOverlapScore(currentUser.getUserId())
                .forEach((userId, score) ->
                        finalScores.merge(userId, score * 0.25, Double::sum));

        calculateFavoritesSimilarityScore(currentUser.getUserId())
                .forEach((userId, score) ->
                        finalScores.merge(userId, score * 0.25, Double::sum));

        // 过滤和排序
        List<User> recommendedUsers = finalScores.entrySet().stream()
                .filter(entry -> !entry.getKey().equals(currentUser.getUserId()))
                .filter(entry -> !followingIds.contains(entry.getKey()))
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .map(entry -> searchUserById(entry.getKey()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 如果要求的数量大于可推荐的用户总数，返回所有可推荐用户
        return k >= recommendedUsers.size() ?
                recommendedUsers :
                recommendedUsers.subList(0, k);
    }

    private Set<String> getUserFavoriteTags(Integer userId) {
        return favoriteRepository.findByUserId(userId)
                .orElse(new ArrayList<>())
                .stream()
                .map(favorite -> tagRepository.findByPostId(favorite.getPostId()).orElse(new ArrayList<>()))
                .flatMap(List::stream)
                .map(Tag::getName)
                .collect(Collectors.toSet());
    }

    private User convertLoginToUser(LoginResponseDto loginInfo) {
        return userRepository.findById(loginInfo.getId()).orElse(new User());
    }

    private Map<Integer, Double> searchSimilarEmailDomainUsers(Integer currentUserId, String emailDomain) {
        try {
            SearchResponse<User> response = elasticsearchClient.search(s -> s
                            .index("users")
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .wildcard(w -> w
                                                            .field("email")
                                                            .wildcard("*@" + emailDomain)
                                                    )
                                            )
                                            .mustNot(mn -> mn
                                                    .term(t -> t
                                                            .field("isDeleted")
                                                            .value(true)
                                                    )
                                            )
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
            User currentUser = userRepository.findById(currentUserId).orElseThrow();
            LocationInfo currentLocationInfo = parseLocation(location);

            SearchResponse<User> response = elasticsearchClient.search(s -> s
                            .index("users")
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .exists(e -> e
                                                            .field("lastLoginLocation")
                                                    )
                                            )
                                            .mustNot(mn -> mn
                                                    .term(t -> t
                                                            .field("isDeleted")
                                                            .value(true)
                                                    )
                                            )
                                    )
                            ),
                    User.class
            );

            Map<Integer, Double> scores = new HashMap<>();
            response.hits().hits().forEach(hit -> {
                User user = hit.source();
                if (user != null && !user.getUserId().equals(currentUserId)) {
                    LocationInfo userLocation = parseLocation(user.getLastLoginLocation());
                    double score = calculateLocationSimilarity(currentLocationInfo, userLocation);
                    scores.put(user.getUserId(), score);
                }
            });
            return scores;
        } catch (Exception e) {
            log.error("搜索附近用户失败", e);
            return Collections.emptyMap();
        }
    }

    private static class LocationInfo {
        String country;
        String province;
        String city;
        String isp;
    }

    private LocationInfo parseLocation(String location) {
        LocationInfo info = new LocationInfo();
        if (location == null || location.isEmpty()) {
            return info;
        }

        String[] parts = location.split(" ");
        if (parts.length >= 1) info.country = parts[0];
        if (parts.length >= 2) info.province = parts[1];
        if (parts.length >= 3) info.city = parts[2];
        if (parts.length >= 4) info.isp = parts[3];

        return info;
    }

    private double calculateLocationSimilarity(LocationInfo loc1, LocationInfo loc2) {
        if (loc1 == null || loc2 == null) return 0.0;

        double score = 0.0;

        // 如果在同一个国家
        if (loc1.country != null && loc1.country.equals(loc2.country)) {
            score += 0.3;

            // 如果在同一个省份
            if (loc1.province != null && loc1.province.equals(loc2.province)) {
                score += 0.4;

                // 如果在同一个城市
                if (loc1.city != null && loc1.city.equals(loc2.city)) {
                    score += 0.3;
                }
            }
        }

        return score;
    }

    private User searchUserById(Integer userId) {
        try {
            SearchResponse<User> response = elasticsearchClient.search(s -> s
                            .index("users")
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .term(t -> t
                                                            .field("userId")
                                                            .value(userId)
                                                    )
                                            )
                                            .mustNot(mn -> mn
                                                    .term(t -> t
                                                            .field("isDeleted")
                                                            .value(true)
                                                    )
                                            )
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