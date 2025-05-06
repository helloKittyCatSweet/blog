package com.kitty.blog.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fs_user_follows", indexes = {
        @Index(name = "idx_follow_unique", columnList = "follower_id,following_id", unique = true),
        @Index(name = "idx_follow_time", columnList = "create_time")
})
@org.hibernate.annotations.Comment("用户关注表")
public class UserFollow implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.Comment("关注ID")
    private Integer id;

    @Column(name = "follower_id", nullable = false)
    @org.hibernate.annotations.Comment("关注者ID")
    private Integer followerId;

    @Column(name = "following_id", nullable = false)
    @org.hibernate.annotations.Comment("被关注者ID")
    private Integer followingId;

    @Column(name = "create_time", nullable = false)
    @org.hibernate.annotations.Comment("关注时间")
    private LocalDateTime createTime;
}