package com.kitty.blog.common.constant;

import lombok.Getter;

@Getter
public enum ReportReason {
    SPAM("垃圾广告"),
    INAPPROPRIATE("不当内容"),
    PLAGIARISM("抄袭内容"),
    ILLEGAL("违法内容"),
    OTHER("其他原因");

    private final String label;

    ReportReason(String label) {
        this.label = label;
    }

    public static ReportReason fromLabel(String label) {
        for (ReportReason reason : values()) {
            if (reason.getLabel().equals(label)) {
                return reason;
            }
        }
        return OTHER;
    }
}