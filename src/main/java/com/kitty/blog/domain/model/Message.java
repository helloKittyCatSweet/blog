package com.kitty.blog.domain.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "fs_messages", indexes = {
                @Index(name = "idx_message_users", columnList = "sender_id,receiver_id"),
                @Index(name = "idx_message_status", columnList = "status"),
                @Index(name = "idx_message_time", columnList = "created_at")
})
@org.hibernate.annotations.Comment("消息表")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "messageId")
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
public class Message implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @org.hibernate.annotations.Comment("消息ID")
        private Integer messageId;

        @Column(name = "sender_id")
        @org.hibernate.annotations.Comment("发送者ID")
        private Integer senderId;

        @Column(name = "receiver_id")
        @org.hibernate.annotations.Comment("接收者ID")
        private Integer receiverId;

        @Column(nullable = false)
        @org.hibernate.annotations.Comment("消息内容")
        private String content;

        @Column(name = "is_read")
        @org.hibernate.annotations.Comment("是否已读")
        private Boolean isRead;

        @Column(name = "message_type")
        @org.hibernate.annotations.Comment("消息类型")
        private String messageType; // 私信类型

        @Column(name = "status")
        @org.hibernate.annotations.Comment("发送状态")
        private String status; // 发送状态

        // 禁止手动加入，禁止更新
        @Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP")
        @org.hibernate.annotations.Comment("创建时间")
        private LocalDate createdAt;

        @Column(name = "updated_at", columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
        @org.hibernate.annotations.Comment("更新时间")
        private LocalDate updatedAt;

        @Column(name = "parent_id")
        @org.hibernate.annotations.Comment("父消息ID")
        private Integer parentId;

        @Column(name = "suspicious")
        @org.hibernate.annotations.Comment("是否可疑")
        private boolean suspicious; // 是否可疑

        @Column(name = "score")
        @org.hibernate.annotations.Comment("可疑评分")
        private Integer score; // 可疑评分

        @Column(name = "reason")
        @org.hibernate.annotations.Comment("可疑原因")
        private String reason; // 可疑原因

        @Column(name = "operation")
        @org.hibernate.annotations.Comment("是否被处理")
        private boolean operation = false; // 是否被处理

        /**
         * Transient 字段，不参与数据库的映射，仅用于业务逻辑处理
         */

        // 关联对象，按需加载
        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "sender_id", insertable = false, updatable = false)
        @JsonManagedReference
        private User sender;

        @JsonIgnore
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "receiver_id", insertable = false, updatable = false)
        @JsonManagedReference
        private User receiver;

        @Override
        public String toString() {
                return "Message{" +
                                "messageId=" + messageId +
                                ", senderId=" + senderId +
                                ", receiverId=" + receiverId +
                                ", content='" + content + '\'' +
                                ", isRead=" + isRead +
                                ", createdAt=" + createdAt +
                                ", parentId=" + parentId +
                                '}';
        }

        @Override
        public boolean equals(Object o) {
                if (this == o)
                        return true;
                if (o == null || getClass() != o.getClass())
                        return false;
                Message message = (Message) o;
                return messageId.equals(message.messageId) &&
                                senderId.equals(message.senderId) &&
                                receiverId.equals(message.receiverId) &&
                                content.equals(message.content) &&
                                isRead.equals(message.isRead) &&
                                createdAt.equals(message.createdAt) &&
                                parentId.equals(message.parentId);
        }

        @Override
        public int hashCode() {
                return messageId.hashCode() + senderId.hashCode() +
                                receiverId.hashCode() + content.hashCode() +
                                isRead.hashCode() + createdAt.hashCode() + parentId.hashCode();
        }
}
