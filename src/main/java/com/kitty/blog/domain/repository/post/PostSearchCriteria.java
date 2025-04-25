package com.kitty.blog.domain.repository.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostSearchCriteria {

    private Boolean isPrivate;

    private String title;
    private String content;
    private Integer userId;
    private Boolean isPublished;
    private String visibility;
    private Integer categoryId;
    private Integer tagId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isDeleted;

    // 分页参数
    private Integer currentPage;
    private Integer pageSize;
    private String sortField;
    private String sortOrder;
}