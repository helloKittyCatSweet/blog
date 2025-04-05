package com.kitty.blog.domain.model.tag;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kitty.blog.domain.model.Post;
import lombok.Data;
import jakarta.persistence.*;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "fs_post_tags")
@DynamicUpdate
@DynamicInsert
public class PostTag implements Serializable{

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private PostTagId id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    @JsonBackReference(value = "post-postTag")
    private Post post;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "tag_id", insertable = false, updatable = false)
    @JsonBackReference(value = "tag-postTag")
    private Tag tag;

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PostTag other = (PostTag) obj;
        return id.equals(other.id);
    }

    @Override
    public String toString() {
        return "PostTag{" +
                "id=" + id +
                ", post=" + post +
                ", tag=" + tag +
                '}';
    }
}
