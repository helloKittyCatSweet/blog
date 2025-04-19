package com.kitty.blog.infrastructure.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
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
public class ElasticsearchConfig{

    @Value("${spring.elasticsearch.host}")
    private String host;

    @Value("${spring.elasticsearch.port}")
    private int port;

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
                new HttpHost(host, port, "http")
        ).build();
    }

    @Bean
    public ElasticsearchClient elasticsearchClient(RestClient restClient, JacksonJsonpMapper jsonpMapper) {
        RestClientTransport transport = new RestClientTransport(
                restClient,
                jsonpMapper
        );
        return new ElasticsearchClient(transport);
    }
}