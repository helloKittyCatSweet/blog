package com.kitty.blog.application.dto.post;

import com.kitty.blog.domain.model.Post;
import com.kitty.blog.domain.model.PostAttachment;
import com.kitty.blog.domain.model.category.Category;
import com.kitty.blog.domain.model.tag.Tag;
import com.kitty.blog.domain.model.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Post post;
    private Category category;
    private List<Tag> tags;
    private List<Comment> comments;
    private String author;
    private List<PostAttachment> attachments;
}