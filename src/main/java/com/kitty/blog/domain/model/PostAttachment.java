package com.kitty.blog.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Table(name = "fs_post_attachments")
@org.hibernate.annotations.Comment("文章附件表")
@NoArgsConstructor
@Entity
public class PostAttachment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.Comment("附件ID")
    private Integer attachmentId;

    @Column(name = "post_id")
    @org.hibernate.annotations.Comment("文章ID")
    private Integer postId;

    @Column(name = "url")
    @org.hibernate.annotations.Comment("附件URL（阿里云）")
    private String url;

    @Column(name = "attachment_name")
    @org.hibernate.annotations.Comment("附件名称")
    private String attachmentName;

    @Column(name = "attachment_type")
    @org.hibernate.annotations.Comment("附件类型")
    private String attachmentType;

    @Column(name = "created_time", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP")
    @org.hibernate.annotations.Comment("创建时间")
    private LocalDateTime createdTime;

    @Column(name = "size")
    @org.hibernate.annotations.Comment("附件大小")
    private Long size;
}
