package com.kitty.blog.constant;

public enum MessageStatus {
    SENT("SENT"),
    DELIVERED("DELIVERED"),
    READ("READ");

    private String value;
    private MessageStatus(String delivered) {
    }
}
