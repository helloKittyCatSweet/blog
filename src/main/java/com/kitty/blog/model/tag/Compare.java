package com.kitty.blog.model.tag;

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
