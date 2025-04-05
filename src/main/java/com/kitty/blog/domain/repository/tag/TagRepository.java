package com.kitty.blog.domain.repository.tag;

import com.kitty.blog.domain.model.tag.Tag;
import com.kitty.blog.domain.repository.BaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends BaseRepository<Tag, Integer>,
        JpaSpecificationExecutor<Tag> {
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
    // save
    @Query("SELECT CASE WHEN COUNT(t) > 0 " +
            "THEN true ELSE false END FROM Tag t WHERE t.tagId = ?1")
    boolean existsById(Integer tagId);

    /**
     * delete section
     */
    // deleteById

    /**
     * update section
     */

    /**
     * find section
     */
    // findById
    // findAll
    // count
    // existsById

    /**
     * 通过标签名查找标签
     * @param name
     * @return
     */
    @Query(value = "SELECT t FROM Tag t WHERE t.name =?1")
    Optional<Tag> findByName(String name);
}
