package com.kitty.blog.domain.service.system.kibana;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitty.blog.infrastructure.config.system.monitor.KibanaMonitoringConfig;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Component
public class KibanaApiClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KibanaMonitoringConfig kibanaMonitoringConfig;

    @Value("${blog.monitor.kibana.url}")
    private String kibanaUrl;

    @Value("${blog.monitor.kibana.username}")
    private String kibanaUsername;

    @Value("${blog.monitor.kibana.password}")
    private String kibanaPassword;

    private static final String API_PREFIX = "/api";

    public <T> ResponseEntity<T> exchange(String path, HttpMethod method, Object body, Class<T> responseType) {
        String url = kibanaUrl + path;
        HttpEntity<Object> entity = new HttpEntity<>(body, getHeaders());
        return restTemplate.exchange(url, method, entity, responseType);
    }

//    public ResponseEntity<Map> find(String type, String title) {
//        String path = API_PREFIX + "/saved_objects/_find";
//
//        // 构建查询参数
//        StringBuilder queryParams = new StringBuilder("?");
//        queryParams.append("type=").append(type);
//        queryParams.append("&fields=").append(String.join(",", "id", "title", "type", "updated_at"));
//        queryParams.append("&per_page=1");
//        if (title != null && !title.isEmpty()) {
//            queryParams.append("&search=").append(title);
//            queryParams.append("&search_fields=").append("title");
//        }
//
//        path += queryParams.toString();
//
//        return exchange(path, HttpMethod.GET, null, Map.class);
//    }

//    public ResponseEntity<Map> update(String type, String id, Map<String, Object> attributes,
//            List<Map<String, Object>> references) {
//        String path = API_PREFIX + "/saved_objects/" + type + "/" + id + "?overwrite=true";
//        Map<String, Object> request = new HashMap<>();
//        request.put("attributes", attributes != null ? attributes : new HashMap<>());
//        if (references != null && !references.isEmpty()) {
//            request.put("references", references);
//        }
//        return exchange(path, HttpMethod.PUT, request, Map.class);
//    }
//
//    public <T> ResponseEntity<T> get(String path, HttpHeaders headers, Class<T> responseType) {
//        String url = kibanaUrl + path;
//        HttpEntity<?> entity = new HttpEntity<>(headers);
//        return restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
//    }
//
//    public <T> ResponseEntity<T> put(String path, HttpEntity<?> entity, Class<T> responseType) {
//        String url = kibanaUrl + path;
//        return restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
//    }

//    public <T> ResponseEntity<T> post(String path, HttpEntity<?> entity, Class<T> responseType) {
//        String url = kibanaUrl + path;
//        return restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
//    }

//    public <T> ResponseEntity<T> put(String path, Map<String, Object> body, Class<T> responseType) {
//        // 如果是 visualization 相关的请求，使用正确的 API 路径
//        if (path.contains("/v8/objects/visualization/")) {
//            path = path.replace("/v8/objects/visualization/", "/saved_objects/visualization/");
//        }
//
//        String url = kibanaUrl + path;
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, getHeaders());
//        return restTemplate.exchange(url, HttpMethod.PUT, entity, responseType);
//    }

//    public <T> ResponseEntity<T> post(String path, Map<String, Object> body, Class<T> responseType) {
//        String url = kibanaUrl + path;
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, getHeaders());
//        return restTemplate.exchange(url, HttpMethod.POST, entity, responseType);
//    }

//    public ResponseEntity<Map> createDataView(Map<String, Object> request, boolean override) {
//        String path = API_PREFIX + "/data_views/data_view";
//        return exchange(path, HttpMethod.POST, request, Map.class);
//    }

