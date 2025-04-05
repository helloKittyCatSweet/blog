package com.kitty.blog.application.dto.user;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class LoginDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    String username;
    String password;
}
