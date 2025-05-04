package com.kitty.blog.domain.service.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.kitty.blog.application.dto.post.PostDto;
import com.kitty.blog.domain.model.Post;
import com.kitty.blog.domain.model.category.Category;
import com.kitty.blog.domain.model.search.CategoryIndex;
import com.kitty.blog.domain.model.search.PostIndex;
import com.kitty.blog.domain.model.search.TagIndex;
import com.kitty.blog.domain.model.tag.Tag;
import com.kitty.blog.domain.repository.search.PostIndexRepository;
import com.kitty.blog.domain.service.post.PostService;
import com.kitty.blog.infrastructure.converter.StringConverter;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchService {

    @Autowired
    private PostIndexRepository postIndexRepository;

    @Autowired
    private ElasticsearchClient client;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ThreadPoolExecutor searchExecutor;

    @PostConstruct
    public void init() {
        try {
            // 检查索引是否存在，不存在则创建
            boolean indexExists = client.indices().exists(e -> e.index("posts")).value();
            if (!indexExists) {
                client.indices().create(c -> c.index("posts"));
            }
            indexExists = client.indices().exists(e -> e.index("categories")).value();
            if (!indexExists) {
                client.indices().create(c -> c.index("categories"));
            }
            indexExists = client.indices().exists(e -> e.index("tags")).value();
            if (!indexExists) {
                client.indices().create(c -> c.index("tags"));
            }
            log.info("Elasticsearch index check completed");
        } catch (Exception e) {
            log.error("Failed to initialize Elasticsearch index", e);
        }
    }

    public void syncPostToEs(PostDto postDto) {
        CompletableFuture.runAsync(() -> {
            try {
                PostIndex postIndex = PostIndex.convertToPostIndex(postDto);
                client.index(i -> i
                        .index("posts")
                        .id(postDto.getPost().getPostId().toString())
                        .document(postIndex)
                        .refresh(Refresh.True) // 添加立即刷新选项
                );
                log.info("Synced post to ES: {}", postDto.getPost().getPostId());
            } catch (Exception e) {
                log.error("Failed to sync post to ES: {}", postDto.getPost().getPostId(), e);
            }
        }, searchExecutor);
    }

    public void syncCategoryToEs(Category category) {
        try {
            CategoryIndex categoryIndex = CategoryIndex.builder()
                    .id(category.getCategoryId()).name(category.getName()).build();
            client.index(i -> i
                    .index("categories")
                    .id(categoryIndex.getId().toString())
                    .document(categoryIndex)
                    .refresh(Refresh.True) // 添加立即刷新选项
            );
            log.info("Synced category to ES: {}", category);
        } catch (Exception e) {
            log.error("Failed to sync category to ES: {}", category, e);
        }
    }

    public void syncTagToEs(Tag tag) {
        try {
            TagIndex tagIndex = TagIndex.builder()
                    .id(tag.getTagId())
                    .name(tag.getName())
                    .weight(tag.getWeight())
                    .build();
            client.index(i -> i
                    .index("tags")
                    .id(tagIndex.getId().toString())
                    .document(tagIndex)
                    .refresh(Refresh.True) // 添加立即刷新选项
            );
            log.info("Synced tag to ES: {}", tag);
        } catch (Exception e) {
            log.error("Failed to sync tag to ES: {}", tag, e);
        }
    }

    public void deletePostFromEs(Integer postId) {
        try {
            client.delete(d -> d
                    .index("posts")
                    .id(postId.toString()));
            log.info("Deleted post from ES: {}", postId);
        } catch (Exception e) {
            log.error("Failed to delete post from ES: {}", postId, e);
        }
    }

    public PostService getPostService() {
        return applicationContext.getBean(PostService.class);
    }

    public Page<PostDto> searchPosts(String keyword, int page, int size, String[] sorts) {
        try {
            // 解析特殊语法
            String tags;
            String category;

            // 使用正则表达式提取标签和分类
            Pattern tagPattern = Pattern.compile("#([^#@]+)#");
            Pattern categoryPattern = Pattern.compile("@([^#@]+)@");

            // 提取标签 (#tag1,tag2#)
            Matcher tagMatcher = tagPattern.matcher(keyword);
            if (tagMatcher.find()) {
                tags = tagMatcher.group(1).trim();
                keyword = keyword.replace(tagMatcher.group(0), "").trim();
            } else {
                tags = "";
            }

            // 提取分类 (@category@)
            Matcher categoryMatcher = categoryPattern.matcher(keyword);
            if (categoryMatcher.find()) {
                category = categoryMatcher.group(1).trim();
                keyword = keyword.replace(categoryMatcher.group(0), "").trim();
            } else {
                category = "";
            }

            String finalKeyword = StringConverter.convertUpperCaseToLowerCase(keyword.trim());
            log.info("Search keyword: {}, tags: {}, category: {}", finalKeyword, tags, category);

            // 主查询构建
            BoolQuery.Builder boolQueryBuilder = QueryBuilders.bool();

            /**
             * 必须满足的条件(filter)
             */
            List<Query> mustQueries = new ArrayList<>();

            // 分类过滤
            if (!category.isEmpty()) {
                mustQueries.add(Query.of(q -> q
                        .match(m -> m
                                .field("category")
                                .query(category))));
            }

            // 标签过滤
            if (!tags.isEmpty()) {
                List<Query> tagQueries = Arrays.stream(tags.split(","))
                        .map(String::trim)
                        .map(tag -> Query.of(q -> q
                                .functionScore(fs -> fs
                                        .query(qq -> qq
                                                .match(m -> m
                                                        .field("tags")
                                                        .query(tag)))
                                        .functions(f -> f
                                                .fieldValueFactor(fvf -> fvf
                                                        .field("weight")
                                                        .factor(1.0)
                                                        .missing(1.0)))
                                        .boostMode(
                                                co.elastic.clients.elasticsearch._types.query_dsl.FunctionBoostMode.Multiply))))
                        .collect(Collectors.toList());

                mustQueries.add(Query.of(q -> q
                        .bool(b -> b
                                .should(tagQueries)
                                .minimumShouldMatch("1"))));
            }

            // 添加必须条件
            if (!mustQueries.isEmpty()) {
                boolQueryBuilder.must(mustQueries);
            }

            /**
             * 应该满足的条件(should) - 关键字搜索
             */
            if (!finalKeyword.isEmpty()) {
                List<Query> shouldQueries = new ArrayList<>();

                // 标题搜索：使用模糊匹配和通配符
                shouldQueries.add(Query.of(q -> q
                        .bool(b -> b
                                .should(s -> s
                                        .match(m -> m
                                                .field("title")
                                                .query(finalKeyword)
                                                .boost(3.0f)))
                                .should(s -> s
                                        .wildcard(w -> w
                                                .field("title")
                                                .value("*" + finalKeyword + "*")
                                                .boost(2.0f)))
                                .should(s -> s
                                        .fuzzy(f -> f
                                                .field("title")
                                                .value(finalKeyword)
                                                .boost(1.5f))))));

                // Tag query(when tags are not specified)
                if (tags.isEmpty()) {
                    shouldQueries.add(Query.of(q -> q
                            .match(m -> m
                                    .field("tags")
                                    .query(finalKeyword))));
                }

                // Category query(when category is not specified)
                if (category.isEmpty()) {
                    shouldQueries.add(Query.of(q -> q
                            .match(m -> m
                                    .field("category")
                                    .query(finalKeyword))));
                }

                // 摘要搜索：使用模糊匹配
                shouldQueries.add(Query.of(q -> q
                        .bool(b -> b
                                .should(s -> s
                                        .match(m -> m
                                                .field("summary")
                                                .query(finalKeyword)
                                                .boost(2.0f)))
                                .should(s -> s
                                        .fuzzy(f -> f
                                                .field("summary")
                                                .value(finalKeyword))))));

                // 内容搜索：使用模糊匹配
                shouldQueries.add(Query.of(q -> q
                        .bool(b -> b
                                .should(s -> s
                                        .match(m -> m
                                                .field("content")
                                                .query(finalKeyword)))
                                .should(s -> s
                                        .fuzzy(f -> f
                                                .field("content")
                                                .value(finalKeyword))))));

                boolQueryBuilder.must(Query.of(q -> q
                        .bool(b -> b
                                .should(shouldQueries)
                                .minimumShouldMatch("1"))));
            }

            SearchResponse<PostIndex> response = client.search(s -> s
                    .index("posts")
                    .query(q -> q.bool(boolQueryBuilder.build()))
                    .highlight(h -> h
                            .fields("title", HighlightField.of(f -> f
                                    .preTags("<em class=\"highlight\">")
                                    .postTags("</em>")))
                            .fields("content", HighlightField.of(f -> f
                                    .preTags("<em class=\"highlight\">")
                                    .postTags("</em>")
                                    .numberOfFragments(3)
                                    .fragmentSize(150)))
                            .fields("summary", HighlightField.of(f -> f
                                    .preTags("<em class=\"highlight\">")
                                    .postTags("</em>")))
                            .fields("tags", HighlightField.of(f -> f
                                    .preTags("<em class=\"highlight\">")
                                    .postTags("</em>")))
                            .fields("category", HighlightField.of(f -> f
                                    .preTags("<em class=\"highlight\">")
                                    .postTags("</em>")))
                    )
                            .sort(sortBuilder -> {
                                if (sorts == null || sorts.length == 0){
                                    // 默认按创建时间排序
                                    return sortBuilder.field(f -> f
                                            .field("createTime")
                                            .order(SortOrder.Desc)
                                    );
                                }

                                // 处理单个排序参数 ["field,direction"]
                                if (sorts.length == 1 && sorts[0].contains(",")){
                                    String[] parts = sorts[0].split(",");
                                    if (parts.length == 2){
                                        String field = parts[0];
                                        String direction = parts[1];
                                        return sortBuilder.field(f -> f
                                                .field(field)
                                                .order(direction.equalsIgnoreCase("asc") ? SortOrder.Asc : SortOrder.Desc)
                                        );
                                    }
                                }

                                // 处理两个元素的情况 ["field", "direction"]
                                if (sorts.length == 2){
                                    String field = sorts[0];
                                    String direction = sorts[1];
                                    if (StringUtils.hasText(field) && StringUtils.hasText(direction)){
                                        return sortBuilder.field(f -> f
                                                .field(field)
                                                .order(direction.equalsIgnoreCase("asc") ? SortOrder.Asc : SortOrder.Desc)
                                        );
                                    }
                                }

                                // 如果格式不正确，使用默认排序
                                return sortBuilder.field(f -> f
                                        .field("createTime")
                                        .order(SortOrder.Desc)
                                );
                            })
                    .from(page * size)
                    .size(size),
                    PostIndex.class);

            List<PostDto> postDtos = new ArrayList<>();
            for (Hit<PostIndex> hit : response.hits().hits()) {
                PostIndex post = hit.source();
                if (post != null) {
                    // 获取完整的postDto
                    PostDto postDto = getPostService().findById(post.getId()).getBody();
                    if (postDto != null) {
                        // 处理高亮
                        Map<String, List<String>> highlights = hit.highlight();
                        if (highlights != null) {
                            if (highlights.containsKey("title")) {
                                post.setTitle(highlights.get("title").get(0));
                            }
                            if (highlights.containsKey("content")) {
                                post.setContent(String.join("...",
                                        highlights.get("content")));
                            }
                            if (highlights.containsKey("summary")) {
                                post.setSummary(highlights.get("summary").get(0));
                            }
                            if (highlights.containsKey("tags")) {
                                post.setTags(highlights.get("tags").get(0));
                            }
                            if (highlights.containsKey("category")) {
                                post.setCategory(highlights.get("category").get(0));
                            }
                        }
                    }
                    postDtos.add(postDto);
                }
            }

            assert response.hits().total() != null;
            return new PageImpl<>(
                    postDtos,
                    PageRequest.of(page, size),
                    response.hits().total().value());
        } catch (Exception e) {
            log.error("Search posts error: {}", e.getMessage());
            return Page.empty();
        }
    }

    public List<String> suggestPostSearch(String keyword) {
        try {
            // 构建多字段匹配查询
            Query titlePrefixQuery = Query.of(q -> q
                    .prefix(p -> p
                            .field("title")
                            .value(keyword)
                            .boost(2.0f)));

            Query titleWildcardQuery = Query.of(q -> q
                    .wildcard(w -> w
                            .field("title")
                            .value("*" + keyword + "*")));

            BoolQuery boolQuery = BoolQuery.of(b -> b
                    .should(titlePrefixQuery)
                    .should(titleWildcardQuery));

            SearchResponse<PostIndex> response = client.search(s -> s
                    .index("posts")
                    .query(q -> q.bool(boolQuery))
                    .size(10),
                    PostIndex.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .map(PostIndex::getTitle)
                    .distinct()
                    .toList();
        } catch (Exception e) {
            log.error("Suggest search error: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<String> suggestCategorySearch(String keyword) {
        try {
            Query categoryPrefixQuery = Query.of(q -> q
                    .prefix(p -> p
                            .field("name")
                            .value(keyword)
                            .boost(2.0f)));

            Query categoryWildcardQuery = Query.of(q -> q
                    .wildcard(w -> w
                            .field("name")
                            .value("*" + keyword + "*")));

            BoolQuery boolQuery = BoolQuery.of(b -> b
                    .should(categoryPrefixQuery)
                    .should(categoryWildcardQuery));

            SearchResponse<CategoryIndex> response = client.search(s -> s
                    .index("categories")
                    .query(q -> q.bool(boolQuery))
                    .size(10),
                    CategoryIndex.class);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .map(CategoryIndex::getName)
                    .distinct()
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> suggestTagSearch(String keyword) {
        try {
            Query tagPrefixQuery = Query.of(q -> q
                    .prefix(p -> p
                            .field("name")
                            .value(keyword)
                            .boost(2.0f)));

            Query tagWildcardQuery = Query.of(q -> q
                    .wildcard(w -> w
                            .field("name")
                            .value("*" + keyword + "*")));

            BoolQuery boolQuery = BoolQuery.of(b -> b
                    .should(tagPrefixQuery)
                    .should(tagWildcardQuery));

            SearchResponse<TagIndex> response = client.search(s -> s
                    .index("tags")
                    .query(q -> q.bool(boolQuery))
                    .size(10),
                    TagIndex.class);

            return response.hits().hits().stream()
                    .map(Hit::source)
                    .filter(Objects::nonNull)
                    .map(TagIndex::getName)
                    .flatMap(tags -> Arrays.stream(tags.split(",")))
                    .distinct()
                    .toList();
        } catch (Exception e) {
            log.error("Suggest search error: {}", e.getMessage());
            return Collections.emptyList();
        }
    }



    // 添加一个检查索引内容的方法
    public void checkIndexContent() {
        try {
            SearchResponse<PostIndex> response = client.search(s -> s
                    .index("posts")
                    .size(1),
                    PostIndex.class);
            log.info("Index total documents: {}", response.hits().total().value());
        } catch (Exception e) {
            log.error("Failed to check index content: {}", e.getMessage());
        }
    }

    /**
     * 获取标签权重
     * 通过查询 Tag 实体获取权重值，如果找不到则返回默认权重 1.0
     *
     * @param tagName 标签名称
     * @return 标签权重
     */
    private float getTagWeight(String tagName) {
        try {
            // 从 ES 中查询标签
            Query query = TermQuery.of(t -> t
                    .field("name")
                    .value(tagName))._toQuery();

            SearchResponse<TagIndex> response = client.search(s -> s
                    .index("tags")
                    .query(query),
                    TagIndex.class);

            if (!response.hits().hits().isEmpty()) {
                TagIndex tagIndex = response.hits().hits().get(0).source();
                return tagIndex != null && tagIndex.getWeight() != null
                        ? tagIndex.getWeight()
                        : 1.0f;
            }
        } catch (Exception e) {
            log.error("Failed to get tag weight for tag: {}", tagName, e);
        }
        return 1.0f;
    }
}