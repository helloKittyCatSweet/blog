package com.kitty.blog.domain.service;

import com.kitty.blog.application.dto.comment.CommentDto;
import com.kitty.blog.application.dto.comment.CommentTreeBuilder;
import com.kitty.blog.application.dto.comment.TreeDto;
import com.kitty.blog.domain.model.Comment;
import com.kitty.blog.domain.model.Post;
import com.kitty.blog.domain.model.User;
import com.kitty.blog.domain.repository.CommentRepository;
import com.kitty.blog.domain.repository.post.PostRepository;
import com.kitty.blog.domain.repository.UserRepository;
import com.kitty.blog.domain.service.contentReview.BaiduContentService;
import com.kitty.blog.infrastructure.utils.UpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BaiduContentService baiduContentService;

    @Transactional
    public ResponseEntity<Boolean> create(Comment comment) {
        // 验证用户和帖子是否存在
        if (!postRepository.existsById(comment.getPostId()) ||
                !userRepository.existsById(comment.getUserId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        // 内容审核
        String s = baiduContentService.checkText(comment.getContent());
        System.out.println(s);
        if (!s.equals("合规")) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        // 设置父评论
        if (!commentRepository.existsById(comment.getParentCommentId())) {
            comment.setParentComment(null);
        }

        // 初始化点赞数
        comment.setLikes(0);

        commentRepository.save(comment);
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }

    @Transactional
    public ResponseEntity<Boolean> update(Comment updatedComment) {
        // 检查评论是否存在
        if (!commentRepository.existsById(updatedComment.getCommentId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        // 获取现有的评论对象
        Comment existingComment = (Comment) commentRepository.findById(updatedComment.getCommentId()).orElse(null);
        if (existingComment == null) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        // 内容审核
        String s = baiduContentService.checkText(updatedComment.getContent());
        System.out.println(s);
        if (!s.equals("合规")) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        // 获取帖子和用户是否匹配，父评论如果有也校验
        if (updatedComment.getPostId() != null &&
                !postRepository.existsById(updatedComment.getPostId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        if (updatedComment.getUserId() != null && !userRepository.existsById(updatedComment.getUserId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        if (updatedComment.getParentCommentId() != null
                && !commentRepository.existsById(updatedComment.getParentCommentId())) {
            updatedComment.setParentComment(null); // 父评论不存在时清除
        }

        try {
            UpdateUtil.updateNotNullProperties(existingComment, updatedComment);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // 保存更新后的评论
        commentRepository.save(existingComment);

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> incrementLikes(Integer commentId, int count) {
        if (!commentRepository.existsById(commentId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        commentRepository.incrementLikes(commentId, count);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<List<TreeDto>> findByPostId(Integer postId) {
        if (!postRepository.existsById(postId)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            List<Comment> comments = commentRepository.findByPostId(postId).orElse(new ArrayList<>());
            return new ResponseEntity<>(
                    CommentTreeBuilder.buildTree(comments),
                    HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<Integer> getCommentCountByPostId(Integer postId) {
        if (!postRepository.existsById(postId)) {
            return new ResponseEntity<>(-1, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(
                    commentRepository.getCommentCountByPostId(postId).orElse(0),
                    HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<List<CommentDto>> findByUserId(Integer userId) {
        if (!userRepository.existsById(userId)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }

        List<Comment> comments = commentRepository.findByUserId(userId)
                .orElse(Collections.emptyList());

        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            Post post = (Post) postRepository.findById(comment.getPostId()).orElse(new Post());
            User user = (User) userRepository.findById(comment.getUserId()).orElse(new User());
            CommentDto commentDto = CommentDto.builder()
                    .commentId(comment.getCommentId())
                    .postId(comment.getPostId())
                    .parentId(comment.getParentCommentId())
                    .parentContent(
                            commentRepository.findById(comment.getParentCommentId())
                                    .map(Comment::getContent).orElse(null)
                    )
                    .title(post.getTitle())
                    .content(comment.getContent())
                    .userId(comment.getUserId())
                    .username(user.getUsername())
                    .createdAt(comment.getCreatedAt().toString())
                    .likes(comment.getLikes())
                    .build();
            commentDtos.add(commentDto);
        }

        return new ResponseEntity<>(commentDtos, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<List<CommentDto>> findByPostAuthor(Integer authorId) {
        // 验证作者是否存在
        if (!userRepository.existsById(authorId)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }

        List<Comment> comments = commentRepository.findByPostAuthorId(authorId)
                .orElse(Collections.emptyList());

        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            Post post = (Post) postRepository.findById(comment.getPostId()).orElse(new Post());
            User user = (User) userRepository.findById(comment.getUserId()).orElse(new User());
            Comment temp = commentRepository.findById(comment.getParentCommentId()).orElse(null);
            CommentDto commentDto = CommentDto.builder()
                    .commentId(comment.getCommentId())
                    .parentId(temp == null ? null : temp.getCommentId())
                    .parentContent(temp == null ? null : temp.getContent())
                    .postId(comment.getPostId())
                    .title(post.getTitle())
                    .content(comment.getContent())
                    .userId(comment.getUserId())
                    .username(user.getUsername())
                    .createdAt(comment.getCreatedAt().toString())
                    .likes(comment.getLikes())
                    .build();
            commentDtos.add(commentDto);
        }

        return new ResponseEntity<>(commentDtos, HttpStatus.OK);
    }

    /**
     * Auto-generated methods
     */
    @Transactional
    public ResponseEntity<Comment> save(Comment comment) {
        String s = baiduContentService.checkText(comment.getContent());
        System.out.println(s);
        if (!s.equals("合规")) {
            return new ResponseEntity<>(new Comment(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>((Comment) commentRepository.save(comment), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Comment> findById(Integer commentId) {
        return new ResponseEntity<>((Comment) commentRepository.findById(commentId).orElse(new Comment()),
                HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<List<TreeDto>> findAll() {
        if (commentRepository.count() == 0) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        } else {
            List<Comment> all = commentRepository.findAll();
            return new ResponseEntity<>(CommentTreeBuilder.buildTree(all), HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<Boolean> deleteById(Integer commentId) {
        if (!existsById(commentId).getBody()) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        commentRepository.deleteById(commentId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

   @Transactional
   public ResponseEntity<Boolean> deleteBatch(List<Integer> commentIds){
        if (commentIds == null || commentIds.isEmpty()) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
        for (Integer commentId : commentIds) {
            if (!commentRepository.existsById(commentId)) {
                return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
            }
            commentRepository.deleteAllById(commentIds);
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
   }

    @Transactional
    public boolean isUnderAuthorPost(Integer commentId, Integer userId) {
        Comment comment = (Comment) commentRepository.findById(commentId).orElse(null);
        if (comment == null || !Objects.equals(comment.getUserId(), userId)) {
            return false;
        } else {
            Post post = (Post) postRepository.findById(comment.getPostId()).orElse(null);
            if (post == null || !Objects.equals(post.getUserId(), userId)) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Transactional
    public boolean isUnderAuthorPost(List<Integer> commentIds, Integer userId) {
        for (Integer commentId : commentIds) {
            if (!isUnderAuthorPost(commentId, userId)) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(commentRepository.count(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> existsById(Integer commentId) {
        return new ResponseEntity<>(commentRepository.existsById(commentId), HttpStatus.OK);
    }
}
