package com.kitty.blog.domain.service.contact;

import com.kitty.blog.application.dto.user.ContactMessageDTO;
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

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String adminEmail;

    private static final String EMAIL_TEMPLATE = """
            收到新的联系消息：

            发送者：%s
            邮箱：%s
            主题：%s

            消息内容：
            %s
            """;

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
                String content = String.format(EMAIL_TEMPLATE,
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

            String autoReplyContent = String.format("""
                    亲爱的 %s：

                    感谢您的留言。我们已收到您的消息，会尽快回复您。

                    祝好，
                    FreeShare 团队
                    """, name);

            helper.setText(autoReplyContent, false);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error("发送自动回复失败", e);
        }
    }
}