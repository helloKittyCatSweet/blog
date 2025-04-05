package com.kitty.blog.domain.model.rolePermission;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
public class RolePermissionId implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "permission_id")
    private Integer permissionId;

    @Column(name = "role_id")
    private Integer roleId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass()!= o.getClass()) return false;
        RolePermissionId that = (RolePermissionId) o;
        return permissionId.equals(that.permissionId) && roleId.equals(that.roleId);
    }

    @Override
    public int hashCode() {
        return permissionId.hashCode() * 31 + roleId.hashCode();
    }

    @Override
    public String toString() {
        return "RolePermissionId{" +
                "permissionId=" + permissionId +
                ", roleId=" + roleId +
                '}';
    }
}
