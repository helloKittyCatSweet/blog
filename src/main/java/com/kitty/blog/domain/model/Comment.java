package com.kitty.blog.domain.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.ToString;
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
@Table(name = "fs_comments", indexes = {
        @Index(name = "idx_post_time", columnList = "post_id,created_at"),
        @Index(name = "idx_user", columnList = "user_id")
})
@org.hibernate.annotations.Comment("评论表")
// @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
// property = "commentId")
@DynamicUpdate
@DynamicInsert
public class Comment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.Comment("评论ID")
    private Integer commentId;

    @Column(name = "post_id")
    @org.hibernate.annotations.Comment("文章ID")
    private Integer postId;

    @Column(name = "user_id")
    @org.hibernate.annotations.Comment("用户ID")
    private Integer userId;

    @Column(nullable = false)
    @org.hibernate.annotations.Comment("评论内容")
    private String content;

    // 禁止手动插入，禁止更新
    @Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @org.hibernate.annotations.Comment("创建时间")
    private LocalDate createdAt;

    @Column
    @org.hibernate.annotations.Comment("点赞数")
    private Integer likes;

    @Column(name = "parent_comment_id")
    @org.hibernate.annotations.Comment("父评论ID")
    private Integer parentCommentId;

    /**
     * Transient 字段，不参与数据库的映射，仅用于业务逻辑处理
     */

    // 关联对象，按需加载
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    @JsonBackReference(value = "post-comment")
    private Post post;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonBackReference(value = "user-comment")
    private User user;

    @JsonIgnore
    @Transient
    private Comment parentComment;

    @Transient
    @JsonIgnore
    // @OneToMany(mappedBy = "parentComment")
    private List<Comment> replies;

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", postId=" + postId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", likes=" + likes +
                ", parentCommentId=" + parentCommentId +
                ", post=" + post +
                ", user=" + user +
                ", parentComment=" + parentComment +
                ", replies=" + replies +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Comment comment = (Comment) o;
        return commentId.equals(comment.commentId) &&
                postId.equals(comment.postId) &&
                userId.equals(comment.userId) &&
                content.equals(comment.content) &&
                createdAt.equals(comment.createdAt) &&
                likes.equals(comment.likes) &&
                parentCommentId.equals(comment.parentCommentId) &&
                post.equals(comment.post) &&
                user.equals(comment.user) &&
                parentComment.equals(comment.parentComment) &&
                replies.equals(comment.replies);
    }

    @Override
    public int hashCode() {
        return commentId.hashCode() + postId.hashCode() +
                userId.hashCode() + content.hashCode() +
                createdAt.hashCode() + likes.hashCode() +
                parentCommentId.hashCode() + post.hashCode() +
                user.hashCode() + parentComment.hashCode() + replies.hashCode();
    }
}
