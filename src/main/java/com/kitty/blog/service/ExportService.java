package com.kitty.blog.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.kitty.blog.model.Post;
import com.kitty.blog.model.User;
import com.kitty.blog.repository.PostRepository;
import com.kitty.blog.utils.AliyunOSSUploader;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class ExportService {

        @Autowired
        private PostRepository postRepository;

        @Autowired
        private AliyunOSSUploader ossUploader;

        /**
         * 导出博客为 PDF 格式
         *
         * @param postId 博客ID
         * @return PDF文件的URL
         */
        public String exportToPdf(Integer postId) throws IOException {
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new RuntimeException("博客不存在"));

                // 创建临时PDF文件
                File pdfFile = File.createTempFile("blog-" + postId + "-", ".pdf");

                // 创建PDF文档
                try (PdfWriter writer = new PdfWriter(pdfFile);
                                PdfDocument pdf = new PdfDocument(writer);
                                Document document = new Document(pdf)) {

                        // 添加标题
                        document.add(new Paragraph(post.getTitle())
                                        .setFontSize(20)
                                        .setBold());

                        // 添加作者和日期信息
                        User author = post.getUser();
                        document.add(new Paragraph("作者：" + (author != null ? author.getUsername() : "未知"))
                                        .setFontSize(12));
                        document.add(new Paragraph("发布时间：" + formatDate(post.getCreatedAt()))
                                        .setFontSize(12));

                        // 添加正文
                        document.add(new Paragraph(post.getContent())
                                        .setFontSize(14));
                }

                // 上传到OSS并返回URL
                String objectName = "exports/pdf/" + postId + "/" +
                                System.currentTimeMillis() + "-blog.pdf";
                String url = ossUploader.uploadFile(pdfFile, objectName, postId);

                // 删除临时文件
                Files.delete(pdfFile.toPath());

                return url;
        }

        /**
         * 导出博客为 Markdown 格式
         *
         * @param postId 博客ID
         * @return Markdown文件的URL
         */
        public String exportToMarkdown(Integer postId) throws IOException {
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new RuntimeException("博客不存在"));

                // 创建临时Markdown文件
                File mdFile = File.createTempFile("blog-" + postId + "-", ".md");

                // 将HTML内容转换为Markdown
                FlexmarkHtmlConverter converter = FlexmarkHtmlConverter.builder().build();
                String markdown = converter.convert(post.getContent());

                // 写入Markdown文件
                try (FileWriter writer = new FileWriter(mdFile)) {
                        // 写入标题
                        writer.write("# " + post.getTitle() + "\n\n");

                        // 写入元信息
                        User author = post.getUser();
                        writer.write("> 作者：" + (author != null ? author.getUsername() : "未知") + "\n");
                        writer.write("> 发布时间：" + formatDate(post.getCreatedAt()) + "\n\n");

                        // 写入正文
                        writer.write(markdown);
                }

                // 上传到OSS并返回URL
                String objectName = "exports/markdown/" + postId + "/" +
                                System.currentTimeMillis() + "-blog.md";
                String url = ossUploader.uploadFile(mdFile, objectName, postId);

                // 删除临时文件
                Files.delete(mdFile.toPath());

                return url;
        }

        /**
         * 将博客内容转换为Markdown格式
         *
         * @param postId 博客ID
         * @return Markdown格式的文本
         */
        public String convertToMarkdown(Integer postId) {
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> new RuntimeException("博客不存在"));

                // 将HTML内容转换为Markdown
                FlexmarkHtmlConverter converter = FlexmarkHtmlConverter.builder().build();
                String markdown = converter.convert(post.getContent());

                // 构建完整的Markdown文档
                StringBuilder sb = new StringBuilder();

                // 添加标题
                sb.append("# ").append(post.getTitle()).append("\n\n");

                // 添加元信息
                User author = post.getUser();
                sb.append("> 作者：").append(author != null ? author.getUsername() : "未知").append("\n");
                sb.append("> 发布时间：")
                                .append(post.getCreatedAt() != null
                                                ? post.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                                : "未知")
                                .append("\n\n");

                // 添加正文
                sb.append(markdown);

                return sb.toString();
        }

        private String formatDate(LocalDate date) {
                return date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "未知";
        }
}