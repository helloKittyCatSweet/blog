package com.kitty.blog.application.dto.userRole;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class FindDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userName;

    private List<String> roleNames;

}
