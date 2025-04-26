package com.kitty.blog.application.dto.comment;

import com.kitty.blog.domain.model.Comment;
import com.kitty.blog.domain.model.Post;
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

    public static CommentDto fromComment(Comment comment, Post post) {
        return CommentDto.builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPostId())
                .title(post.getTitle())
                .content(comment.getContent())
                .parentId(comment.getParentCommentId())
                .userId(comment.getUserId())
                .createdAt(comment.getCreatedAt().toString())
                .likes(comment.getLikes())
                .build();
    }
}
