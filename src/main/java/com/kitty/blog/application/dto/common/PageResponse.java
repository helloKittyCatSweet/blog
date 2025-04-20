package com.kitty.blog.application.dto.common;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageResponse<T> {
    private List<T> content;
    private long total;
    private int pages;
    private int current;
    private int size;
    
    public static <T> PageResponse<T> from(Page<T> page) {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(page.getContent());
        response.setTotal(page.getTotalElements());
        response.setPages(page.getTotalPages());
        response.setCurrent(page.getNumber() + 1);
        response.setSize(page.getSize());
        return response;
    }
}
