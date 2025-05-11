package com.kitty.blog.domain.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "fs_user_activities", indexes = {
        @Index(name = "idx_user_time", columnList = "user_id,created_at"),
        @Index(name = "idx_activity_type", columnList = "activity_type")
})
@org.hibernate.annotations.Comment("用户动态表")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "activityId")
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@Audited
@Slf4j
public class UserActivity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.Comment("动态ID")
    private Integer activityId;

    @Column(name = "user_id")
    @org.hibernate.annotations.Comment("用户ID")
    private Integer userId;

    @Column(name = "activity_type")
    @org.hibernate.annotations.Comment("动态类型")
    private String activityType;

    @Column(name = "post_id")
    @org.hibernate.annotations.Comment("文章ID")
    private Integer postId;

    @Column(name = "comment_id")
    @org.hibernate.annotations.Comment("评论ID")
    private Integer commentId;

    @Column(name = "activity_detail")
    @org.hibernate.annotations.Comment("动态详情")
    private String activityDetail;

    // 禁止手动插入，禁止更新
    @Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @org.hibernate.annotations.Comment("创建时间")
    private LocalDate createdAt;

    @Column(name = "is_deleted")
    @org.hibernate.annotations.Comment("作者是否删除")
    private boolean isDeleted;

    /**
     * Transient属性，不参与数据库的映射，仅用于业务逻辑处理
     */

    // 关联对象，按需加载
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonManagedReference
    private User user;

    @NotAudited
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    @JsonManagedReference
    private Post post;

    public UserActivity(Integer userId, String activityType, Integer postId, Integer count) {
        this.userId = userId;
        this.activityType = activityType;
        this.postId = postId;
        this.activityDetail = activityType + " " + count + " times";
    }

    @Override
    public String toString() {
        return "UserActivity{" +
                "activityId=" + activityId +
                ", userId=" + userId +
                ", activityType='" + activityType + '\'' +
                ", postId=" + postId +
                ", activityDetail='" + activityDetail + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserActivity that = (UserActivity) o;
        return activityId.equals(that.activityId) &&
                userId.equals(that.userId) &&
                activityType.equals(that.activityType) &&
                postId.equals(that.postId) &&
                activityDetail.equals(that.activityDetail) &&
                createdAt.equals(that.createdAt);
    }

    @Override
    public int hashCode() {
        return activityId.hashCode() + userId.hashCode() +
                activityType.hashCode() + postId.hashCode() +
                activityDetail.hashCode() + createdAt.hashCode();
    }
}
