package com.kitty.blog.domain.model.userRole;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.Audited;

import java.io.Serial;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class UserRoleId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "role_id")
    private Integer roleId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass()!= o.getClass()) return false;
        UserRoleId that = (UserRoleId) o;
        return userId.equals(that.userId) && roleId.equals(that.roleId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode() * 31 + roleId.hashCode();
    }

    @Override
    public String toString() {
        return "UserRoleId{" +
                "userId=" + userId +
                ", roleId=" + roleId +
                '}';
    }
}
