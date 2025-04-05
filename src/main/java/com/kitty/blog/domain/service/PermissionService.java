package com.kitty.blog.domain.service;

import com.kitty.blog.domain.model.Permission;
import com.kitty.blog.domain.repository.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "permission")
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Transactional
    public ResponseEntity<Permission> findByName(String name) {
        return ResponseEntity.ok(permissionRepository.findByName(name).orElse(new Permission()));
    }

    @Transactional
    public ResponseEntity<List<Permission>> findByNameLike(String name) {
        return ResponseEntity.ok(permissionRepository.findByNameLike(name).orElse(new ArrayList<>()));
    }

    /**
     * Auto-generated methods
     */

    @Transactional
    public ResponseEntity<Boolean> save(Permission permission) {
        return ResponseEntity.ok(permissionRepository.save(permission)!= null);
    }

    @Transactional
    public ResponseEntity<Permission> findById(Integer permissionId) {
        return ResponseEntity.ok((Permission)
                permissionRepository.findById(permissionId).orElse(null));
    }

    @Transactional
    public ResponseEntity<List<Permission>> findAll() {
        return ResponseEntity.ok(permissionRepository.findAll());
    }

    @Transactional
    public ResponseEntity<Boolean> deleteById(Integer permissionId) {
        permissionRepository.deleteById(permissionId);
        return ResponseEntity.ok(true);
    }

    @Transactional
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(permissionRepository.count());
    }

    @Transactional
    public ResponseEntity<Boolean> existsById(Integer permissionId) {
        return ResponseEntity.ok(permissionRepository.existsById(permissionId));
    }

}
