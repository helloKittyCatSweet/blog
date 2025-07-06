package com.kitty.blog.common.aspect;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.kitty.blog.common.constant.ElasticsearchConstants;
import com.kitty.blog.common.constant.LogConstants;
import com.kitty.blog.infrastructure.config.system.monitor.KibanaMonitoringConfig;
import com.sun.management.OperatingSystemMXBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SystemMetricsCollector {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    @Autowired
    private KibanaMonitoringConfig monitoringConfig;

    @Scheduled(fixedDelayString = "${blog.elasticsearch.metrics.interval:300}000")
    public void collectSystemMetrics() {
        try {
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

            // 收集系统指标
            double cpuUsage = osBean.getCpuLoad() * 100.0;
            long totalMemory = osBean.getTotalMemorySize();
            long freeMemory = osBean.getFreeMemorySize();
            double memoryUsed = (double) (totalMemory - freeMemory) / (1024 * 1024); // 转换为MB

            // 准备数据
            Map<String, Object> source = new HashMap<>();
            source.put(ElasticsearchConstants.Fields.TIMESTAMP, LocalDateTime.now().toInstant(ZoneOffset.UTC));
            source.put(ElasticsearchConstants.Fields.METRICS_NAME, LogConstants.LogType.SYSTEM_METRICS);
            source.put(ElasticsearchConstants.Fields.CPU_USAGE, cpuUsage);
            source.put(ElasticsearchConstants.Fields.MEMORY_USED, memoryUsed);
            source.put(ElasticsearchConstants.Fields.TOTAL_MEMORY, totalMemory / (1024 * 1024));
            source.put(ElasticsearchConstants.Fields.FREE_MEMORY, freeMemory / (1024 * 1024));

            // 添加标签
            source.put(ElasticsearchConstants.Fields.HOST, java.net.InetAddress.getLocalHost().getHostName());
            source.put(ElasticsearchConstants.Fields.SERVICE, ElasticsearchConstants.Services.BLOG_SYSTEM);
            source.put(ElasticsearchConstants.Fields.ENVIRONMENT, ElasticsearchConstants.Environments.PRODUCTION);

            // 发送请求，使用别名而不是具体的索引
            elasticsearchClient.index(i -> i
                    .index(ElasticsearchConstants.SYSTEM_METRICS_INDEX_ALIAS)
                    .document(source));

            log.debug("系统指标已收集 - CPU使用率: {}%, 内存使用: {}MB, 总内存: {}MB, 空闲内存: {}MB",
                    String.format("%.2f", cpuUsage),
                    String.format("%.2f", memoryUsed),
                    String.format("%.2f", totalMemory / (1024 * 1024.0)),
                    String.format("%.2f", freeMemory / (1024 * 1024.0)));

        } catch (Exception e) {
            log.error("收集系统指标失败", e);
        }
    }
}