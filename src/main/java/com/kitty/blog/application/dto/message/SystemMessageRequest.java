package com.kitty.blog.application.dto.message;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SystemMessageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String message;
    private String targetRole;
}