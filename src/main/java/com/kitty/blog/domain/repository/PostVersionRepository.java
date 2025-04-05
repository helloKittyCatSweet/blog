package com.kitty.blog.domain.repository;

import com.kitty.blog.domain.model.PostVersion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostVersionRepository extends BaseRepository<PostVersion, Integer> {
    /*
      save(S entity)：保存实体。
      findById(ID id)：根据主键查找实体。
      findAll()：查找所有实体。
      deleteById(ID id)：根据主键删除实体。
      count()：返回实体的总数。
      existsById(ID id)：检查实体是否存在。
     */
    @Query("SELECT CASE WHEN COUNT(pv) > 0 " +
            "THEN true ELSE false END FROM PostVersion pv WHERE pv.versionId = ?1")
    boolean existsById(Integer versionId);

    /**
     * find
     */

    /**
     * 根据postId查找版本列表
     * @param postId
     */
    @Query("select pv from PostVersion pv where pv.postId = ?1 " +
            "order by pv.versionAt desc")
    Optional<List<PostVersion>> findByPostId(Integer postId);
}
