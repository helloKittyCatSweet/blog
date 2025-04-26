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

    private static final String FONT_PATH = "static/fonts/simsun.ttf";
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

            // 添加装饰线
            document.add(new Paragraph("").setHeight(2)
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                    .setMarginBottom(20));

            // 优化标题样式
            Paragraph title = new Paragraph(postDto.getPost().getTitle())
                    .setFont(font)
                    .setFontSize(28)
                    .setFontColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setMarginBottom(30);
            document.add(title);

            // 优化作者信息区域
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

            // 处理文章内容
            String content = postDto.getPost().getContent();
            Pattern pattern = Pattern.compile("!\\[(.*?)\\]\\((.*?)\\)");
            Matcher matcher = pattern.matcher(content);
            int lastIndex = 0;

            while (matcher.find()) {
                String textBefore = content.substring(lastIndex, matcher.start());
                if (!textBefore.trim().isEmpty()) {
                    // 优化正文段落样式
                    document.add(new Paragraph(textBefore)
                            .setFont(font)
                            .setFontSize(14)
                            .setFirstLineIndent(28)
                            .setMultipliedLeading(1.5f));
                }

                String imageUrl = matcher.group(2);
                try {
                    Image image = new Image(ImageDataFactory.create(new URL(imageUrl)));
                    float maxWidth = PageSize.A4.getWidth() - 2 * MARGIN;
                    if (image.getImageWidth() > maxWidth) {
                        image.setWidth(maxWidth);
                    }
                    // 优化图片样式
                    image.setHorizontalAlignment(HorizontalAlignment.CENTER)
                            .setMarginTop(20)
                            .setMarginBottom(20);
                    document.add(image);
                } catch (Exception e) {
                    log.error("Failed to add image: " + imageUrl, e);
                }

                lastIndex = matcher.end();
            }

            if (lastIndex < content.length()) {
                document.add(new Paragraph(content.substring(lastIndex))
                        .setFont(font)
                        .setFontSize(14)
                        .setFirstLineIndent(28)
                        .setMultipliedLeading(1.5f));
            }

            // 添加底部分隔线
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("").setHeight(2)
                    .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                    .setMarginTop(20)
                    .setMarginBottom(20));

            // 优化签名样式
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