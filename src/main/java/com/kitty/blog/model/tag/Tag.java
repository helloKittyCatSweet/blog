package com.kitty.blog.model.tag;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "fs_tags")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "tagId")
@DynamicInsert
@DynamicUpdate
public class Tag implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tagId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "use_count")
    private Integer useCount = 0; // 使用次数

    @Column(name = "click_count")
    private Integer clickCount = 0; // 点击次数

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt; // 最后一次使用时间

    @Column
    private Integer weight; // 权重，默认值10

    @Column(name = "admin_weight")
    private Integer adminWeight; // 管理员权重，默认值0

    /**
     * Transient字段，用于查询时不返回该字段
     */
    @Transient
    @JsonIgnore
    @OneToMany(mappedBy = "tag")
    @JsonManagedReference(value = "tag-postTag")
    private List<PostTag> postTags;

    @Override
    public int hashCode() {
        return tagId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass()!= obj.getClass()) {
            return false;
        }
        Tag other = (Tag) obj;
        return tagId.equals(other.tagId);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tagId=" + tagId +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                '}';
    }
}
