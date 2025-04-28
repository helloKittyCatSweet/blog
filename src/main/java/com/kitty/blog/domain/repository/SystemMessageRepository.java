package com.kitty.blog.domain.repository;

import com.kitty.blog.domain.model.SystemMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemMessageRepository extends BaseRepository<SystemMessage, Integer> {

    Page<SystemMessage> findByTargetRoleInOrderByCreatedAtDesc(List<String> targetRoles, Pageable pageable);

    Page<SystemMessage> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT m FROM SystemMessage m WHERE " +
            "(:message IS NULL OR LOWER(m.message) LIKE LOWER(CONCAT('%', :message, '%'))) AND " +
            "(m.targetRole = :targetRole OR :targetRole IS NULL) " + // 精准匹配 targetRole
            "ORDER BY m.createdAt DESC")
    Page<SystemMessage> findByMessageContainingAndTargetRole(
            @Param("message") String message,
            @Param("targetRole") String targetRole,
            Pageable pageable);

    @Query("SELECT m FROM SystemMessage m WHERE " +
            "(:targetRoles IS NULL OR m.targetRole IN :targetRoles) AND " +
            "(:message IS NULL OR LOWER(m.message) LIKE LOWER(CONCAT('%', :message, '%'))) AND " +
            "(m.targetRole = :targetRole OR :targetRole IS NULL) " + // 精准匹配 targetRole
            "ORDER BY m.createdAt DESC")
    Page<SystemMessage> findByTargetRoleInAndMessageContainingAndTargetRole(
            @Param("targetRoles") List<String> targetRoles,
            @Param("message") String message,
            @Param("targetRole") String targetRole,
            Pageable pageable);
}
