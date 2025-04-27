package com.kitty.blog.infrastructure.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分页工具类
 * 提供通用的分页和排序功能
 */
public class PageUtil {

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE = 0;

    /**
     * 默认每页大小
     */
    public static final int DEFAULT_SIZE = 10;

    /**
     * 最大每页大小
     */
    public static final int MAX_SIZE = 100;

    /**
     * 默认排序字段
     */
    public static final String DEFAULT_SORT_FIELD = "createdAt";

    /**
     * 默认排序方向
     */
    public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.DESC;

    /**
     * 创建分页请求
     * 
     * @param page 页码(从0开始)
     * @param size 每页大小
     * @param sort 排序参数数组，格式：["field,direction", ...]
     * @return PageRequest对象
     */
    public static PageRequest createPageRequest(Integer page, Integer size, String[] sort) {
        // 校验并设置默认值
        page = validatePage(page);
        size = validateSize(size);

        // 创建排序
        Sort sorting = createSort(sort);

        return PageRequest.of(page, size, sorting);
    }

    /**
     * 创建分页请求（使用默认排序）
     * 
     * @param page 页码(从0开始)
     * @param size 每页大小
     * @return PageRequest对象
     */
    public static PageRequest createPageRequest(Integer page, Integer size) {
        return createPageRequest(page, size, null);
    }

    /**
     * 创建排序对象
     * 
     * @param sort 排序参数数组，格式：["field,direction", ...]
     * @return Sort对象
     */
    public static Sort createSort(String[] sort) {
        if (sort == null || sort.length == 0) {
            return Sort.by(DEFAULT_SORT_DIRECTION, DEFAULT_SORT_FIELD);
        }

        // 处理单个元素的情况 ["field,direction"]
        if (sort.length == 1 && sort[0].contains(",")) {
            String[] parts = sort[0].split(",");
            if (parts.length == 2) {
                String field = parts[0];
                String direction = parts[1];
                return direction.equalsIgnoreCase("asc")
                        ? Sort.by(Sort.Direction.ASC, field)
                        : Sort.by(Sort.Direction.DESC, field);
            }
        }

        // 处理两个元素的情况 ["field", "direction"]
        if (sort.length == 2) {
            String field = sort[0];
            String direction = sort[1];
            if (StringUtils.hasText(field) && StringUtils.hasText(direction)) {
                return direction.equalsIgnoreCase("asc")
                        ? Sort.by(Sort.Direction.ASC, field)
                        : Sort.by(Sort.Direction.DESC, field);
            }
        }

        // 如果格式不正确，使用默认排序
        return Sort.by(DEFAULT_SORT_DIRECTION, DEFAULT_SORT_FIELD);
    }

    /**
     * 校验页码
     * 
     * @param page 页码
     * @return 有效的页码
     */
    private static int validatePage(Integer page) {
        return page == null || page < 0 ? DEFAULT_PAGE : page;
    }

    /**
     * 校验每页大小
     * 
     * @param size 每页大小
     * @return 有效的每页大小
     */
    private static int validateSize(Integer size) {
        if (size == null || size <= 0) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    /**
     * 构建排序字符串
     * 
     * @param field     字段名
     * @param direction 排序方向
     * @return 排序字符串，格式：field,direction
     */
    public static String buildSortString(String field, String direction) {
        if (!StringUtils.hasText(field)) {
            field = DEFAULT_SORT_FIELD;
        }
        if (!StringUtils.hasText(direction)
                || (!direction.equalsIgnoreCase("asc") && !direction.equalsIgnoreCase("desc"))) {
            direction = DEFAULT_SORT_DIRECTION.name();
        }
        return field + "," + direction.toLowerCase();
    }
}