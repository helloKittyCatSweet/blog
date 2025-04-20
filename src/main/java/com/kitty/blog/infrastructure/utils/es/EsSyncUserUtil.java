package com.kitty.blog.infrastructure.utils.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import com.kitty.blog.domain.model.User;
import com.kitty.blog.domain.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class EsSyncUserUtil {

    @Autowired
    private ElasticsearchClient client;

    @Autowired
    private UserRepository userRepository;

    private static final String USER_INDEX = "users";
    private static final int BATCH_SIZE = 1000;

    @PostConstruct
    public void init() {
        try {
            log.info("开始初始化用户数据到 Elasticsearch");
            syncAllUsers();
        } catch (Exception e) {
            log.error("初始化用户数据到 Elasticsearch 失败", e);
            // 不抛出异常，让应用继续启动
        }
    }

    public void syncAllUsers() {
        try {
            createOrUpdateIndex();
            importUsers();
            refreshIndex();
        } catch (Exception e) {
            log.error("同步用户数据到 ES 失败", e);
            throw new RuntimeException("同步用户数据失败", e);
        }
    }

    private void createOrUpdateIndex() throws Exception {
        boolean indexExists = client.indices().exists(e -> e.index(USER_INDEX)).value();
        if (indexExists) {
            client.indices().delete(d -> d.index(USER_INDEX));
        }

        client.indices().create(c -> c
                .index(USER_INDEX)
                .mappings(m -> m
                        .properties("userId", p -> p.integer(i -> i))
                        .properties("email", p -> p.text(t -> t))
                        .properties("lastLoginLocation", p -> p.text(t -> t))
                        .properties("isDeleted", p -> p.boolean_(b -> b))
                        .properties("tags",p -> p.keyword(k -> k))
                )
        );
        log.info("用户索引创建/更新成功");
    }

    private void importUsers() throws Exception {
        List<User> allUsers = userRepository.findAll();
        int totalBatches = (allUsers.size() + BATCH_SIZE - 1) / BATCH_SIZE;
        int importedCount = 0;

        for (List<User> batch : partition(allUsers, BATCH_SIZE)) {
            BulkRequest.Builder br = new BulkRequest.Builder();

            for (User user : batch) {
                br.operations(op -> op
                        .index(idx -> idx
                                .index(USER_INDEX)
                                .id(user.getUserId().toString())
                                .document(user)
                        )
                );
            }

            client.bulk(br.build());
            importedCount += batch.size();
            log.info("导入用户进度: {}/{}", importedCount, allUsers.size());
        }

        log.info("成功导入 {} 个用户到 Elasticsearch", allUsers.size());
    }

    private void refreshIndex() throws Exception {
        client.indices().refresh(r -> r.index(USER_INDEX));
    }

    public long checkIndexContent() {
        try {
            var response = client.search(s -> s
                            .index(USER_INDEX)
                            .size(1),
                    User.class
            );
            long total = response.hits().total().value();
            log.info("用户索引总文档数: {}", total);
            return total;
        } catch (Exception e) {
            log.error("检查用户索引失败", e);
            throw new RuntimeException("检查ES用户索引失败", e);
        }
    }

    private <T> List<List<T>> partition(List<T> list, int size) {
        List<List<T>> parts = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            parts.add(list.subList(i, Math.min(list.size(), i + size)));
        }
        return parts;
    }
}