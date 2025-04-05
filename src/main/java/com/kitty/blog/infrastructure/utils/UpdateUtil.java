package com.kitty.blog.utils;

import java.lang.reflect.Field;
import java.util.Objects;

public class UpdateUtil {

    public static <T> void updateNotNullProperties(T source, T target) throws IllegalAccessException {
        Class<?> clazz = source.getClass();
        Field[] fields = clazz.getDeclaredFields(); // 获取所有字段
        for (Field field : fields) {
            // 检查字段是否为 serialVersionUID
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            field.setAccessible(true); // 设置访问权限
            Object sourceValue = field.get(source);
            // 如果 source 中的字段不为空，则更新到 target 中
            if (Objects.nonNull(sourceValue)) {
                field.set(target, sourceValue);
            }
        }
    }
}
