package com.kitty.blog.model.userActivity;

import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
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
}
