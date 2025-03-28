package com.kitty.blog.model.report;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public enum StatusType {
    PENDING("PENDING"),
    CONFIRMED("CONFIRMED"),
    REJECTED("REJECTED");

    public String status;

    StatusType(String status){
        this.status = status;
    }
}
