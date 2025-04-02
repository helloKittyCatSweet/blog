package com.kitty.blog.service;

import com.kitty.blog.dto.category.CategoryTreeBuilder;
import com.kitty.blog.dto.category.TreeDto;
import com.kitty.blog.model.category.Category;
import com.kitty.blog.repository.CategoryRepository;
import com.kitty.blog.repository.PostRepository;
import com.kitty.blog.service.contentReview.BaiduContentService;
import com.kitty.blog.utils.UpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@CacheConfig(cacheNames = "category")
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BaiduContentService baiduContentService;

    @Autowired
    private PostRepository postRepository;

    @Transactional
    public ResponseEntity<Boolean> create(Category category) {
        // 已经存在
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            return new ResponseEntity<>(false, HttpStatus.CONFLICT);
        }
        String name = baiduContentService.checkText(category.getName());
        String description = baiduContentService.checkText(category.getDescription());
        if (!name.equals("合规") || !description.equals("合规")){
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
        save(category);
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }

    @Transactional
    @CacheEvict(key = "#category.categoryId")
    public ResponseEntity<Boolean> update(Category category) {
        // 验证目标分类是否存在
        if (Boolean.FALSE.equals(existsById(category.getCategoryId()).getBody())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        String name = baiduContentService.checkText(category.getName());
        String description = baiduContentService.checkText(category.getDescription());
        if (!name.equals("合规") || !description.equals("合规")){
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        Category targetCategory = findById(category.getCategoryId()).getBody();

        try {
            UpdateUtil.updateNotNullProperties(category, targetCategory);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        // 如果其他字段需要动态更新，也可以按此逻辑操作

        // 保存更新后的分类
        // System.out.println("update category: " + targetCategory);
        save(targetCategory);

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(key = "#categoryId")
    public ResponseEntity<Boolean> deleteWithSubCategories(Integer categoryId) {
        if (!existsById(categoryId).getBody()) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        categoryRepository.deleteWithSubCategories(categoryId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(key = "#categoryId")
    public ResponseEntity<Boolean> deleteLeafCategory(Integer categoryId) {
        if (!existsById(categoryId).getBody()) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        categoryRepository.deleteLeafCategory(categoryId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#name", unless = "#result.body == null")
    public ResponseEntity<Category> findByName(String name) {
        return new ResponseEntity<>(
                categoryRepository.findByName(name).orElse(new Category()),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#parentCategoryId", unless = "#result.body.isEmpty()")
    public ResponseEntity<List<TreeDto>> findByParentName(String parentName) {
        Category category = categoryRepository.findByName(parentName).orElse(new Category());
        if (category.getCategoryId() == null) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            List<Category> categories = categoryRepository.
                    findCategoriesByParentId(category.getCategoryId())
                    .orElse(new ArrayList<>());
            return new ResponseEntity<>(
                    CategoryTreeBuilder.buildTree(categories, category.getCategoryId()),
                    HttpStatus.OK);
        }
    }

    // 可以同时处理是否有父节点的情况
    @Transactional
    @Cacheable
    public ResponseEntity<List<TreeDto>> findDescendantsByParentName(String parentName) {
        Category category = categoryRepository.findByName(parentName).orElse(new Category());
        if (category.getCategoryId() == null) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            List<Category> descendants = new ArrayList<>();
            List<Integer> currentLevelIds = new ArrayList<>();
            currentLevelIds.add(category.getCategoryId());

            while (!currentLevelIds.isEmpty()) {
                List<Integer> nextLevelIds = new ArrayList<>();
                for (Integer parentId : currentLevelIds) {
                    List<Category> children = categoryRepository.findCategoriesByParentId(parentId)
                            .orElse(new ArrayList<>());
                    descendants.addAll(children);
                    for (Category child : children) {
                        nextLevelIds.add(child.getCategoryId());
                    }
                }
                currentLevelIds = nextLevelIds;
            }

            return new ResponseEntity<>(
                    CategoryTreeBuilder.buildTree(descendants, category.getCategoryId()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<Integer> findByPostId(Integer postId) {
        if (!postRepository.existsById(postId)){
            return new ResponseEntity<>(0, HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(categoryRepository.findByPostId(postId).orElse(0),
                    HttpStatus.OK);
        }
    }

    /**
     * Auto-generated methods
     */
    @CacheEvict(key = "#category.categoryId")
    @Transactional
    // 主键不存在则插入，主键存在则更新
    public ResponseEntity<Category> save(Category category) {
        Category oldCategory = this.findByName(category.getName()).getBody();
        // 这个分类已经存在,新增一个已经存在的Category
        if (oldCategory != null && !oldCategory.equals(new Category())
                && !Objects.equals(oldCategory.getCategoryId(),
                        category.getCategoryId())) {
            return new ResponseEntity<>(new Category(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>((Category) categoryRepository.save(category), HttpStatus.OK);
    }

    @Cacheable(key = "#categoryId", unless = "#result.body == null")
    @Transactional
    public ResponseEntity<Category> findById(Integer categoryId) {
        if (!existsById(categoryId).getBody()) {
            return new ResponseEntity<>(new Category(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>((Category) categoryRepository.findById(categoryId).orElse(new Category()),
                HttpStatus.OK);
    }

    @Cacheable(key = "'all'", unless = "#result.body.isEmpty()")
    @Transactional
    public ResponseEntity<List<TreeDto>> findAll() {
        List<Category> allCategories = categoryRepository.findAll();
        if (allCategories.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }

        // 找出所有根节点（parentCategoryId = 0 的节点）
        List<Category> rootCategories = allCategories.stream()
                .filter(category -> category.getParentCategoryId() == 0)
                .toList();

        // 直接构建完整的树
        return new ResponseEntity<>(
                CategoryTreeBuilder.buildTree(allCategories, 0),
                HttpStatus.OK
        );
    }

    @CacheEvict(key = "#categoryId")
    @Transactional
    public ResponseEntity<Boolean> deleteById(Integer categoryId) {
        if (!existsById(categoryId).getBody()) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        categoryRepository.deleteById(categoryId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(categoryRepository.count(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> existsById(Integer categoryId) {
        return new ResponseEntity<>(categoryRepository.existsById(categoryId), HttpStatus.OK);
    }
}
