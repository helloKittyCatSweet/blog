package com.kitty.blog.service;

import com.kitty.blog.model.rolePermission.RolePermission;
import com.kitty.blog.repository.RolePermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@CacheConfig(cacheNames = "rolePermission")
public class RolePermissionService {

    @Autowired
    private RolePermissionRepository rolePermissionRepository;

    @Transactional
    public ResponseEntity<Boolean> existsByRoleIdAndPermissionId(Integer roleId, Integer permissionId) {
        return ResponseEntity.ok(rolePermissionRepository.
                existsByRoleIdAndPermissionId(roleId, permissionId));
    }

    @Transactional
    public ResponseEntity<List<RolePermission>> findByRoleId(Integer roleId) {
        return ResponseEntity.ok(rolePermissionRepository.
                findByRoleId(roleId).orElse(null));
    }

    @Transactional
    public ResponseEntity<List<RolePermission>> findByPermissionId(Integer permissionId) {
        return ResponseEntity.ok(rolePermissionRepository.
                findByPermissionId(permissionId).orElse(null));
    }

    @Transactional
    public ResponseEntity<RolePermission> findExplicit(Integer roleId, Integer permissionId) {
        return ResponseEntity.ok(rolePermissionRepository.
                findExplicit(roleId, permissionId).orElse(null));
    }

    /**
     * Auto-generated methods
     */

    @Transactional
    public ResponseEntity<Boolean> save(RolePermission rolePermission) {
        return ResponseEntity.ok((Boolean)rolePermissionRepository.save(rolePermission));
    }

    @Transactional
    public ResponseEntity<RolePermission> findById(Integer id) {
        return ResponseEntity.ok((RolePermission)
                rolePermissionRepository.findById(id).orElse(null));
    }

    @Transactional
    public ResponseEntity<List<RolePermission>> findAll() {
        return ResponseEntity.ok(rolePermissionRepository.findAll());
    }

    @Transactional
    public ResponseEntity<Boolean> deleteById(Integer id) {
        rolePermissionRepository.deleteById(id);
        return ResponseEntity.ok(true);
    }

    @Transactional
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(rolePermissionRepository.count());
    }
}
