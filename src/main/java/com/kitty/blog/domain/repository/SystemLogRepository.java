package com.kitty.blog.domain.repository;

import com.kitty.blog.domain.model.SystemLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public interface SystemLogRepository extends BaseRepository<SystemLog, Long> {

    // 按日志级别查询
    List<SystemLog> findByLevel(String level);

    // 按消息内容搜索
    List<SystemLog> findByMessageContaining(String keyword);

    // 按时间范围查询
    List<SystemLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    // 统计各级别日志数量
    @Query("SELECT l.level as level, COUNT(l) as count FROM SystemLog l GROUP BY l.level")
    Map<String, Long> countByLevel();

    // 查询最近的错误日志
    @Query("SELECT l FROM SystemLog l WHERE l.level = 'ERROR' ORDER BY l.createdAt DESC")
    List<SystemLog> findRecentErrors();

    // 按日志来源查询
    List<SystemLog> findByLoggerName(String loggerName);

    // 按线程名查询
    List<SystemLog> findByThreadName(String threadName);

    // 删除指定时间之前的日志
    void deleteByCreatedAtBefore(LocalDateTime dateTime);
}