package com.kitty.blog.domain.repository;

import com.kitty.blog.domain.model.Permission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, Integer> {
    /*
      save(S entity)：保存实体。
      findById(ID id)：根据主键查找实体。
      findAll()：查找所有实体。
      deleteById(ID id)：根据主键删除实体。
      count()：返回实体的总数。
      existsById(ID id)：检查实体是否存在。
     */

    /**
     * 根据权限ID查询权限是否存在
     * @param id
     * @return
     */
    @Query("select count (p) from Permission p where p.permissionId =?1")
    boolean existsById(Integer id);

    /**
     * 根据权限名称查询权限
     * @param name
     * @return
     */
    @Query("select p from Permission p where p.name =?1")
    Optional<Permission> findByName(String name);

    /**
     * 根据权限名称模糊查询权限
     * @param name
     * @return
     */
    @Query("select p from Permission p where p.name like ?1")
    Optional<List<Permission>> findByNameLike(String name);
}
