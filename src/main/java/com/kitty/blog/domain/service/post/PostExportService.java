package com.kitty.blog.domain.service.post;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.kitty.blog.application.dto.post.PostDto;
import com.kitty.blog.domain.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class PostExportService {

    @Autowired
    private UserService userService;

    private static final String FONT_PATH = "config/static/fonts/simsun.ttf";
    private static final String CODE_FONT_PATH = "config/static/fonts/JetBrainsMono-Regular.ttf";
    private static final float MARGIN = 36.0f;
    private static final float HEADER_HEIGHT = 20.0f;
    private static final float FOOTER_HEIGHT = 20.0f;

    public byte[] exportToPdf(PostDto postDto) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(MARGIN + HEADER_HEIGHT, MARGIN, MARGIN + FOOTER_HEIGHT, MARGIN);

        pdf.addEventHandler(PdfDocumentEvent.END_PAGE, new HeaderFooterHandler());

        try {
            PdfFont font = PdfFontFactory.createFont(FONT_PATH);
            PdfFont codeFont = PdfFontFactory.createFont(CODE_FONT_PATH);

            // 添加装饰线
            document.add(new Paragraph("").setHeight(2)
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                    .setMarginBottom(20));

            // 添加标题
            Paragraph title = new Paragraph(postDto.getPost().getTitle())
                    .setFont(font)
                    .setFontSize(28)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setMarginBottom(30);
            document.add(title);

            // 添加作者信息
            Table infoTable = new Table(2).useAllAvailableWidth();
            infoTable.setMarginBottom(40);
            infoTable.setBorder(Border.NO_BORDER);

            Cell authorCell = new Cell()
                    .add(new Paragraph("作者：" + postDto.getAuthor())
                            .setFont(font)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.GRAY))
                    .setBorder(Border.NO_BORDER);

            Cell dateCell = new Cell()
                    .add(new Paragraph("发布时间：" +
                            postDto.getPost().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                            .setFont(font)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.GRAY))
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.RIGHT);

            infoTable.addCell(authorCell);
            infoTable.addCell(dateCell);
            document.add(infoTable);

            // 添加摘要
            document.add(new Paragraph(postDto.getPost().getAbstractContent())
                    .setFont(font)
                    .setFontSize(12)
                    .setFontColor(ColorConstants.GRAY)
                    .setMarginTop(10)
                    .setMarginBottom(20)
                    .setFirstLineIndent(28)
                    .setMultipliedLeading(1.2f));

            // 处理文章内容
            String content = postDto.getPost().getContent();
            String[] lines = content.split("\n");
            boolean inCodeBlock = false;

            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    document.add(new Paragraph("\n"));
                    continue;
                }

                // 处理代码块
                if (line.startsWith("```")) {
                    inCodeBlock = !inCodeBlock;
                    document.add(new Paragraph(line)
                            .setFont(codeFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.DARK_GRAY)
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY, 0.3f)
                            .setPadding(8)
                            .setMarginTop(10)
                            .setMarginBottom(10));
                    continue;
                }

                if (inCodeBlock) {
                    document.add(new Paragraph(line)
                            .setFont(codeFont)
                            .setFontSize(12)
                            .setFontColor(ColorConstants.DARK_GRAY)
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY, 0.3f)
                            .setPadding(8));
                    continue;
                }

                // 处理标题
                if (line.matches("^#{1,6}\\s.*")) {
                    int level = 0;
                    while (level < line.length() && line.charAt(level) == '#') {
                        level++;
                    }
                    String titleText = line.substring(level).trim();
                    float fontSize = Math.max(32 - (level * 4), 14);
                    document.add(new Paragraph(titleText)
                            .setFont(font)
                            .setFontSize(fontSize)
                            .setBold()
                            .setMarginTop(20)
                            .setMarginBottom(10));
                    continue;
                }

                // 处理引用
                if (line.startsWith(">")) {
                    document.add(new Paragraph(line.substring(1).trim())
                            .setFont(font)
                            .setFontSize(14)
                            .setFontColor(ColorConstants.GRAY)
                            .setMarginLeft(30)
                            .setMarginTop(5)
                            .setMarginBottom(5)
                            .setBorderLeft(new SolidBorder(ColorConstants.LIGHT_GRAY, 3))
                            .setPaddingLeft(15)
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY, 0.1f));
                    continue;
                }

                // 处理列表
                if (line.matches("^[*-+]\\s.*")) {
                    document.add(new Paragraph(line)
                            .setFont(font)
                            .setFontSize(14)
                            .setMarginLeft(30)
                            .setFirstLineIndent(-15)
                            .setMarginTop(3)
                            .setMarginBottom(3));
                    continue;
                }

                // 处理图片
                if (line.contains("![") && line.contains("](")) {
                    Pattern pattern = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)");
                    Matcher matcher = pattern.matcher(line);
                    int lastIndex = 0;

                    while (matcher.find()) {
                        // 处理图片前的文本
                        String textBefore = line.substring(lastIndex, matcher.start());
                        if (!textBefore.trim().isEmpty()) {
                            document.add(new Paragraph(textBefore)
                                    .setFont(font)
                                    .setFontSize(14)
                                    .setMultipliedLeading(1.5f));
                        }

                        String imageUrl = matcher.group(2);
                        try {
                            Image image = new Image(ImageDataFactory.create(new URL(imageUrl)));
                            float maxWidth = PageSize.A4.getWidth() - 2 * MARGIN;
                            if (image.getImageWidth() > maxWidth) {
                                float ratio = maxWidth / image.getImageWidth();
                                image.setWidth(maxWidth)
                                        .setHeight(image.getImageHeight() * ratio);
                            }
                            image.setHorizontalAlignment(HorizontalAlignment.CENTER)
                                    .setMarginTop(20)
                                    .setMarginBottom(20);
                            document.add(image);
                        } catch (Exception e) {
                            log.error("Failed to add image: " + imageUrl, e);
                            document.add(new Paragraph("[图片加载失败: " + matcher.group(1) + "]")
                                    .setFont(font)
                                    .setFontSize(12)
                                    .setFontColor(ColorConstants.RED));
                        }
                        lastIndex = matcher.end();
                    }

                    // 处理图片后的文本
                    if (lastIndex < line.length()) {
                        String remainingText = line.substring(lastIndex).trim();
                        if (!remainingText.isEmpty()) {
                            document.add(new Paragraph(remainingText)
                                    .setFont(font)
                                    .setFontSize(14)
                                    .setMultipliedLeading(1.5f));
                        }
                    }
                    continue;
                }

                // 处理加粗文本
                if (line.contains("**")) {
                    Pattern pattern = Pattern.compile("\\*\\*(.*?)\\*\\*");
                    Matcher matcher = pattern.matcher(line);
                    int lastIndex = 0;
                    StringBuilder paragraph = new StringBuilder();

                    while (matcher.find()) {
                        // 添加加粗前的普通文本
                        paragraph.append(line.substring(lastIndex, matcher.start()));
                        // 添加加粗文本
                        String boldText = matcher.group(1);
                        paragraph.append("[bold]").append(boldText).append("[/bold]");
                        lastIndex = matcher.end();
                    }

                    if (lastIndex < line.length()) {
                        paragraph.append(line.substring(lastIndex));
                    }

                    // 将处理后的文本转换为带格式的段落
                    String[] parts = paragraph.toString().split("\\[bold\\]|\\[/bold\\]");
                    Paragraph p = new Paragraph()
                            .setFont(font)
                            .setFontSize(14)
                            .setMultipliedLeading(1.0f) // 减小行距
                            .setFirstLineIndent(28); // 首行缩进

                    boolean isBold = false;
                    for (String part : parts) {
                        Text text = new Text(part);
                        if (isBold) {
                            text.setBold();
                        }
                        p.add(text);
                        isBold = !isBold;
                    }
                    document.add(p);
                    continue;
                }

                // 处理普通文本
                if (!line.trim().isEmpty()) {
                    document.add(new Paragraph(line)
                            .setFont(font)
                            .setFontSize(14)
                            .setMultipliedLeading(1.0f) // 减小行距
                            .setFirstLineIndent(28) // 首行缩进
                            .setMarginTop(0)
                            .setMarginBottom(6));
                }
            }

            // 添加底部分隔线和签名
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("").setHeight(2)
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                    .setMarginTop(20)
                    .setMarginBottom(20));

            // 添加签名
            String signature = userService.getSignatureUrl(postDto.getPost().getUserId()).getBody();
            if (signature != null && !signature.isEmpty()) {
                try {
                    Image signatureImage = new Image(ImageDataFactory.create(new URL(signature)))
                            .setWidth(150)
                            .setHeight(50)
                            .setHorizontalAlignment(HorizontalAlignment.RIGHT)
                            .setMarginTop(10);
                    document.add(signatureImage);
                } catch (Exception e) {
                    log.error("Failed to add signature", e);
                }
            }
        } finally {
            document.close();
        }
        return baos.toByteArray();
    }

    public byte[] exportToMarkdown(PostDto postDto) throws IOException {
        StringBuilder markdown = new StringBuilder();

        // 添加标题
        markdown.append("# ").append(postDto.getPost().getTitle()).append("\n\n");

        // 添加摘要
        markdown.append("> ").append(postDto.getPost().getAbstractContent()).append("\n\n");

        // 添加正文内容
        markdown.append(postDto.getPost().getContent()).append("\n\n");

        // 添加水印信息
        markdown.append("---\n\n");
        markdown.append("> 作者: ").append(postDto.getAuthor()).append("\n");
        markdown.append("> 来源: FreeShare\n");
        markdown.append("> 发布时间: ")
                .append(postDto.getPost().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .append("\n");
        markdown.append("> 版权声明: 本文遵循 CC BY-NC-SA 4.0 协议，转载请注明出处。\n");

        // 添加签名（如果有）
        String signature = userService.getSignatureUrl(postDto.getPost().getUserId()).getBody();
        if (signature != null && !signature.isEmpty()) {
            markdown.append("\n![签名](").append(signature).append(")");
        }

        return markdown.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static class HeaderFooterHandler implements IEventHandler {
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfDocument pdf = docEvent.getDocument();
            Document document = new Document(pdf);

            try {
                PdfFont font = PdfFontFactory.createFont(FONT_PATH);

                // 页眉
                Paragraph header = new Paragraph("FreeShare")
                        .setFont(font)
                        .setFontSize(8)
                        .setFontColor(ColorConstants.GRAY);
                document.showTextAligned(header, MARGIN,
                        PageSize.A4.getHeight() - MARGIN,
                        pdf.getNumberOfPages(),
                        TextAlignment.LEFT,
                        VerticalAlignment.TOP,
                        0);

                // 页脚
                Paragraph footer = new Paragraph(String.format("第 %d 页", pdf.getNumberOfPages()))
                        .setFont(font)
                        .setFontSize(8)
                        .setFontColor(ColorConstants.GRAY);
                document.showTextAligned(footer, MARGIN,
                        MARGIN,
                        pdf.getNumberOfPages(),
                        TextAlignment.LEFT,
                        VerticalAlignment.BOTTOM,
                        0);

                // 版权信息
                Paragraph copyright = new Paragraph("© FreeShare. All rights reserved.")
                        .setFont(font)
                        .setFontSize(8)
                        .setFontColor(ColorConstants.GRAY);
                document.showTextAligned(copyright,
                        PageSize.A4.getWidth() - MARGIN,
                        MARGIN,
                        pdf.getNumberOfPages(),
                        TextAlignment.RIGHT,
                        VerticalAlignment.BOTTOM,
                        0);
            } catch (IOException e) {
                log.error("Failed to create font", e);
            }
        }
    }
}