package com.kitty.blog.application.dto.comment;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class CommentDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer commentId;
    private Integer postId;
    private String title;
    private String content;
    private Integer parentId;
    private String parentContent;
    private String parentUsername;
    private Integer userId;
    private String username;
    private String createdAt;
    private Integer likes;

    private List<CommentDto> children;
}
