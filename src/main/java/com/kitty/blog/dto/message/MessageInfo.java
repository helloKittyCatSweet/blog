package com.kitty.blog.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MessageInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String username;
    private String avatar;
    private String message;
    private Integer unreadCount;
    private LocalDate lastMessageTime;

    // 私有构造函数
    private MessageInfo() {}

    // Builder 类
    public static class Builder {
        private final MessageInfo messageInfo;

        public Builder() {
            messageInfo = new MessageInfo();
        }

        public Builder userId(Integer userId) {
            messageInfo.userId = userId;
            return this;
        }

        public Builder username(String username) {
            messageInfo.username = username;
            return this;
        }

        public Builder avatar(String avatar) {
            messageInfo.avatar = avatar;
            return this;
        }

        public Builder lastMessage(String lastMessage) {
            messageInfo.message = lastMessage;
            return this;
        }

        public Builder unreadCount(Integer unreadCount) {
            messageInfo.unreadCount = unreadCount;
            return this;
        }

        public Builder lastMessageTime(LocalDate lastMessageTime) {
            messageInfo.lastMessageTime = lastMessageTime;
            return this;
        }

        public MessageInfo build() {
            return messageInfo;
        }
    }

    // 提供静态方法创建Builder
    public static Builder builder() {
        return new Builder();
    }
}