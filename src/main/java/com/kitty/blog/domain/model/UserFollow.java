package com.kitty.blog.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fs_user_follows", indexes = {
        @Index(name = "idx_follow_unique", columnList = "follower_id,following_id", unique = true),
        @Index(name = "idx_follow_time", columnList = "create_time")
})
public class UserFollow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "follower_id", nullable = false)
    private Integer followerId;

    @Column(name = "following_id", nullable = false)
    private Integer followingId;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
}