package com.kitty.blog.domain.repository;

import com.kitty.blog.domain.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
  /*
   * save(S entity)：保存实体。
   * findById(ID id)：根据主键查找实体。
   * findAll()：查找所有实体。
   * deleteById(ID id)：根据主键删除实体。
   * count()：返回实体的总数。
   * existsById(ID id)：检查实体是否存在。
   */

  /**
   * create section
   */
  // save
  @Query("SELECT CASE WHEN COUNT(c) > 0 " +
      "THEN true ELSE false END FROM Comment c WHERE c.commentId = ?1")
  boolean existsById(Integer commentId);

  /**
   * delete section
   */
  /**
   * update section
   */

  /**
   * 点赞数+1
   *
   * @param commentId
   */
  @Modifying
  @Query("UPDATE Comment c SET c.likes = c.likes + ?2 WHERE c.commentId = ?1")
  void incrementLikes(Integer commentId, int count);

  /**
   * find section
   */

  /**
   * 根据postId查找评论列表
   * 
   * @param postId
   * @return
   */
  @Query("SELECT c FROM Comment c WHERE c.postId = ?1")
  Optional<List<Comment>> findByPostId(Integer postId);

  /**
   * 根据userId查找评论列表
   * 
   * @param postId
   * @return
   */
  @Query("SELECT c FROM Comment c WHERE c.userId = ?1")
  Optional<Integer> getCommentCountByPostId(Integer postId);
}
