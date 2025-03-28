package com.kitty.blog.controller.user.security;

import com.kitty.blog.service.CaptchaService;
import com.kitty.blog.utils.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
@Tag(name = "图片验证码")
@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class CaptchaController {

    @Autowired
    private CaptchaService captchaService;

    @GetMapping("/auth/captcha")
    public ResponseEntity<byte[]> getCaptcha(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // 1. 获取或生成sessionId
        String sessionId = request.getSession().getId();
        log.info("Generating captcha for session ID: {}", sessionId);

        // 2. 生成验证码图片
        BufferedImage captchaImage = captchaService.generateCaptcha(sessionId);

        // 3. 转换图片为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(captchaImage, "jpeg", baos);
        byte[] imageBytes = baos.toByteArray();

        // 4. 关键修改：添加CORS暴露头
        response.setHeader("Access-Control-Expose-Headers", "Session-Id, session-id");

        // 5. 设置响应头（同时设置两种大小写格式）
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setCacheControl("no-store, no-cache");
        headers.set("Session-Id", sessionId);  // 标准大小写
        headers.set("session-id", sessionId); // 小写兼容

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/auth/verify")
    public ResponseEntity<Response<String>> verifyCaptcha(HttpServletRequest request,
            @RequestParam String code,
            @RequestParam String sessionId) {
        // 获取当前会话的 ID
        log.error("Verifying captcha code for session ID: {}", sessionId);
        try {
            if (captchaService.verifyCaptcha(sessionId, code)) {
                return Response.ok("验证码正确");
            } else {
                return Response.error("验证码错误");
            }
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }
}