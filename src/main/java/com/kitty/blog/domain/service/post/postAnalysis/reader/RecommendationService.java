package com.kitty.blog.domain.service.post.postAnalysis.reader;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScore;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.kitty.blog.application.dto.user.LoginResponseDto;
import com.kitty.blog.domain.model.Comment;
import com.kitty.blog.domain.model.Post;

import com.kitty.blog.common.constant.ActivityType;
import com.kitty.blog.domain.model.UserActivity;
import com.kitty.blog.domain.model.category.Category;
import com.kitty.blog.domain.model.category.PostCategory;
import com.kitty.blog.domain.model.search.PostIndex;
import com.kitty.blog.domain.model.tag.PostTag;
import com.kitty.blog.domain.model.tag.Tag;
import com.kitty.blog.domain.repository.CategoryRepository;
import com.kitty.blog.domain.repository.CommentRepository;
import com.kitty.blog.domain.repository.post.PostRepository;
import com.kitty.blog.domain.repository.UserActivityRepository;
import com.kitty.blog.domain.repository.post.PostSpecification;
import com.kitty.blog.domain.repository.tag.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.time.LocalTime.now;
import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
@Service
public class RecommendationService {

    @Autowired
    private UserActivityRepository userActivityRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Value("${recommend.weights.like:0.1}")
    private double likeWeight;

    @Value("${recommend.weights.favorite:0.2}")
    private double favoriteWeight;

    @Value("${recommend.weights.comment:0.15}")
    private double commentWeight;

    @Value("${recommend.weights.like_comment:0.25}")
    private double likeCommentWeight;

    @Value("${recommend.weights.view:0.05}")
    private double viewWeight;

    /**
     * 混合推荐（基于用户兴趣 + 协同过滤 + 热门降级）
     *
     * @param userId 用户ID
     * @param limit  推荐数量
     * @return 推荐的文章列表
     */
    @Cacheable(value = "userRecommendPosts", key = "#userId")
    public List<Post> recommendPosts(Integer userId, int limit) {
        // 1. 获取用户的历史行为
        List<UserActivity> userActivities = userActivityRepository.findByUserId(userId)
                .orElse(new ArrayList<>());

        // 降级策略
        if (userActivities.isEmpty()) {
            return getHotPostsFromES(limit);
        }

        // 2. 批量查询相关数据（减少DB查询）
        Set<Integer> postIds = userActivities.stream()
                .map(UserActivity::getPostId)
                .collect(Collectors.toSet());
        Map<Integer, Post> postsMap = searchPostsByIds(postIds);
        Map<Integer, Category> categoriesMap = getPostCategoriesMap(postIds);
        Map<Integer, List<Tag>> tagsMap = getPostTagsMap(postIds);

        // 3. 计算用户兴趣模型
        UserInterestScores interestScores = analyzeUserInterests(userActivities, postsMap, categoriesMap, tagsMap);

        // 4. 获取候选文章（排除已读）
        Set<Integer> readPostIds = userActivities.stream() // 修复：添加readPostIds的定义
                .map(UserActivity::getPostId)
                .collect(Collectors.toSet());
        List<Post> candidatePosts = searchCandidatePosts(readPostIds, interestScores);

        // 5. 混合推荐（内容 + 协同过滤）
        return hybridRecommend(candidatePosts, interestScores, limit);
    }

    private Map<Integer, List<Tag>> getPostTagsMap(Set<Integer> postIds) {
        List<PostTag> relations = postRepository.findPostTagMappingByPostIdsIn(postIds);

        Set<Integer> tagIds = relations.stream()
                .map(pt -> pt.getId().getTagId())
                .collect(Collectors.toSet());

        List<Tag> tags = tagRepository.findByTagIdIn(tagIds);
        Map<Integer, Tag> tagMap = tags.stream()
                .sorted(Comparator.comparingInt(Tag::getWeight).reversed())
                .collect(Collectors.toMap(
                        Tag::getTagId,
                        Function.identity(),
                        // 如果有重复，保留第一个权重更高的
                        (existing, replacement) -> existing,
                        LinkedHashMap::new
                ));

        return relations.stream()
                .collect(Collectors.groupingBy(
                        pt -> pt.getId().getPostId(),
                        Collectors.mapping(
                                pt -> tagMap.get(pt.getId().getTagId()),
                                Collectors.toList()
                        )
                ));
    }

