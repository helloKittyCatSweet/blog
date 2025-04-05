package com.kitty.blog.common.constant;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Compare {
    GREATER_THAN(">"),
    LESS_THAN("<"),
    EQUAL_TO("=");

    private final String symbol;

    Compare(String symbol) {
        this.symbol = symbol;
    }

}
