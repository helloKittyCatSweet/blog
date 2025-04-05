package com.kitty.blog.dto.category;

import com.kitty.blog.model.category.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoryTreeBuilder {

    public static List<TreeDto> buildTree(List<Category> categories, int rootParentId) {
        Map<Integer, TreeDto> categoryTreeMap = new HashMap<>();
        List<TreeDto> rootCategories = new ArrayList<>();

        // 初始化所有 Category 的 TreeDto
        for (Category category : categories) {
            TreeDto treeDto = new TreeDto();
            treeDto.setCategory(category);
            categoryTreeMap.put(category.getCategoryId(), treeDto);
        }

        // 构建树状结构
        for (Category category : categories) {
            TreeDto treeDto = categoryTreeMap.get(category.getCategoryId());
            if (category.getParentCategoryId() == rootParentId) {
                rootCategories.add(treeDto);
            } else {
                TreeDto parentTreeDto = categoryTreeMap.get(category.getParentCategoryId());
                if (parentTreeDto != null) {
                    parentTreeDto.getChildren().add(treeDto);
                }
            }
        }

        return rootCategories;
    }
}
