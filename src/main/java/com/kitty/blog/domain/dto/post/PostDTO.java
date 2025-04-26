package com.kitty.blog.domain.dto.post;

import com.kitty.blog.domain.dto.BaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PostDTO extends BaseDTO {
    private Integer id;
    private String title;
    private String content;
    private Integer userId;
    private String username;
    private Integer categoryId;
    private String categoryName;
    private Boolean isPublished;
    private String visibility;
    private Integer views;
    private Integer likes;
    private Integer comments;
    private Integer favorites;
    private String createTime;
    private String updateTime;
}