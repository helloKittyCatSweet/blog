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
    private TesseractOCR tesseractOCR; // OCR服务

    @Autowired
    private AliyunOSSUploader aliyunOSSUploader; // 阿里云OSS上传服务

    public String generateSignature(Integer userId, String text) {
        try {
            // 计算合适的图片尺寸和字体大小
            int fontSize = 40;
            int baseWidth = 300;
            int baseHeight = 100;

            // 创建临时图片计算文字尺寸
            BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
            Graphics2D tempG2d = tempImage.createGraphics();
            Font font = new Font("楷体", Font.BOLD, fontSize);
            tempG2d.setFont(font);
            FontMetrics fm = tempG2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            tempG2d.dispose();

            // 如果文字宽度超过基础宽度，调整图片宽度和字体大小
            int finalWidth = baseWidth;
            if (textWidth > baseWidth - 40) { // 预留左右边距
                float ratio = (float) (baseWidth - 40) / textWidth;
                fontSize = Math.max((int) (fontSize * ratio), 20); // 最小字号20
                finalWidth = Math.max(textWidth + 40, baseWidth); // 确保最小宽度
            }

            // 创建最终图片
            BufferedImage image = new BufferedImage(finalWidth, baseHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = image.createGraphics();

            // 设置白色背景
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, finalWidth, baseHeight);

            // 添加防伪底纹
            addSecurityPattern(g2d, finalWidth, baseHeight);

            // 添加签名文字
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("楷体", Font.BOLD, fontSize));
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            Random random = new Random();
            int angle = random.nextInt(20) - 10;
            g2d.rotate(Math.toRadians(angle), finalWidth / 2, baseHeight / 2);

            // 重新计算文字位置
            fm = g2d.getFontMetrics();
            int x = (finalWidth - fm.stringWidth(text)) / 2;
            g2d.drawString(text, x, 60);

            // 添加随机笔画效果
            addStrokeEffect(g2d, text, x, 60, fontSize);

            // 添加防伪水印
            addWatermark(g2d, userId.toString(), finalWidth, baseHeight);

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

    private void addSecurityPattern(Graphics2D g2d, int width, int height) {
        // 添加细微的网格底纹
        g2d.setColor(new Color(245, 245, 245));
        for (int i = 0; i < width; i += 5) {
            for (int j = 0; j < height; j += 5) {
                g2d.drawLine(i, j, i + 3, j);
                g2d.drawLine(i, j, i, j + 3);
            }
        }
    }

    private void addWatermark(Graphics2D g2d, String userId, int width, int height) {
        // 添加半透明水印
        g2d.setColor(new Color(0, 0, 0, 30));
        g2d.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        // 生成水印文本（包含时间戳和用户ID）
        String watermark = String.format("FreeShare %s %s",
                userId, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")));

        // 旋转45度添加水印
        AffineTransform old = g2d.getTransform();
        g2d.rotate(Math.toRadians(-45));

        // 重复添加水印，根据图片尺寸调整
        for (int i = -width; i < width; i += 100) {
            for (int j = -height; j < height * 2; j += 50) {
                g2d.drawString(watermark, i, j);
            }
        }

        g2d.setTransform(old);
    }

    private void addStrokeEffect(Graphics2D g2d, String text, int x, int y, int fontSize) {
        Random random = new Random();
        g2d.setStroke(new BasicStroke(0.5f));
        int charSpacing = fontSize * 3 / 4; // 根据字体大小调整字符间距

        for (int i = 0; i < text.length(); i++) {
            int offsetX = random.nextInt(4) - 2;
            int offsetY = random.nextInt(4) - 2;
            g2d.drawLine(x + i * charSpacing + offsetX, y + offsetY,
                    x + (i + 1) * charSpacing + offsetX, y + offsetY);
        }
    }
}