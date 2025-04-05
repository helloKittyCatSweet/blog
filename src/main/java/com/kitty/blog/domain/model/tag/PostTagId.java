package com.kitty.blog.model.tag;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Data
@Embeddable
public class PostTagId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "tag_id")
    private Integer tagId;

    // 无参构造函数
    public PostTagId() {
    }

    // 带参构造函数
    public PostTagId(Integer postId, Integer tagId) {
        this.postId = postId;
        this.tagId = tagId;
    }

    // 必须实现 hashCode 和 equals 方法
    @Override
    public int hashCode() {
        return Objects.hash(postId, tagId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostTagId postTagId = (PostTagId) o;
        return Objects.equals(postId, postTagId.postId) && Objects.equals(tagId, postTagId.tagId);
    }

    @Override
    public String toString() {
        return "PostTagId{" +
                "postId=" + postId +
                ", tagId=" + tagId +
                '}';
    }
}
