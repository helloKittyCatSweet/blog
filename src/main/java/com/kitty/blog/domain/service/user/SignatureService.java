package com.kitty.blog.domain.service.user;

import com.kitty.blog.infrastructure.utils.AliyunOSSUploader;
import com.kitty.blog.infrastructure.utils.TesseractOCR;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@Slf4j
public class SignatureService {
    @Autowired
    private TesseractOCR tesseractOCR;  // OCR服务

    @Autowired
    private AliyunOSSUploader aliyunOSSUploader;  // 阿里云OSS上传服务

    public String generateSignature(Integer userId, String text) {
        try {
            BufferedImage image = new BufferedImage(300, 100, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // 设置白色背景
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, 300, 100);

            // 添加防伪底纹
            addSecurityPattern(g2d);

            // 添加签名文字
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("楷体", Font.BOLD, 40));
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Random random = new Random();
            int angle = random.nextInt(20) - 10;
            g2d.rotate(Math.toRadians(angle), 150, 50);

            FontMetrics fm = g2d.getFontMetrics();
            int x = (300 - fm.stringWidth(text)) / 2;
            g2d.drawString(text, x, 60);

            // 添加随机笔画效果
            addStrokeEffect(g2d, text, x, 60);

            // 添加防伪水印
            addWatermark(g2d, userId.toString());

            g2d.dispose();

            // 创建临时文件并上传
            File tempFile = File.createTempFile("signature_", ".png");
            ImageIO.write(image, "PNG", tempFile);

            try {
                return aliyunOSSUploader.uploadSignature(tempFile, userId);
            } finally {
                if (!tempFile.delete()) {
                    log.error("删除临时文件失败：{}", tempFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            log.error("生成签名失败", e);
            throw new RuntimeException("生成签名失败");
        }
    }

    private void addSecurityPattern(Graphics2D g2d) {
        // 添加细微的网格底纹
        g2d.setColor(new Color(245, 245, 245));
        for (int i = 0; i < 300; i += 5) {
            for (int j = 0; j < 100; j += 5) {
                g2d.drawLine(i, j, i + 3, j);
                g2d.drawLine(i, j, i, j + 3);
            }
        }
    }

    private void addWatermark(Graphics2D g2d, String userId) {
        // 添加半透明水印
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        // 生成水印文本（包含时间戳和用户ID）
        String watermark = String.format("FreeShare %s %s",
                userId, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));

        // 旋转45度添加水印
        AffineTransform old = g2d.getTransform();
        g2d.rotate(Math.toRadians(-45));

        // 重复添加水印
        for (int i = -300; i < 300; i += 100) {
            for (int j = -100; j < 200; j += 50) {
                g2d.drawString(watermark, i, j);
            }
        }

        g2d.setTransform(old);
    }


    private void addStrokeEffect(Graphics2D g2d, String text, int x, int y) {
        Random random = new Random();
        g2d.setStroke(new BasicStroke(0.5f));

        for (int i = 0; i < text.length(); i++) {
            int offsetX = random.nextInt(4) - 2;
            int offsetY = random.nextInt(4) - 2;
            g2d.drawLine(x + i * 30 + offsetX, y + offsetY,
                    x + (i + 1) * 30 + offsetX, y + offsetY);
        }
    }
}