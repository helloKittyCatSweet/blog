package com.kitty.blog.domain.model;

import com.kitty.blog.common.constant.MessageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fs_system_messages")
@org.hibernate.annotations.Comment("系统消息表")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.Comment("系统消息ID")
    private Integer id;

    @Column(nullable = false)
    @org.hibernate.annotations.Comment("消息内容")
    private String message;

    @Column(name = "target_role", nullable = false)
    @org.hibernate.annotations.Comment("目标角色")
    private String targetRole;

    @Column(name = "sender_id", nullable = false)
    @org.hibernate.annotations.Comment("发送者ID")
    private Integer senderId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    @org.hibernate.annotations.Comment("创建时间")
    private LocalDate createdAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.Comment("消息状态")
    private MessageStatus status = MessageStatus.UNREAD;

    @Column(name = "read_user_ids", columnDefinition = "TEXT")
    @org.hibernate.annotations.Comment("已读用户id")
    private String readUserIds = ""; // 存储已读用户id，用逗号分隔
}