package com.kitty.blog.application.dto.favorite;

import com.kitty.blog.domain.model.Post;
import lombok.Data;

@Data
public class FavoriteDto {

    private Post post;

    private Integer favoriteId;

    private String folderName;

    public FavoriteDto(Post post, Integer favoriteId, String folderName) {
        this.favoriteId = favoriteId;
        this.post = post;
        this.folderName = folderName;
    }
}
