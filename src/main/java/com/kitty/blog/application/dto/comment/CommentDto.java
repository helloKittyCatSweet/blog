package com.kitty.blog.application.dto.comment;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
public class CommentDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer commentId;
    private Integer postId;
    private String title;
    private String content;
    private Integer userId;
    private String username;
    private String createdAt;
    private Integer likes;

}
