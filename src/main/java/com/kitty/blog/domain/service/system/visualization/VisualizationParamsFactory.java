package com.kitty.blog.domain.service.system.visualization;

import com.kitty.blog.infrastructure.config.monitor.KibanaMonitoringConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisualizationParamsFactory {

    public static Map<String, Object> createParams(KibanaMonitoringConfig.VisualizationConfig visualization) {
        Map<String, Object> params = switch (visualization.getType()) {
            case "line", "area" -> createLineOrAreaParams(visualization);
            case "bar" -> createBarParams();
            case "pie" -> createPieParams();
            case "gauge" -> createGaugeParams();
            case "metric" -> createMetricParams();
            default -> new HashMap<>(Map.of("type", visualization.getType()));
        };

        // Merge custom parameters if provided
        Map<String, Object> customParams = visualization.getParams();
        if (customParams != null) {
            params.putAll(customParams);
        }

        return params;
    }

    private static Map<String, Object> createLineOrAreaParams(
            KibanaMonitoringConfig.VisualizationConfig visualization) {
        Map<String, Object> params = new HashMap<>();
        params.put("type", visualization.getType());
        params.put("grid", Map.of(
                "categoryLines", false,
                "valueAxis", "ValueAxis-1",
                "categoryAxis", "CategoryAxis-1"));
        params.put("categoryAxes", List.of(Map.of(
                "id", "CategoryAxis-1",
                "type", "category",
                "position", "bottom",
                "show", true,
                "scale", Map.of("type", "linear"),
                "labels", Map.of("show", true, "truncate", 100),
                "title", Map.of())));
        params.put("valueAxes", List.of(Map.of(
                "id", "ValueAxis-1",
                "name", "LeftAxis-1",
                "type", "value",
                "position", "left",
                "show", true,
                "scale", Map.of("type", "linear", "mode", "normal"),
                "labels", Map.of("show", true, "rotate", 0, "filter", false, "truncate", 100),
                "title", Map.of("text", "Count"))));
        params.put("seriesParams", List.of(Map.of(
                "show", true,
                "type", visualization.getType(),
                "mode", "normal",
                "data", Map.of("label", "Count", "id", "1"),
                "valueAxis", "ValueAxis-1",
                "drawLinesBetweenPoints", true,
                "lineWidth", 2,
                "showCircles", true)));
        return params;
    }

    private static Map<String, Object> createBarParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("type", "histogram");
        params.put("grid", Map.of(
                "categoryLines", false,
                "valueAxis", "ValueAxis-1",
                "categoryAxis", "CategoryAxis-1"));
        params.put("categoryAxes", List.of(Map.of(
                "id", "CategoryAxis-1",
                "type", "category",
                "position", "bottom",
                "show", true,
                "scale", Map.of("type", "linear"),
                "labels", Map.of("show", true, "truncate", 100, "rotate", 0),
                "title", Map.of())));
        params.put("valueAxes", List.of(Map.of(
                "id", "ValueAxis-1",
                "name", "LeftAxis-1",
                "type", "value",
                "position", "left",
                "show", true,
                "scale", Map.of("type", "linear", "mode", "normal"),
                "labels", Map.of("show", true, "rotate", 0, "filter", false, "truncate", 100),
                "title", Map.of("text", "Count"))));
        params.put("seriesParams", List.of(Map.of(
                "show", true,
                "type", "histogram",
                "mode", "normal",
                "data", Map.of("label", "Count", "id", "1"),
                "valueAxis", "ValueAxis-1")));
        return params;
    }

    private static Map<String, Object> createPieParams() {
        return Map.of(
                "type", "pie",
                "addTooltip", true,
                "addLegend", true,
                "legendPosition", "right",
                "isDonut", false,
                "labels", Map.of(
                        "show", true,
                        "values", true,
                        "last_level", true,
                        "truncate", 100));
    }

    private static Map<String, Object> createGaugeParams() {
        Map<String, Object> gauge = new HashMap<>();
        gauge.put("verticalSplit", false);
        gauge.put("extendRange", true);
        gauge.put("percentageMode", false);
        gauge.put("gaugeType", "Arc");
        gauge.put("gaugeStyle", "Full");
        gauge.put("backStyle", "Full");
        gauge.put("orientation", "vertical");
        gauge.put("colorSchema", "Green to Red");
        gauge.put("gaugeColorMode", "Labels");
        gauge.put("colorsRange", List.of(
                Map.of("from", 0, "to", 50),
                Map.of("from", 50, "to", 75),
                Map.of("from", 75, "to", 100)));
        gauge.put("invertColors", false);
        gauge.put("labels", Map.of("show", true, "color", "black"));
        gauge.put("scale", Map.of("show", true, "labels", false, "color", "#333"));
        gauge.put("type", "meter");
        gauge.put("style", Map.of(
                "bgWidth", 0.9,
                "width", 0.9,
                "mask", false,
                "bgMask", false,
                "maskBars", 50,
                "bgFill", "#eee",
                "bgColor", false,
                "subText", "",
                "fontSize", 60,
                "labelColor", true));

        return Map.of(
                "type", "gauge",
                "addTooltip", true,
                "addLegend", false,
                "isDisplayWarning", false,
                "gauge", gauge);
    }

    private static Map<String, Object> createMetricParams() {
        return Map.of(
                "type", "metric",
                "addTooltip", true,
                "addLegend", false,
                "metric", Map.of(
                        "percentageMode", false,
                        "useRanges", false,
                        "colorSchema", "Green to Red",
                        "metricColorMode", "None",
                        "colorsRange", List.of(Map.of("from", 0, "to", 10000)),
                        "labels", Map.of("show", true),
                        "style", Map.of(
                                "bgFill", "#000",
                                "bgColor", false,
                                "labelColor", false,
                                "subText", "",
                                "fontSize", 60)));
    }
}