package com.kitty.blog.domain.service.system;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import com.kitty.blog.infrastructure.config.monitor.KibanaMonitoringConfig;
import com.sun.management.OperatingSystemMXBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
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

    @Scheduled(fixedDelayString = "${blog.elasticsearch.metrics.interval:60}000")
    public void collectSystemMetrics() {
        try {
            OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

            // 收集系统指标
            double cpuUsage = osBean.getCpuLoad() * 100.0;
            long totalMemory = osBean.getTotalMemorySize();
            long freeMemory = osBean.getFreeMemorySize();
            double memoryUsed = (double) (totalMemory - freeMemory) / (1024 * 1024); // 转换为MB

            // 准备数据
            String indexName = monitoringConfig.getElasticsearch().getIndexPrefix() +
                    "-" + LocalDateTime.now().toLocalDate();

            Map<String, Object> source = new HashMap<>();
            source.put("@timestamp", LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
            source.put("cpu_usage", cpuUsage);
            source.put("memory_used", memoryUsed);

            // 发送请求
            elasticsearchClient.index(i -> i
                    .index(indexName)
                    .document(source));

            log.debug("系统指标已收集 - CPU使用率: {}%, 内存使用: {}MB",
                    String.format("%.2f", cpuUsage),
                    String.format("%.2f", memoryUsed));

        } catch (Exception e) {
            log.error("收集系统指标失败", e);
        }
    }
}