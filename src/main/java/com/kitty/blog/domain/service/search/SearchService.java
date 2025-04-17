package com.kitty.blog.domain.service.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.Highlight;
import co.elastic.clients.elasticsearch.core.search.HighlightField;
import com.kitty.blog.application.dto.post.PostDto;
import com.kitty.blog.domain.model.Post;
import com.kitty.blog.domain.model.search.PostIndex;
import com.kitty.blog.domain.model.tag.Tag;
import com.kitty.blog.domain.repository.search.PostIndexRepository;
import com.kitty.blog.domain.service.post.PostService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
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

    @PostConstruct
    public void init() {
        try {
            // 检查索引是否存在，不存在则创建
            boolean indexExists = client.indices().exists(e -> e.index("posts")).value();
            if (!indexExists) {
                client.indices().create(c -> c.index("posts"));
            }
            log.info("Elasticsearch index check completed");
        } catch (Exception e) {
            log.error("Failed to initialize Elasticsearch index", e);
        }
    }

    public void syncPostToEs(PostDto postDto) {
        try {
            PostIndex postIndex = convertToPostIndex(postDto);
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
    }

    public void deletePostFromEs(Integer postId) {
        try {
            client.delete(d -> d
                    .index("posts")
                    .id(postId.toString())
            );
            log.info("Deleted post from ES: {}", postId);
        } catch (Exception e) {
            log.error("Failed to delete post from ES: {}", postId, e);
        }
    }

    public PostService getPostService() {
        return applicationContext.getBean(PostService.class);
    }

    public Page<PostDto> searchPosts(String keyword, int page, int size) {
        try {
            // 标题搜索：使用模糊匹配和通配符
            Query titleQuery = Query.of(q -> q
                    .bool(b -> b
                            .should(s -> s
                                    .match(m -> m
                                            .field("title")
                                            .query(keyword)
                                            .boost(3.0f)
                                    ))
                            .should(s -> s
                                    .wildcard(w -> w
                                            .field("title")
                                            .value("*" + keyword + "*")
                                            .boost(2.0f)
                                    ))
                            .should(s -> s
                                    .fuzzy(f -> f
                                            .field("title")
                                            .value(keyword)
                                            .boost(1.5f)
                                    ))
                    )
            );

            // 摘要搜索：使用模糊匹配
            Query summaryQuery = Query.of(q -> q
                    .bool(b -> b
                            .should(s -> s
                                    .match(m -> m
                                            .field("summary")
                                            .query(keyword)
                                            .boost(2.0f)
                                    ))
                            .should(s -> s
                                    .fuzzy(f -> f
                                            .field("summary")
                                            .value(keyword)
                                    ))
                    )
            );

            // 内容搜索：使用模糊匹配
            Query contentQuery = Query.of(q -> q
                    .bool(b -> b
                            .should(s -> s
                                    .match(m -> m
                                            .field("content")
                                            .query(keyword)
                                    ))
                            .should(s -> s
                                    .fuzzy(f -> f
                                            .field("content")
                                            .value(keyword)
                                    ))
                    )
            );

            Query tagsQuery = Query.of(q -> q
                    .match(m -> m
                            .field("tags")
                            .query(keyword)
                    )
            );

            Query categoryQuery = Query.of(q -> q
                    .match(m -> m
                            .field("category")
                            .query(keyword)
                    )
            );

            BoolQuery boolQuery = BoolQuery.of(b -> b
                    .should(titleQuery)
                    .should(summaryQuery)
                    .should(contentQuery)
                    .should(tagsQuery)
                    .should(categoryQuery)
                    .minimumShouldMatch("1") // 至少匹配1个条件
            );

            SearchResponse<PostIndex> response = client.search(s -> s
                            .index("posts")
                            .query(q -> q.bool(boolQuery))
                            .highlight(h -> h
                                    .fields("title", HighlightField.of(f -> f
                                            .preTags("<em class=\"highlight\">")
                                            .postTags("</em>")
                                    ))
                                    .fields("content", HighlightField.of(f -> f
                                            .preTags("<em class=\"highlight\">")
                                            .postTags("</em>")
                                            .numberOfFragments(3)
                                            .fragmentSize(150)
                                    ))
                                    .fields("summary", HighlightField.of(f -> f
                                            .preTags("<em class=\"highlight\">")
                                            .postTags("</em>")
                                    ))
                                    .fields("tags", HighlightField.of(f -> f
                                            .preTags("<em class=\"highlight\">")
                                            .postTags("</em>")
                                    ))
                                    .fields("category", HighlightField.of(f -> f
                                            .preTags("<em class=\"highlight\">")
                                            .postTags("</em>")
                                    ))
                            )
                            .from(page * size)
                            .size(size),
                    PostIndex.class
            );

            List<PostDto> postDtos =  new ArrayList<>();
            for (Hit<PostIndex> hit : response.hits().hits()) {
                PostIndex post = hit.source();
                if (post != null) {
                    // 获取完整的postDto
                    PostDto postDto = getPostService().findById(post.getId()).getBody();
                    if (postDto != null){
                        // 处理高亮
                        Map<String, List<String>> highlights = hit.highlight();
                        if (highlights != null) {
                            if (highlights.containsKey("title")) {
                                post.setTitle(highlights.get("title").get(0));
                            }
                            if (highlights.containsKey("content")) {
                                post.setContent(String.join("...", highlights.get("content")));
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
                    response.hits().total().value()
            );
        } catch (Exception e) {
            log.error("Search posts error: {}", e.getMessage());
            return Page.empty();
        }
    }

    public List<String> suggestSearch(String keyword) {
        try {
            // 构建多字段匹配查询
            Query titlePrefixQuery = Query.of(q -> q
                    .prefix(p -> p
                            .field("title")
                            .value(keyword)
                            .boost(2.0f)
                    )
            );

            Query titleWildcardQuery = Query.of(q -> q
                    .wildcard(w -> w
                            .field("title")
                            .value("*" + keyword + "*")
                    )
            );

            BoolQuery boolQuery = BoolQuery.of(b -> b
                    .should(titlePrefixQuery)
                    .should(titleWildcardQuery)
            );

            SearchResponse<PostIndex> response = client.search(s -> s
                            .index("posts")
                            .query(q -> q.bool(boolQuery))
                            .size(10),
                    PostIndex.class
            );

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

    private PostIndex convertToPostIndex(PostDto postDto) {
        return PostIndex.builder()
                .id(postDto.getPost().getPostId())
                .title(postDto.getPost().getTitle())
                .content(postDto.getPost().getContent())
                .summary(postDto.getPost().getAbstractContent())
                .authorId(postDto.getPost().getUserId())
                .authorName(postDto.getAuthor())
                .createTime(postDto.getPost().getCreatedAt())
                .viewCount(postDto.getPost().getViews())
                .likeCount(postDto.getPost().getLikes())
                .tags(postDto.getTags() != null ?
                        postDto.getTags().stream().map(Tag::getName).collect(Collectors.joining(",")) :
                        "")
                .category(postDto.getCategory() != null ? postDto.getCategory().getName() : "")
                .build();
    }

    // 添加一个检查索引内容的方法
    public void checkIndexContent() {
        try {
            SearchResponse<PostIndex> response = client.search(s -> s
                            .index("posts")
                            .size(1),
                    PostIndex.class
            );
            log.info("Index total documents: {}", response.hits().total().value());
        } catch (Exception e) {
            log.error("Failed to check index content: {}", e.getMessage());
        }
    }
}