package com.kitty.blog.domain.service.system;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitty.blog.domain.service.system.kibana.KibanaApiClient;
import com.kitty.blog.domain.service.system.visualization.VisualizationParamsFactory;
import com.kitty.blog.infrastructure.config.monitor.KibanaMonitoringConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class KibanaInitializationService {

    // 定义静态类型引用，避免使用匿名内部类
    private static final TypeReference<List<Map<String, Object>>> PANELS_TYPE = new TypeReference<>() {
    };

    private static final String KIBANA_API_V8_PREFIX = "/api/v8";

    @Autowired
    private KibanaApiClient kibanaClient;

    @Autowired
    private KibanaMonitoringConfig monitoringConfig;

    @Autowired
    private ObjectMapper objectMapper;

    private volatile boolean isInitialized = false;

    @PostConstruct
    private void initializeKibanaDashboards() {
        if (isInitialized) {
            log.info("Kibana仪表板已经初始化过，跳过初始化过程");
            return;
        }

        try {
            log.info("开始初始化Kibana监控仪表板");
            createIndexPatternsIfNotExist();
            initializeDashboards();
            isInitialized = true;
            log.info("Kibana监控仪表板初始化完成");
        } catch (Exception e) {
            log.error("初始化Kibana监控仪表板失败", e);
        }
    }

    private void initializeDashboards() {
        monitoringConfig.getKibana().getDashboards().forEach(dashboard -> {
            try {
                ResponseEntity<Map> response = kibanaClient.find("dashboard", dashboard.getTitle());
                String dashboardId = null;

                if (response.getStatusCode() == HttpStatus.OK) {
                    List<Map<String, Object>> hits = (List<Map<String, Object>>) ((Map<?, ?>) response.getBody())
                            .get("saved_objects");
                    if (!hits.isEmpty()) {
                        dashboardId = (String) ((Map<?, ?>) hits.get(0)).get("id");
                    }
                }

                if (dashboardId == null) {
                    dashboardId = createDashboard(dashboard);
                    if (dashboardId != null) {
                        createAndAddVisualizations(dashboard, dashboardId);
                    }
                } else {
                    log.info("仪表板已存在: {}", dashboard.getTitle());
                    updateDashboardVisualizations(dashboard, dashboardId);
                }
            } catch (Exception e) {
                log.error("处理仪表板失败: {}", dashboard.getTitle(), e);
            }
        });
    }

    private String createDashboard(KibanaMonitoringConfig.DashboardConfig dashboard) {
        try {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", dashboard.getTitle());
            attributes.put("description", dashboard.getDescription());
            attributes.put("panelsJSON", "[]");
            attributes.put("version", 1);
            attributes.put("timeRestore", false);
            attributes.put("optionsJSON", objectMapper.writeValueAsString(Map.of(
                    "useMargins", true,
                    "hidePanelTitles", false)));
            attributes.put("kibanaSavedObjectMeta", Map.of(
                    "searchSourceJSON", objectMapper.writeValueAsString(Map.of(
                            "query", Map.of("query", "", "language", "kuery"),
                            "filter", List.of()))));

            ResponseEntity<Map> response = kibanaClient.create("dashboard", null, attributes, null);

            if (response.getStatusCode() == HttpStatus.OK) {
                String dashboardId = (String) ((Map<?, ?>) response.getBody()).get("id");
                log.info("创建仪表板成功: {} (ID: {})", dashboard.getTitle(), dashboardId);
                return dashboardId;
            }
            return null;
        } catch (Exception e) {
            log.error("创建仪表板失败: {}", dashboard.getTitle(), e);
            return null;
        }
    }

    private void createAndAddVisualizations(KibanaMonitoringConfig.DashboardConfig dashboard, String dashboardId) {
        dashboard.getVisualizations().forEach(vis -> {
            String visId = createVisualization(vis);
            if (visId != null) {
                addVisualizationToDashboard(dashboardId, visId, vis);
            }
        });
    }

    private void updateDashboardVisualizations(KibanaMonitoringConfig.DashboardConfig dashboard, String dashboardId) {
        try {
            String url = "/api/saved_objects/dashboard/" + dashboardId;
            HttpHeaders headers = getHeaders();

            // 获取当前仪表板的可视化图表
            ResponseEntity<Map> response = kibanaClient.get(url, headers, Map.class);
            Map<String, Object> dashboardAttributes = (Map<String, Object>) ((Map<?, ?>) response.getBody())
                    .get("attributes");

            // 构建面板配置
            List<Map<String, Object>> panels = new ArrayList<>();
            List<Map<String, Object>> references = new ArrayList<>();
            int panelIndex = 0;

            for (KibanaMonitoringConfig.VisualizationConfig vis : dashboard.getVisualizations()) {
                String visId = createVisualization(vis);
                if (visId != null) {
                    // 计算面板位置
                    int x = (panelIndex * 24) % 48;
                    int y = (panelIndex / 2) * 15;

                    // 创建面板配置
                    Map<String, Object> panel = new HashMap<>();
                    panel.put("version", "8.11.1");
                    panel.put("type", "visualization");
                    panel.put("gridData", Map.of(
                            "x", x,
                            "y", y,
                            "w", 24,
                            "h", 15,
                            "i", visId));
                    panel.put("panelIndex", visId);
                    panel.put("embeddableConfig", Map.of());
                    panel.put("panelRefName", "panel_" + visId);
                    panels.add(panel);

                    // 添加引用
                    references.add(Map.of(
                            "name", "panel_" + visId,
                            "type", "visualization",
                            "id", visId));

                    panelIndex++;
                }
            }

            // 更新仪表板属性
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", dashboard.getTitle());
            attributes.put("description", dashboard.getDescription());
            attributes.put("version", 1);
            attributes.put("panelsJSON", objectMapper.writeValueAsString(panels));
            attributes.put("optionsJSON", objectMapper.writeValueAsString(Map.of(
                    "useMargins", true,
                    "hidePanelTitles", false)));
            attributes.put("timeRestore", false);
            attributes.put("kibanaSavedObjectMeta", Map.of(
                    "searchSourceJSON", objectMapper.writeValueAsString(Map.of(
                            "query", Map.of("query", "", "language", "kuery"),
                            "filter", new ArrayList<>()))));

            // 构建更新请求
            Map<String, Object> updateRequest = new HashMap<>();
            updateRequest.put("attributes", attributes);
            updateRequest.put("references", references);

            // 发送更新请求
            String updateUrl = url + "?overwrite=true";
            HttpEntity<Map<String, Object>> updateEntity = new HttpEntity<>(updateRequest, headers);
            kibanaClient.put(updateUrl, updateEntity, Map.class);

            log.info("更新仪表板可视化图表成功: {}", dashboard.getTitle());
        } catch (Exception e) {
            log.error("更新仪表板可视化图表失败: {}", dashboard.getTitle(), e);
        }
    }

    private void addVisualizationToDashboard(String dashboardId, String visId,
            KibanaMonitoringConfig.VisualizationConfig vis) {
        try {
            String url = "/api/saved_objects/dashboard/" + dashboardId;
            HttpHeaders headers = getHeaders();

            ResponseEntity<Map> getResponse = kibanaClient.get(url, headers, Map.class);
            Map<?, ?> dashboard = (Map<?, ?>) getResponse.getBody();

            String panelsJSON = (String) ((Map<?, ?>) dashboard.get("attributes")).get("panelsJSON");
            List<Map<String, Object>> panels = objectMapper.readValue(panelsJSON, PANELS_TYPE);

            // 计算新面板的位置
            int panelIndex = panels.size();
            int x = (panelIndex * 24) % 48; // 每行最多放置2个面板
            int y = (panelIndex / 2) * 15; // 每个面板高度为15

            Map<String, Object> newPanel = new HashMap<>();
            newPanel.put("version", "8.5.0");
            newPanel.put("type", "visualization");
            newPanel.put("id", visId);
            newPanel.put("gridData", Map.of(
                    "x", x,
                    "y", y,
                    "w", 24,
                    "h", 15,
                    "i", String.valueOf(panelIndex)));
            newPanel.put("panelIndex", String.valueOf(panelIndex));
            newPanel.put("embeddableConfig", new HashMap<>());
            newPanel.put("title", vis.getTitle());

            panels.add(newPanel);

            String updateUrl = url + "?overwrite=true";
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("panelsJSON", objectMapper.writeValueAsString(panels));

            Map<String, Object> updateRequest = new HashMap<>();
            updateRequest.put("attributes", attributes);
            updateRequest.put("references", List.of(Map.of(
                    "id", visId,
                    "name", vis.getTitle(),
                    "type", "visualization")));

            HttpEntity<Map<String, Object>> updateEntity = new HttpEntity<>(updateRequest, headers);
            kibanaClient.put(updateUrl, updateEntity, String.class);

            log.info("添加可视化图表到仪表板成功: {} -> {}", vis.getTitle(), dashboardId);
        } catch (Exception e) {
            log.error("添加可视化图表到仪表板失败: {} -> {}", vis.getTitle(), dashboardId, e);
        }
    }

    private void createIndexPatternsIfNotExist() {
        for (KibanaMonitoringConfig.IndexPattern pattern : monitoringConfig.getKibana().getIndexPatterns()) {
            try {
                Map<String, Object> request = new HashMap<>();
                Map<String, Object> dataView = new HashMap<>();

                // 设置基本信息
                dataView.put("title", pattern.getTitle());
                dataView.put("name", pattern.getName());
                dataView.put("timeFieldName", "@timestamp");

                // 构建 runtimeFieldMap
                Map<String, Object> runtimeFieldMap = new HashMap<>();
                pattern.getFields().forEach((fieldName, fieldConfig) -> {
                    Map<String, Object> fieldDef = new HashMap<>();
                    fieldDef.put("type", fieldConfig.getType());

                    Map<String, Object> script = new HashMap<>();
                    script.put("source", String.format("emit(doc['%s'].value)", fieldName));
                    fieldDef.put("script", script);

                    runtimeFieldMap.put(fieldName, fieldDef);
                });
                dataView.put("runtimeFieldMap", runtimeFieldMap);

                // 构建最终请求体
                request.put("data_view", dataView);
                request.put("override", true);

                ResponseEntity<Map> response = kibanaClient.createDataView(request, true);
                if (response.getStatusCode() == HttpStatus.OK) {
                    log.info("创建索引模式成功: {}", pattern.getName());
                }
            } catch (Exception e) {
                log.error("创建索引模式失败: {}", pattern.getName(), e);
            }
        }
    }

    private String createVisualization(KibanaMonitoringConfig.VisualizationConfig visualization) {
        try {
            String visId = "vis_" + visualization.getTitle()
                    .toLowerCase()
                    .replaceAll("[^a-z0-9]", "_")
                    .replaceAll("_+", "_")
                    .replaceAll("^_|_$", "");

            // 修改 API 路径
            String url = "/api/saved_objects/visualization/" + visId;

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", visualization.getTitle());
            attributes.put("type", getKibanaVisualizationType(visualization.getType()));

            // Build visualization state
            Map<String, Object> visState = new HashMap<>();
            visState.put("title", visualization.getTitle());
            visState.put("type", getKibanaVisualizationType(visualization.getType()));
            visState.put("params", getVisualizationParams(visualization));
            visState.put("aggs", createAggregations(visualization));
            // 添加 Kibana 8.5.0 版本特定配置
            visState.put("version", "8.5.0");
            attributes.put("visState", objectMapper.writeValueAsString(visState));

            // Build search source
            Map<String, Object> searchSource = new HashMap<>();
            searchSource.put("index", visualization.getIndex());
            searchSource.put("query", Map.of("query", "", "language", "kuery", "query", ""));
            searchSource.put("filter", List.of());
            searchSource.put("version", "8.5.0");

            // Add time range
            Map<String, Object> timeRange = new HashMap<>();
            timeRange.put("from", "now-24h");
            timeRange.put("to", "now");
            searchSource.put("timeRange", timeRange);

            attributes.put("kibanaSavedObjectMeta", Map.of(
                    "searchSourceJSON", objectMapper.writeValueAsString(searchSource)));
            attributes.put("version", "8.5.0");

            List<Map<String, Object>> references = List.of(Map.of(
                    "name", "kibanaSavedObjectMeta.searchSourceJSON.index",
                    "type", "index-pattern",
                    "id", visualization.getIndex()));

            Map<String, Object> request = new HashMap<>();
            request.put("attributes", attributes);
            request.put("references", references);

            ResponseEntity<Map> response = kibanaClient.create("visualization", visId, attributes, references);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("Successfully created visualization: {}", visualization.getTitle());
                return visId;
            } else {
                log.error("Failed to create visualization: {} (status: {})", visualization.getTitle(),
                        response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("Error creating visualization: {} (error: {})", visualization.getTitle(), e.getMessage(), e);
            return null;
        }
    }

    private Map<String, Object> getVisualizationParams(
            KibanaMonitoringConfig.VisualizationConfig visualization) {
        return VisualizationParamsFactory.createParams(visualization);
    }

    private String getKibanaVisualizationType(String type) {
        return switch (type) {
            case "line", "area" -> "line";
            case "bar" -> "histogram";
            case "pie" -> "pie";
            case "gauge" -> "gauge";
            case "metric" -> "metric";
            default -> type;
        };
    }

    private Map<String, Object> createAggregations(KibanaMonitoringConfig.VisualizationConfig visualization) {
        Map<String, Object> aggs = new HashMap<>();

        try {
            if (visualization.getMetrics() != null) {
                Map<String, Object> metricsAgg = new HashMap<>();
                String aggType = (String) visualization.getMetrics().get("aggregation");
                String field = (String) visualization.getMetrics().get("field");

                if (aggType == null) {
                    log.error("聚合类型不能为空: {}", visualization.getTitle());
                    return aggs;
                }

                metricsAgg.put("id", "1");
                metricsAgg.put("enabled", true);
                metricsAgg.put("type", aggType);
                metricsAgg.put("schema", "metric");

                Map<String, Object> params = new HashMap<>();
                if (!"count".equals(aggType)) {
                    if (field == null) {
                        log.error("字段名不能为空: {} (聚合类型: {})", visualization.getTitle(), aggType);
                        return aggs;
                    }
                    params.put("field", field);
                }

                switch (aggType) {
                    case "avg", "sum", "min", "max" -> params.put("customLabel", field);
                    case "cardinality" -> {
                        params.put("precision_threshold", 3000);
                        params.put("customLabel", "Unique " + field);
                    }
                    case "count" -> params.put("customLabel", "Count");
                    default -> {
                        log.warn("未知的聚合类型: {} (图表: {})", aggType, visualization.getTitle());
                        return aggs;
                    }
                }

                metricsAgg.put("params", params);
                aggs.put("1", metricsAgg);
            }

            if (visualization.getBuckets() != null) {
                Map<String, Object> bucketConfig = visualization.getBuckets();
                Map<String, Object> bucketsAgg = new HashMap<>();
                bucketsAgg.put("id", "2");
                bucketsAgg.put("enabled", true);
                bucketsAgg.put("schema", "segment");

                if (bucketConfig.containsKey("date_histogram")) {
                    Map<String, Object> dateHistogram = (Map<String, Object>) bucketConfig.get("date_histogram");
                    String field = (String) dateHistogram.get("field");
                    String interval = (String) dateHistogram.get("interval");

                    if (field == null || interval == null) {
                        log.error("date_histogram配置错误: {} (field: {}, interval: {})",
                                visualization.getTitle(), field, interval);
                        return aggs;
                    }

                    bucketsAgg.put("type", "date_histogram");
                    Map<String, Object> params = new HashMap<>();
                    params.put("field", field);
                    params.put("useNormalizedEsInterval", true);
                    params.put("interval", interval);
                    params.put("drop_partials", false);
                    params.put("min_doc_count", 0);
                    params.put("extended_bounds", Map.of());
                    params.put("customLabel", "Time");
                    bucketsAgg.put("params", params);

                } else if (bucketConfig.containsKey("terms")) {
                    Map<String, Object> terms = (Map<String, Object>) bucketConfig.get("terms");
                    String field = (String) terms.get("field");
                    Integer size = (Integer) terms.get("size");

                    if (field == null) {
                        log.error("terms配置错误: {} (field: {})", visualization.getTitle(), field);
                        return aggs;
                    }

                    bucketsAgg.put("type", "terms");
                    Map<String, Object> params = new HashMap<>();
                    params.put("field", field);
                    params.put("orderBy", "1");
                    params.put("order", "desc");
                    params.put("size", size != null ? size : 10);
                    params.put("otherBucket", false);
                    params.put("otherBucketLabel", "Other");
                    params.put("missingBucket", false);
                    params.put("missingBucketLabel", "Missing");
                    params.put("customLabel", field);
                    bucketsAgg.put("params", params);
                }

                aggs.put("2", bucketsAgg);
            }
        } catch (Exception e) {
            log.error("创建聚合配置失败: {} (错误: {})", visualization.getTitle(), e.getMessage());
        }

        return aggs;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("kbn-xsrf", "true");

        // 使用配置的用户名和密码
        String auth = "elastic" + ":" + "123456";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + new String(encodedAuth));
        return headers;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        if (waitForKibana()) {
            initializeKibanaDashboards();
        } else {
            log.error("Kibana服务未就绪，初始化失败");
        }
    }

    private boolean waitForKibana() {
        KibanaMonitoringConfig.HealthCheckConfig config = monitoringConfig.getKibana().getHealthCheck();
        log.info("等待Kibana服务就绪...");

        for (int i = 0; i < config.getMaxAttempts(); i++) {
            try {
                ResponseEntity<String> response = kibanaClient.exchange("/api/status", HttpMethod.GET, null,
                        String.class);
                if (response.getStatusCode() == HttpStatus.OK) {
                    log.info("Kibana服务已就绪");
                    return true;
                }
            } catch (Exception e) {
                log.warn("Kibana服务检查失败，将在{}秒后重试", config.getIntervalSeconds());
            }

            try {
                TimeUnit.SECONDS.sleep(config.getIntervalSeconds());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
}