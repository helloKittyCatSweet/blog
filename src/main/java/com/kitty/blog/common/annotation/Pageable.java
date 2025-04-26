package com.kitty.blog.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 分页注解，用于标记需要分页的接口
 * 使用方式：在Controller方法上添加@Pageable注解
 * 参数会自动被转换为PageRequest对象
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Pageable {
    /**
     * 默认页码
     */
    int defaultPage() default 1;

    /**
     * 默认每页大小
     */
    int defaultSize() default 10;

    /**
     * 最大每页大小
     */
    int maxSize() default 100;
}