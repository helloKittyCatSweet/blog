package com.kitty.blog.domain.model;

import com.kitty.blog.common.constant.MessageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fs_system_messages")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String message;

    @Column(name = "target_role", nullable = false)
    private String targetRole;

    @Column(name = "sender_id", nullable = false)
    private Integer senderId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDate createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageStatus status = MessageStatus.UNREAD;

    @Column(name = "read_user_ids", columnDefinition = "TEXT")
    private String readUserIds = ""; // 存储已读用户id，用逗号分隔
}