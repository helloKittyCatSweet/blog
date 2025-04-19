package com.kitty.blog.domain.model.search;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

@Data
@Document(indexName = "tags")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagIndex {
    @Id
    private Integer id;

    // 使用 Text 类型，并添加多字段映射
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "ik_max_word"),
            otherFields = {
                    @InnerField(suffix = "english", type = FieldType.Text, analyzer = "english")
            }
    )
    private String name;
}
