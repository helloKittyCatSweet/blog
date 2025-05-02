package com.kitty.blog.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Kitty Blog API")
                        .version("1.0")
                        .description("API documentation for Kitty Blog application"))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    // 用户模块 API 分组
    @Bean
    public GroupedOpenApi userModuleApi() {
        return GroupedOpenApi.builder()
                .group("用户模块") // Swagger UI 下拉框中显示的名字
                .pathsToMatch("/api/user/**", "/api/auth/**")
                .build();
    }

    // 文章模块 API 分组
    @Bean
    public GroupedOpenApi postModuleApi() {
        return GroupedOpenApi.builder()
                .group("文章模块") // Swagger UI 下拉框中显示的名字
                .pathsToMatch("/api/post/**")
                .build();
    }

    // 全局模块 API 分组
    @Bean
    public GroupedOpenApi globalModuleApi() {
        return GroupedOpenApi.builder()
                .group("全局模块") // Swagger UI 下拉框中显示的名字
                .pathsToMatch("/api/category/**", "/api/tag/**", "/api/comment/**")
                .build();
    }

    // WebSocket API 分组
    @Bean
    public GroupedOpenApi webSocketApi() {
        return GroupedOpenApi.builder()
                .group("WebSocket和系统消息") // Swagger UI 下拉框中显示的名字
                .pathsToMatch("/api/ws/**")
                .build();
    }

    // 系统监控日志 API 分组
    @Bean
    public GroupedOpenApi systemMonitorApi() {
        return GroupedOpenApi.builder()
                .group("系统监控") // Swagger UI 下拉框中显示的名字
                .pathsToMatch("/api/admin/logs/**")
                .build();
    }

    // 全文搜索API
    @Bean
    public GroupedOpenApi searchApi() {
        return GroupedOpenApi.builder()
                .group("全文搜索") // Swagger UI 下拉框中显示的名字
                .pathsToMatch("/api/search/**")
                .build();
    }

    // 统计数据模块
    @Bean
    public GroupedOpenApi analyticsApi() {
        return GroupedOpenApi.builder()
                .group("统计数据") // Swagger UI 下拉框中显示的名字
                .pathsToMatch("/api/stat/**")
                .build();
    }

    // 联系我们模块
    @Bean
    public GroupedOpenApi contactApi() {
        return GroupedOpenApi.builder()
                .group("联系我们") // Swagger UI 下拉框中显示的名字
                .pathsToMatch("/api/contact/**")
                .build();
    }
}
   