package com.kitty.blog.domain.repository;

import com.kitty.blog.domain.model.SystemMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemMessageRepository extends BaseRepository<SystemMessage, Integer> {

    Page<SystemMessage> findByTargetRoleInOrderByCreatedAtDesc(List<String> targetRoles, Pageable pageable);

    Page<SystemMessage> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
