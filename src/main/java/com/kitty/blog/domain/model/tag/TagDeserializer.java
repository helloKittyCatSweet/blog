package com.kitty.blog.domain.model.tag;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.kitty.blog.domain.model.tag.Tag;

import java.io.IOException;

public class TagDeserializer extends JsonDeserializer<Tag> {
    @Override
    public Tag deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        Tag tag = new Tag();
        if (node.isNumber()) {
            // 如果输入只是一个数字，则认为是tagId
            tag.setTagId(node.asInt());
        } else {
            // 否则解析完整的Tag对象
            if (node.has("name")) {
                tag.setName(node.get("name").asText());
            }
            if (node.has("adminWeight")) {
                tag.setAdminWeight(node.get("adminWeight").asInt());
            }

            // 其他字段设置默认值
            if (node.has("tagId")) {
                tag.setTagId(node.get("tagId").asInt());
            }
            if (node.has("useCount")) {
                tag.setUseCount(node.get("useCount").asInt());
            } else {
                tag.setUseCount(0);
            }
            if (node.has("clickCount")) {
                tag.setClickCount(node.get("clickCount").asInt());
            } else {
                tag.setClickCount(0);
            }
            if (node.has("weight")) {
                tag.setWeight(node.get("weight").asInt());
            } else {
                tag.setWeight(0);
            }
        }
        return tag;
    }
}