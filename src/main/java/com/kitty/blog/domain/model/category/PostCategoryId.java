package com.kitty.blog.domain.model.category;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class PostCategoryId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "post_id")
    @Comment("文章ID")
    private Integer postId;

    @Column(name = "category_id")
    @Comment("分类ID")
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
