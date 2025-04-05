package com.kitty.blog.dto.category;

import com.kitty.blog.model.category.Category;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class TreeDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Category category;

    private List<TreeDto> children = new ArrayList<>();
}
