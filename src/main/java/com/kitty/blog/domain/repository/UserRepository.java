package com.kitty.blog.domain.repository;

import com.kitty.blog.domain.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    /*
     * save(S entity)：保存实体。
     * findById(ID id)：根据主键查找实体。
     * findAll()：查找所有实体。
     * deleteById(ID id)：根据主键删除实体。
     * count()：返回实体的总数。
     * existsById(ID id)：检查实体是否存在。
     */

    /**
     * create section
     */
    // save

    /**
     * 给用户添加角色 ------------- user_role
     */
    // UserRoleRepository.save()
    @Query("SELECT CASE WHEN COUNT(u) > 0 " +
            "THEN true ELSE false END FROM User u WHERE u.id = ?1 AND u.isDeleted = false")
    boolean existsById(Integer userId);

    /**
     * 给用户添加设置 ---------- user_setting
     *
     * @param userId
     * @param theme
     * @param notifications
     */
    // UserSettingRepository.save()

    /**
     * delete section
     */
    // deleteById

    /**
     * 删除用户的角色 - user_role
     *
     * @param userId
     * @param roleId
     */
    // UserRoleRepository.deleteRole()

    /**
     * update section
     */
    /**
     * 更新用户设置 ----------- user_setting
     *
     * @param theme
     * @param notifications
     * @param userId
     */

    /**
     * 激活一个用户
     *
     * @param userId
     */
    @Modifying
    @Query("UPDATE User SET isActive = ?2 WHERE id = ?1 AND isDeleted = false")
    void activateUser(Integer userId, boolean isActive);

    /**
     * 重置密码
     *
     * @param userId
     * @param password
     */
    @Modifying
    @Query("UPDATE User SET password = ?2 WHERE id = ?1 AND isActive = true")
    void resetPassword(Integer userId, String password);

    /**
     * find section
     */
    // findAll
    // findById
    // existsById
    // count

    /**
     * 根据用户名查询
     *
     * @param username
     * @return
     */
    @Query("SELECT u FROM User u WHERE u.username = ?1")
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查询
     *
     * @param email
     * @return
     */
    @Query("SELECT u FROM User u WHERE u.email LIKE %?1% AND u.isDeleted = false")
    Optional<User> findByEmail(String email);

    /**
     * 根据邮箱后缀查询
     *
     * @param emailSuffix
     * @return
     */
    @Query("SELECT u FROM User u WHERE SUBSTRING(u.email, LOCATE('@', u.email) + 1) LIKE %?1% " +
            "AND u.isDeleted = false")
    Optional<List<User>> findByEmailSuffix(String emailSuffix);

    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    @Query("SELECT u FROM User u WHERE u.username = ?1 AND u.password = ?2 " +
            "AND u.isActive = true")
    Optional<Boolean> login(String username, String password);

    /**
     * 获取所有 激活 或 未激活 的用户
     *
     * @param isActive
     * @return
     */
    @Query("SELECT u FROM User u WHERE u.isActive = ?1 and u.isDeleted = false")
    Optional<List<User>> findByActivated(boolean isActive);

    boolean existsByEmail(String email);

    /**
     * 根据邮箱查询用户
     *
     * @param email
     * @return
     */
    @Query("SELECT u FROM User u WHERE u.email = ?1 AND u.isDeleted = false")
    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false")
    @NotNull
    List<User> findAll();

    @Query("SELECT u FROM User u WHERE u.username LIKE %?1% OR u.email LIKE %?1% AND u.isDeleted = false")
    Optional<List<User>> findUserByUsernameAndEmail(String keyword);

    List<User> findByUserIdIn(List<Integer> userIds);
}
