package com.kitty.blog.service;

import com.kitty.blog.model.tag.Compare;
import com.kitty.blog.model.tag.Tag;
import com.kitty.blog.repository.tag.TagSpecification;
import com.kitty.blog.repository.tag.TagRepository;
import com.kitty.blog.service.contentReview.BaiduContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Objects;

@Service
@CacheConfig(cacheNames = "tag")
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private BaiduContentService baiduContentService;

    @Transactional
    public ResponseEntity<Boolean> create(Tag tag) {
        if (tagRepository.findByName(tag.getName()).isPresent()) {
            return new ResponseEntity<>(false, HttpStatus.CONFLICT);
        }

        String s = baiduContentService.checkText(tag.getName());
        if (!s.equals("合规")) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
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

        String s = baiduContentService.checkText(tag.getName());
        if (!s.equals("合规")) {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        Tag existingTag = (Tag) tagRepository.findById(tag.getTagId()).orElseThrow();
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
    public ResponseEntity<List<Tag>> findTagsByWeight(Integer weight, Compare compare) {
        Specification<Tag> spec = TagSpecification.weightCompareTo(weight, compare);
        List<Tag> tags = tagRepository.findAll(spec);
        return new ResponseEntity<>(tags, HttpStatus.OK);
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
    public ResponseEntity<List<Tag>> findAll() {
        if (tagRepository.count() == 0) {
            return new ResponseEntity<>(List.of(new Tag()), HttpStatus.OK);
        }
        return new ResponseEntity<>(tagRepository.findAll(), HttpStatus.OK);
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
        if (!existsById(tagId).getBody()) {
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

    // @Cacheable(key = "'hot_tags'", unless = "#result.body.isEmpty()")
    // public ResponseEntity<List<Tag>> findHotTags() {
    // // Implementation of findHotTags method
    // return null; // Placeholder return, actual implementation needed
    // }

}
