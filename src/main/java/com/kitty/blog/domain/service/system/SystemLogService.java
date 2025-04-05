package com.kitty.blog.domain.service.system;

import com.kitty.blog.domain.model.SystemLog;
import com.kitty.blog.domain.repository.SystemLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SystemLogService {
    @Autowired
    private SystemLogRepository systemLogRepository;

    public Page<SystemLog> getLogs(Pageable pageable) {
        return systemLogRepository.findAll(pageable);
    }

    public List<SystemLog> getErrorLogs() {
        return systemLogRepository.findByLevel("ERROR");
    }

    public Map<String, Long> getLogStatistics() {
        return systemLogRepository.countByLevel();
    }

    public List<SystemLog> searchLogs(String keyword) {
        return systemLogRepository.findByMessageContaining(keyword);
    }
}