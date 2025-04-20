package com.kitty.blog.application.dto.common;

import lombok.Data;

@Data
public class PageRequest {
    private int page = 1;
    private int size = 10;
    
    public int getOffset() {
        return (page - 1) * size;
    }
    
    public org.springframework.data.domain.PageRequest toSpringPageRequest() {
        return org.springframework.data.domain.PageRequest.of(page - 1, size);
    }
}