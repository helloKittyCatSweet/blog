package com.kitty.blog.application.dto.report;

import com.kitty.blog.common.constant.ReportStatus;
import com.kitty.blog.domain.model.Report;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDate;

@Data
@Builder
public class ReportDto implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer reportId;
    private Integer postId;
    private Integer userId;
    private String reason;
    private LocalDate createdAt;
    private ReportStatus status;
    private String processInstanceId;
    private String comment;

    private String username;
    private String postTitle;

}
