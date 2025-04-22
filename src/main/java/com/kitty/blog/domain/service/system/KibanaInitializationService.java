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
import java.util.UUID;
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


    /**
     * Create a new Kibana dashboard with the given title and description.
     *
     * @param dashboard The configuration of the dashboard to be created.
     * @return The ID of the created dashboard, or null if the creation failed.
     */
    private String createDashboard(KibanaMonitoringConfig.DashboardConfig dashboard) {
        try {
            // 生成仪表板ID
            String dashboardId = dashboard.getTitle().toLowerCase()
                    .replaceAll("[^a-z0-9]", "_")
                    .replaceAll("_+", "_")
                    .replaceAll("^_|_$", "");

            // 构建请求体
            Map<String, Object> request = new HashMap<>();

            // 构建 attributes
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", dashboard.getTitle());
            attributes.put("description", dashboard.getDescription());
            attributes.put("panelsJSON", "[]"); // 初始化为空数组，后续会添加可视化
            attributes.put("version", 1);
            attributes.put("timeRestore", false);
            attributes.put("optionsJSON", "{\"useMargins\":true,\"hidePanelTitles\":false}");

            // 构建 kibanaSavedObjectMeta
            Map<String, Object> kibanaSavedObjectMeta = new HashMap<>();
            kibanaSavedObjectMeta.put("searchSourceJSON", "{\"query\":{\"query\":\"\",\"language\":\"kuery\"},\"filter\":[]}");
            attributes.put("kibanaSavedObjectMeta", kibanaSavedObjectMeta);

            request.put("attributes", attributes);

            // 发送创建请求
            String url = "/api/saved_objects/dashboard/" + dashboardId;
            HttpHeaders headers = getHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            ResponseEntity<Map> response = kibanaClient.post(url, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("创建仪表板成功: {} (ID: {})", dashboard.getTitle(), dashboardId);
                return dashboardId;
            } else {
                log.error("创建仪表板失败: {} (状态码: {})", dashboard.getTitle(), response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("创建仪表板异常: {}", dashboard.getTitle(), e);
            return null;
        }
    }

    private void createAndAddVisualizations(KibanaMonitoringConfig.DashboardConfig dashboard, String dashboardId) {
        dashboard.getVisualizations().forEach(vis -> {
            String visId = createLensVisualization(vis);
            if (visId != null) {
                addVisualizationToDashboard(dashboardId, visId, vis);
            }
        });
    }

    private void updateDashboardVisualizations(KibanaMonitoringConfig.DashboardConfig dashboard, String dashboardId) {
        try {
            String url = "/api/saved_objects/dashboard/" + dashboardId;
            HttpHeaders headers = getHeaders();

            ResponseEntity<Map> getResponse = kibanaClient.get(url, headers, Map.class);
            Map<?, ?> dashboardResponse = (Map<?, ?>) getResponse.getBody();

            String panelsJSON = (String) ((Map<?, ?>) dashboardResponse.get("attributes")).get("panelsJSON");
            List<Map<String, Object>> panels = objectMapper.readValue(panelsJSON, PANELS_TYPE);

            // 构建面板配置
            List<Map<String, Object>> references = new ArrayList<>();
            int panelIndex = 0;

            for (KibanaMonitoringConfig.VisualizationConfig vis : dashboard.getVisualizations()) {
                String visId = createLensVisualization(vis);
                if (visId != null) {
                    // 计算面板位置
                    int x = (panelIndex * 24) % 48; // 每行最多放置2个面板
                    int y = (panelIndex / 2) * 15; // 每个面板高度为15

                    // 创建面板配置
                    Map<String, Object> panel = new HashMap<>();
                    panel.put("version", monitoringConfig.getKibana().getVersion());
                    // 使用lens类型而不是visualization
                    panel.put("type", "lens");
                    panel.put("gridData", Map.of(
                            "x", x,
                            "y", y,
                            "w", 24,
                            "h", 15,
                            "i", String.valueOf(panelIndex)));
                    panel.put("panelIndex", String.valueOf(panelIndex));
                    panel.put("embeddableConfig", Map.of());

                    // 使用正确的引用格式
                    String panelRefName = "panel:" + visId;
                    panel.put("panelRefName", panelRefName);
                    panels.add(panel);

                    // 添加引用，使用正确的名称格式
                    references.add(Map.of(
                            "id", visId,
                            "name", panelRefName,
                            "type", "lens"));

                    panelIndex++;
                }
            }

            // 更新仪表板配置
            Map<String, Object> updateRequest = new HashMap<>();
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("panelsJSON", objectMapper.writeValueAsString(panels));
            updateRequest.put("attributes", attributes);
            updateRequest.put("references", references);

            // 发送更新请求
            String updateUrl = "/api/saved_objects/dashboard/" + dashboardId;
            HttpEntity<Map<String, Object>> updateEntity = new HttpEntity<>(updateRequest, headers);
            ResponseEntity<String> updateResponse = kibanaClient.put(updateUrl, updateEntity, String.class);

            if (updateResponse.getStatusCode() == HttpStatus.OK) {
                log.info("更新仪表板可视化图表成功: {}", dashboard.getTitle());
            } else {
                log.error("更新仪表板可视化图表失败: {} (状态码: {})", dashboard.getTitle(), updateResponse.getStatusCode());
            }
        } catch (Exception e) {
            log.error("更新仪表板可视化图表异常: {}", dashboard.getTitle(), e);
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

    private String createLensVisualization(KibanaMonitoringConfig.VisualizationConfig visualization) {
        try {
            // 确保生成唯一的ID
            String title = visualization.getTitle();
            if (title == null || title.trim().isEmpty()) {
                title = "unnamed_visualization_" + System.currentTimeMillis();
            }

            String visId = "vis_" + title.toLowerCase()
                    .replaceAll("[^a-z0-9]", "_")
                    .replaceAll("_+", "_")
                    .replaceAll("^_|_$", "");

            // 添加随机后缀以确保唯一性
            if (visId.equals("vis_") || visId.length() < 5) {
                visId = "vis_" + UUID.randomUUID().toString().substring(0, 8);
            }

            // 使用lens API路径
            String url = "/api/saved_objects/lens/" + visId;

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("title", visualization.getTitle());
            attributes.put("description", "显示" + visualization.getTitle() + "图表");
            attributes.put("visualizationType", getLensVisualizationType(visualization.getType()));

            // 构建Lens state
            Map<String, Object> state = createLensState(visualization);
            attributes.put("state", state);

            // 构建references
            List<Map<String, Object>> references = List.of(Map.of(
                    "id", visualization.getIndex(),
                    "name", "indexpattern-datasource-layer1-col1-col2",
                    "type", "index-pattern"));

            // 构建请求体
            Map<String, Object> request = new HashMap<>();
            request.put("attributes", attributes);
            request.put("references", references);

            // 发送创建请求
            HttpHeaders headers = getHeaders();
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            // 添加overwrite=true参数，以便在对象已存在时覆盖它
            String urlWithParams = url + "?overwrite=true";
            ResponseEntity<Map> response = kibanaClient.post(urlWithParams, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                log.info("成功创建Lens可视化: {} (ID: {})", visualization.getTitle(), visId);
                return visId;
            } else {
                log.error("创建Lens可视化失败: {} (状态码: {})", visualization.getTitle(), response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            log.error("创建Lens可视化异常: {} (错误: {})", visualization.getTitle(), e.getMessage(), e);
            return null;
        }
    }

    private String getLensVisualizationType(String type) {
        return switch (type) {
            case "line", "area" -> "lnsXY";
            case "bar" -> "lnsXY";
            case "pie" -> "lnsPie";
            case "gauge" -> "lnsGauge";
            case "metric" -> "lnsMetric";
            default -> "lnsXY";
        };
    }

    private Map<String, Object> createLensState(KibanaMonitoringConfig.VisualizationConfig visualization) {
        Map<String, Object> state = new HashMap<>();

        // 构建datasourceStates
        Map<String, Object> datasourceStates = new HashMap<>();
        Map<String, Object> indexpattern = new HashMap<>();
        Map<String, Object> layers = new HashMap<>();

        // 构建layer1
        Map<String, Object> layer1 = new HashMap<>();
        layer1.put("columnOrder", List.of("col1", "col2"));

        // 构建columns
        Map<String, Object> columns = new HashMap<>();

        // 时间列或分组列
        Map<String, Object> col1 = new HashMap<>();
        if (visualization.getBuckets() != null && visualization.getBuckets().containsKey("date_histogram")) {
            Map<String, Object> dateHistogram = (Map<String, Object>) visualization.getBuckets().get("date_histogram");
            String field = (String) dateHistogram.get("field");
            String interval = (String) dateHistogram.get("interval");

            col1.put("dataType", "date");
            col1.put("isBucketed", true);
            col1.put("label", "Time");
            col1.put("operationType", "date_histogram");
            col1.put("params", Map.of("interval", interval != null ? interval : "1m"));
            col1.put("sourceField", field != null ? field : "@timestamp");
        } else if (visualization.getBuckets() != null && visualization.getBuckets().containsKey("terms")) {
            Map<String, Object> terms = (Map<String, Object>) visualization.getBuckets().get("terms");
            String field = (String) terms.get("field");
            Integer size = (Integer) terms.get("size");

            col1.put("dataType", "string");
            col1.put("isBucketed", true);
            col1.put("label", field);
            col1.put("operationType", "terms");
            col1.put("params", Map.of("size", size != null ? size : 10));
            col1.put("sourceField", field);
        } else {
            // 默认时间列
            col1.put("dataType", "date");
            col1.put("isBucketed", true);
            col1.put("label", "Time");
            col1.put("operationType", "date_histogram");
            col1.put("params", Map.of("interval", "1m"));
            col1.put("sourceField", "@timestamp");
        }

        // 度量列
        Map<String, Object> col2 = new HashMap<>();
        if (visualization.getMetrics() != null) {
            String field = (String) visualization.getMetrics().get("field");
            String aggregation = (String) visualization.getMetrics().get("aggregation");

            col2.put("dataType", "number");
            col2.put("isBucketed", false);
            col2.put("label", field != null ? field : "Value");
            col2.put("operationType", aggregation != null ? aggregation : "average");
            if ("cardinality".equals(aggregation)) {
                col2.put("params", Map.of("precision_threshold", 3000));
            }
            col2.put("sourceField", field);
        } else {
            // 默认度量列
            col2.put("dataType", "number");
            col2.put("isBucketed", false);
            col2.put("label", "Value");
            col2.put("operationType", "count");
        }

        columns.put("col1", col1);
        columns.put("col2", col2);

        layer1.put("columns", columns);
        layers.put("layer1", layer1);
        indexpattern.put("layers", layers);
        datasourceStates.put("indexpattern", indexpattern);
        state.put("datasourceStates", datasourceStates);

        // 构建visualization
        Map<String, Object> visualizationConfig = new HashMap<>();

        String visType = visualization.getType();
        if ("line".equals(visType) || "area".equals(visType) || "bar".equals(visType)) {
            visualizationConfig.put("axisTitlesVisibilitySettings", Map.of(
                "x", true,
                "yLeft", true,
                "yRight", true
            ));
            visualizationConfig.put("fittingFunction", "None");
            visualizationConfig.put("gridlinesVisibilitySettings", Map.of(
                "x", true,
                "yLeft", true,
                "yRight", true
            ));

            List<Map<String, Object>> layersList = new ArrayList<>();
            Map<String, Object> layerConfig = new HashMap<>();
            layerConfig.put("accessors", List.of("col2"));
            layerConfig.put("layerId", "layer1");

            String seriesType;
            if ("line".equals(visType)) {
                seriesType = "line";
            } else if ("area".equals(visType)) {
                seriesType = "area";
            } else {
                seriesType = "bar_horizontal";
            }

            layerConfig.put("seriesType", seriesType);
            layerConfig.put("xAccessor", "col1");

            List<Map<String, Object>> yConfig = new ArrayList<>();
            Map<String, Object> yConfigItem = new HashMap<>();
            yConfigItem.put("forAccessor", "col2");

            String color;
            if ("line".equals(visType)) {
                color = "blue";
            } else if ("area".equals(visType)) {
                color = "purple";
            } else {
                color = "orange";
            }

            yConfigItem.put("color", color);
            yConfig.add(yConfigItem);

            layerConfig.put("yConfig", yConfig);
            layersList.add(layerConfig);

            visualizationConfig.put("layers", layersList);
            visualizationConfig.put("preferredSeriesType", seriesType);
            visualizationConfig.put("tickLabelsVisibilitySettings", Map.of(
                "x", true,
                "yLeft", true,
                "yRight", true
            ));
            visualizationConfig.put("valueLabels", "hide");
        } else if ("pie".equals(visType)) {
            List<Map<String, Object>> layersList = new ArrayList<>();
            Map<String, Object> layerConfig = new HashMap<>();
            layerConfig.put("categoryDisplay", "default");
            layerConfig.put("groups", List.of("col1"));
            layerConfig.put("layerId", "layer1");
            layerConfig.put("legendDisplay", "default");
            layerConfig.put("metric", "col2");
            layerConfig.put("nestedLegend", false);
            layerConfig.put("percentDecimals", 1);
            layersList.add(layerConfig);

            visualizationConfig.put("shape", "pie");
            visualizationConfig.put("layers", layersList);
        } else if ("gauge".equals(visType)) {
            List<Map<String, Object>> layersList = new ArrayList<>();
            Map<String, Object> layerConfig = new HashMap<>();
            layerConfig.put("layerId", "layer1");
            layerConfig.put("metricAccessor", "col1");
            layerConfig.put("colorMode", "Labels");
            layerConfig.put("maxMode", "percentage");
            layerConfig.put("maxValue", 100);
            layerConfig.put("minMode", "static");
            layerConfig.put("minValue", 0);
            layerConfig.put("ticksPosition", "auto");
            layerConfig.put("gaugeType", "semi");
            layerConfig.put("labelMajorMode", "auto");
            layerConfig.put("labelMinorMode", "none");
            layersList.add(layerConfig);

            visualizationConfig.put("layers", layersList);
        }

        state.put("visualization", visualizationConfig);

        return state;
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