package com.kitty.blog.domain.model;

import com.fasterxml.jackson.annotation.*;
import com.kitty.blog.domain.model.userRole.UserRole;
import lombok.Data;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.envers.Audited;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "fs_roles")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "roleId")
@DynamicUpdate
@DynamicInsert
@NoArgsConstructor
@Audited
public class Role implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;

    @Column(nullable = false, unique = true)
    private String roleName;

    @Column
    private String description;

    @Column(nullable = false)
    private String administratorName;

    @Column(nullable = false)
    private Integer count;

    /**
     * Transient字段，仅用于业务
     */
    @Transient
    @JsonIgnore
    @OneToMany(mappedBy = "role")
    @JsonManagedReference(value = "role-userRole")
    private List<UserRole> userRoles;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Role role = (Role) o;
        return roleId.equals(role.roleId) && roleName.equals(role.roleName)
                && description.equals(role.description);
    }

    @Override
    public int hashCode() {
        return roleId.hashCode() + roleName.hashCode() + description.hashCode();
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
