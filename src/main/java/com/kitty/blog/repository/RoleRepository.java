package com.kitty.blog.repository;

import com.kitty.blog.model.Role;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<Role, Integer> {
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
    @Query("SELECT CASE WHEN COUNT(r) > 0 " +
            "THEN true ELSE false END FROM Role r WHERE r.roleId = ?1")
    boolean existsById(Integer id);

    /**
     * delete section
     */
    //deleteById

    /**
     * 根据角色名删除角色
     *
     * @param roleName
     */
    @Modifying
    @Query("delete from Role r where r.roleName =?1")
    void deleteByRoleName(String roleName);

    /**
     * update section
     */


    /**
     * find section
     */
    //findById
    //findAll
    //count
    //existsById

    /**
     * 根据角色名查询角色
     * @param roleName
     * @return
     */
    @Query("select r from Role r where r.roleName =?1")
    Optional<Role> findByRoleName(String roleName);

    /**
     * 根据角色名模糊查询角色
     * @param roleName
     * @return
     */
    @Query("select r from Role r where r.roleName like %?1%")
    Optional<List<Role>> findByRoleNameLike(String roleName);

    /**
     *  根据角色描述模糊查询角色
     * @param description
     * @return
     */
    @Query("select r from Role r where r.description like %?1%")
    Optional<List<Role>> findByDescriptionLike(String description);
}
