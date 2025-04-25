package com.kitty.blog.domain.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "fs_user_settings")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "settingId")
@DynamicInsert
@DynamicUpdate
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSetting implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer settingId;

    @Column(name = "user_id")
    private Integer userId;

    @Column
    private String theme = "light";

    @Column
    private Boolean notifications = true;

    @Column(name = "github_account")
    private String githubAccount = null;

    @Column(name = "csdn_account")
    private String CSDNAccount = null;

    @Column(name = "bilibili_account")
    private String BiliBiliAccount = null;

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

    public UserSetting(Integer userId){
        this.userId = userId;
    }
}
