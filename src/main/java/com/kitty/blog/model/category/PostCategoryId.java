package com.kitty.blog.model.category;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class PostCategoryId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "category_id")
    private Integer categoryId;

    // 确保实现equals和hashCode方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostCategoryId that = (PostCategoryId) o;
        return postId.equals(that.postId) && categoryId.equals(that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, categoryId);
    }

    @Override
    public String toString() {
        return "PostCategoryId{" +
                "postId=" + postId +
                ", categoryId=" + categoryId +
                '}';
    }
}
