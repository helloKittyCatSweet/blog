package com.kitty.blog.domain.service;

import com.kitty.blog.domain.model.Role;
import com.kitty.blog.domain.repository.RoleRepository;
//import com.kitty.blog.domain.service.contentReview.BaiduContentService;
import com.kitty.blog.infrastructure.utils.UpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@CacheConfig(cacheNames = "role")
public class RoleService {

    @Autowired
    private RoleRepository repository;

//    @Autowired
//    private BaiduContentService contentService;

    @Transactional
    public ResponseEntity<Boolean> create(Role role) {
        if (repository.findByRoleName(role.getRoleName()).isPresent()) {
            return new ResponseEntity<>(false, HttpStatus.CONFLICT);
        }

//        String s = contentService.checkText(role.getRoleName());
//        if (!s.equals("合规")){
//            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
//        }
        repository.save(role);
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }

    @Transactional
    @CacheEvict(value = "role", allEntries = true)
    public ResponseEntity<Boolean> update(Role updatedRole) {
        if (!repository.existsById(updatedRole.getRoleId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
//        String s = contentService.checkText(updatedRole.getRoleName());
//        if (!s.equals("合规")){
//            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
//        }

        Role existingRole = (Role) repository.findById(updatedRole.getRoleId()).orElseThrow();
        // 按需更新
        try {
            UpdateUtil.updateNotNullProperties(updatedRole, existingRole);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        repository.save(existingRole);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }


    @Transactional
    @CacheEvict(value = "role", allEntries = true)
    public ResponseEntity<Boolean> deleteByRoleName(String roleName) {
        Role role = findByRoleName(roleName).getBody();
        if (Objects.equals(role, new Role())){
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        repository.deleteByRoleName(roleName);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    @Cacheable(value = "role", key = "#roleName")
    public ResponseEntity<Role> findByRoleName(String roleName) {
        return new ResponseEntity<>(
                repository.findByRoleName(roleName).orElse(new Role()),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(value = "role", key = "#roleName")
    public ResponseEntity<List<Role>> findByRoleNameLike(String roleName) {
        return new ResponseEntity<>(
                repository.findByRoleNameLike(roleName).orElse(List.of(new Role())),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(value = "role", key = "#description")
    public ResponseEntity<List<Role>> findByDescriptionLike(String description) {
        return new ResponseEntity<>(
                repository.findByDescriptionLike(description).orElse(List.of(new Role())),
                HttpStatus.OK);
    }

    /**
     * Auto-generated methods
     */

    @Transactional
    @Cacheable(value = "role", key = "#roleId")
    // 主键不存在则插入，主键存在则更新
    public ResponseEntity<Role> save(Role role) {
        Role oldRole = findByRoleName(role.getRoleName()).getBody();
        // 这个角色已经存在,新增一个已经存在的角色
        if (oldRole != null && !Objects.equals(oldRole, new Role())
                && !Objects.equals(oldRole.getRoleId(),role.getRoleId())){
            return new ResponseEntity<>(new Role(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>((Role) repository.save(role), HttpStatus.OK);
    }

    @Transactional
    @Cacheable(value = "role", key = "#roleId")
    public ResponseEntity<Role> findById(Integer roleId) {
        return new ResponseEntity(
                (Role) repository.findById(roleId).orElse(new Role()),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(value = "role", key = "'all'")
    public ResponseEntity<List<Role>> findAll() {
        if (repository.count() == 0){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(repository.findAll(), HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(value = "role", allEntries = true)
    public ResponseEntity<Boolean> deleteById(Integer roleId) {
        if (!existsById(roleId).getBody()){
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        repository.deleteById(roleId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(repository.count(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> existsById(Integer roleId) {
        return new ResponseEntity<>(repository.existsById(roleId), HttpStatus.OK);
    }
}
