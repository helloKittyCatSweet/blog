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
@org.hibernate.annotations.Comment("用户设置表")
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
    @org.hibernate.annotations.Comment("设置ID")
    private Integer settingId;

    @Column(name = "user_id")
    @org.hibernate.annotations.Comment("用户ID")
    private Integer userId;

    @Column(name = "theme")
    @org.hibernate.annotations.Comment("主题")
    private String theme = "light";

    @Column(name = "github_account")
    @org.hibernate.annotations.Comment("github账号")
    private String githubAccount = null;

    @Column(name = "csdn_account")
    @org.hibernate.annotations.Comment("CSDN账号")
    private String CSDNAccount = null;

    @Column(name = "bilibili_account")
    @org.hibernate.annotations.Comment("Bilibili账号")
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
                userId.equals(that.userId) && theme.equals(that.theme);
    }

    @Override
    public int hashCode() {
        return settingId.hashCode() + userId.hashCode() + theme.hashCode();
    }

    @Override
    public String toString() {
        return "UserSetting{" +
                "settingId=" + settingId +
                ", userId=" + userId +
                ", theme='" + theme + '\'' +
                '}';
    }

    public UserSetting(Integer userId){
        this.userId = userId;
    }
}
