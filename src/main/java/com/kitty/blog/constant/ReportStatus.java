package com.kitty.blog.constant;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum ReportStatus {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    REJECTED("REJECTED");

    public String status;

    ReportStatus(String status){
        this.status = status;
    }
}
