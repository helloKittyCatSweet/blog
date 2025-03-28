package com.kitty.blog.repository;

import com.kitty.blog.model.userActivity.UserActivity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserActivityRepository extends BaseRepository<UserActivity, Integer> {
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
     * 根据类型查找用户活动
     * @param activityType
     * @return
     */
    @Query("SELECT ua FROM UserActivity ua WHERE ua.activityType = ?1")
    Optional<List<UserActivity>> findByActivityType(String activityType);

    /**
     * 根据用户ID、文章ID、类型查找用户活动
     * @param userId
     * @param postId
     * @param activityType
     * @return
     */
    @Query("SELECT ua FROM UserActivity ua WHERE ua.userId = ?1 AND ua.postId = ?2 " +
            "AND ua.activityType = ?3")
    Optional<UserActivity> findExplicit(Integer userId, Integer postId, String activityType);
}
