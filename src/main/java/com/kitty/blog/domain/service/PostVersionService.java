package com.kitty.blog.application.service;

import com.kitty.blog.model.PostVersion;
import com.kitty.blog.repository.PostRepository;
import com.kitty.blog.repository.PostVersionRepository;
import com.kitty.blog.repository.UserRepository;
import com.kitty.blog.utils.UpdateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@CacheConfig(cacheNames = "postVersion")
public class PostVersionService {

    @Autowired
    private PostVersionRepository postVersionRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;


    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> update(Integer versionId, PostVersion updatedPostVersion) {
        if (!postVersionRepository.existsById(versionId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        PostVersion existingPostVersion = (PostVersion) postVersionRepository.
                findById(versionId).orElseThrow();
        try {
            UpdateUtil.updateNotNullProperties(updatedPostVersion, existingPostVersion);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        postVersionRepository.save(existingPostVersion);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }


    @Transactional
    @Cacheable(key = "#postId")
    public ResponseEntity<List<PostVersion>> findByPostId(Integer postId) {
        if (!postRepository.existsById(postId)){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(
                    postVersionRepository.findByPostId(postId).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }


    /**
     * Auto-generated methods
     */

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<PostVersion> save(PostVersion postVersion) {
        int version = 1;
        if (postRepository.existsById(postVersion.getPostId())) {
            version = postRepository.getLatestVersion(postVersion.getPostId()) + 1;
        }
        postVersion.setVersion(version);

        return new ResponseEntity<>(
                (PostVersion) postVersionRepository.save(postVersion),
                HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<PostVersion> findById(Integer versionId) {
        return new ResponseEntity<>(
                (PostVersion) postVersionRepository.findById(versionId).orElse(new PostVersion()),
                HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<List<PostVersion>> findAll() {
        if (postVersionRepository.count() == 0){
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(
                (List<PostVersion>) postVersionRepository.findAll(),
                HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> deleteById(Integer versionId) {
        if (!postVersionRepository.existsById(versionId)){
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        postVersionRepository.deleteById(versionId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Long> count(){
        return new ResponseEntity<>(postVersionRepository.count(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> existsById(Integer versionId) {
        return new ResponseEntity<>(postVersionRepository.existsById(versionId), HttpStatus.OK);
    }
}
