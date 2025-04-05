package com.kitty.blog.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Table(name = "fs_post_attachments")
@NoArgsConstructor
@Entity
public class PostAttachment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer attachmentId;

    @Column(name = "post_id")
    private Integer postId;

    private String url;

    @Column(name = "attachment_name")
    private String attachmentName;

    @Column(name = "attachment_type")
    private String attachmentType;

    @Column(name = "created_time", insertable = false, updatable = false,
            columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP")
    private LocalDateTime createdTime;
}
