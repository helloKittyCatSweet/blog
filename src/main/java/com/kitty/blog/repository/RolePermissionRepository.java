package com.kitty.blog.repository;

import com.kitty.blog.model.rolePermission.RolePermission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends BaseRepository<RolePermission, Integer> {
    /*
      save(S entity)：保存实体。
      findById(ID id)：根据主键查找实体。
      findAll()：查找所有实体。
      deleteById(ID id)：根据主键删除实体。
      count()：返回实体的总数。
      existsById(ID id)：检查实体是否存在。
     */

    /**
     * 根据角色ID和权限ID查询是否存在
     * @param roleId
     * @param permissionId
     * @return
     */
    @Query("select case when count(r) > 0 then true else false end from RolePermission r " +
            "where r.id.roleId = :roleId and r.id.permissionId = :permissionId")
    boolean existsByRoleIdAndPermissionId(Integer roleId, Integer permissionId);

    /**
     * 根据角色ID查询权限
     * @param roleId
     * @return
     */
    @Query("select r from RolePermission r where r.id.roleId = :roleId")
    Optional<List<RolePermission>> findByRoleId(Integer roleId);

    /**
     * 根据权限ID查询角色
     * @param permissionId
     * @return
     */
    @Query("select r from RolePermission r where r.id.permissionId = :permissionId")
    Optional<List<RolePermission>> findByPermissionId(Integer permissionId);

    /**
     * 根据角色ID和权限ID查询角色权限
     * @param roleId
     * @param permissionId
     * @return
     */
    @Query("select r from RolePermission r where r.id.roleId = :roleId and " +
            "r.id.permissionId = :permissionId")
    Optional<RolePermission> findExplicit(Integer roleId, Integer permissionId);
}
