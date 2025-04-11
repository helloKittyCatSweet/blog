package com.kitty.blog.application.dto.message;

import lombok.Builder;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
public class MessageDto implements Serializable {
    private Integer messageId;
    private Integer senderId;
    private String senderName;
    private Integer receiverId;
    private String receiverName;
    private String content;
    private LocalDate createdAt;
    private boolean suspicious;
    private Integer score;
    private String reason;
    private boolean operation;
}
