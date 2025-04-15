package com.kitty.blog.domain.repository;

import com.kitty.blog.domain.model.UserActivity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserActivityRepository extends BaseRepository<UserActivity, Integer>{
     /*
      save(S entity)：保存实体。
      findById(ID id)：根据主键查找实体。
      findAll()：查找所有实体。
      deleteById(ID id)：根据主键删除实体。
      count()：返回实体的总数。
      existsById(ID id)：检查实体是否存在。
     */
     @Query("SELECT CASE WHEN COUNT(ua) > 0 " +
             "THEN true ELSE false END FROM UserActivity ua WHERE ua.activityId = ?1")
     boolean existsById(Integer userId);

    /**
     * find
     */

//    findById(Integer id)：根据主键查找实体。
//    findAll()：查找所有实体。
//    deleteById(Integer id)：根据主键删除实体。
//    count()：返回实体的总数。
//    existsById(Integer id)：检查实体是否存在。

    /**
     * 根据用户ID查找用户活动
     * @param postId
     * @return
     */
    @Query("SELECT ua FROM UserActivity ua WHERE ua.postId = ?1")
    Optional<List<UserActivity>> findByPostId(Integer postId);

    /**
     * 根据用户ID查找用户活动
     * @param userId
     * @return
     */
    @Query("SELECT ua FROM UserActivity ua WHERE ua.userId = ?1")
    Optional<List<UserActivity>> findByUserId(Integer userId);

    /**
     * 根据活动类型和用户ID搜索互动记录
     */
    @Query(value = """
       SELECT DISTINCT ua.* FROM fs_user_activities ua 
       INNER JOIN fs_posts p ON ua.post_id = p.post_id
       WHERE p.user_id = :authorId
       AND (:activityType IS NULL OR ua.activity_type = :activityType)
       AND ua.is_deleted = false
       ORDER BY ua.created_at DESC
       """, nativeQuery = true)
    Optional<List<UserActivity>> findByActivityType(
            @Param("authorId") Integer authorId,
            @Param("activityType") String activityType);

    /**
     * 根据用户ID、文章ID、类型查找用户活动
     * @param userId
     * @param postId
     * @param activityType
     * @return
     */
    @Query("SELECT ua FROM UserActivity ua WHERE ua.userId = ?1 AND ua.postId = ?2 " +
            "AND ua.activityType = ?3 AND ua.isDeleted = false")
    Optional<UserActivity> findPostActivityExplicit(Integer userId, Integer postId, String activityType);

    /**
     * 根据用户ID、评论ID、类型查找用户活动
     * @param userId
     * @param commentId
     * @param activityType
     * @return
     */
    @Query("SELECT ua FROM UserActivity ua WHERE ua.userId = ?1 AND ua.commentId = ?2 " +
            "AND ua.activityType = ?3 AND ua.isDeleted = false")
    Optional<UserActivity> findCommentActivityExplicit(Integer userId, Integer commentId, String activityType);

    // 查询其他用户对当前用户文章的有效互动记录
    @Query(value = """
       SELECT ua.* FROM fs_user_activities ua 
       INNER JOIN fs_posts p ON ua.post_id = p.post_id
       WHERE p.user_id = :authorId
       AND ua.activity_type IN ('LIKE', 'FAVORITE', 'COMMENT')
       AND ua.is_deleted = false
       ORDER BY ua.created_at DESC
       """, nativeQuery = true)
    List<UserActivity> findPostInteractions(@Param("authorId") Integer authorId);
}
