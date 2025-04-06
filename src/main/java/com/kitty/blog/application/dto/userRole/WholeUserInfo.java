package com.kitty.blog.application.dto.userRole;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kitty.blog.domain.model.Role;
import com.kitty.blog.domain.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class WholeUserInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private User user;

    @JsonSerialize(using = RoleListSerializer.class)
    @JsonDeserialize(using = RoleListDeserializer.class)
    @JsonProperty("roles")
    private List<Role> roles;

    public WholeUserInfo(User user, List<Role> roles) {
        this.user = user;
        this.roles = roles;
    }
}