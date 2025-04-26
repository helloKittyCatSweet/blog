package com.kitty.blog.domain.service;

import com.kitty.blog.domain.model.Role;
import com.kitty.blog.application.dto.userRole.FindDto;

import com.kitty.blog.domain.model.User;
import com.kitty.blog.domain.model.userRole.UserRole;
import com.kitty.blog.domain.model.userRole.UserRoleId;
import com.kitty.blog.domain.repository.RoleRepository;
import com.kitty.blog.domain.repository.UserRepository;
import com.kitty.blog.domain.repository.UserRoleRepository;
import com.kitty.blog.infrastructure.utils.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "userRole")
@Slf4j
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

        Role role = roleRepository.findById(roleId).orElse(new Role());
        role.setCount(role.getCount() - 1);
        roleRepository.save(role);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * Auto-generated methods
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<UserRole> save(UserRole userRole) {
        Role role = roleRepository.findById(userRole.getId().getRoleId()).orElse(null);
        if (role == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        role.setCount(role.getCount() + 1);
        roleRepository.save(role);

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
//        log.info("userId:: " + id);
        for (UserRole userRole : userRoles) {
            Role role = roleRepository.findById(userRole.getId().getRoleId()).orElse(null);
//            log.info("role:: " + role);
            roles.add(role);
        }
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#id")
    public Page<User> findByRoleId(Integer id, Integer page, Integer size, String[] sort) {
        PageRequest pageRequest = PageUtil.createPageRequest(page, size, sort);
        Page<UserRole> userRoles = userRoleRepository.findByRoleId(id, pageRequest);
        return userRoles.map(userRole ->
                userRepository.findById(userRole.getId().getUserId()).orElse(null));
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

    @Transactional
    public ResponseEntity<String> importRoleData(MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            // 读取角色表
            Sheet roleSheet = workbook.getSheetAt(0);
            for (int i = 1; i <= roleSheet.getLastRowNum(); i++) {
                Row row = roleSheet.getRow(i);
                if (row == null) continue;

                String roleName = getCellValueAsString(row.getCell(0));
                String description = getCellValueAsString(row.getCell(1));

                // 只更新已存在的角色
                Optional<Role> roleOptional = roleRepository.findByRoleName(roleName);
                if (roleOptional.isEmpty()) {
                    log.warn("角色 {} 不存在，跳过导入", roleName);
                    continue;
                }

                // 只更新描述字段
                Role role = roleOptional.get();
                role.setDescription(description);
                roleRepository.save(role);

                // 读取对应的用户表
                Sheet userSheet = workbook.getSheet(roleName + "-用户列表");
                if (userSheet != null) {
                    for (int j = 1; j <= userSheet.getLastRowNum(); j++) {
                        Row userRow = userSheet.getRow(j);
                        if (userRow == null) continue;

                        String username = getCellValueAsString(userRow.getCell(0));
                        if (username == null || username.isEmpty()) continue;

                        // 只处理已存在的用户
                        Optional<User> userOptional = userRepository.findByUsername(username);
                        if (userOptional.isEmpty()) {
                            log.warn("用户 {} 不存在，跳过导入", username);
                            continue;
                        }

                        User user = userOptional.get();
                        if (!userRoleRepository.exist(user.getUserId(), role.getRoleId())) {
                            UserRole userRole = new UserRole();
                            userRole.setId(new UserRoleId(user.getUserId(), role.getRoleId()));
                            save(userRole);
                        }
                    }
                }
            }
            return new ResponseEntity<>("导入成功", HttpStatus.OK);
        } catch (Exception e) {
            log.error("导入角色数据失败", e);
            return new ResponseEntity<>("导入失败: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            default:
                return "";
        }
    }

}
