package com.kitty.blog.constant;

import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ToString
@Getter
public enum ActivityType {
    VIEW("view"),
    COMMENT("comment"),
    LIKE("like"),
    FAVORITE("favorite"),
    FOLLOW("follow"),
    UNFOLLOW("unfollow"),
    SHARE("share");

    public final String type;

    ActivityType(String type) {
        this.type = type;
    }

    public static Optional<ActivityType> fromType(String type) {
        return Arrays.stream(ActivityType.values())
                .filter(activityType -> activityType.getType().equalsIgnoreCase(type))
                .findFirst();
    }

    /**
     * 获取所有互动类型
     */
    public static List<String> getInteractTypes() {
        return Arrays.asList(
                LIKE.getType(),
                COMMENT.getType(),
                FAVORITE.getType(),
                FOLLOW.getType(),
                UNFOLLOW.getType(),
                SHARE.getType()
        );
    }

    // 检查是否为有效的互动类型
    public static boolean isValidInteractType(String type) {
        return type == null || getInteractTypes().contains(type.toLowerCase());
    }
}
