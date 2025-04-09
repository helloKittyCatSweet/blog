package     com.kitty.blog.domain.repository.post;

import com.kitty.blog.domain.model.Post;
import com.kitty.blog.domain.model.category.PostCategory;
import com.kitty.blog.domain.model.tag.PostTag;
import com.kitty.blog.domain.repository.post.PostSearchCriteria;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class PostSpecification {
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

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}