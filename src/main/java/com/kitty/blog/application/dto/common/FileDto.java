package com.kitty.blog.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private File file;

    private Integer someId;

    private String idType;
}
