package com.kitty.blog.domain.repository.post;

import com.kitty.blog.domain.model.Post;
import com.kitty.blog.domain.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PostRepository extends BaseRepository<Post, Integer>, JpaSpecificationExecutor<Post> {
    /*
     * save(S entity)：保存实体。
     * findById(ID id)：根据主键查找实体。
     * findAll()：查找所有实体。
     * deleteById(ID id)：根据主键删除实体。
     * count()：返回实体的总数。
     * existsById(ID id)：检查实体是否存在。
     */

    /*
     * create section
     */

    // save

    /**
     * 检查文章是否存在
     * 
     * @param postId
     * @return
     */
    @Query("SELECT CASE WHEN COUNT(p) > 0 " +
            "THEN true ELSE false END FROM Post p WHERE p.postId = ?1 and p.isDeleted = false")
    boolean existsById(Integer postId);

    /**
     * 给文章添加分类 ----------- post_category
     *
     * @param postId
     * @param categoryId
     */
    @Modifying
    @Query("INSERT INTO PostCategory (id.postId, id.categoryId) VALUES (:postId, :categoryId)")
    void addCategory(@Param("postId") Integer postId,
            @Param("categoryId") Integer categoryId);

    /**
     * 给文章添加标签 ------------- post_tag
     *
     * @param postId
     * @param tagId
     */
    @Modifying
    @Query("INSERT INTO PostTag (id.postId, id.tagId) VALUES (:postId, :tagId)")
    void addTag(@Param("postId") Integer postId,
            @Param("tagId") Integer tagId);

    /**
     * 给文章添加版本号 ---------- post_version
     *
     * @param postId
     * @param version
     */
    // @Modifying
    // @Query("INSERT INTO PostVersion (postId, content, userId, version) VALUES " +
    // "(:postId, :content, :userId, :version)")
    // void addVersion(@Param("postId") Integer postId,
    // @Param("content") String content,
    // @Param("userId") Integer userId,
    // @Param("version") Integer version);

    /**
     * 删除分类 -------------- post_category
     *
     * @param postId
     * @param categoryId
     */
    @Modifying
    @Query("DELETE FROM PostCategory pc WHERE pc.id.postId = ?1 AND pc.id.categoryId = ?2")
    void deleteCategory(Integer postId, Integer categoryId);

    /**
     * 删除标签 ---------------post_tag
     * 
     * @param postId
     * @param tagId
     */
    @Modifying
    @Query("DELETE FROM PostTag pt WHERE pt.id.postId = ?1 AND pt.id.tagId = ?2")
    int deleteTag(Integer postId, Integer tagId);

    /**
     * update section
     */
    /**
     * 更新可见度
     * @param postId
     * @param visibility
     * @return
     */
    @Modifying
    @Query("UPDATE Post p set p.visibility = ?2 WHERE p.postId = ?1")
    int setVisibility(Integer postId, String visibility);

    /**
     * find section
     */

    /**
     * 根据用户查找文章
     * 
     * @param userId
     * @return
     */
    @Query("SELECT p FROM Post p WHERE p.userId = ?1 and p.isDeleted = false")
    Optional<List<Post>> findByUserId(Integer userId);

    /**
     * 标题模糊搜索
     * 
     * @param keyword
     * @return
     */
    @Query("SELECT p FROM Post p WHERE p.title LIKE %?1% and p.isDeleted = false")
    Optional<List<Post>> findByTitleContaining(String keyword);

    /**
     * 内容模糊搜索
     * 
     * @param keyword
     * @return
     */
    @Query("SELECT p FROM Post p WHERE p.content LIKE %?1% and p.isDeleted = false")
    Optional<List<Post>> findByContentContaining(String keyword);

    /**
     * 已发布文章 或 草稿文章 查询
     * 
     * @param isPublished
     * @param userId
     * @return
     */
    @Query("SELECT p FROM Post p WHERE p.isPublished = ?1 AND p.userId = ?2 and p.isDeleted = false")
    Optional<List<Post>> findByUserIdIsPublished(Boolean isPublished, Integer userId);

    /**
     * 分类查询
     * 
     * @param categoryId
     * @return
     */
    @Query("SELECT p FROM Post p JOIN p.postCategories pc WHERE pc.id.categoryId = ?1 " +
            "and p.isDeleted = false")
    Optional<List<Post>> findByCategoryId(Integer categoryId);

    /**
     * 标签查询
     * 
     * @param tagId
     * @return
     */
    @Query("SELECT p FROM Post p JOIN p.postTags pt WHERE pt.id.tagId = ?1 and p.isDeleted = false")
    Optional<List<Post>> findByTagId(Integer tagId);

    // 根据标签Id集合查询文章
    @Query("SELECT p FROM Post p JOIN p.postTags pt WHERE pt.id.tagId IN ?1 and p.isDeleted = false")
    Optional<List<Post>> findByTagsIn(Set<Integer> tags);

    /**
     * 根据文章ID查询最新版本号
     * 
     * @param postId
     * @return
     */
    @Query("SELECT MAX(v.version) FROM PostVersion v WHERE v.postId = ?1")
    Integer getLatestVersion(Integer postId);

    /**
     * 根据可见度查找列表
     * @param visibility
     * @return
     */
    @Query("SELECT p FROM Post p WHERE p.visibility = ?1 AND p.userId = ?2 and p.isDeleted = false")
    Optional<List<Post>> findByVisibility(String visibility, Integer userId);



    @Query("SELECT COALESCE(SUM(p.views), 0) FROM Post p")
    Integer getTotalViews();

    @Query("SELECT COALESCE((SELECT COUNT(c) FROM Comment c), 0)")
    Integer getTotalComments();

    @Query("SELECT COALESCE(SUM(p.likes), 0) FROM Post p")
    Integer getTotalLikes();

    @Query("SELECT COALESCE(SUM(p.favorites), 0) FROM Post p")
    Integer getTotalFavorites();

    @Query("SELECT MONTH(p.createdAt) as month, COUNT(p) as count " +
            "FROM Post p " +
            "WHERE p.createdAt >= :startDate AND p.createdAt <= :endDate " +
            "GROUP BY MONTH(p.createdAt) " +
            "ORDER BY month")
    List<Object[]> getMonthlyPostCount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT MONTH(p.createdAt) as month, SUM(p.views) as views " +
            "FROM Post p " +
            "WHERE p.createdAt >= :startDate AND p.createdAt <= :endDate " +
            "GROUP BY MONTH(p.createdAt) " +
            "ORDER BY month")
    List<Object[]> getMonthlyViewCount(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Post> findTop5ByOrderByCreatedAtDesc();

    @Query("UPDATE PostCategory pc SET pc.id.categoryId = :categoryId WHERE " +
            "pc.id.postId = :postId AND pc.id.categoryId = :oldCategoryId")
    void updatePostCategory(Integer postId, Integer categoryId, Integer oldCategoryId);

    @Query("UPDATE Post p SET p.likes = p.likes + :count WHERE p.postId = :postId")
    void addLikes(Integer postId, Integer count);

    @Query("UPDATE Post p SET p.favorites = p.favorites + :count WHERE p.postId = :postId")
    void addFavorites(Integer postId, Integer count);
}