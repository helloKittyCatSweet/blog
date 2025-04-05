package com.kitty.blog.application.service;

import com.kitty.blog.dto.userRole.FindDto;
import com.kitty.blog.model.Role;
import com.kitty.blog.model.userRole.UserRole;
import com.kitty.blog.repository.RoleRepository;
import com.kitty.blog.repository.UserRepository;
import com.kitty.blog.repository.UserRoleRepository;
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

@Service
@CacheConfig(cacheNames = "userRole")
public class UserRoleService {

    @Autowired
    private UserRoleRepository userRoleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> deleteRole(Integer userId, Integer roleId) {
        if (!userRepository.existsById(userId) ||!roleRepository.existsById(roleId)){
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        userRoleRepository.deleteRole(userId, roleId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * Auto-generated methods
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<UserRole> save(UserRole userRole) {

        return new ResponseEntity<>(
                (UserRole) userRoleRepository.save(userRole),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#id")
    public ResponseEntity<List<Role>> findByUserId(Integer id) {
        ArrayList<UserRole> userRoles =
                new ArrayList<>(userRoleRepository.findByUserId(id).orElse(new ArrayList<>()));
        ArrayList<Role> roles = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            roles.add(userRole.getRole());
        }
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#id")
    public ResponseEntity<List<UserRole>> findByRoleId(Integer id) {
        return new ResponseEntity<>
                (new ArrayList<>(userRoleRepository.findByRoleId(id).orElse(new ArrayList<>())),
                        HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#username")
    public ResponseEntity<List<FindDto>> findAll() {
//        List<UserRole> userRoles = userRoleRepository.findAll();
//        Map<String, FindDto> userRoleMap = new HashMap<>();
//
//        for (UserRole userRole : userRoles) {
//            String userName = userRole.getUser().getUsername();
//
//            userRoleMap.putIfAbsent(userName, new FindDto(userName, new ArrayList<>()));
//            userRoleMap.get(userName).getRoleNames().add(roleName);
//        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(userRoleRepository.count(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> exist(Integer userId, Integer roleId) {
        return new ResponseEntity<>(userRoleRepository.exist(userId,roleId), HttpStatus.OK);
    }
}
