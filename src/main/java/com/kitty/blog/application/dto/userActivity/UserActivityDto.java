package com.kitty.blog.application.dto.userActivity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer activityId;

    private String username;

    private String activityType;

    private Integer postId;

    private String postTitle;

    private String activityDetail;

    private LocalDate createdAt;
}