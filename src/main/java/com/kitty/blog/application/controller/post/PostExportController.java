package com.kitty.blog.application.controller.post;

import com.kitty.blog.application.dto.post.PostDto;
import com.kitty.blog.domain.service.post.PostExportService;
import com.kitty.blog.domain.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Tag(name = "文章导出", description = "文章导出接口")
@RestController
@RequestMapping("/api/post/export")
@CrossOrigin
@Slf4j
public class PostExportController {

    @Autowired
    private PostExportService postExportService;

    @Autowired
    private PostService postService;

    @Operation(summary = "导出文章为PDF", description = "导出文章为PDF")
    @GetMapping("/public/pdf/{postId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "导出成功"),
            @ApiResponse(responseCode = "404", description = "文章不存在"),
            @ApiResponse(responseCode = "500", description = "导出失败")
    })
    public ResponseEntity<byte[]> exportToPdf(@PathVariable Integer postId) {
        try {
            PostDto postDto = postService.findById(postId).getBody();
            if (postDto == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] pdfContent = postExportService.exportToPdf(postDto);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\""
                                    + URLEncoder.encode(
                                    postDto.getPost().getTitle(),
                                    StandardCharsets.UTF_8)
                                    + ".pdf\"")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
                    .body(pdfContent);
        } catch (Exception e) {
            log.error("导出PDF失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "导出文章为Markdown", description = "导出文章为Markdown")
    @GetMapping("/public/markdown/{postId}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "导出成功"),
            @ApiResponse(responseCode = "404", description = "文章不存在"),
            @ApiResponse(responseCode = "500", description = "导出失败")
    })
    public ResponseEntity<byte[]> exportToMarkdown(@PathVariable Integer postId) {
        try {
            PostDto postDto = postService.findById(postId).getBody();
            if (postDto == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] markdownContent = postExportService.exportToMarkdown(postDto);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + URLEncoder.encode(postDto.getPost().getTitle(), StandardCharsets.UTF_8) + ".md\"")
                    .header(HttpHeaders.CONTENT_TYPE, "text/markdown;charset=utf-8")
                    .body(markdownContent);
        } catch (Exception e) {
            log.error("导出Markdown失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
