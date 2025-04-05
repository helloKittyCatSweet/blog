package com.kitty.blog.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kitty.blog.domain.model.rolePermission.RolePermission;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "fs_permissions")
@Data
public class Permission implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // Getters and Setters
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer permissionId;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    private String description;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;


    @Transient
    @JsonIgnore
    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolePermission> rolePermissions;


}
