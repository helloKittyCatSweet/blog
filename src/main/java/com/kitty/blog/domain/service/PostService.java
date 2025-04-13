package com.kitty.blog.domain.service;

import com.kitty.blog.application.dto.post.PostAttachmentDto;
import com.kitty.blog.application.dto.post.PostDto;
import com.kitty.blog.common.constant.Visibility;
import com.kitty.blog.application.dto.common.FileDto;
import com.kitty.blog.common.exception.ResourceNotFoundException;
import com.kitty.blog.domain.model.*;
import com.kitty.blog.domain.model.category.Category;
import com.kitty.blog.domain.model.tag.Tag;
import com.kitty.blog.common.constant.ActivityType;
import com.kitty.blog.domain.model.UserActivity;
import com.kitty.blog.domain.repository.*;
import com.kitty.blog.domain.repository.CategoryRepository;
import com.kitty.blog.domain.repository.post.PostRepository;
import com.kitty.blog.domain.repository.post.PostSearchCriteria;
import com.kitty.blog.domain.repository.post.PostSpecification;
import com.kitty.blog.domain.repository.tag.TagRepository;
import com.kitty.blog.domain.service.contentReview.BaiduContentService;
import com.kitty.blog.domain.service.post.abstractContent.XunfeiService;
import com.kitty.blog.domain.service.tag.TagWeightService;
import com.kitty.blog.infrastructure.utils.AliyunOSSUploader;
import com.kitty.blog.infrastructure.utils.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private XunfeiService xunfeiService;

    @Transactional
    public ResponseEntity<PostDto> create(PostDto input) {
        Post post = input.getPost();
        Integer categoryId = input.getCategory().getCategoryId();
        List<Integer> tagIds = input.getTags().stream().map(Tag::getTagId).toList();

        // 验证用户是否存在
        if (!userRepository.existsById(post.getUserId())) {
            return new ResponseEntity<>(new PostDto(), HttpStatus.NOT_FOUND);
        }

        // 内容审核
        String s = baiduContentService.checkText(post.getContent());
        if (!s.equals("合规")) {
            return new ResponseEntity<>(new PostDto(), HttpStatus.BAD_REQUEST);
        }

        // 初始化默认值
        post.setLikes(0);
        post.setFavorites(0);
        post.setIsDraft(false);
        post.setVersion(1);
        log.info("create post: " + post.toString());

        // 保存文章
        Post savedPost = (Post) postRepository.save(post);

        // 保存版本记录
        postVersionRepository.save(
                new PostVersion(savedPost.getPostId(), savedPost.getContent(),
                        savedPost.getUserId(), savedPost.getVersion()));

        // 添加分类
        if (categoryId != null) {
            addCategory(savedPost.getPostId(), categoryId);
        }

        // 添加标签
        if (!tagIds.isEmpty()) {
            for (Integer tagId : tagIds) {
                addTag(savedPost.getPostId(), tagId);
            }
        }

        // 构建返回的 PostDto
        PostDto postDto = new PostDto();
        postDto.setPost(savedPost);
        postDto.setCategory(categoryRepository.findByPostId(savedPost.getPostId()).orElse(new Category()));
        postDto.setTags(tagRepository.findByPostId(savedPost.getPostId()).orElse(new ArrayList<>()));
        postDto.setComments(new ArrayList<>());
        User author = userRepository.findById(savedPost.getUserId()).orElse(null);
        if (author != null) {
            postDto.setAuthor(author.getUsername());
        }
        postDto.setAttachments(postAttachmentRepository.findByPostId(savedPost.getPostId()).
                orElse(new ArrayList<>()));

        return new ResponseEntity<>(postDto, HttpStatus.CREATED);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<PostDto> update(PostDto input) {
        Post post = input.getPost();
        Integer categoryId = input.getCategory().getCategoryId();
        List<Integer> tagIds = input.getTags().stream().map(Tag::getTagId).toList();

        // 检查文章是否存在
        if (!postRepository.existsById(post.getPostId())) {
            return new ResponseEntity<>(new PostDto(), HttpStatus.NOT_FOUND);
        }

        // 获取现有文章
        Post existingPost = (Post) postRepository.findById(post.getPostId()).orElse(null);
        if (existingPost == null) {
            return new ResponseEntity<>(new PostDto(), HttpStatus.NOT_FOUND);
        }

        // 如果内容有变化才进行审核
        if (post.getContent() != null && !post.getContent().equals(existingPost.getContent())){
            // 内容审核
            String s = baiduContentService.checkText(post.getContent());
            if (!s.equals("合规")) {
                return new ResponseEntity<>(new PostDto(), HttpStatus.BAD_REQUEST);
            }
        }

        // 更新文章字段
        try {
            UpdateUtil.updateNotNullProperties(post, existingPost);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // 保存更新后的文章
        Post updatedPost = (Post) postRepository.save(existingPost);

        // 更新分类和标签
        if (categoryId != null) {
            updateCategory(post.getPostId(), categoryId);
        }
        if (!tagIds.isEmpty()) {
            updateTags(post.getPostId(), tagIds);
        }

        // 同步版本管理
        PostVersion postVersion = postVersionRepository.findByPostIdAndVersion
                (updatedPost.getPostId(), updatedPost.getVersion()).orElse(new PostVersion());
        postVersion.setContent(updatedPost.getContent());
        postVersionRepository.save(postVersion);

        // 构建返回的 PostDto
        PostDto postDto = new PostDto();
        postDto.setPost(updatedPost);
        postDto.setCategory(categoryRepository.findByPostId(updatedPost.getPostId()).orElse(new Category()));
        postDto.setTags(tagRepository.findByPostId(updatedPost.getPostId()).orElse(new ArrayList<>()));
        postDto.setComments(commentRepository.findByPostId(updatedPost.getPostId()).orElse(new ArrayList<>()));
        User author = userRepository.findById(updatedPost.getUserId()).orElse(null);
        if (author != null) {
            postDto.setAuthor(author.getUsername());
        }
        postDto.setAttachments(postAttachmentRepository.findByPostId(updatedPost.getPostId()).
                orElse(new ArrayList<>()));

        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> uploadAttachment(FileDto fileDto) {
        if (!postRepository.existsById(fileDto.getSomeId())) {
            return new ResponseEntity<>("false", HttpStatus.NOT_FOUND);
        }
        fileDto.setIdType("postId");
        String attachment = aliyunOSSUploader.uploadByFileType(fileDto.getFile(), fileDto.getSomeId(),
                fileDto.getIdType());
        // 保存关联信息到数据库
        PostAttachment postAttachment = new PostAttachment();
        postAttachment.setPostId(fileDto.getSomeId());
        postAttachment.setUrl(attachment);
        postAttachment.setAttachmentName(fileDto.getFile().getName());
        postAttachment.setSize(fileDto.getFile().length());

        Tika tika = new Tika();
        try {
            postAttachment.setAttachmentType(tika.detect(fileDto.getFile()));
            postAttachmentRepository.save(postAttachment);

            return new ResponseEntity<>(attachment, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("false", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<String> uploadPostCover(FileDto fileDto) {
        if (!postRepository.existsById(fileDto.getSomeId())) {
            return new ResponseEntity<>("false", HttpStatus.NOT_FOUND);
        }
        fileDto.setIdType("postCover");
        String attachment = aliyunOSSUploader.uploadByFileType(fileDto.getFile(), fileDto.getSomeId(),
                fileDto.getIdType());
        // 保存关联信息到数据库
        PostAttachment postAttachment = new PostAttachment();
        postAttachment.setPostId(fileDto.getSomeId());
        postAttachment.setUrl(attachment);
        postAttachment.setAttachmentName(fileDto.getFile().getName());
        postAttachment.setSize(fileDto.getFile().length());
        postAttachmentRepository.save(postAttachment);

        Post post = (Post) postRepository.findById(fileDto.getSomeId()).orElse(new Post());
        post.setCoverImage(attachment);
        postRepository.save(post);
        return new ResponseEntity<>(attachment, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> deleteAttachment(Integer attachmentId) {
        PostAttachment attachment = postAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("附件不存在"));

        try {
            // 先删除OSS文件
            String objectName = aliyunOSSUploader.parseObjectName(attachment.getUrl());
            aliyunOSSUploader.deleteFile(objectName);

            // 再删除数据库记录
            postAttachmentRepository.deleteById(attachmentId);
            return new ResponseEntity<>("删除成功", HttpStatus.OK);
        } catch (Exception e) {
            log.error("附件删除失败 attachmentId: {}", attachmentId, e);
            throw new RuntimeException("附件删除失败，请稍后重试");
        }
    }

    // 添加附件权限验证方法
    public boolean isAttachmentOwner(Integer attachmentId, Integer userId) {
        PostAttachment attachment = postAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("附件不存在"));
        Post post = postRepository.findById(attachment.getPostId()).orElse(new Post());
        return post.getUserId().equals(userId);
    }

    @Transactional
    public ResponseEntity<Boolean> addCategory(Integer postId, Integer categoryId) {
        if (!categoryRepository.existsById(categoryId) || !postRepository.existsById(postId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        postRepository.addCategory(postId, categoryId);
        // 更新分类使用次数
        Category category = categoryRepository.findById(categoryId).orElse(new Category());
        category.setUseCount(category.getUseCount() + 1);
        categoryRepository.save(category);

        return new ResponseEntity<>(true, HttpStatus.OK);

    }

    @Transactional
    public ResponseEntity<Boolean> addTag(Integer postId, Integer tagId) {
        if (!tagRepository.existsById(tagId) || !postRepository.existsById(postId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        postRepository.addTag(postId, tagId);
        // 增加使用数
        Tag tag = (Tag) tagRepository.findById(tagId).orElse(new Tag());
        tagWeightService.incrementUseCount(tag);
        tagWeightService.updateWeight(tag);
        tag.setLastUsedAt(LocalDateTime.now());
        tagRepository.save(tag);

        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<PostVersion> addVersion(Integer postId, String content, Integer userId) {
        if (!postRepository.existsById(postId)
                || !userRepository.existsById(userId)) {
            return new ResponseEntity<>(new PostVersion(), HttpStatus.NOT_FOUND);
        }

        if (content != null && !content.trim().isEmpty()) {
            String s = baiduContentService.checkText(content);
            if (!s.equals("合规")) {
                return new ResponseEntity<>(new PostVersion(), HttpStatus.BAD_REQUEST);
            }
        }

        PostVersion postVersion = (PostVersion) postVersionRepository.
                save(new PostVersion(postId, content, userId, getLatestVersion(postId).getBody() + 1));
        if (postVersion.equals(new PostVersion()))
            return new ResponseEntity<>(new PostVersion(), HttpStatus.INTERNAL_SERVER_ERROR);

        return new ResponseEntity<>(postVersion, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> setVisibility(Integer postId, String visibility) {
        if (!postRepository.existsById(postId)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        Post post = (Post) postRepository.findById(postId).orElse(new Post());
        try {
            Visibility.valueOf(visibility);
            post.setVisibility(visibility);
            postRepository.save(post);
        } catch (IllegalArgumentException e) {
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
    @CacheEvict(allEntries = true)
    public ResponseEntity<PostDto> updateCategory(Integer postId, Integer categoryId) {
        // 检查文章是否存在
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("文章不存在"));

        // 获取当前分类ID
        Integer oldCategoryId = categoryRepository.findByPostId(postId)
                .orElse(new Category()).getCategoryId();

        // 如果分类没有变化，直接返回
        if (Objects.equals(categoryId, oldCategoryId)) {
            return findById(postId);
        }

        // 检查新分类是否存在
        if (categoryId != null && !categoryRepository.existsById(categoryId)) {
            log.warn("Attempted to update to non-existent category: {}", categoryId);
            return new ResponseEntity<>(new PostDto(), HttpStatus.BAD_REQUEST);
        }

        // 更新分类
        if (oldCategoryId != null) {
            deleteCategory(postId, oldCategoryId);
            // 减少旧分类的使用次数
            Category oldCategory = categoryRepository.findById(oldCategoryId).orElse(new Category());
            oldCategory.setUseCount(Math.max(0, oldCategory.getUseCount() - 1));
            categoryRepository.save(oldCategory);
        }
        if (categoryId != null) {
            addCategory(postId, categoryId);
            // 增加新分类的使用次数
            Category newCategory = categoryRepository.findById(categoryId).orElse(new Category());
            newCategory.setUseCount(newCategory.getUseCount() + 1);
            categoryRepository.save(newCategory);
        }

        return findById(postId);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<PostDto> updateTags(Integer postId, List<Integer> tagIds) {
        // 检查文章是否存在
        Post existingPost = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("文章不存在"));

        // 获取当前标签
        List<Tag> currentTags = tagRepository.findByPostId(postId)
                .orElse(new ArrayList<>());

        Set<Integer> currentTagIds = currentTags.stream()
                .map(Tag::getTagId)
                .collect(Collectors.toSet());
        Set<Integer> newTagIds = new HashSet<>(tagIds);

        // 删除需要移除的标签
        currentTags.stream()
                .filter(tag -> !newTagIds.contains(tag.getTagId()))
                .forEach(tag -> {
                    deleteTag(postId, tag.getTagId());
                    tagWeightService.decrementUseCount(tag);
                    tagWeightService.updateWeight(tag);
                });

        // 添加新标签
        tagIds.stream()
                .filter(tagId -> !currentTagIds.contains(tagId))
                .forEach(tagId -> {
                    if (tagRepository.existsById(tagId)) {
                        addTag(postId, tagId);
                        Tag tag = tagRepository.findById(tagId).orElse(new Tag());
                        tagWeightService.incrementUseCount(tag);
                        tagWeightService.updateWeight(tag);
                    }
                });

        return findById(postId);
    }

    @Transactional
    @Cacheable(key = "#postId")
    public ResponseEntity<List<PostDto>> findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (!user.isPresent()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        } else {
            Integer userId = user.get().getUserId();
            List<Post> posts = postRepository.findByUserId(userId).orElse(new ArrayList<>());
            List<PostDto> postDtos = posts.stream().map(post -> {
                PostDto postDto = new PostDto();
                postDto.setPost(post);
                postDto.setCategory(categoryRepository.findByPostId(post.getPostId()).orElse(new Category()));
                postDto.setTags(tagRepository.findByPostId(post.getPostId()).orElse(new ArrayList<>()));
                postDto.setComments(commentRepository.findByPostId(post.getPostId()).orElse(new ArrayList<>()));
                // 设置作者信息
                User author = userRepository.findById(post.getUserId()).orElse(null);
                if (author != null) {
                    postDto.setAuthor(author.getUsername());
                }
                postDto.setAttachments(postAttachmentRepository.findByPostId(post.getPostId()).
                        orElse(new ArrayList<>()));
                return postDto;
            }).collect(Collectors.toList());
            return new ResponseEntity<>(postDtos, HttpStatus.OK);
        }
    }

    @Transactional
    @Cacheable(key = "#postId")
    public ResponseEntity<List<PostDto>> findByTitleContaining(String keyword) {
        List<Post> posts = postRepository.findByTitleContaining(keyword).orElse(new ArrayList<>());
        List<PostDto> postDtos = posts.stream().map(post -> {
            PostDto postDto = new PostDto();
            postDto.setPost(post);
            postDto.setCategory(categoryRepository.findByPostId(post.getPostId()).orElse(new Category()));
            postDto.setTags(tagRepository.findByPostId(post.getPostId()).orElse(new ArrayList<>()));
            postDto.setComments(commentRepository.findByPostId(post.getPostId()).orElse(new ArrayList<>()));

            // 设置作者信息
            User author = userRepository.findById(post.getUserId()).orElse(null);
            if (author != null) {
                postDto.setAuthor(author.getUsername());
            }
            postDto.setAttachments(postAttachmentRepository.findByPostId(post.getPostId()).
                    orElse(new ArrayList<>()));

            return postDto;
        }).collect(Collectors.toList());

        return new ResponseEntity<>(postDtos, HttpStatus.OK);
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
        } else {
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
        if (!postRepository.existsById(postId)) {
            return false;
        } else {
            Post post = postRepository.findById(postId).orElse(new Post());
            return post.getUserId().equals(userId);
        }
    }

    @Transactional
    public ResponseEntity<List<Post>> findByVisibility(String visibility, Integer userId) {
        try {
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
        if (!s.equals("合规")) {
            return new ResponseEntity<>(new Post(), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>((Post) postRepository.save(post), HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<PostDto> findById(Integer postId) {
        if (!postRepository.existsById(postId)) {
            return new ResponseEntity<>(new PostDto(), HttpStatus.NOT_FOUND);
        }
        Post post = (Post) postRepository.findById(postId).orElse(new Post());
        PostDto postDto = new PostDto();
        postDto.setPost(post);
        postDto.setCategory(categoryRepository.findByPostId(post.getPostId()).orElse(new Category()));
        postDto.setTags(tagRepository.findByPostId(post.getPostId()).orElse(new ArrayList<>()));
        postDto.setComments(commentRepository.findByPostId(post.getPostId()).orElse(new ArrayList<>()));
        userRepository.findById(post.getUserId()).
                ifPresent(author -> postDto.setAuthor(author.getUsername()));
        postDto.setAttachments(postAttachmentRepository.findByPostId(post.getPostId()).
                orElse(new ArrayList<>()));
        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<List<PostDto>> findAll() {
        // 创建搜索条件
        PostSearchCriteria criteria = PostSearchCriteria.builder()
                .isPublished(true)
                .visibility("PUBLIC")
                .build();

        // 使用已有的搜索方法获取公开文章
        List<PostDto> postDtos = searchPostsByMultipleCriteria(criteria);

        if (postDtos.isEmpty()) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(postDtos, HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> deleteById(Integer postId) {
        if (!existsById(postId).getBody()) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        postRepository.findById(postId).orElse(new Post()).setDeleted(true);

//        postRepository.deleteById(postId);
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

    /**
     * 控制台数据首页数据渲染
     *
     * @return
     */

    @Transactional
    @Cacheable(key = "'dashboard_stats'")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            stats.put("totalPosts", postRepository.count());
            stats.put("totalViews", postRepository.getTotalViews());
            stats.put("totalComments", postRepository.getTotalComments());
            stats.put("totalLikes", postRepository.getTotalLikes());
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            log.error("获取仪表盘统计数据失败", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Map<String, Object> getMonthlyStats() {
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusMonths(5).withDayOfMonth(1); // 获取6个月前的第一天
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth()); // 当月最后一天

        List<Object[]> postCounts = postRepository.getMonthlyPostCount(startDate, endDate);
        List<Object[]> viewCounts = postRepository.getMonthlyViewCount(startDate, endDate);

        Map<Integer, Integer> postMap = new HashMap<>();
        Map<Integer, Integer> viewMap = new HashMap<>();

        // 初始化最近6个月的数据为0
        for (int i = 0; i < 6; i++) {
            int month = now.minusMonths(i).getMonthValue();
            postMap.put(month, 0);
            viewMap.put(month, 0);
        }

        // 填充实际数据
        for (Object[] row : postCounts) {
            int month = ((Number) row[0]).intValue();
            int count = ((Number) row[1]).intValue();
            postMap.put(month, count);
        }

        for (Object[] row : viewCounts) {
            int month = ((Number) row[0]).intValue();
            int views = row[1] == null ? 0 : ((Number) row[1]).intValue();
            viewMap.put(month, views);
        }

        List<String> months = postMap.keySet().stream()
                .sorted()
                .map(month -> month + "月")
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("months", months);
        result.put("posts", new ArrayList<>(postMap.values()));
        result.put("views", new ArrayList<>(viewMap.values()));

        return result;
    }

    @Transactional
    @Cacheable(key = "'interaction_stats'")
    public ResponseEntity<Map<String, Integer>> getInteractionStats() {
        Map<String, Integer> stats = new HashMap<>();
        try {
            stats.put("likes", postRepository.getTotalLikes());
            stats.put("comments", postRepository.getTotalComments());
            stats.put("favorites", postRepository.getTotalFavorites());
            return new ResponseEntity<>(stats, HttpStatus.OK);
        } catch (Exception e) {
            log.error("获取互动统计数据失败", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<List<PostDto>> getRecentPosts() {
        List<Post> posts = postRepository.findTop5ByOrderByCreatedAtDesc();
        List<PostDto> postDtos = posts.stream()
                .map(post -> {
                    PostDto dto = new PostDto();
                    dto.setPost(post);
                    // 设置分类、标签和评论列表
                    dto.setCategory(categoryRepository.findByPostId(post.getPostId()).orElse(new Category()));
                    dto.setTags(tagRepository.findByPostId(post.getPostId()).orElse(new ArrayList<>()));
                    dto.setComments(commentRepository.findByPostId(post.getPostId()).orElse(new ArrayList<>()));

                    // 设置作者信息
                    User author = userRepository.findById(post.getUserId()).orElse(null);
                    if (author != null) {
                        dto.setAuthor(author.getUsername());
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(postDtos, HttpStatus.OK);
    }

    @Transactional
    public List<Post> searchPosts(PostSearchCriteria criteria) {
        Specification<Post> spec = PostSpecification.createSpecification(criteria);
        return postRepository.findAll(spec);
    }

    @Transactional
    public List<PostDto> searchPostsByMultipleCriteria(PostSearchCriteria searchCriteria) {
        // 构建搜索条件
        PostSearchCriteria.PostSearchCriteriaBuilder builder = PostSearchCriteria.builder();
        if (!searchCriteria.isPrivate()){
            builder.visibility("PUBLIC");
            builder.isPublished(true);
        }else {
            Optional.ofNullable(searchCriteria.getIsPublished())
                    .ifPresent(builder::isPublished);

            Optional.ofNullable(searchCriteria.getVisibility())
                    .filter(visibility -> !visibility.trim().isEmpty())
                    .ifPresent(builder::visibility);
        }

        // 动态添加搜索条件
        Optional.ofNullable(searchCriteria.getTitle())
                .filter(title -> !title.trim().isEmpty())
                .ifPresent(builder::title);

        Optional.ofNullable(searchCriteria.getContent())
                .filter(content -> !content.trim().isEmpty())
                .ifPresent(builder::content);

        Optional.ofNullable(searchCriteria.getUserId())
                .ifPresent(builder::userId);

        Optional.ofNullable(searchCriteria.getCategoryId())
                .ifPresent(builder::categoryId);

        Optional.ofNullable(searchCriteria.getTagId())
                .ifPresent(builder::tagId);

        Optional.ofNullable(searchCriteria.getStartDate())
                .ifPresent(builder::startDate);

        Optional.ofNullable(searchCriteria.getEndDate())
                .ifPresent(builder::endDate);

        Optional.of(false).ifPresent(builder::isDeleted);

        // 执行搜索
        List<Post> posts = searchPosts(builder.build());

        // 转换为 PostDto
        return posts.stream()
                .map(post -> {
                    PostDto dto = new PostDto();
                    dto.setPost(post);
                    dto.setCategory(categoryRepository.findByPostId(post.getPostId()).orElse(new Category()));
                    // 获取标签
                    List<Tag> tags = tagRepository.findByPostId(post.getPostId()).orElse(new ArrayList<>());
                    dto.setTags(new ArrayList<>(tags));
                    dto.setComments(commentRepository.findByPostId(post.getPostId()).orElse(new ArrayList<>()));

                    User author = userRepository.findById(post.getUserId()).orElse(null);
                    if (author != null) {
                        dto.setAuthor(author.getUsername());
                    }

                    dto.setAttachments(postAttachmentRepository.findByPostId(post.getPostId()).
                            orElse(new ArrayList<>()));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PostAttachmentDto> findAttachmentsByUserId(Integer userId) {
        // 1. 获取用户的所有文章
        List<Post> userPosts = postRepository.findByUserId(userId)
                .orElse(Collections.emptyList());

        // 2. 收集所有文章的附件
        List<PostAttachmentDto> attachmentDtos = new ArrayList<>();
        for (Post post : userPosts) {
            List<PostAttachment> attachments = postAttachmentRepository
                    .findByPostId(post.getPostId())
                    .orElse(Collections.emptyList());

            // 3. 转换为DTO
            for (PostAttachment attachment : attachments) {
                PostAttachmentDto dto = PostAttachmentDto.builder()
                        .attachmentId(attachment.getAttachmentId())
                        .postId(post.getPostId())
                        .postTitle(post.getTitle())
                        .attachmentName(attachment.getAttachmentName())
                        .attachmentType(attachment.getAttachmentType())
                        .attachmentUrl(attachment.getUrl())
                        .createdTime(attachment.getCreatedTime())
                        .size(attachment.getSize())
                        .build();
                attachmentDtos.add(dto);
            }
        }

        return attachmentDtos;
    }

    @Transactional
    public String generateAbstract(String content){
        final String instruction = "请仔细阅读以下文章，生成一个准确、完整且简洁的摘要。摘要应当：" +
                "\\n1. 包含文章的主要论点和核心观点" +
                "\\n2. 保留关键的事实和数据\\n3. 使用简洁清晰的语言\\n4. 确保摘要的逻辑性和连贯性";
        String summary = xunfeiService.chat(instruction + content);
        log.info("生成摘要请求：{}, 结果：{}", content, summary );
        return summary;
    }

    @Transactional
    public void addViews(Integer postId) {
        Post post = postRepository.findById(postId).orElse(new Post());
        post.setViews(post.getViews() + 1);
        postRepository.save(post);
    }
}