package com.kitty.blog.domain.service;

import com.kitty.blog.application.dto.category.CategoryTreeBuilder;
import com.kitty.blog.application.dto.category.TreeDto;
import com.kitty.blog.domain.model.category.Category;
import com.kitty.blog.domain.repository.CategoryRepository;
import com.kitty.blog.domain.repository.post.PostRepository;
import com.kitty.blog.domain.service.contentReview.BaiduContentService;
import com.kitty.blog.infrastructure.utils.PageUtil;
import com.kitty.blog.infrastructure.utils.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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
        try {
            // {"error_code":18,"error_msg":"Open api qps request limit reached"}
            // 百度api限制每秒只能发送一个请求，这里休眠1秒
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String description = baiduContentService.checkText(category.getDescription());
        log.info("create a category: check: name: {}, description: {}", name, description);
        if (!name.equals("合规") || !description.equals("合规")) {
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
        try {
            // api调用频率限制，一秒一次
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String description = baiduContentService.checkText(category.getDescription());
        if (!name.equals("合规") || !description.equals("合规")) {
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
    @Cacheable(key = "#name", unless = "#result.body.isEmpty()")
    public ResponseEntity<List<Category>> findByNameLike(String name) {
        return new ResponseEntity<>(
                categoryRepository.findByNameLike(name).orElse(new ArrayList<>()),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#parentName", unless = "#result.body.isEmpty()")
    public ResponseEntity<List<TreeDto>> findByParentNameLike(String parentName) {
        List<Category> parentCategories = categoryRepository.findByNameLike(parentName).orElse(new ArrayList<>());
        if (parentCategories.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }

        List<TreeDto> allTrees = new ArrayList<>();
        // 为每个匹配的父分类构建一棵树
        for (Category parent : parentCategories) {
            List<Category> children = categoryRepository.findCategoriesByParentId(parent.getCategoryId())
                    .orElse(new ArrayList<>());
            if (!children.isEmpty()) {
                allTrees.addAll(CategoryTreeBuilder.buildTree(children, parent.getCategoryId()));
            }
        }
        if (!allTrees.isEmpty()) {
            return new ResponseEntity<>(allTrees, HttpStatus.OK);
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
    }

    @Transactional
    @Cacheable(key = "#parentName", unless = "#result.body.isEmpty()")
    public ResponseEntity<Page<TreeDto>> findDescendantsByParentNameLike
            (String parentName, Integer page, Integer size, String[] sorts) {
        List<Category> parentCategories = categoryRepository.findByNameLike(parentName).orElse(new ArrayList<>());
        if (parentCategories.isEmpty()) {
            return new ResponseEntity<>(
                    new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 10), 0),
                    HttpStatus.NOT_FOUND);
        }

        List<TreeDto> allTrees = new ArrayList<>();
        for (Category parent : parentCategories) {
            List<Category> descendants = new ArrayList<>();
            List<Integer> currentLevelIds = new ArrayList<>();
            currentLevelIds.add(parent.getCategoryId());

            while (!currentLevelIds.isEmpty()) {
                List<Integer> nextLevelIds = new ArrayList<>();
                for (Integer parentId : currentLevelIds) {
                    List<Category> children = categoryRepository.findCategoriesByParentId(parentId)
                            .orElse(new ArrayList<>());
                    descendants.addAll(children);
                    children.forEach(child -> nextLevelIds.add(child.getCategoryId()));
                }
                currentLevelIds = nextLevelIds;
            }

            if (!descendants.isEmpty()) {
                allTrees.addAll(CategoryTreeBuilder.buildTree(descendants, parent.getCategoryId()));
            }
        }

        // 创建分页对象
        PageRequest pageRequest = PageUtil.createPageRequest(page, size, sorts);
        return new ResponseEntity<>(
                new PageImpl<>(allTrees, pageRequest, allTrees.size()),
                allTrees.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Category> findByPostId(Integer postId) {
        if (!postRepository.existsById(postId)) {
            return new ResponseEntity<>(new Category(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(categoryRepository.findByPostId(postId).orElse(new Category()),
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
        return new ResponseEntity<>((Category) categoryRepository.save(category), HttpStatus.OK);
    }

    @Cacheable(key = "#categoryId", unless = "#result.body == null")
    @Transactional
    public ResponseEntity<Category> findById(Integer categoryId) {
        if (Boolean.FALSE.equals(existsById(categoryId).getBody())) {
            return new ResponseEntity<>(new Category(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>((Category) categoryRepository.findById(categoryId).orElse(new Category()),
                HttpStatus.OK);
    }

    @Cacheable(key = "'all'", unless = "#result.body.isEmpty()")
    @Transactional
    public ResponseEntity<Page<TreeDto>> findAll(Integer page, Integer size, String[] sorts) {
        PageRequest pageRequest = PageUtil.createPageRequest(page, size, sorts);
        Page<Category> allCategories = categoryRepository.findAll(pageRequest);

        // 更新所有分类的使用次数
        allCategories.getContent().forEach(category -> {
            Integer count = categoryRepository.countPostsByCategoryId(category.getCategoryId());
            category.setUseCount(count);
            categoryRepository.updateUseCount(category.getCategoryId(), count);
        });

        if (allCategories.isEmpty()) {
            return new ResponseEntity<>(
                    new PageImpl<>(new ArrayList<>(), pageRequest, 0),
                    HttpStatus.OK);
        }

        // 找出所有根节点（parentCategoryId = 0 的节点）
        List<Category> rootCategories = allCategories.getContent().stream()
                .filter(category -> category.getParentCategoryId() == 0)
                .toList();

        // 构建树结构
        List<TreeDto> trees = CategoryTreeBuilder.buildTree(allCategories.getContent(), 0);

        // 创建分页的树结构结果
        return new ResponseEntity<>(
                new PageImpl<>(trees, pageRequest, allCategories.getTotalElements()),
                HttpStatus.OK);
    }

    @Cacheable(key = "'all'", unless = "#result.body.isEmpty()")
    @Transactional
    public ResponseEntity<List<TreeDto>> findAll() {
        List<Category> allCategories = categoryRepository.findAll();
        // 构建树结构
        List<TreeDto> trees = CategoryTreeBuilder.buildTree(allCategories, 0);

        return new ResponseEntity<>(trees, HttpStatus.OK);
    }

    @CacheEvict(key = "#categoryId")
    @Transactional
    public ResponseEntity<Boolean> deleteById(Integer categoryId) {
        if (Boolean.FALSE.equals(existsById(categoryId).getBody())) {
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
