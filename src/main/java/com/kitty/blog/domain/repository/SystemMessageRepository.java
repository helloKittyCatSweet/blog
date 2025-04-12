package com.kitty.blog.domain.repository;

import com.kitty.blog.domain.model.SystemMessage;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemMessageRepository extends BaseRepository<SystemMessage, Integer> {

    List<SystemMessage> findByTargetRoleInOrderByCreatedAtDesc(List<String> targetRoles);

    List<SystemMessage> findAllByOrderByCreatedAtDesc();
}
