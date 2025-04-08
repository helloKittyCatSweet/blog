package com.kitty.blog.domain.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@Table(name = "fs_post_versions")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "versionId")
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class PostVersion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer versionId;

    @Column(name = "post_id")
    private Integer postId;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    @Lob
    private String content;

    // 禁止手动加入，禁止更新
    @Column(name = "version_at",
            insertable = false,
            updatable = false,
    columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDate versionAt;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "version")
    private Integer version;


    /**
     * Transient属性，不参与数据库的映射，仅用于业务逻辑处理
     */

    // 关联对象，按需加载
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    @JsonManagedReference
    private Post post;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonManagedReference
    private User user;

    public PostVersion(Integer postId, String content, Integer userId, Integer version) {
        this.postId = postId;
        this.content = content;
        this.userId = userId;
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostVersion that = (PostVersion) o;
        return versionId.equals(that.versionId) &&
                postId.equals(that.postId) &&
                content.equals(that.content) &&
                userId.equals(that.userId) &&
                version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return versionId.hashCode() + postId.hashCode() +
                content.hashCode() + userId.hashCode() + version.hashCode();
    }

    @Override
    public String toString() {
        return "PostVersion{" +
                "versionId=" + versionId +
                ", postId=" + postId +
                ", content='" + content + '\'' +
                ", versionAt=" + versionAt +
                ", userId=" + userId +
                ", version=" + version +
                '}';
    }
}
