package com.kitty.blog.application.dto.userRole;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.kitty.blog.domain.model.Role;

import java.io.IOException;
import java.util.List;

public class RoleListSerializer extends JsonSerializer<List<Role>> {
    @Override
    public void serialize(List<Role> roles, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartArray();
        for (Role role : roles) {
            gen.writeObject(role);
        }
        gen.writeEndArray();
    }
}