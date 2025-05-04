package com.kitty.blog.domain.model;

import com.fasterxml.jackson.annotation.*;
import com.kitty.blog.domain.model.category.PostCategory;
import com.kitty.blog.domain.model.tag.PostTag;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "fs_posts", indexes = {
        @Index(name = "idx_created_at", columnList = "created_at"),
        @Index(name = "idx_title", columnList = "title"),
        @Index(name = "idx_is_public", columnList = "is_published"),
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "postId")
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Post implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer postId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", name = "abstract_content")
    private String abstractContent;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    @Lob
    private String content;

    // 禁止手动加入，禁止更新
    @Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP")
    private LocalDate createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP")
    private LocalDate updatedAt;

    @Column(name = "is_published")
    private Boolean isPublished;

    @Column(name = "is_draft")
    private Boolean isDraft;

    @Column
    private Integer views;

    @Column
    private Integer likes;

    @Column
    private Integer favorites;

    @Column(name = "cover_image")
    private String coverImage;

    @Column(name = "version")
    private Integer version;

    @Column(name = "visibility")
    private String visibility;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    /**
     * Transient 字段，不参与数据库的映射，仅用于业务逻辑处理
     */

    // 关联对象，按需加载
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonBackReference(value = "user-post")
    private User user;

    @JsonIgnore
    @OneToMany(mappedBy = "post")
    @JsonManagedReference(value = "post-comment")
    private List<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "post")
    @JsonBackReference(value = "post-postCategory")
    private List<PostCategory> postCategories;

    @JsonIgnore
    @OneToMany(mappedBy = "post")
    @JsonManagedReference(value = "post-postTag")
    private List<PostTag> postTags;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Post post = (Post) o;
        return postId.equals(post.postId);
    }

    @Override
    public int hashCode() {
        return postId.hashCode();
    }

    @Override
    public String toString() {
        return "Post{" +
                "postId=" + postId +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isPublished=" + isPublished +
                ", isDraft=" + isDraft +
                ", views=" + views +
                ", likes=" + likes +
                ", favorites=" + favorites +
                ", coverImage='" + coverImage + '\'' +
                ", version=" + version +
                '}';
    }
}
