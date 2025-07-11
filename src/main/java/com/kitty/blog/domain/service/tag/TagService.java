package com.kitty.blog.domain.service.tag;

import com.kitty.blog.common.constant.Compare;
import com.kitty.blog.domain.model.tag.Tag;
import com.kitty.blog.domain.repository.tag.TagRepository;
import com.kitty.blog.domain.repository.tag.TagSpecification;
//import com.kitty.blog.domain.service.contentReview.BaiduContentService;
import com.kitty.blog.infrastructure.utils.PageUtil;
import com.kitty.blog.infrastructure.utils.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "tag")
@Slf4j
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagWeightService tagWeightService;

//    @Autowired
//    private BaiduContentService baiduContentService;

    @Transactional
    public ResponseEntity<Boolean> create(Tag tag) {
        if (tagRepository.findByName(tag.getName()).isPresent()) {
            log.info("Tag already exists: " + tag.getName());
            return new ResponseEntity<>(false, HttpStatus.CONFLICT);
        }

//        String s = baiduContentService.checkText(tag.getName());
//        if (!s.equals("合规")) {
//            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
//        }

        // 设置初始值
        tag.setUseCount(0);
        tag.setClickCount(0);
        tag.setLastUsedAt(LocalDateTime.now());

        // 如果管理员没有指定权重，则使用默认权重
        if (tag.getAdminWeight() == null) {
            tag.setAdminWeight(10);
        } else {
            // 如果指定了权重，将其作为管理员权重
            tag.setAdminWeight(tag.getAdminWeight());
            tag.setWeight(tagWeightService.calculateWeight(tag));
        }

        tagRepository.save(tag);
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }

    @Transactional
    @CacheEvict(key = "#tag.tagId")
    public ResponseEntity<Boolean> update(Tag tag) {
        if (!tagRepository.existsById(tag.getTagId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

//        String s = baiduContentService.checkText(tag.getName());
//        if (!s.equals("合规")) {
//            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
//        }

        Tag existingTag = (Tag) tagRepository.findById(tag.getTagId()).orElseThrow();
        try {
            UpdateUtil.updateNotNullProperties(tag, existingTag);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        if (tag.getName() != null) {
            existingTag.setName(existingTag.getName());
        }
        tagRepository.save(existingTag);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#tag.tagId", unless = "#result.body == null")
    public ResponseEntity<Tag> findByName(String name) {
        return new ResponseEntity<>(
                tagRepository.findByName(name).orElse(new Tag()),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#tag.tagId", unless = "#result.body == null")
    public Page<Tag> findTagsByWeight(Integer weight, Compare compare, Integer page, Integer size, String[] sort) {
        Specification<Tag> spec = TagSpecification.weightCompareTo(weight, compare);
        PageRequest pageRequest = PageUtil.createPageRequest(page, size, sort);
        return tagRepository.findAll(spec, pageRequest);
    }

    /**
     * Auto-generated methods
     */

    @Cacheable(key = "#tagId", unless = "#result.body == null")
    @Transactional
    public ResponseEntity<Tag> findById(Integer tagId) {
        return new ResponseEntity<>(
                (Tag) tagRepository.findById(tagId).orElse(new Tag()),
                HttpStatus.OK);
    }

    @Cacheable(key = "'all'", unless = "#result.body.isEmpty()")
    @Transactional
    public Page<Tag> findAll(Integer page, Integer size, String[] sort) {
        if (tagRepository.count() == 0) {
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 10), 0);
        }
        PageRequest pageRequest = PageUtil.createPageRequest(page, size, sort);
        log.info("pageRequest: " + pageRequest);
        return tagRepository.findAll(pageRequest);
    }

    @CacheEvict(key = "#tag.tagId")
    @Transactional
    public ResponseEntity<Tag> save(Tag tag) {
        Tag oldTag = findByName(tag.getName()).getBody();
        // 这个分类已经存在,新增一个已经存在的Tag
        if (oldTag != null && !oldTag.equals(new Tag())
                && !Objects.equals(oldTag.getTagId(), tag.getTagId())) {
            return new ResponseEntity<>(new Tag(), HttpStatus.CONFLICT);
        }
        return new ResponseEntity<>((Tag) tagRepository.save(tag), HttpStatus.OK);
    }

    @CacheEvict(key = "#tagId")
    @Transactional
    public ResponseEntity<Boolean> deleteById(Integer tagId) {
        if (Boolean.FALSE.equals(existsById(tagId).getBody())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        tagRepository.deleteById(tagId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(tagRepository.count(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> existsById(Integer tagId) {
        return new ResponseEntity<>(tagRepository.existsById(tagId), HttpStatus.OK);
    }

    @Transactional
    public Page<Tag> findByCombined
            (String name, Integer weight, String operator, Integer page, Integer size, String[] sort) {
        try {
            Specification<Tag> spec = TagSpecification.combinedSearch(name, weight, operator);
            PageRequest pageRequest = PageUtil.createPageRequest(page, size, sort);
            return tagRepository.findAll(spec, pageRequest);
        } catch (Exception e) {
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(0, 10), 0);
        }
    }

    @Transactional
    public ResponseEntity<List<Tag>> findHotTags(int limit) {
        List<Tag> allTags = tagRepository.findAll();
        if (allTags.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(
                allTags.stream()
                        .sorted((t1, t2) -> Integer.compare(t2.getWeight(), t1.getWeight())) // 修改排序顺序
                        .limit(limit)
                        .collect(Collectors.toList())
        );
    }

    @Transactional
    public void handleTagUsage(Tag tag, String action) {
        switch (action.toLowerCase()) {
            case "click":
                tagWeightService.incrementClickCount(tag);
                tagWeightService.updateWeight(tag);
            case "use":
                tagWeightService.incrementUseCount(tag);
                tagWeightService.updateWeight(tag);
            default:
                throw new IllegalArgumentException("Invalid action: " + action);

        }
    }
}
