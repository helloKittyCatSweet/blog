package com.kitty.blog.application.controller.search;

import com.kitty.blog.application.dto.post.PostDto;
import com.kitty.blog.domain.service.search.SearchService;
import com.kitty.blog.infrastructure.converter.StringConverter;
import com.kitty.blog.infrastructure.utils.es.EsSyncPostRelevantUtil;
import com.kitty.blog.infrastructure.utils.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    private EsSyncPostRelevantUtil esSyncPostRelevantUtil;

    @Operation(summary = "搜索文章")
    @GetMapping("/public/posts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "搜索成功"),
            @ApiResponse(responseCode = "500", description = "搜索失败")
    })
    public ResponseEntity<Response<Page<PostDto>>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
//            keyword = StringConverter.convertUpperCaseToLowerCase(keyword);
            Page<PostDto> results = searchService.searchPosts(keyword, page, size);
            return Response.ok(results);
        } catch (Exception e) {
            return Response.error("搜索失败：" + e.getMessage());
        }
    }

    @Operation(summary = "搜索文章建议")
    @GetMapping("/public/suggest/post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取搜索建议成功"),
            @ApiResponse(responseCode = "500", description = "获取搜索建议失败")
    })
    public ResponseEntity<Response<List<String>>> suggestPost(@RequestParam String keyword) {
        try {
            keyword = StringConverter.convertUpperCaseToLowerCase(keyword);
            List<String> suggestions = searchService.suggestPostSearch(keyword);
            return Response.ok(suggestions);
        } catch (Exception e) {
            return Response.error("获取搜索建议失败：" + e.getMessage());
        }
    }

    @Operation(summary = "搜索分类建议")
    @GetMapping("/public/suggest/category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取搜索建议成功"),
            @ApiResponse(responseCode = "500", description = "获取搜索建议失败")
    })
    public ResponseEntity<Response<List<String>>> suggestCategory(@RequestParam String keyword) {
        try {
            keyword = StringConverter.convertUpperCaseToLowerCase(keyword);
            List<String> suggestions = searchService.suggestCategorySearch(keyword);
            return Response.ok(suggestions);
        } catch (Exception e) {
            return Response.error("获取搜索建议失败：" + e.getMessage());
        }
    }

    @Operation(summary = "搜索标签建议")
    @GetMapping("/public/suggest/tag")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取搜索建议成功"),
            @ApiResponse(responseCode = "500", description = "获取搜索建议失败")
    })
    public ResponseEntity<Response<List<String>>> suggestTag(@RequestParam String keyword) {
        try {
            keyword = StringConverter.convertUpperCaseToLowerCase(keyword);
            List<String> suggestions = searchService.suggestTagSearch(keyword);
            return Response.ok(suggestions);
        } catch (Exception e) {
            return Response.error("获取搜索建议失败：" + e.getMessage());
        }
    }

    @Operation(summary = "检查索引")
    @GetMapping("/admin/check-index")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "检查索引成功"),
            @ApiResponse(responseCode = "500", description = "检查索引失败")
    })
    public ResponseEntity<Response<String>> checkIndex() {
        try {
            long count = esSyncPostRelevantUtil.checkIndexContent();
            return Response.ok(String.format("检查完成，索引中共有 %d 条数据", count));
        } catch (Exception e) {
            log.error("Check index failed", e);
            return Response.error("检查索引失败：" + e.getMessage());
        }
    }
}