package com.kitty.blog.application.controller.search;

import com.kitty.blog.application.dto.post.PostDto;
import com.kitty.blog.domain.model.search.PostIndex;
import com.kitty.blog.domain.service.search.SearchService;
import com.kitty.blog.infrastructure.utils.EsSyncUtil;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/search")
@Tag(name = "搜索模块", description = "搜索文章、获取建议、检查索引")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private EsSyncUtil esSyncUtil;

    @Operation(summary = "搜索文章")
    @GetMapping("/public/posts")
    public ResponseEntity<Response<Page<PostDto>>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<PostDto> results = searchService.searchPosts(keyword, page, size);
            return Response.ok(results);
        } catch (Exception e) {
            return Response.error("搜索失败：" + e.getMessage());
        }
    }

    @Operation(summary = "搜索建议")
    @GetMapping("/public/suggest")
    public ResponseEntity<Response<List<String>>> suggest(@RequestParam String keyword) {
        try {
            List<String> suggestions = searchService.suggestSearch(keyword);
            return Response.ok(suggestions);
        } catch (Exception e) {
            return Response.error("获取搜索建议失败：" + e.getMessage());
        }
    }

    @GetMapping("/admin/check-index")
    public ResponseEntity<Response<String>> checkIndex() {
        try {
            long count = esSyncUtil.checkIndexContent();
            return Response.ok(String.format("检查完成，索引中共有 %d 条数据", count));
        } catch (Exception e) {
            log.error("Check index failed", e);
            return Response.error("检查索引失败：" + e.getMessage());
        }
    }
}