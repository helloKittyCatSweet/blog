package com.kitty.blog.application.dto.post;

import lombok.Data;
import java.util.List;

@Data
public class PostTagsRequestDto {
    private List<String> tags;
    private Integer page;
    private Integer size;
    private String[] sorts;
}