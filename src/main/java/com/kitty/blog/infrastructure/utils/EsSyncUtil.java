package com.kitty.blog.infrastructure.utils;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.kitty.blog.application.dto.category.TreeDto;
import com.kitty.blog.application.dto.post.PostDto;
import com.kitty.blog.domain.model.category.Category;
import com.kitty.blog.domain.model.tag.Tag;
import com.kitty.blog.domain.repository.CategoryRepository;
import com.kitty.blog.domain.repository.tag.TagRepository;
import com.kitty.blog.domain.service.CategoryService;
import com.kitty.blog.domain.service.post.PostService;
import com.kitty.blog.domain.service.search.SearchService;
import com.kitty.blog.domain.service.tag.TagService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class EsSyncUtil {

    @Autowired
    private PostService postService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private SearchService searchService;

    @Autowired
    private ElasticsearchClient client;

    @PostConstruct
    public void init() {
        try {
            log.info("Starting initial sync of posts to Elasticsearch");
            syncAllPosts();
            syncAllCategories();
            syncAllTags();
        } catch (Exception e) {
            log.error("Failed to perform initial sync of posts to Elasticsearch", e);
            // 不抛出异常，让应用继续启动
        }
    }

    public void syncAllPosts() {
        try {
            // 检查索引是否存在，不存在则创建
            boolean indexExists = client.indices().exists(e -> e.index("posts")).value();
            if (!indexExists) {
                client.indices().create(c -> c.index("posts"));
                log.info("Created posts index");
            }

            List<PostDto> posts = postService.findAll().getBody();
            log.info("Starting sync {} posts to ES", posts.size());

            for (PostDto post : posts) {
                searchService.syncPostToEs(post);
            }

            // 强制刷新索引
            client.indices().refresh(r -> r.index("posts"));

            log.info("Finished sync {} posts to ES", posts.size());
        } catch (Exception e) {
            log.error("Failed to sync posts to ES", e);
            throw new RuntimeException("同步文章到ES失败", e);
        }
    }

    public void syncAllCategories() {
        try {
            boolean indexExists = client.indices().exists(b -> b.index("categories")).value();
            // 检查索引是否存在，不存在则创建
            if (!indexExists) {
                client.indices().create(c -> c.index("categories"));
                log.info("Created categories index");
            }

            List<Category> categories = categoryRepository.findAll();
            log.info("Starting sync {} categories to ES", categories.size());

            for (Category category : categories) {
                searchService.syncCategoryToEs(category);
            }

            // 强制刷新索引
            client.indices().refresh(r -> r.index("categories"));
        } catch (Exception e) {
            log.error("Failed to create categories index", e);
            throw new RuntimeException("创建分类索引失败", e);
        }
    }

    public void syncAllTags() {
        try {
            boolean indexExists = client.indices().exists(b -> b.index("tags")).value();
            // 检查索引是否存在，不存在则创建
            if (!indexExists) {
                client.indices().create(c -> c.index("tags"));
                log.info("Created tags index");
            }

            List<Tag> tags = tagRepository.findAll();
            log.info("Starting sync {} tags to ES", tags.size());

            for (Tag tag : tags) {
                searchService.syncTagToEs(tag);
            }

            // 强制刷新索引
            client.indices().refresh(r -> r.index("tags"));
        } catch (Exception e) {
            log.error("Failed to create tags index", e);
            throw new RuntimeException("创建标签索引失败", e);
        }
    }

    public long checkIndexContent() {
        try {
            var response = client.search(s -> s
                            .index("posts")
                            .size(1),
                    PostDto.class
            );
            long total = response.hits().total().value();
            log.info("Index total documents: {}", total);
            return total;
        } catch (Exception e) {
            log.error("Failed to check index content", e);
            throw new RuntimeException("检查ES索引失败", e);
        }
    }
}