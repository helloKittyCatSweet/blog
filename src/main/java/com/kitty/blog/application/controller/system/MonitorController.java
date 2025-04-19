package com.kitty.blog.application.controller.system;

import com.kitty.blog.infrastructure.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.metrics.MetricsEndpoint;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    @Autowired
    private HealthEndpoint healthEndpoint;

    @Autowired
    private MetricsEndpoint metricsEndpoint;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/system-status")
    public Response<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = new HashMap<>();

        // 系统健康状态
        HealthComponent health = healthEndpoint.health();
        status.put("health", health);

        // JVM 信息
        status.put("memory", metricsEndpoint.metric("jvm.memory.used", null));
        status.put("threads", metricsEndpoint.metric("jvm.threads.live", null));

        // 系统信息
        status.put("cpu", Runtime.getRuntime().availableProcessors());
        status.put("diskSpace", new File("/").getFreeSpace() / 1024 / 1024 / 1024 + "GB");

        return Response.ok(status).getBody();
    }
}