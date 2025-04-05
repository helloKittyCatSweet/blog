package com.kitty.blog.common.constant;

public enum ReportStatus {
    PENDING("待处理"),
    REVIEWING("审核中"),
    APPROVED("已通过"),
    REJECTED("已驳回");

    private final String description;

    ReportStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}