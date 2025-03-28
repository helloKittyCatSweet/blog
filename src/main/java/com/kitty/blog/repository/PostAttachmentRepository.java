package com.kitty.blog.repository;

import com.kitty.blog.model.PostAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PostAttachmentRepository extends JpaRepository<PostAttachment, Integer> {

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
     * delete section
     */

    /**
     * update section
     */

    /**
     * query section
     */

    /**
     * 根据postId查询附件列表
     * @param postId
     * @return
     */
    @Query("select p from PostAttachment p where p.postId = :postId")
    Optional<List<PostAttachment>> findByPostId(Integer postId);
}
