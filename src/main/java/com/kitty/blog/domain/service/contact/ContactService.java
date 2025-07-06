package com.kitty.blog.domain.service.contact;

import com.kitty.blog.application.dto.user.ContactMessageDTO;
import com.kitty.blog.infrastructure.config.constant.MailTemplateConfig;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContactService {

    private final JavaMailSender mailSender;

    private final MailTemplateConfig mailTemplateConfig;

    @Value("${spring.mail.username}")
    private String adminEmail;

    @Async("emailTaskExecutor")
    public CompletableFuture<String> sendContactMessage(ContactMessageDTO message) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

                // 设置发件人为系统邮箱
                helper.setFrom(adminEmail);
                // 发送给管理员
                helper.setTo(adminEmail);
                // 设置回复邮箱为发送者的邮箱
                helper.setReplyTo(message.getEmail());

                // 设置邮件主题
                helper.setSubject("新的联系消息: " + message.getSubject());

                // 设置邮件内容
                String content = String.format(mailTemplateConfig.getAdminNotification(),
                        message.getName(),
                        message.getEmail(),
                        message.getSubject(),
                        message.getMessage());
                helper.setText(content, false);

                mailSender.send(mimeMessage);

                // 发送自动回复给用户
                sendAutoReply(message.getEmail(), message.getName());

                return "消息已成功发送";
            } catch (Exception e) {
                log.error("发送联系消息失败", e);
                throw new RuntimeException("消息发送失败，请稍后重试");
            }
        });
    }

    private void sendAutoReply(String toEmail, String name) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(adminEmail);
            helper.setTo(toEmail);
            helper.setSubject("感谢您的联系");

            String autoReplyContent = String.format(mailTemplateConfig.getAutoReply(), name);

            helper.setText(autoReplyContent, false);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("发送自动回复失败", e);
        }
    }
}