package com.kitty.blog.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.relational.core.sql.In;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@Table(name = "messages")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "messageId")
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @Column(name = "sender_id")
    private Integer senderId;

    @Column(name = "receiver_id")
    private Integer receiverId;

    @Column(nullable = false)
    private String content;

    @Column(name = "is_read")
    private Boolean isRead;

    // 禁止手动加入，禁止更新
    @Column(name = "created_at",
            insertable = false,
            updatable = false,
            columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP")
    private LocalDate createdAt;

    @Column(name = "parent_id")
    private Integer parentId;

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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
