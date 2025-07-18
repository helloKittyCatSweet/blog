package com.kitty.blog.application.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class EmailVerificationDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String email;

    private String code;
}
