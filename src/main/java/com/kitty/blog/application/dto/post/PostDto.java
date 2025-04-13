package com.kitty.blog.application.dto.post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostDto implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Post post;
    private Category category;
    private List<Tag> tags = new ArrayList<>();  // 初始化为空列表
    private List<Comment> comments = new ArrayList<>();  // 初始化为空列表
    private String author;
    private List<PostAttachment> attachments = new ArrayList<>();  // 初始化为空列表

    // 添加安全的setter方法
    public void setTags(List<Tag> tags) {
        this.tags = tags != null ? tags.stream()
                .filter(Objects::nonNull)
                .filter(tag -> tag instanceof Tag)  // 确保元素是Tag类型
                .toList() : new ArrayList<>();
    }

    // 添加安全的getter方法
    public List<Tag> getTags() {
        return tags != null ? tags : new ArrayList<>();
    }
}