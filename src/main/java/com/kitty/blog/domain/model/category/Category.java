package com.kitty.blog.domain.model.category;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.ToString;
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
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "categoryId")
@DynamicUpdate // 动态更新注解
@DynamicInsert // 动态插入注解
public class Category implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer categoryId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Column(name = "parent_category_id")
    private Integer parentCategoryId;

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
