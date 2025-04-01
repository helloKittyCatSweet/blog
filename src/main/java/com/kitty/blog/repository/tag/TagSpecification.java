package com.kitty.blog.repository.tag;

import com.kitty.blog.constant.Compare;
import com.kitty.blog.model.tag.Tag;
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
}
