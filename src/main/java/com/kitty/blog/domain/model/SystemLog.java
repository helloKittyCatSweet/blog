package com.kitty.blog.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "fs_system_logs")
@Data
public class SystemLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String level;  // INFO, WARN, ERROR 等

    @Column(nullable = false)
    private String message;

    @Column(name = "stack_trace", length = 4000)
    private String stackTrace;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "logger_name")
    private String loggerName;

    @Column(name = "thread_name")
    private String threadName;
}