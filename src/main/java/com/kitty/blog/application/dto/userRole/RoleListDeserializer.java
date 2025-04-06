package com.kitty.blog.application.dto.userRole;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kitty.blog.domain.model.Role;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class RoleListDeserializer extends JsonDeserializer<List<Role>> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Role> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        List<Role> roles = new ArrayList<>();

        if (node.isArray()) {
            for (JsonNode elementNode : node) {
                if (elementNode.isObject()) {
                    roles.add(objectMapper.treeToValue(elementNode, Role.class));
                } else if (elementNode.isNumber()) {
                    // 如果是数字（ID），创建包含该ID的Role对象
                    Role role = new Role();
                    role.setRoleId(elementNode.asInt());
                    roles.add(role);
                }
            }
        }

        return roles;
    }
}