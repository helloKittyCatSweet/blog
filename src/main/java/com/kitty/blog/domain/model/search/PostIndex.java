package com.kitty.blog.domain.model.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDate;

@Data
@Document(indexName = "posts")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostIndex {
    @Id
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
}