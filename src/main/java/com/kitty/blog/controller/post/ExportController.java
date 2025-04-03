package com.kitty.blog.controller.post;

import com.kitty.blog.service.post.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post/export")
@Tag(name = "导出接口", description = "博客导出相关接口")
@CrossOrigin
public class ExportController {

    @Autowired
    private ExportService exportService;

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "导出为PDF", description = "将指定博客导出为PDF格式")
    @GetMapping("/public/pdf/{postId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "导出成功"),
            @ApiResponse(responseCode = "404", description = "博客不存在"),
            @ApiResponse(responseCode = "500", description = "导出失败")
    })
    public ResponseEntity<String> exportToPdf(
            @Parameter(description = "博客ID", required = true) @PathVariable Integer postId) {
        try {
            String pdfUrl = exportService.exportToPdf(postId);
            return ResponseEntity.ok(pdfUrl);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("导出PDF失败: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "导出为Markdown", description = "将指定博客导出为Markdown格式")
    @GetMapping("/public/markdown/{postId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "导出成功"),
            @ApiResponse(responseCode = "404", description = "博客不存在"),
            @ApiResponse(responseCode = "500", description = "导出失败")
    })
    public ResponseEntity<String> exportToMarkdown(
            @Parameter(description = "博客ID", required = true) @PathVariable Integer postId) {
        try {
            String markdownUrl = exportService.exportToMarkdown(postId);
            return ResponseEntity.ok(markdownUrl);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("导出Markdown失败: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole(T(com.kitty.blog.constant.Role).ROLE_USER)")
    @Operation(summary = "获取Markdown格式内容", description = "将博客内容转换为Markdown格式并返回")
    @GetMapping(value = "public/markdown/text/{postId}", produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "转换成功"),
            @ApiResponse(responseCode = "404", description = "博客不存在"),
            @ApiResponse(responseCode = "500", description = "转换失败")
    })
    public ResponseEntity<String> getMarkdownText(
            @Parameter(description = "博客ID", required = true) @PathVariable Integer postId) {
        try {
            String markdown = exportService.convertToMarkdown(postId);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(markdown);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("转换Markdown失败: " + e.getMessage());
        }
    }
}