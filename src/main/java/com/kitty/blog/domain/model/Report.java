package com.kitty.blog.domain.model;

import com.fasterxml.jackson.annotation.*;
import com.kitty.blog.common.constant.ReportReason;
import com.kitty.blog.common.constant.ReportStatus;
import lombok.Data;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "fs_reports")
@org.hibernate.annotations.Comment("举报表")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "reportId")
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@Audited
public class Report implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.Comment("举报ID")
    private Integer reportId;

    @Column(name = "post_id")
    @org.hibernate.annotations.Comment("文章ID")
    private Integer postId;

    @Column(name = "user_id")
    @org.hibernate.annotations.Comment("用户ID")
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    @org.hibernate.annotations.Comment("举报原因")
    private ReportReason reason;

    @Column
    @org.hibernate.annotations.Comment("举报描述")
    private String description;

    // 禁止手动加入，禁止更新
    @Column(name = "created_at",
            insertable = false,
            updatable = false,
    columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @org.hibernate.annotations.Comment("创建时间")
    private LocalDate createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @org.hibernate.annotations.Comment("举报状态")
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "process_instance_id")
    @org.hibernate.annotations.Comment("流程实例ID")
    private String processInstanceId;

    @Column(name = "comment")
    @org.hibernate.annotations.Comment("处理意见")
    private String comment;

    /**
     * Transient 字段，不参与数据库的映射，仅用于业务逻辑
     */

    // 关联对象，按需加载
    @NotAudited
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", insertable = false, updatable = false)
    @JsonManagedReference
    private Post post;

    @NotAudited
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
