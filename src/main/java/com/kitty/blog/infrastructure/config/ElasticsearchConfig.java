package com.kitty.blog.infrastructure.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.util.Map;

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
                RestClientTransport transport = new RestClientTransport(restClient, jsonpMapper);
                ElasticsearchClient client = new ElasticsearchClient(transport);

                try {
                        // 创建生命周期策略
                        client.ilm().putLifecycle(r -> r
                                        .name("blog-policy")
                                        .policy(p -> p
                                                        .phases(ph -> ph
                                                                        .hot(h -> h
                                                                                        .actions(JsonData.of(Map.of(
                                                                                                        "set_priority",
                                                                                                        Map.of("priority",
                                                                                                                        100)))))
                                                                        .warm(w -> w
                                                                                        .minAge(Time.of(t -> t
                                                                                                        .time("7d")))
                                                                                        .actions(JsonData.of(Map.of(
                                                                                                        "set_priority",
                                                                                                        Map.of("priority",
                                                                                                                        50),
                                                                                                        "readonly",
                                                                                                        Map.of()))))
                                                                        .cold(c -> c
                                                                                        .minAge(Time.of(t -> t
                                                                                                        .time("30d")))
                                                                                        .actions(JsonData.of(Map.of(
                                                                                                        "set_priority",
                                                                                                        Map.of("priority",
                                                                                                                        0)))))
                                                                        .delete(d -> d
                                                                                        .minAge(Time.of(t -> t
                                                                                                        .time("90d")))
                                                                                        .actions(JsonData.of(Map.of(
                                                                                                        "delete",
                                                                                                        Map.of())))))));

                        String[] indices = {
                                        "blog-api-metrics",
                                        "blog-error",
                                        "blog-user-activity",
                                        "blog-post-metrics",
                                        "blog-system-metrics"
                        };

                        String today = java.time.LocalDate.now()
                                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy.MM.dd"));

                        for (String index : indices) {
                                // 创建具体的初始索引
                                String initialIndex = String.format("%s-%s", index, today);

                                // 检查索引是否存在
                                if (!client.indices().exists(e -> e.index(initialIndex)).value()) {
                                        // 创建新索引
                                        client.indices().create(c -> c
                                                        .index(initialIndex)
                                                        .settings(s -> s
                                                                        .numberOfShards("1")
                                                                        .numberOfReplicas("0")
                                                                        .refreshInterval(new Time.Builder().time("5s")
                                                                                        .build())
                                                                        .lifecycle(l -> l.name("blog-policy")))
                                                        .mappings(m -> m
                                                                        .properties("@timestamp", p -> p
                                                                                        .date(d -> d
                                                                                                        .format("strict_date_optional_time||epoch_millis")))
                                                                        .properties("metrics_name",
                                                                                        p -> p.keyword(k -> k))
                                                                        .properties("value", p -> p.double_(d -> d))
                                                                        .properties("log_type", p -> p.keyword(k -> k))
                                                                        .properties("application",
                                                                                        p -> p.keyword(k -> k))
                                                                        .properties("phase", p -> p.keyword(k -> k))
                                                                        .properties("class", p -> p.keyword(k -> k))
                                                                        .properties("method", p -> p.keyword(k -> k))
                                                                        .properties("args", p -> p.text(t -> t))
                                                                        .properties("tags", p -> p.object(o -> o
                                                                                        .properties("host", t -> t
                                                                                                        .keyword(k -> k))
                                                                                        .properties("service", t -> t
                                                                                                        .keyword(k -> k))
                                                                                        .properties("environment",
                                                                                                        t -> t.keyword(k -> k))))));
                                }
                        }
                } catch (Exception e) {
                        log.error("创建 Elasticsearch 索引失败", e);
                }

                return client;
        }
}