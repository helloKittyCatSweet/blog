package com.kitty.blog.domain.model.search;

import com.kitty.blog.application.dto.post.PostDto;
import com.kitty.blog.domain.model.Post;
import com.kitty.blog.domain.model.tag.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Data
@Document(indexName = "posts")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostIndex {
    @Id
    @Field(type = FieldType.Integer)
    private Integer id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word",
                    searchAnalyzer = "ik_smart"),
            otherFields = {
                    @InnerField(suffix = "english", type = FieldType.Text, analyzer = "english")
            }
    )
    private String content;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String summary;

    @Field(type = FieldType.Integer)
    private Integer authorId;

    @Field(type = FieldType.Keyword)
    private String authorName;

    @Field(type = FieldType.Date)
    private LocalDate createTime;

    @Field(type = FieldType.Integer)
    private Integer viewCount;

    @Field(type = FieldType.Integer)
    private Integer likeCount;

    @Field(type = FieldType.Integer)
    private Integer favoriteCount;

    @Field(type = FieldType.Binary)
    private boolean isDeleted;

    @Field(type = FieldType.Keyword)
    private String tags;

    @Field(type = FieldType.Keyword)
    private String category;

    public static PostIndex convertToPostIndex(PostDto postDto) {
        return PostIndex.builder()
                .id(postDto.getPost().getPostId())
                .title(postDto.getPost().getTitle())
                .content(postDto.getPost().getContent())
                .summary(postDto.getPost().getAbstractContent())
                .authorId(postDto.getPost().getUserId())
                .authorName(postDto.getAuthor())
                .createTime(postDto.getPost().getCreatedAt())
                .viewCount(postDto.getPost().getViews())
                .likeCount(postDto.getPost().getLikes())
                .favoriteCount(postDto.getPost().getFavorites())
                .isDeleted(postDto.getPost().isDeleted())
                .tags(postDto.getTags() != null
                        ? postDto.getTags().stream().map(Tag::getName)
                        .collect(Collectors.joining(","))
                        : "")
                .category(postDto.getCategory() != null ? postDto.getCategory().getName() : "")
                .build();
    }

    public static Post convertToPost(PostIndex postIndex) {
        return Post.builder()
                .postId(postIndex.getId())
                .userId(postIndex.getAuthorId())
                .title(postIndex.getTitle())
                .abstractContent(postIndex.getSummary())
                .content(postIndex.getContent())
                .createdAt(postIndex.getCreateTime())
                .isPublished(true)
                .isDraft(false)
                .visibility("PUBLIC")
                .views(postIndex.getViewCount())
                .likes(postIndex.getLikeCount())
                .favorites(postIndex.getFavoriteCount())
                .build();
    }
}