package com.kitty.blog.domain.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@Table(name = "fs_favorites", indexes = {
        @Index(name = "idx_user_post", columnList = "user_id,post_id", unique = true),
        @Index(name = "idx_user_folder", columnList = "user_id,folder_name"),
        @Index(name = "idx_favorite_time", columnList = "created_at")
})
@org.hibernate.annotations.Comment("收藏表")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "favoriteId")
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@Audited
public class Favorite implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.Comment("收藏ID")
    private Integer favoriteId;

    @Column(name = "user_id")
    @org.hibernate.annotations.Comment("用户ID")
    private Integer userId;

    @Column(name = "post_id")
    @org.hibernate.annotations.Comment("文章ID")
    private Integer postId;

    @Column(name = "folder_name")
    @org.hibernate.annotations.Comment("收藏夹名称")
    private String folderName = "默认收藏夹";

    // 禁止手动插入，禁止更新
    @Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP")
    @org.hibernate.annotations.Comment("创建时间")
    private LocalDate createdAt;

    /**
     * Transient 字段，不参与数据库的映射，仅用于业务逻辑
     */

    // 关联对象，按需加载
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonBackReference(value = "user-favorite")
    private User user;

    @NotAudited
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    private Post post;

    public Favorite(Integer userId, Integer postId) {
        this.userId = userId;
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "favoriteId=" + favoriteId +
                ", userId=" + userId +
                ", postId=" + postId +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Favorite favorite = (Favorite) o;
        return favoriteId.equals(favorite.favoriteId) &&
                userId.equals(favorite.userId) &&
                postId.equals(favorite.postId);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(favoriteId, userId, postId);
    }
}
