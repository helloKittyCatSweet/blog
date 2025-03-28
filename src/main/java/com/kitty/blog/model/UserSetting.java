package com.kitty.blog.model;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "user_settings")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "settingId")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
public class UserSetting implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer settingId;

    @Column(name = "user_id")
    private Integer userId;

    @Column
    private String theme;

    @Column
    private Boolean notifications;

    @Column(name = "github_account")
    private String githubAccount;

    @Column(name = "csdn_account")
    private String CSDNAccount;

    @Column(name = "bilibili_account")
    private String BiliBiliAccount;

    /**
     * Transient 字段，不参与数据库的映射，仅用于业务逻辑
     */

    // 关联对象，按需加载
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonBackReference(value = "user-userSetting")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSetting that = (UserSetting) o;
        return settingId.equals(that.settingId) &&
                userId.equals(that.userId) && theme.equals(that.theme)
                && notifications.equals(that.notifications);
    }

    @Override
    public int hashCode() {
        return settingId.hashCode() + userId.hashCode() + theme.hashCode() + notifications.hashCode();
    }

    @Override
    public String toString() {
        return "UserSetting{" +
                "settingId=" + settingId +
                ", userId=" + userId +
                ", theme='" + theme + '\'' +
                ", notifications=" + notifications +
                '}';
    }
}
