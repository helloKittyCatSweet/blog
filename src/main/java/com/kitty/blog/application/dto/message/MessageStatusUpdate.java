package com.kitty.blog.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatusUpdate {
    private Integer messageId;
    private String status;  // SENT, DELIVERED, READ
    private Integer senderId;
    private Integer receiverId;
    private Long timestamp;

    public MessageStatusUpdate(Integer messageId, String status) {
        this.messageId = messageId;
        this.status = status;
        this.timestamp = System.currentTimeMillis();
    }
}