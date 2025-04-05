package com.kitty.blog.application.dto.comment;

import com.kitty.blog.domain.model.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentTreeBuilder {

    public static List<TreeDto> buildTree(List<Comment> comments) {
        Map<Integer, TreeDto> commentTreeMap = new HashMap<>();
        List<TreeDto> rootComments = new ArrayList<>();

        // 初始化所有 Comment 的 TreeDto
        for (Comment comment : comments) {
            TreeDto treeDto = new TreeDto();
            treeDto.setComment(comment);
            commentTreeMap.put(comment.getCommentId(), treeDto);
        }

        // 构建树状结构
        for (Comment comment : comments) {
            TreeDto treeDto = commentTreeMap.get(comment.getCommentId());
            if (comment.getParentCommentId() == 0) {
                rootComments.add(treeDto);
            } else {
                TreeDto parentTreeDto = commentTreeMap.get(comment.getParentCommentId());
                if (parentTreeDto != null) {
                    parentTreeDto.getChildren().add(treeDto);
                }
            }
        }

        return rootComments;
    }
}
