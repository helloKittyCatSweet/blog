package com.kitty.blog.domain.model.rolePermission;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Entity
@Table(name = "fs_role_permissions")
@NoArgsConstructor
public class RolePermission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private RolePermissionId id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolePermission that = (RolePermission) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "RolePermission{" +
                "id=" + id +
                '}';
    }

}
