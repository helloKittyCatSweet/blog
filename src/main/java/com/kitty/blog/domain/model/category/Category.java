package com.kitty.blog.domain.model.category;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "fs_categories")// 避免循环调用问题
@Comment("分类表")
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "categoryId")
@DynamicUpdate // 动态更新注解
@DynamicInsert // 动态插入注解
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("分类ID")
    private Integer categoryId;

    @Column(nullable = false, unique = true)
    @Comment("分类名称")
    private String name;

    @Column
    @Comment("分类描述")
    private String description;

    @Column(name = "parent_category_id")
    @Comment("父分类ID")
    private Integer parentCategoryId;

    @Column(name = "use_count")
    @Comment("使用次数")
    private Integer useCount = 0; // 添加使用次数字段

    /**
     * Transient 注解的作用是：该字段不会被映射到数据库表的字段中，而是仅仅作为一个临时变量存在。
     */
    // 关联对象，按需加载
//    @JsonIgnore
//    @Transient
//    private Category parentCategory;
//
//    @Transient
//    @JsonIgnore
//    private List<Category> subCategories;

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", parentCategoryId=" + parentCategoryId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass()!= o.getClass()) return false;
        Category category = (Category) o;
        return categoryId.equals(category.categoryId);
    }

    @Override
    public int hashCode() {
        return categoryId.hashCode();
    }
}
