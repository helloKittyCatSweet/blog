package com.kitty.blog.domain.model.category;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.kitty.blog.domain.model.Post;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "fs_post_categories")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@DynamicUpdate
@DynamicInsert
public class PostCategory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private PostCategoryId id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId") // 指定联合主键中的 postId
    @JoinColumn(name = "post_id")
    @JsonManagedReference(value = "post-postCategories")
    private Post post;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId") // 指定联合主键中的 categoryId
    @JoinColumn(name = "category_id")
    private Category category;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostCategory that = (PostCategory) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "PostCategory{" +
                "id=" + id +
                ", post=" + post +
                ", category=" + category +
                '}';
    }
}

