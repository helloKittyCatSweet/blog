package com.kitty.blog.application.controller.system;

import com.kitty.blog.domain.model.SystemLog;
import com.kitty.blog.domain.service.system.SystemLogService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "系统日志管理", description = "系统日志管理相关接口")
@RestController
@RequestMapping("/api/admin/logs")
@PreAuthorize("hasRole(T(com.kitty.blog.common.constant.Role).ROLE_USER)")
public class SystemLogController {
    @Autowired
    private SystemLogService logService;

    @GetMapping
    public ResponseEntity<Page<SystemLog>> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(logService.getLogs(PageRequest.of(page, size)));
    }

    @GetMapping("/errors")
    public ResponseEntity<List<SystemLog>> getErrorLogs() {
        return ResponseEntity.ok(logService.getErrorLogs());
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getLogStatistics() {
        return ResponseEntity.ok(logService.getLogStatistics());
    }

    @GetMapping("/search")
    public ResponseEntity<List<SystemLog>> searchLogs(@RequestParam String keyword) {
        return ResponseEntity.ok(logService.searchLogs(keyword));
    }
}