//    public ResponseEntity<Map> create(String type, String id, Map<String, Object> attributes,
//            List<Map<String, Object>> references) {
//        String path = API_PREFIX + "/saved_objects/" + type + (id != null ? "/" + id : "") + "?overwrite=true";
//        Map<String, Object> request = new HashMap<>();
//        request.put("attributes", attributes != null ? attributes : new HashMap<>());
//        if (references != null && !references.isEmpty()) {
//            request.put("references", references);
//        }
//
//        // 添加默认的时间范围和刷新设置
//        if (type.equals("dashboard")) {
//            Map<String, Object> timeRange = new HashMap<>();
//            timeRange.put("from", "now-7d");
//            timeRange.put("to", "now");
//            timeRange.put("refreshInterval", Map.of(
//                    "pause", false,
//                    "value", 5000 // 5秒刷新一次
//            ));
//
//            if (attributes != null) {
//                attributes.put("timeRange", timeRange);
//            }
//        }
//
//        // 修改版本信息为 8.9.0
//        Map<String, String> migrationVersion = new HashMap<>();
//        switch (type) {
//            case "dashboard":
//                migrationVersion.put("dashboard", kibanaMonitoringConfig.getKibana().getVersion());
//                break;
//            case "visualization":
//                migrationVersion.put("visualization", kibanaMonitoringConfig.getKibana().getVersion());
//                break;
//            case "index-pattern":
//                migrationVersion.put("index-pattern", kibanaMonitoringConfig.getKibana().getVersion());
//                break;
//            default:
//                migrationVersion.put(type, kibanaMonitoringConfig.getKibana().getVersion());
//        }
//        request.put("migrationVersion", migrationVersion);
//
//        return exchange(path, HttpMethod.POST, request, Map.class);
//    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("kbn-xsrf", "true");
        headers.setBasicAuth("elastic", "123456");
        headers.set("Accept", "*/*");
        return headers;
    }

    /**
     * 获取Kibana实际运行版本
     * 
     * @return Kibana版本号
     */
    public String detectKibanaVersion() {
        try {
            String path = "/api/status";
            ResponseEntity<Map> response = exchange(path, HttpMethod.GET, null, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (body.containsKey("version")) {
                    Map<String, Object> versionInfo = (Map<String, Object>) body.get("version");
                    String detectedVersion = (String) versionInfo.get("number");
                    log.info("检测到Kibana实际运行版本: {}", detectedVersion);
                    return detectedVersion;
                }
            }
            log.warn("无法检测Kibana版本，使用配置的默认版本: {}", kibanaMonitoringConfig.getKibana().getVersion());
        } catch (Exception e) {
            log.error("检测Kibana版本失败: {}", e.getMessage());
        }
        return kibanaMonitoringConfig.getKibana().getVersion();
    }

    /**
     * 初始化方法，在服务启动时自动检测并设置Kibana版本
     */
    @PostConstruct
    public void init() {
        try {
            String detectedVersion = detectKibanaVersion();
            // 更新配置中的版本
            kibanaMonitoringConfig.getKibana().setVersion(detectedVersion);
            log.info("已将Kibana版本设置为: {}", detectedVersion);
        } catch (Exception e) {
            log.warn("初始化Kibana版本失败，使用默认版本: {}", kibanaMonitoringConfig.getKibana().getVersion());
        }
    }

    public ResponseEntity<String> importNdjsonWithForm(String filePath) {
        String path = API_PREFIX + "/saved_objects/_import?createNewCopies=true";

        // 设置正确的请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("kbn-xsrf", "true");
        headers.setBasicAuth(kibanaUsername, kibanaPassword);

        // 不要设置 Content-Type，Spring 会自动处理 multipart/form-data

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        try {
            Path file = Paths.get(filePath);
            if (!Files.exists(file)) {
                throw new IllegalArgumentException("文件不存在: " + filePath);
            }

            // 使用自定义 Resource 确保正确的文件名和内容类型
            body.add("file", new InputStreamResource(Files.newInputStream(file)) {
                @Override
                public String getFilename() {
                    return file.getFileName().toString();
                }

                @Override
                public long contentLength() throws IOException {
                    return Files.size(file);
                }
            });

            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);
            log.info("开始上传文件: {}", filePath);
            return restTemplate.postForEntity(kibanaUrl + path, entity, String.class);
        } catch (Exception e) {
            log.error("文件上传失败: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + filePath, e);
        }
    }
}