package com.kitty.blog.domain.model.search;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

@Data
@Document(indexName = "categories")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryIndex {
    @Id
    private Integer id;

    // 使用 Text 类型，并添加多字段映射
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart"),
            otherFields = {
                    @InnerField(suffix = "pinyin", type = FieldType.Text, analyzer = "pinyin_analyzer"),
                    @InnerField(suffix = "english", type = FieldType.Text, analyzer = "english"),
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            }
    )
    private String name;
}
