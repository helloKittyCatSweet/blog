package com.kitty.blog.model.report;

import com.fasterxml.jackson.annotation.*;
import com.kitty.blog.model.Post;
import com.kitty.blog.model.User;
import lombok.Data;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "reports")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "reportId")
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
public class Report implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reportId;

    @Column(name = "post_id")
    private Integer postId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(nullable = false)
    private String reason;

    // 禁止手动加入，禁止更新
    @Column(name = "created_at",
            insertable = false,
            updatable = false,
    columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDate createdAt;


    private String status;

    /**
     * Transient 字段，不参与数据库的映射，仅用于业务逻辑
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return reportId.equals(report.reportId) &&
                postId.equals(report.postId) &&
                userId.equals(report.userId) &&
                reason.equals(report.reason) &&
                createdAt.equals(report.createdAt);
    }

    @Override
    public int hashCode() {
        return reportId.hashCode() + postId.hashCode() +
                userId.hashCode() + reason.hashCode() + createdAt.hashCode();
    }

    @Override
    public String toString() {
        return "Report{" +
                "reportId=" + reportId +
                ", postId=" + postId +
                ", userId=" + userId +
                ", reason='" + reason + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