    private Map<Integer, Category> getPostCategoriesMap(Set<Integer> postIds) {
        List<PostCategory> relations = postRepository.findPostCategoryMappingByPostIdsIn(postIds);

        Set<Integer> categoryIds = relations.stream()
                .map(pc -> pc.getId().getCategoryId())
                .collect(Collectors.toSet());

        Map<Integer, Category> categoriesMap = categoryRepository.findByCategoryIdIn(categoryIds).stream()
                .collect(Collectors.toMap(
                        Category::getCategoryId,
                        Function.identity()));

        return relations.stream()
                .collect(Collectors.toMap(
                        pc -> pc.getId().getPostId(),
                        pc -> categoriesMap.get(pc.getId().getCategoryId()),
                        (existing, replacement) -> existing));
    }

    // 创建一个内部类来存储两种得分
        private record UserInterestScores(Map<Integer, Double> categoryScores, Map<Integer, Double> tagScores) {
    }

    /**
     * 分析用户兴趣（优化版）
     */
    private UserInterestScores analyzeUserInterests(
            List<UserActivity> activities,
            Map<Integer, Post> postsMap,
            Map<Integer, Category> categoriesMap,
            Map<Integer, List<Tag>> tagsMap) {

        // 分类得分
        Map<Integer, Double> categoryScores = new HashMap<>();
        // 标签得分
        Map<Integer, Double> tagScores = new HashMap<>();

        for (UserActivity activity : activities) {
            Post post = postsMap.get(activity.getPostId());
            if (post != null) {
                double score = calculateActivityScore(activity);

                Category category = categoriesMap.get(post.getPostId());
                if (category != null) {
                    categoryScores.merge(category.getCategoryId(), score, Double::sum);
                }

                List<Tag> tags = tagsMap.get(post.getPostId());
                if (tags != null) {
                    for (Tag tag : tags) {
                        tagScores.merge(tag.getTagId(), score, Double::sum);
                    }
                }
            }
        }

        // 归一化
        return new UserInterestScores(
                normalizeScores(categoryScores),
                normalizeScores(tagScores)
        );
    }

