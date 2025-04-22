package com.kitty.blog.infrastructure.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableAutoConfiguration(exclude = {
        ElasticsearchRestClientAutoConfiguration.class
})
@EnableElasticsearchRepositories(basePackages = "com.kitty.blog.domain.repository.search")
@Slf4j
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.host}")
    private String host;

    @Value("${spring.elasticsearch.port}")
    private int port;

    @Value("${spring.elasticsearch.username:elastic}")
    private String username;

    @Value("${spring.elasticsearch.password:123456}")
    private String password;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public JacksonJsonpMapper jacksonJsonpMapper(ObjectMapper objectMapper) {
        return new JacksonJsonpMapper(objectMapper);
    }

    @Bean
    public RestClient restClient() {
        return RestClient.builder(
                new HttpHost(host, port, "http"))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        .setDefaultCredentialsProvider(credentialsProvider()))
                .build();
    }

    private CredentialsProvider credentialsProvider() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
        return credentialsProvider;
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient, JacksonJsonpMapper jsonpMapper) {
        RestClientTransport transport = new RestClientTransport(
                restClient,
                jsonpMapper);
        ElasticsearchClient client = new ElasticsearchClient(transport);

        // Move index creation logic here
        try {
            String[] indices = {
                    "blog-api-metrics-*",
                    "blog-error-logs-*",
                    "blog-user-activity-*",
                    "blog-post-metrics-*",
                    "blog-system-metrics-*"
            };

            for (String index : indices) {
                if (!client.indices().exists(e -> e.index(index)).value()) {
                    client.indices().create(c -> c
                            .index(index)
                            .mappings(m -> m
                                    .properties("@timestamp", p -> p
                                            .date(d -> d
                                                    .format("strict_date_optional_time||epoch_millis")))));
                }
            }
        } catch (Exception e) {
            log.error("创建 Elasticsearch 索引失败", e);
        }

        return client;
    }

}