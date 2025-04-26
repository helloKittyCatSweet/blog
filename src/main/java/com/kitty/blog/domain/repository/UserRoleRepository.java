package com.kitty.blog.domain.repository;

import com.kitty.blog.domain.model.userRole.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends BaseRepository<UserRole, Integer> {
    /*
      save(S entity)：保存实体。
      findById(ID id)：根据主键查找实体。
      findAll()：查找所有实体。
      deleteById(ID id)：根据主键删除实体。
      count()：返回实体的总数。
      existsById(ID id)：检查实体是否存在。
     */

    @Query("SELECT CASE WHEN COUNT(ur) > 0 " +
            "THEN true ELSE false END FROM UserRole ur WHERE ur.id.userId = ?1 AND ur.id.roleId = ?2")
    boolean exist(Integer userId, Integer roleId);

    /**
     * delete
     */

    /**
     * 删除用户的角色
     *
     * @param userId
     * @param roleId
     */
    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.id.userId = ?1 AND ur.id.roleId = ?2")
    void deleteRole(Integer userId, Integer roleId);


    /**
     * 根据用户ID查询角色
     * @param userId
     * @return
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.id.userId = ?1")
    Optional<List<UserRole>> findByUserId(Integer userId);

    /**
     *  根据角色ID查询用户
     * @param roleId
     * @return
     */
    @Query("SELECT ur FROM UserRole ur WHERE ur.id.roleId = ?1")
    Page<UserRole> findByRoleId(Integer roleId, Pageable pageable);

}
