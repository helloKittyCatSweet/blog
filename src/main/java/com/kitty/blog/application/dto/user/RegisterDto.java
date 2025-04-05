package com.kitty.blog.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class RegisterDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private String email;
}
