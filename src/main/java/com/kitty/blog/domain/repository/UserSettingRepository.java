package com.kitty.blog.domain.repository;

import com.kitty.blog.domain.model.UserSetting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingRepository extends BaseRepository<UserSetting, Integer> {
     /*
      save(S entity)：保存实体。
      findById(ID id)：根据主键查找实体。
      findAll()：查找所有实体。
      deleteById(ID id)：根据主键删除实体。
      count()：返回实体的总数。
      existsById(ID id)：检查实体是否存在。
     */

    @Query("SELECT CASE WHEN COUNT(us) > 0 " +
            "THEN true ELSE false END FROM UserSetting us WHERE us.settingId = ?1")
    boolean existsById(Integer settingId);

    /**
     * find
     */

    Optional<UserSetting> findByUserId(Integer userId);

    Optional<UserSetting> findByGithubAccount(String githubAccount);
}
