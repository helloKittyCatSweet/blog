package com.kitty.blog.domain.repository;

import com.kitty.blog.domain.model.category.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CategoryRepository extends BaseRepository<Category, Integer> {
    /*
      save(S entity)：保存实体。
      findById(ID id)：根据主键查找实体。
      findAll()：查找所有实体。
      deleteById(ID id)：根据主键删除实体。
      count()：返回实体的总数。
      existsById(ID id)：检查实体是否存在。
     */

    /**
     * create section
     */

    // 创建一个分类（without parent category）
    // 返回值是一个对象：save

    @Query("SELECT CASE WHEN COUNT(c) > 0 " +
            "THEN true ELSE false END FROM Category c WHERE c.categoryId = ?1")
    boolean existsById(Integer categoryId);

    /**
     * delete section
      */

    // 删除单独的子节点
    // 返回值是void:deleteById

    /**
     * delete a category with sub categories
     *
     * @param categoryId
     */
    @Modifying
    @Query(value = "DELETE FROM Category WHERE " +
            "parentCategoryId = :categoryId OR categoryId = :categoryId")
    void deleteWithSubCategories(Integer categoryId);

    /**
     * delete leaf category
     *
     * @param categoryId
     */
    @Modifying
    @Query(value = "DELETE FROM Category WHERE " +
            "categoryId = :categoryId")
    void deleteLeafCategory(Integer categoryId);

    /**
     * update section
     * DynamicUpdate：使用动态更新，只更新需要更新的字段。
     */

    /**
     *find section
     */

    // findById
    // findAll

    /**
     * 根据分类名称查询
     * @param name
     * @return
     */
    @Query(value = "SELECT c FROM Category c WHERE c.name = ?1")
    Optional<Category> findByName(String name);

    /**
     * 根据分类名称模糊查询
     * @param name
     * @return
     */
    @Query(value = "SELECT c FROM Category c WHERE c.name LIKE %?1%")
    Optional<List<Category>> findByNameLike(String name);


    /**
     * findCategoriesByParentId
     * @return List<Category>
     */
    // 不要展开写，就写一个对象
    @Query(value = "SELECT c FROM Category c WHERE c.parentCategoryId = :parentCategoryId")
    Optional<List<Category>> findCategoriesByParentId(Integer parentCategoryId);

    /**
     * 通过文章ID查找分类列表
     * @param postId 文章ID
     * @return 分类列表
     */
    @Query("SELECT c FROM Category c " +
            "JOIN PostCategory pc ON c.categoryId = pc.id.categoryId " +
            "WHERE pc.id.postId = :postId")
    Optional<Category> findByPostId(Integer postId);

    @Query("SELECT COUNT(pc.id.postId) FROM PostCategory pc WHERE pc.id.categoryId = :categoryId")
    Integer countPostsByCategoryId(@Param("categoryId") Integer categoryId);

    @Modifying
    @Query("UPDATE Category c SET c.useCount = :count WHERE c.categoryId = :categoryId")
    void updateUseCount(@Param("categoryId") Integer categoryId, @Param("count") Integer count);

    List<Category> findByCategoryIdIn(Set<Integer> categoryIds);
}
