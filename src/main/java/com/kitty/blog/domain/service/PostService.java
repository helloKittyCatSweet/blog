package com.kitty.blog.application.service;

import com.kitty.blog.constant.Visibility;
import com.kitty.blog.dto.common.FileDto;
import com.kitty.blog.model.*;
import com.kitty.blog.model.category.Category;
import com.kitty.blog.model.tag.Tag;
import com.kitty.blog.constant.ActivityType;
import com.kitty.blog.model.UserActivity;
import com.kitty.blog.repository.*;
import com.kitty.blog.repository.CategoryRepository;
import com.kitty.blog.repository.tag.TagRepository;
import com.kitty.blog.application.service.contentReview.BaiduContentService;
import com.kitty.blog.application.service.tag.TagWeightService;
import com.kitty.blog.utils.AliyunOSSUploader;
import com.kitty.blog.utils.UpdateUtil;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "post")
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostVersionRepository postVersionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserActivityService userActivityService;

    @Autowired
    private AliyunOSSUploader aliyunOSSUploader;

    @Autowired
    private PostAttachmentRepository postAttachmentRepository;

    @Autowired
    private BaiduContentService baiduContentService;

    @Autowired
    private TagWeightService tagWeightService;

    @Transactional
    public ResponseEntity<Boolean> create(Post post) {
        // 验证用户是否存在
        if (!userRepository.existsById(post.getUserId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        String s = baiduContentService.checkText(post.getContent());
        System.out.println(s);
        if (!s.equals("合规")){
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        // 初始化一些默认值
        post.setLikes(0);
        post.setFavorites(0);
        post.setIsDraft(false);

        // 保存 Post
        Post savedPost = (Post) postRepository.save(post);
        postVersionRepository.save(
                new PostVersion(savedPost.getPostId(), savedPost.getContent(), savedPost.getUserId()));
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> update(Post post) {
        // 检查 Post 是否存在
        if (!postRepository.existsById(post.getPostId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        String s = baiduContentService.checkText(post.getContent());
        System.out.println(s);
        if (!s.equals("合规")){
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        // 获取已有的 Post 信息
        Post existingPost = (Post) postRepository.findById(post.getPostId()).orElse(null);
        if (existingPost == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        // 按需更新字段
        try {
            UpdateUtil.updateNotNullProperties(post, existingPost);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        postRepository.save(existingPost);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> uploadAttachment(FileDto fileDto) {
        if (!postRepository.existsById(fileDto.getSomeId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        fileDto.setIdType("postId");
        String attachment = aliyunOSSUploader.uploadByFileType(fileDto.getFile(), fileDto.getSomeId(),
                fileDto.getIdType());
        // 保存关联信息到数据库
        PostAttachment postAttachment = new PostAttachment();
        postAttachment.setPostId(fileDto.getSomeId());
        postAttachment.setUrl(attachment);
        postAttachment.setAttachmentName(fileDto.getFile().getName());

        Tika tika = new Tika();
        try {
            postAttachment.setAttachmentType(tika.detect(fileDto.getFile()));
            postAttachmentRepository.save(postAttachment);

            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<Boolean> addCategory(Integer postId, Integer categoryId) {
        // 前端逻辑：如果Category不存在，跳转到创建Category页面
        if (!categoryRepository.existsById(categoryId) || !postRepository.existsById(postId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        } else {
            postRepository.addCategory(postId, categoryId);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
    }

    @Transactional
    public ResponseEntity<Boolean> addTag(Integer postId, Integer tagId) {
        // 前端逻辑：如果Tag不存在，跳转到创建Tag页面
        if (!tagRepository.existsById(tagId) || !postRepository.existsById(postId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        postRepository.addTag(postId, tagId);
        // 增加使用数
        Tag tag = (Tag) tagRepository.findById(tagId).orElse(new Tag());
        tagWeightService.incrementUseCount(tag);
        tagWeightService.updateWeight(tag);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> addVersion(Integer postId, String content, Integer userId) {
        if (!postRepository.existsById(postId)
                || !userRepository.existsById(userId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        String s = baiduContentService.checkText(content);
        if (!s.equals("合规")){
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }

        PostVersion postVersion = (PostVersion) postVersionRepository.save(new PostVersion(postId, content, userId));
        if (postVersion.equals(new PostVersion()))
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> setVisibility(Integer postId, String visibility) {
        if (!postRepository.existsById(postId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        Post post = (Post) postRepository.findById(postId).orElse(new Post());
        try{
            Visibility.valueOf(visibility);
            post.setVisibility(visibility);
            postRepository.save(post);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> deleteCategory(Integer postId, Integer categoryId) {
        if (!postRepository.existsById(postId) || !categoryRepository.existsById(categoryId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        postRepository.deleteCategory(postId, categoryId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> deleteTag(Integer postId, Integer tagId) {
        if (!postRepository.existsById(postId) || !tagRepository.existsById(tagId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        postRepository.deleteTag(postId, tagId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> deleteVersion(Integer postId, Integer versionId) {
        if (!postRepository.existsById(postId) || !postVersionRepository.existsById(versionId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        postVersionRepository.deleteById(versionId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#postId")
    public ResponseEntity<List<Post>> findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }else {
            Integer userId = user.get().getUserId();
            return new ResponseEntity<>(
                    postRepository.findByUserId(userId).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#postId")
    public ResponseEntity<List<Post>> findByTitleContaining(String keyword) {
        return new ResponseEntity<>(
                postRepository.findByTitleContaining(keyword).orElse(new ArrayList<>()),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#postId")
    public ResponseEntity<List<Post>> findByContentContaining(String keyword) {
        return new ResponseEntity<>(
                postRepository.findByContentContaining(keyword).orElse(new ArrayList<>()),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#postId")
    public ResponseEntity<List<Post>> findByUsernameIsPublished(Boolean isPublished, String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }else {
            Integer userId = user.get().getUserId();
            return new ResponseEntity<>(
                    postRepository.findByUserIdIsPublished(isPublished, userId)
                            .orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#postId")
    public ResponseEntity<List<Post>> findByCategory(String category) {
        Category targetCategory = categoryRepository.findByName(category).orElse(new Category());
        if (!categoryRepository.existsById(targetCategory.getCategoryId())) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(
                    postRepository.findByCategoryId(targetCategory.getCategoryId())
                            .orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#postId")
    public ResponseEntity<List<Post>> findByTag(String tag) {
        Tag targetTag = tagRepository.findByName(tag).orElse(new Tag());
        if (!tagRepository.existsById(targetTag.getTagId())) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(
                    postRepository.findByTagId(targetTag.getTagId()).orElse(new ArrayList<>()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#postId")
    public ResponseEntity<Integer> getLatestVersion(Integer postId) {
        if (!postRepository.existsById(postId)) {
            return new ResponseEntity<>(-1, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(
                postRepository.getLatestVersion(postId),
                HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> increaseLikes(Integer postId, Integer count) {
        if (!postRepository.existsById(postId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        Post post = (Post) postRepository.findById(postId).orElse(new Post());
        post.setLikes(post.getLikes() + count);
        postRepository.save(post);
        // 用户点赞
        if (count > 0) {
            userActivityService.create(new UserActivity(post.getUserId(), ActivityType.LIKE.name(),
                    post.getPostId(), count));
        } else {
            UserActivity userActivity = userActivityService.findExplicit(post.getUserId(), post.getPostId(),
                    ActivityType.LIKE.name()).getBody();
            if (!Objects.equals(userActivity, new UserActivity())) {
                assert userActivity != null;
                userActivityService.deleteById(userActivity.getActivityId(), userActivity.getUserId());
            } else {
                return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> increaseFavorites(Integer userId, Integer postId, Integer count) {
        if (!postRepository.existsById(postId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        Post post = (Post) postRepository.findById(postId).orElse(new Post());
        post.setFavorites(post.getFavorites() + count);
        postRepository.save(post);
        // 用户收藏
        if (count > 0) {
            favoriteService.save(new Favorite(userId, postId));
            userActivityService.create(new UserActivity(userId, ActivityType.FAVORITE.name(),
                    postId, count));
        } else {
            Favorite favorite = favoriteService.findByUserIdAndPostId(userId, postId).getBody();
            assert favorite != null;
            favoriteService.deleteById(favorite.getFavoriteId());
            UserActivity userActivity = userActivityService.findExplicit(userId, postId,
                    ActivityType.FAVORITE.name()).getBody();
            assert userActivity != null;
            userActivityService.deleteById(userActivity.getActivityId(), userActivity.getUserId());
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public boolean isAuthorOfOpenPost(Integer userId, Integer postId) {
        if (!postRepository.existsById(postId)){
            return false;
        }else {
            Post post = postRepository.findById(postId).orElse(new Post());
            return post.getUserId().equals(userId);
        }
    }

    @Transactional
    public boolean isAuthorOfOpenPost(String username, Integer postId) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return false;
        } else {
            Integer userId = user.get().getUserId();
            return isAuthorOfOpenPost(userId, postId);
        }
    }

    @Transactional
    public ResponseEntity<List<Post>> findByVisibility(String visibility, Integer userId){
        try{
            Visibility.valueOf(visibility);
            List<Post> posts = postRepository.
                    findByVisibility(visibility, userId).orElse(new ArrayList<>());
            return ResponseEntity.ok(posts);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Auto-generated method signature
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Post> save(Post post) {
        String s = baiduContentService.checkText(post.getContent());
        System.out.println(s);
        if (!s.equals("合规")){
            return new ResponseEntity<>(new Post(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>((Post) postRepository.save(post), HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Post> findById(Integer postId) {
        return new ResponseEntity<>(
                (Post) postRepository.findById(postId).orElse(new Post()),
                HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<List<Post>> findAll() {
        if (postRepository.count() == 0) {
            {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
            }
        }
        return new ResponseEntity<>(
                (List<Post>) postRepository.findAll(),
                HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> deleteById(Integer postId) {
        if (!existsById(postId).getBody()) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        postRepository.deleteById(postId);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(postRepository.count(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> existsById(Integer postId) {
        return new ResponseEntity<>(postRepository.existsById(postId), HttpStatus.OK);
    }
}
