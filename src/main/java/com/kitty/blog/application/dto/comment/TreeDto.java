package com.kitty.blog.dto.comment;

import com.kitty.blog.model.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class TreeDto {
    private Integer id;
    private Integer parentId;
    private Comment comment;
    private List<TreeDto> children = new ArrayList<>();

    public TreeDto(Comment comment) {
        this.id = comment.getCommentId();
        this.parentId = comment.getParentCommentId();
        this.comment = comment;
    }
}
