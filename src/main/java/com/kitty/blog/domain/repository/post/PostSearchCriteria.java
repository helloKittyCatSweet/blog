package com.kitty.blog.domain.repository.post;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class PostSearchCriteria {

    private boolean isPrivate;

    private String title;
    private String content;
    private Integer userId;
    private Boolean isPublished;
    private String visibility;
    private Integer categoryId;
    private Integer tagId;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean isDeleted;
}