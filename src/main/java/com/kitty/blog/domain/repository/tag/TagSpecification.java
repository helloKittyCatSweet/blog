package com.kitty.blog.domain.repository.tag;

import com.kitty.blog.common.constant.Compare;
import com.kitty.blog.domain.model.tag.Tag;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;


public class TagSpecification {

    public static Specification<Tag> weightCompareTo(Integer weight, Compare operator) {
        return (root, criteriaQuery, criteriaBuilder) -> {
            Path<Integer> weightPath = root.get("weight");

            return switch (operator.getSymbol()) {
                case ">" -> criteriaBuilder.greaterThan(weightPath, weight);
                case "<" -> criteriaBuilder.lessThan(weightPath, weight);
                case "=" -> criteriaBuilder.equal(weightPath, weight);
                default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
            };
        };
    }

    public static Specification<Tag> nameContains(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) {
                return null;
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Tag> combinedSearch(String name, Integer weight, String operator) {
        return Specification.where(nameContains(name))
                .and(weight == null ? null : weightCompareTo(weight, Compare.valueOf(operator)));
    }
}
