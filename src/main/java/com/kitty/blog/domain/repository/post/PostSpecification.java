package com.kitty.blog.domain.repository.post;

import com.kitty.blog.domain.model.Post;
import com.kitty.blog.domain.model.category.PostCategory;
import com.kitty.blog.domain.model.tag.PostTag;
import com.kitty.blog.domain.repository.post.PostSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PostSpecification {
    /**
     * 个人文章查询方法
     */
    public static Specification<Post> createSpecification(PostSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 标题搜索
            if (criteria.getTitle() != null && !criteria.getTitle().isEmpty()) {
                predicates.add(cb.like(root.get("title"), "%" + criteria.getTitle() + "%"));
            }

            // 内容搜索
            if (criteria.getContent() != null && !criteria.getContent().isEmpty()) {
                predicates.add(cb.like(root.get("content"), "%" + criteria.getContent() + "%"));
            }

            // 用户ID搜索
            if (criteria.getUserId() != null) {
                predicates.add(cb.equal(root.get("userId"), criteria.getUserId()));
            }

            // 发布状态搜索
            if (criteria.getIsPublished() != null) {
                predicates.add(cb.equal(root.get("isPublished"), criteria.getIsPublished()));
            }

            // 可见度搜索
            if (criteria.getVisibility() != null) {
                predicates.add(cb.equal(root.get("visibility"), criteria.getVisibility()));
            }

            // 分类搜索
            if (criteria.getCategoryId() != null) {
                Join<Post, PostCategory> categoryJoin = root.join("postCategories");
                predicates.add(cb.equal(categoryJoin.get("id").get("categoryId"), criteria.getCategoryId()));
            }

            // 标签搜索
            if (criteria.getTagId() != null) {
                Join<Post, PostTag> tagJoin = root.join("postTags");
                predicates.add(cb.equal(tagJoin.get("id").get("tagId"), criteria.getTagId()));
            }

            // 时间范围搜索
            if (criteria.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.getStartDate()));
            }
            if (criteria.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), criteria.getEndDate()));
            }

            // 删除状态
            predicates.add(cb.isFalse(root.get("isDeleted"))); // 默认不返回已删除

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
        /**
         * 热门文章排序规则
         */
        public static Specification<Post> hotPostsSpec() {
            return (root, query, cb) -> {
                // 1. 计算热度分数表达式
                Expression<Double> likes = root.get("likes").as(Double.class);
                Expression<Double> favorites = root.get("favorites").as(Double.class);
                Expression<Double> commentCount = cb.size(root.get("comments")).as(Double.class);

                // 基础分数 = 0.3*点赞 + 0.2*收藏 + 0.1*评论数
                Expression<Double> baseScore = cb.sum(
                        cb.prod(cb.literal(0.3), likes),
                        cb.prod(cb.literal(0.2), favorites)
                );

                baseScore = cb.sum(
                        baseScore,
                        cb.prod(cb.literal(0.1), commentCount)
                );

                // 2. 时间衰减因子 (30天半衰期)
                Expression<LocalDate> createdAt = root.get("createdAt").as(LocalDate.class);
                Expression<Long> daysOld = cb.function(
                        "DATEDIFF",
                        Long.class,
                        cb.literal(LocalDate.now()),
                        createdAt
                );
                Expression<Double> timeDecay = cb.exp(
                        cb.quot(
                                cb.neg(daysOld),
                                cb.literal(30.0)
                        )
                );

                // 3. 最终排序表达式
                Expression<Double> hotnessScore = cb.prod(baseScore, timeDecay);

                // 4. 添加排序
                assert query != null;
                query.orderBy(cb.desc(hotnessScore));
                return null; // 不添加额外过滤条件
            };
        }
    }
