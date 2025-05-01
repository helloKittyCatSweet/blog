package com.kitty.blog.domain.repository;

import com.kitty.blog.domain.model.Favorite;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends BaseRepository<Favorite, Integer> {
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
  @Query("SELECT CASE WHEN COUNT(f) > 0 " +
      "THEN true ELSE false END FROM Favorite f WHERE f.favoriteId = ?1")
  boolean existsById(Integer favoriteId);

  /**
   * delete section
   */

  // 根据favoriteId删除收藏
  // deleteById

  /**
   * update section
   */
  /**
   * find section
   */

  // findById(ID id)：根据主键查找实体。
  // findAll()：查找所有实体。
  // count()：返回实体的总数。

  /**
   * 根据userId查找收藏
   * 
   * @param userId
   * @return
   */
  @EntityGraph(attributePaths = { "post" })
  @Query("select f from Favorite f where f.userId = ?1")
  Page<Favorite> findByUserId(Integer userId, Pageable pageable);

  @EntityGraph(attributePaths = { "post" })
  @Query("select f from Favorite f where f.userId = ?1")
  Optional<List<Favorite>> findByUserId(Integer userId);

  /**
   * 根据userId和关键字查找收藏
   * 
   * @param userId   用户ID
   * @param keyword  搜索关键字
   * @param pageable 分页信息
   * @return 收藏分页数据
   */
  @EntityGraph(attributePaths = { "post" })
  @Query("select f from Favorite f where f.userId = ?1 and (f.post.title like %?2% or f.post.content like %?2%)")
  Page<Favorite> findByUserIdAndKeyword(Integer userId, String keyword, Pageable pageable);

  /**
   * 根据userId获得收藏数
   * 
   * @param userId
   * @return
   */
  @Query("select count(f) from Favorite f where f.userId = ?1")
  Integer countByUserId(Integer userId);

  /**
   * 根据postId查找收藏
   * 
   * @param postId
   * @return
   */
  // @Query("select f from favorites f where f.post_id = ?1")
  // Optional<Favorite> findByPostId(Integer postId);

  /**
   * 根据postId获得收藏数
   * 
   * @param postId
   * @return
   */
  @Query("select count(f) from Favorite f where f.postId = ?1")
  Integer countByPostId(Integer postId);

  /**
   * 根据userId和postId查找收藏
   * 
   * @param userId
   * @param postId
   * @return
   */
  @EntityGraph(attributePaths = { "post" })
  @Query("select f from Favorite f where f.userId = ?1 and f.postId = ?2")
  Optional<Favorite> findByUserIdAndPostId(Integer userId, Integer postId);

  /**
   * 获取用户的所有收藏夹名称
   */
  @EntityGraph(attributePaths = { "post" })
  @Query("SELECT DISTINCT f.folderName FROM Favorite f WHERE f.userId = ?1")
  List<String> findFolderNamesByUserId(Integer userId);

  /**
   * 获取用户特定收藏夹中的收藏
   */
  @EntityGraph(attributePaths = { "post" })
  @Query("SELECT f FROM Favorite f WHERE f.userId = ?1 AND f.folderName = ?2")
  Page<Favorite> findByUserIdAndFolderName(Integer userId, String folderName,
      Pageable pageable);

  @EntityGraph(attributePaths = { "post" })
  @Query("SELECT f FROM Favorite f WHERE f.userId = ?1 AND f.folderName = ?2")
  Optional<List<Favorite>> findByUserIdAndFolderName(Integer userId, String folderName);

  /**
   * 获取用户特定收藏夹中的收藏数量
   */
  @Query("SELECT COUNT(f) FROM Favorite f WHERE f.userId = ?1 AND f.folderName = ?2")
  Integer countByUserIdAndFolderName(Integer userId, String folderName);

  /**
   * 检查文件夹是否存在
   */
  @Query("SELECT COUNT(f) > 0 FROM Favorite f WHERE f.userId = ?1 AND f.folderName = ?2")
  boolean existsByUserIdAndFolderName(Integer userId, String folderName);

}
