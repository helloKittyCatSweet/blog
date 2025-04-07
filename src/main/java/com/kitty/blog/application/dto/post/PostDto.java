package com.kitty.blog.application.dto.post;

import com.kitty.blog.domain.model.category.Category;
import com.kitty.blog.domain.model.tag.Tag;
import com.kitty.blog.domain.model.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {
    private Integer id;
    private String title;
    private String content;
    private Category category;
    private List<Tag> tags;
    private List<Comment> comments;
    private Integer views;
    private Integer likes;
    private Integer favorites;
    private String createTime;
    private String updateTime;
    private String author;
    private Boolean isDraft;
    private String visibility;
}