    // 归一化分数
    private Map<Integer, Double> normalizeScores(Map<Integer, Double> scores) {
        if (scores.isEmpty())
            return scores;

        double maxScore = Collections.max(scores.values());
        double minScore = Collections.min(scores.values());
        double range = maxScore - minScore;

        // 处理全零分数和相同分数的情况
        if (range == 0) {
            return scores.keySet().stream()
                    .collect(Collectors.toMap(k -> k, k -> 0.5)); // 统一中值
        }

        return scores.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> (e.getValue() - minScore) / range // Min-Max归一化
                ));
    }

    /**
     * 混合推荐策略
     */
    private List<Post> hybridRecommend(
            List<Post> candidates,
            UserInterestScores interestScores,
            int limit) {
        // 获取当前用户行为数据量
        int userActivityCount = userActivityRepository.countByUserId(getCurrentUserId());

        // 计算动态权重
        double cfWeight = userActivityCount > 10 ? 0.7 : 0.3; // 动态权重
        double hotWeight = candidates.size() > 100 ? 0.1 : 0.0; // 热门降级权重
        double contentWeight = 1.0 - cfWeight - hotWeight; // 内容权重

        // 计算各维度权重
        Map<Post, Double> contentScores = candidates.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        post -> calculateContentScore(post, interestScores)));
        Map<Post, Double> cfScores = calculateHybridCFScores(candidates, getCurrentUserId());
        Map<Post, Double> hotScores = candidates.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        this::calculateHotnessScore));

        // 混合排序
        return candidates.stream()
                .sorted(Comparator.comparingDouble(
                                post -> contentWeight * contentScores.getOrDefault(post, 0.0) +
                                        cfWeight * cfScores.getOrDefault(post, 0.0) +
                                        hotWeight * hotScores.getOrDefault(post, 0.0))
                        .reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return ((LoginResponseDto) authentication.getPrincipal()).getId();
        }
        return null; // 或抛出异常
    }

    /**
     * 计算文章内容推荐分数
     */
    private double calculateContentScore(Post post, UserInterestScores userInterestScores) {
        Category category = categoryRepository.findByPostId(post.getPostId()).orElse(null);
        double categoryScore = (category != null) ?
                userInterestScores.categoryScores.getOrDefault(category.getCategoryId(), 0.0)
                : 0.0;

        List<Tag> tags = tagRepository.findByPostId(post.getPostId()).orElse(new ArrayList<>());
        double tagScore = 0.0;
        if (!tags.isEmpty()) {
            double totalWeight = 0.0;
            double weightedSum = 0.0;

            for (Tag tag : tags) {
                double weight = tag.getWeight();
                double score = userInterestScores.tagScores.getOrDefault(tag.getTagId(), 0.0);
                weightedSum += weight * score;
                totalWeight += weight;
            }

            tagScore = totalWeight > 0 ? weightedSum / totalWeight : 0.0;
        }

        // 合并分类和标签得分
        double contentScore = categoryScore * 0.6 + tagScore * 0.4;
        contentScore += calculateHotnessScore(post);

        if (post.getCreatedAt() == null){
            return contentScore +
                    (post.getLikes() != null ? post.getLikes() * likeWeight : 0) +
                    (post.getViews() != null ? post.getViews() * 0.05 : 0);
        }

        // 时间衰减（30天半衰期）
        double timeDecay = Math.exp(-DAYS.between(
                post.getCreatedAt(), LocalDate.now()) / 30.0);

        return contentScore * timeDecay +
                (post.getLikes() != null ? post.getLikes() * likeWeight : 0) +
                (post.getViews() != null ? post.getViews() * 0.05 : 0);
    }

    /**
     * 计算活动权重分数
     */
    private double calculateActivityScore(UserActivity activity) {
        // 如果字符串是小写的，转换为大写
        String activityType = activity.getActivityType().toUpperCase();
        return switch (ActivityType.valueOf(activityType)) {
            case LIKE -> likeWeight;
            case FAVORITE -> favoriteWeight;
            case COMMENT -> commentWeight;
            case LIKE_COMMENT -> likeCommentWeight;
            default -> 0.5;
        };
    }

    /**
     * 基于文章的协调过滤
     */
    private Map<Post, Double> calculateItemCFScores(List<Post> candidates, Integer userId) {
        // 1. 获取所有用户的交互数据（用户ID -> 该用户交互过的文章ID集合）
        Map<Integer, Set<Integer>> userInteractions = userActivityRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        UserActivity::getUserId,
                        Collectors.mapping(UserActivity::getPostId, Collectors.toSet())));

        // 2. 构建文章共现矩阵（文章A -> {文章B -> 共现次数}）
        Map<Integer, Map<Integer, Integer>> coOccurrenceMatrix = buildCoOccurrenceMatrix(userInteractions);

        // 3. 获取目标用户的历史交互文章
        Set<Integer> userInteractedPosts = userInteractions.getOrDefault(userId, Collections.emptySet());

        // 4. 计算候选文章的CF分数
        return candidates.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        post -> calculateItemCFScore(post.getPostId(),
                                userInteractedPosts, coOccurrenceMatrix)));
    }

    // 构建共现矩阵
    private Map<Integer, Map<Integer, Integer>> buildCoOccurrenceMatrix(
            Map<Integer, Set<Integer>> userInteractions) {
        Map<Integer, Map<Integer, Integer>> matrix = new HashMap<>();
        userInteractions.values().forEach(interactedPosts -> {
            List<Integer> posts = new ArrayList<>(interactedPosts);
            for (int i = 0; i < posts.size(); i++) {
                for (int j = i + 1; j < posts.size(); j++) {
                    int postA = posts.get(i), postB = posts.get(j);
                    matrix.computeIfAbsent(
                            postA,
                            k -> new HashMap<>()).merge(postB, 1, Integer::sum);
                    matrix.computeIfAbsent(
                            postB,
                            k -> new HashMap<>()).merge(postA, 1, Integer::sum);
                }
            }
        });
        return matrix;
    }

    // 计算单个文章的ItemCF分数
    private double calculateItemCFScore(Integer postId, Set<Integer> userInteractedPosts,
                                        Map<Integer, Map<Integer, Integer>> coOccurrenceMatrix) {
        double rawScore = userInteractedPosts.stream()
                .mapToDouble(interactedPost -> coOccurrenceMatrix
                        .getOrDefault(interactedPost, Collections.emptyMap())
                        .getOrDefault(postId, 0))
                .sum();
        return rawScore / (userInteractedPosts.size() + 1); // 控制到[0,1]附近
    }

    /**
     * 基于用户的协同过滤
     */
    /**
     * 计算所有用户之间的相似度矩阵
     *
     * @return Map<用户A, Map < 用户B, 相似度>>
     */
    private Map<Integer, Map<Integer, Double>> calculateUserSimilarities() {
        // 1. 获取所有用户的行为数据（用户ID -> 交互过的文章ID集合）
        Map<Integer, Set<Integer>> userInteractions = userActivityRepository.findAll().stream()
                .collect(Collectors.groupingBy(
                        UserActivity::getUserId,
                        Collectors.mapping(UserActivity::getPostId, Collectors.toSet())));

        // 2. 转换为用户向量（用于余弦相似度计算）
        // 所有文章ID列表
        List<Integer> allPostIds = postRepository.findAll().stream().map(Post::getPostId).toList();
        Map<Integer, double[]> userVectors = new HashMap<>();

        userInteractions.forEach((userId, postIds) -> {
            double[] vector = new double[allPostIds.size()];
            for (int i = 0; i < allPostIds.size(); i++) {
                vector[i] = postIds.contains(allPostIds.get(i)) ? 1.0 : 0.0; // 二进制向量
            }
            userVectors.put(userId, vector);
        });

        // 3. 计算用户间余弦相似度
        Map<Integer, Map<Integer, Double>> similarityMatrix = new HashMap<>();
        List<Integer> userIds = new ArrayList<>(userVectors.keySet());

        for (int i = 0; i < userIds.size(); i++) {
            Integer userA = userIds.get(i);
            double[] vectorA = userVectors.get(userA);
            Map<Integer, Double> similarities = new HashMap<>();

            for (int j = 0; j < userIds.size(); j++) {
                if (i == j)
                    continue; // 跳过自己

                Integer userB = userIds.get(j);
                double[] vectorB = userVectors.get(userB);
                similarities.put(userB, cosineSimilarity(vectorA, vectorB));
            }
            similarityMatrix.put(userA, similarities);
        }

        return similarityMatrix;
    }

    // 余弦相似度计算
    private double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private Map<Post, Double> calculateUserCFScores(List<Post> candidates, Integer userId) {
        // 1. 计算用户相似度矩阵
        Map<Integer, Map<Integer, Double>> userSimilarities = calculateUserSimilarities();

        // 2. 找出Top20相似用户
        List<Integer> similarUsers = userSimilarities
                .getOrDefault(userId, Collections.emptyMap()).entrySet().stream()
                .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
                .limit(20)
                .map(Map.Entry::getKey)
                .toList();

        // 3. 统计带权重的文章分数（考虑用户相似度）
        Map<Integer, Double> postScores = new HashMap<>();
        similarUsers.forEach(similarUser -> {
            double similarity = userSimilarities.get(userId).get(similarUser); // 获取相似度
            userActivityRepository.findByUserId(similarUser).ifPresent(activities -> {
                activities.forEach(activity -> {
                    // 用相似度加权而不仅仅是+1.0
                    postScores.merge(activity.getPostId(), similarity, Double::sum);
                });
            });
        });

        // 4. 归一化处理
        if (!postScores.isEmpty()) {
            double maxScore = Collections.max(postScores.values());
            double minScore = Collections.min(postScores.values());
            double range = maxScore - minScore;

            // 处理所有分数相同的情况
            final double finalRange = (range == 0) ? 1.0 : range;
            final double finalMinScore = minScore;

            return candidates.stream()
                    .collect(Collectors.toMap(
                            Function.identity(),
                            post -> postScores.containsKey(post.getPostId())
                                    ? (postScores.get(post.getPostId())
                                    - finalMinScore) / finalRange
                                    : 0.0));
        }

        return candidates.stream()
                .collect(Collectors.toMap(Function.identity(), post -> 0.0));
    }

    private Map<Post, Double> calculateHybridCFScores(List<Post> candidates, Integer userId) {
        Map<Post, Double> itemCFScores = calculateItemCFScores(candidates, userId);
        Map<Post, Double> userCFScores = calculateUserCFScores(candidates, userId);

        return candidates.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        post -> 0.6 * itemCFScores.getOrDefault(post, 0.0) +
                                0.4 * userCFScores.getOrDefault(post, 0.0)));
    }

    /**
     * 计算热门文章（与用户无关）
     *
     * @param limit 返回的热门文章数量
     * @return 热门文章列表
     */
    @Cacheable(value = "hotPosts", key = "#limit")
    public List<Post> getHotPosts(int limit) {
        // 1. 获取所有文章
        Pageable pageable = PageRequest.of(0, limit);
        List<Post> allPosts = postRepository.findAll(
                PostSpecification.hotPostsSpec(),
                pageable).getContent();

        // 2. 计算每篇文章的热度分数
        Map<Post, Double> hotnessScores = allPosts.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        this::calculateHotnessScore));

        // 3. 对文章按热度分数降序排序并返回前 limit 篇
        return allPosts.stream()
                .sorted(Comparator.comparingDouble(post -> hotnessScores.getOrDefault(post, 0.0))
                        .reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    // 使用ES获取热门文章
    private List<Post> getHotPostsFromES(int limit) {
        try {
            SearchResponse<PostIndex> response = elasticsearchClient.search(
                    s -> s.index("posts")
                            .query(q -> q
                                    .bool(b -> b
                                            .mustNot(mn -> mn
                                                    .term(t -> t
                                                            .field("isDeleted")
                                                            .value(true)))))
                            .sort(sort -> sort
                                    .script(script -> script
                                            .script(s1 -> s1
                                                    .inline(i -> i
                                                            .source(
                                                                    "doc['likeCount'].value * params.likeWeight + "
                                                                            +
                                                                            "doc['viewCount'].value * params.viewWeight + "
                                                                            +
                                                                            "doc['favoriteCount'].value * params.favoriteWeight")
                                                            .params("likeWeight",
                                                                    JsonData.of(likeWeight))
                                                            .params("viewWeight",
                                                                    JsonData.of(viewWeight))
                                                            .params("favoriteWeight",
                                                                    JsonData.of(favoriteWeight))))
                                            .order(SortOrder.Desc)))
                            .size(limit),
                    PostIndex.class);
            List<Post> posts = response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .map(PostIndex::convertToPost)
                    .toList();

            // 如果ES查询结果为空，使用数据库降级策略
            if (posts.isEmpty()) {
                return getHotPosts(limit);
            }
            return posts;
        } catch (Exception e) {
            log.error("从ES获取热门文章失败", e);
            return getHotPosts(limit);
        }
    }

    // 使用ES搜索文章
    private Map<Integer, Post> searchPostsByIds(Set<Integer> postIds) {
        try {
            SearchResponse<PostIndex> response = elasticsearchClient.search(
                    s -> s.index("posts")
                            .query(q -> q
                                    .bool(b -> b
                                            .must(m -> m
                                                    .terms(t -> t
                                                            .field("id")
                                                            .terms(builder -> builder
                                                                    .value(postIds.stream()
                                                                            .map(FieldValue::of)
                                                                            .collect(Collectors
                                                                                    .toList())))))
                                            .mustNot(mn -> mn
                                                    .term(t -> t
                                                            .field("isDeleted")
                                                            .value(true)))))
                            .size(postIds.size()),
                    PostIndex.class);

            // 添加返回值处理
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .map(PostIndex::convertToPost)
                    .collect(Collectors.toMap(
                            Post::getPostId,
                            Function.identity(),
                            (existing, replacement) -> existing));
        } catch (Exception e) {
            log.error("从ES搜索文章失败", e);
            // 降级到原有方法
            return postRepository.findByPostIdIn(postIds).stream()
                    .collect(Collectors.toMap(Post::getPostId, Function.identity()));
        }
    }

    // 使用ES搜索候选文章
    private List<Post> searchCandidatePosts(Set<Integer> excludePostIds,
                                            UserInterestScores userInterestScores) {
        try {
            // 构建分类权重 boost 查询
            List<FunctionScore> functionScores = new ArrayList<>();

            // 添加分类权重
            functionScores.addAll(userInterestScores.categoryScores
                    .entrySet().stream()
                    .map(entry -> FunctionScore.of(fs -> fs
                            .filter(f -> f
                                    .term(t -> t
                                            .field("category")
                                            .value(entry.getKey())))
                            .weight(entry.getValue() * 0.6)))
                    .toList());

            // 添加标签权重
            functionScores.addAll(userInterestScores.tagScores
                    .entrySet().stream()
                    .map(entry -> FunctionScore.of(fs -> fs
                            .filter(f -> f
                                    .term(t -> t
                                            .field("tags")
                                            .value(entry.getKey())))
                            .weight(entry.getValue() * 0.4)))
                    .toList());

            SearchResponse<PostIndex> response = elasticsearchClient.search(s -> s
                            .index("posts")
                            .query(q -> q
                                    .functionScore(fs -> fs
                                            .functions(functionScores)
                                            .query(innerQ -> innerQ
                                                    .bool(b -> b
                                                            .mustNot(mn -> mn
                                                                    .terms(t -> t
                                                                            .field("id")
                                                                            .terms(builder -> builder
                                                                                    .value(excludePostIds
                                                                                            .stream()
                                                                                            .map(FieldValue::of)
                                                                                            .collect(Collectors
                                                                                                    .toList())))))
                                                            .mustNot(mn -> mn
                                                                    .term(t -> t
                                                                            .field("isDeleted")
                                                                            .value(true)))))))
                            .size(100), // 获取足够多的候选文章
                    PostIndex.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .map(PostIndex::convertToPost)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("从ES搜索候选文章失败", e);
            // 降级到原有方法
            return postRepository.findByPostIdNotIn(excludePostIds);
        }
    }

    /**
     * 计算单篇文章的热度分数
     *
     * @param post 文章对象
     * @return 热度分数
     */
    private double calculateHotnessScore(Post post) {
        double score = 0.0;

        // 点赞数权重
        if (post.getLikes() != null) {
            score += post.getLikes() * likeWeight;
        }

        // 浏览量权重
        if (post.getViews() != null) {
            score += post.getViews() * viewWeight; // 浏览量权重可以适当调整
        }

        // 评论数权重
        List<Comment> comments = commentRepository.findByPostId(post.getPostId())
                .orElse(Collections.emptyList());
        score += comments.size() * commentWeight;

        // 收藏权重
        if (post.getFavorites() != null) {
            score += post.getFavorites() * favoriteWeight;
        }

        if (post.getCreatedAt() == null){
            return score;
        }

        // 时间衰减（30天半衰期）
        // 分段衰减函数示例
        long daysOld = DAYS.between(post.getCreatedAt(), LocalDate.now());
        double timeDecay = daysOld > 7
                ? 0.8 * Math.exp(-(daysOld - 7) / 60.0)
                : Math.exp(-daysOld / 30.0);

        return score * timeDecay;
    }
}