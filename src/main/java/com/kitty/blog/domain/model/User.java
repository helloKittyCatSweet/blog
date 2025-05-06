package com.kitty.blog.domain.model;

import com.fasterxml.jackson.annotation.*;
import com.kitty.blog.infrastructure.converter.EncryptedAttributeConverter;
import com.kitty.blog.domain.model.userRole.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.io.Serial;
import java.io.Serializable;
import java.security.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "fs_users", indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_last_login", columnList = "last_login_time")
})
@org.hibernate.annotations.Comment("用户表")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
@DynamicInsert
@DynamicUpdate
@Audited
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @org.hibernate.annotations.Comment("用户ID")
    private Integer userId;

    @Column(nullable = false, unique = true)
    @org.hibernate.annotations.Comment("用户名")
    private String username;

    @org.hibernate.annotations.Comment("昵称")
    private String nickname;

    @Column(nullable = false)
    @NotAudited
    @org.hibernate.annotations.Comment("密码")
    private String password;

    @Column(nullable = false, unique = true)
    @org.hibernate.annotations.Comment("邮箱")
    private String email;

    @Column
    @org.hibernate.annotations.Comment("头像url")
    private String avatar;

    @Column
    @org.hibernate.annotations.Comment("token")
    private String token;

    // 禁止手动加入，禁止更新
    @Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP")
    @org.hibernate.annotations.Comment("创建时间")
    private LocalDate createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @org.hibernate.annotations.Comment("更新时间")
    private LocalDate updatedAt;

    @Column(name = "is_active")
    @org.hibernate.annotations.Comment("是否激活")
    private Boolean isActive;

    @Column(name = "gender")
    @org.hibernate.annotations.Comment("性别")
    private Integer gender;

    @Column(name = "birthday")
    @org.hibernate.annotations.Comment("生日")
    private LocalDate birthday;

    @Column(name = "phone")
    @org.hibernate.annotations.Comment("手机号")
    @Convert(converter = EncryptedAttributeConverter.class)
    private String phone;

    @Column(columnDefinition = "TEXT", name = "address")
    @org.hibernate.annotations.Comment("地址")
    @Convert(converter = EncryptedAttributeConverter.class)
    private String address;

    @Column(columnDefinition = "TEXT")
    @org.hibernate.annotations.Comment("简介")
    private String introduction;

    @Column(name = "last_login_time")
    @org.hibernate.annotations.Comment("最后登录时间")
    private LocalDate lastLoginTime;

    @Column
    @Size(max = 5, message = "标签数量不能超过5个")
    @Valid
    @org.hibernate.annotations.Comment("标签")
    private List<String> tags;

    @Column(name = "is_deleted")
    @org.hibernate.annotations.Comment("是否删除")
    private boolean isDeleted;

    @Column(name = "last_login_ip", length = 45)
    @org.hibernate.annotations.Comment("最后登录IP")
    private String lastLoginIp;

    @Column(name = "last_login_location")
    @org.hibernate.annotations.Comment("最后登录位置")
    private String lastLoginLocation;

    @Column(columnDefinition = "LONGTEXT")
    @Lob
    @org.hibernate.annotations.Comment("个性签名")
    private String signature;

    /**
     * Transient区域，用于存放一些不希望被序列化的字段
     */
    @NotAudited
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-post")
    private List<Post> posts;

    @NotAudited
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-comment")
    private List<Comment> comments;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-favorite")
    private List<Favorite> favorites;

    @NotAudited
    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-userSetting")
    private List<UserSetting> userSettings;

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference(value = "user-userRole")
    private List<UserRole> userRoles;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return userId.equals(user.userId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", avatar='" + avatar + '\'' +
                ", token='" + token + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", isActive=" + isActive +
                ", gender=" + gender +
                ", birthday=" + birthday +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", introduction='" + introduction + '\'' +
                ", lastLoginTime=" + lastLoginTime +
                '}';
    }

    // 添加 getter 和 setter
    public List<String> getTags() {
        return tags == null ? new ArrayList<>() : tags;
    }
}
