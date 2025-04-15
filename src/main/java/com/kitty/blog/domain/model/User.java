package com.kitty.blog.domain.model;

import com.fasterxml.jackson.annotation.*;
import com.kitty.blog.infrastructure.converter.EncryptedAttributeConverter;
import com.kitty.blog.domain.model.userRole.UserRole;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.io.Serial;
import java.io.Serializable;
import java.security.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "fs_users")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "userId")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Audited
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    @Column(nullable = false, unique = true)
    private String username;

    private String nickname;

    @Column(nullable = false)
    @NotAudited
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String avatar;

    @Column
    private String token;

    // 禁止手动加入，禁止更新
    @Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP")
    private LocalDate createdAt;

    @Column(name = "updated_at", columnDefinition = "TIMESTAMP default CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDate updatedAt;

    @Column(name = "is_active")
    private Boolean isActive;

    private Integer gender;

    private LocalDate birthday;

    @Convert(converter = EncryptedAttributeConverter.class)
    private String phone;

    @Convert(converter = EncryptedAttributeConverter.class)
    private String address;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    @Column(name = "last_login_time")
    private LocalDate lastLoginTime;

    @Column
    private List<String> tags;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "last_login_ip",length = 45)
    private String lastLoginIp;

    @Column(name = "last_login_location")
    private String lastLoginLocation;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    @Lob
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
        return  "User{" +
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
}
