package com.kitty.blog.application.dto.post;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class PostAttachmentDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer postId;
    private String postTitle;
    private Integer attachmentId;
    private String attachmentName;
    private String attachmentType;
    private String attachmentUrl;
    private LocalDateTime createdTime;
    private Long size;
}
