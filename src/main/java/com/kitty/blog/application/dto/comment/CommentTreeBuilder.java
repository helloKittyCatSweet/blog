package com.kitty.blog.application.dto.comment;

import com.kitty.blog.domain.model.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentTreeBuilder {

    public static List<CommentDto> buildCommentTree(List<CommentDto> comments) {
        List<CommentDto> rootComments = new ArrayList<>();

        // 创建评论ID到评论对象的映射
        Map<Integer, CommentDto> commentMap = new HashMap<>();
        for (CommentDto comment : comments) {
            commentMap.put(comment.getCommentId(), comment);
        }

        // 构建树形结构
        for (CommentDto comment : comments) {
            if (comment.getParentId() == null || comment.getParentId() == 0) {
                rootComments.add(comment);
            } else {
                CommentDto parentComment = commentMap.get(comment.getParentId());
                if (parentComment != null) {
                    if (parentComment.getChildren() == null) {
                        parentComment.setChildren(new ArrayList<>());
                    }
                    parentComment.getChildren().add(comment);
                }
            }
        }

        return rootComments;
    }
}
