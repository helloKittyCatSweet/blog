package com.kitty.blog.domain.service.post;

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
import com.kitty.blog.domain.service.FavoriteService;
import com.kitty.blog.domain.service.UserActivityService;
//import com.kitty.blog.domain.service.contentReview.BaiduContentService;
import com.kitty.blog.domain.service.post.abstractContent.XunfeiService;
import com.kitty.blog.domain.service.search.SearchService;
import com.kitty.blog.domain.service.tag.TagWeightService;
import com.kitty.blog.domain.service.user.UserFollowService;
import com.kitty.blog.infrastructure.utils.AliyunOSSUploader;
import com.kitty.blog.infrastructure.utils.PageUtil;
import com.kitty.blog.infrastructure.utils.UpdateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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

//    @Autowired
//    private BaiduContentService baiduContentService;

    @Autowired
    private TagWeightService tagWeightService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private XunfeiService xunfeiService;

    @Autowired
    private UserFollowService userFollowService;

    @Autowired
    private SearchService searchService;

    @Transactional
    public ResponseEntity<PostDto> create(PostDto input) {
        Post post = input.getPost();

        // 验证用户是否存在
        if (!userRepository.existsById(post.getUserId())) {
            return new ResponseEntity<>(new PostDto(), HttpStatus.NOT_FOUND);
        }

        // 内容审核
//        String s = baiduContentService.checkText(post.getContent());
//        if (!s.equals("合规")) {
//            return new ResponseEntity<>(new PostDto(), HttpStatus.BAD_REQUEST);
//        }

        // 初始化默认值
        post.setVersion(1);
        log.info("create post: " + post.toString());

        // 保存文章
        Post savedPost = (Post) postRepository.save(post);

        // 如果是公开发布的文章，通知关注者
        if (!savedPost.getIsDraft() && "PUBLIC".equals(savedPost.getVisibility())
                && savedPost.getIsPublished() && !savedPost.isDeleted()) {
            userFollowService.notifyFollowers(
                    savedPost.getUserId(),
                    savedPost.getTitle(),
                    savedPost.getPostId());
        }

        // 保存版本记录
        postVersionRepository.save(
                new PostVersion(savedPost.getPostId(), savedPost.getContent(),
                        savedPost.getUserId(), savedPost.getVersion()));

        // 添加分类
        if (input.getCategory() != null) {
            addCategory(savedPost.getPostId(), input.getCategory().getCategoryId());
        }

        // 添加标签
        if (!input.getTags().isEmpty()) {
            List<Integer> tagIds = input.getTags().stream().map(Tag::getTagId).toList();
            for (Integer tagId : tagIds) {
                addTag(savedPost.getPostId(), tagId);
            }
        }

        // 构建返回的 PostDto
        PostDto postDto = convertToPostDto(savedPost);

        // 同步到es
        searchService.syncPostToEs(postDto);

        return new ResponseEntity<>(postDto, HttpStatus.CREATED);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<PostDto> update(PostDto input) {
        Post post = input.getPost();
        // log.info("update post: {}", post.getPostId());
        // log.info("update postdto: {}",input);

        // 检查文章是否存在
        if (!postRepository.existsById(post.getPostId())) {
            return new ResponseEntity<>(new PostDto(), HttpStatus.NOT_FOUND);
        }

        // 获取现有文章
        Post existingPost = (Post) postRepository.findById(post.getPostId()).orElse(null);
        if (existingPost == null) {
            return new ResponseEntity<>(new PostDto(), HttpStatus.NOT_FOUND);
        } else {
            // 如果文章从草稿变成公开发布，通知关注者
            if (existingPost.getIsDraft() &&
                    !post.getIsDraft() && "PUBLIC".equals(post.getVisibility())) {
                userFollowService.notifyFollowers(
                        existingPost.getUserId(),
                        existingPost.getTitle(),
                        existingPost.getPostId());
            }
        }

        // 如果内容有变化才进行审核
//        if (post.getContent() != null && !post.getContent().equals(existingPost.getContent())) {
//            // 内容审核
//            String s = baiduContentService.checkText(post.getContent());
//            if (!s.equals("合规")) {
//                return new ResponseEntity<>(new PostDto(), HttpStatus.BAD_REQUEST);
//            }
//        }

        // 更新文章字段
        try {
            UpdateUtil.updateNotNullProperties(post, existingPost);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // 保存更新后的文章
        Post updatedPost = (Post) postRepository.save(existingPost);

        // 更新分类和标签
        if (input.getCategory() != null) {
            updateCategory(post.getPostId(), input.getCategory().getCategoryId());
        }
        if (!input.getTags().isEmpty()) {
            List<Integer> tagIds = input.getTags().stream().map(Tag::getTagId).toList();
            updateTags(post.getPostId(), tagIds);
        }

        // 同步版本管理
        PostVersion postVersion = postVersionRepository
                .findByPostIdAndVersion(updatedPost.getPostId(), updatedPost.getVersion())
                .orElse(new PostVersion(
                        updatedPost.getPostId(),
                        updatedPost.getContent(),
                        updatedPost.getUserId(),
                        updatedPost.getVersion()));
        postVersion.setContent(updatedPost.getContent());
        postVersionRepository.save(postVersion);

        // 构建返回的 PostDto
        PostDto postDto = convertToPostDto(updatedPost);

        // 同步到es
        searchService.syncPostToEs(postDto);

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

        // 获取文件类型
        String fileName = fileDto.getFile().getName().toLowerCase();
        String mimeType;

        // 处理特殊文件类型
        if (fileName.endsWith(".svg")) {
            mimeType = "image/svg+xml";
        } else if (fileName.endsWith(".md")) {
            mimeType = "text/markdown";
        } else if (fileName.endsWith(".vue")) {
            mimeType = "text/x-vue";
        } else if (fileName.endsWith(".jsx") || fileName.endsWith(".tsx")) {
            mimeType = "text/jsx";
        } else if (fileName.endsWith(".less")) {
            mimeType = "text/x-less";
        } else if (fileName.endsWith(".sass") || fileName.endsWith(".scss")) {
            mimeType = "text/x-sass";
        } else if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
            mimeType = "application/x-yaml";
        } else if (fileName.endsWith(".toml")) {
            mimeType = "application/toml";
        } else {
            Tika tika = new Tika();
            try {
                mimeType = tika.detect(fileDto.getFile());
                // 处理一些 Tika 可能识别错误的类型
                if (fileName.endsWith(".ts") && "text/plain".equals(mimeType)) {
                    mimeType = "application/typescript";
                }
            } catch (IOException e) {
                log.error("文件类型检测失败: {}", fileName, e);
                mimeType = "application/octet-stream";
            }
        }

        postAttachment.setAttachmentType(mimeType);
        postAttachmentRepository.save(postAttachment);

        return new ResponseEntity<>(attachment, HttpStatus.OK);
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

//        if (content != null && !content.trim().isEmpty()) {
//            String s = baiduContentService.checkText(content);
//            if (!s.equals("合规")) {
//                return new ResponseEntity<>(new PostVersion(), HttpStatus.BAD_REQUEST);
//            }
//        }

        PostVersion postVersion = (PostVersion) postVersionRepository
                .save(new PostVersion(postId, content, userId, getLatestVersion(postId).getBody() + 1));
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
                    tagRepository.save(tag); // 确保保存更改
                });

        // 添加新标签
        tagIds.stream()
                .filter(tagId -> !currentTagIds.contains(tagId))
                .forEach(tagId -> {
                    if (tagRepository.existsById(tagId)) {
                        Tag tag = tagRepository.findById(tagId).orElse(new Tag());
                        tagWeightService.incrementUseCount(tag);
                        tag.setLastUsedAt(LocalDateTime.now()); // 明确设置最后使用时间
                        tagWeightService.updateWeight(tag);
                        tagRepository.save(tag); // 先保存标签更新
                        postRepository.addTag(postId, tagId); // 再添加关联
                    }
                });

        return findById(postId);
    }

    @Transactional
    @Cacheable(key = "#postId")
    public ResponseEntity<List<PostDto>> findByUserId(Integer userId) {
        List<Post> posts = postRepository.findByUserId(userId).orElse(new ArrayList<>());
        List<PostDto> postDtos = posts.stream().map(this::convertToPostDto).collect(Collectors.toList());
        return new ResponseEntity<>(postDtos, HttpStatus.OK);
    }

    public Page<PostDto> findByUserId(Integer userId, Integer page, Integer size, String[] sort) {
        // 创建排序
        Sort sorting = createSort(sort);

        // 创建分页请求
        PageRequest pageRequest = PageRequest.of(page, size, sorting);

        // 调用repository
        Page<Post> postPage = postRepository.findByUserId(userId, pageRequest);

        return postPage.map(this::convertToPostDto);
    }

    @Transactional
    @Cacheable(key = "#postId")
    public Page<PostDto> findByTitleContaining(String keyword, Integer page, Integer size, String[] sort) {
        // 创建排序
        Sort sorting = createSort(sort);
        // 创建分页请求
        PageRequest pageRequest = PageRequest.of(page, size, sorting);
        // 调用repository
        Page<Post> postPage = postRepository.findByTitleContaining(keyword, pageRequest);
        return postPage.map(this::convertToPostDto);
    }

    @Transactional
    @Cacheable(key = "#postId")
    public Page<PostDto> findByContentContaining(String keyword, Integer page, Integer size, String[] sort) {
        // 创建排序
        Sort sorting = createSort(sort);
        // 创建分页请求
        PageRequest pageRequest = PageRequest.of(page, size, sorting);
        // 调用repository
        Page<Post> postPage = postRepository.findByContentContaining(keyword, pageRequest);
        return postPage.map(this::convertToPostDto);
    }

    @Transactional
    @Cacheable(key = "#postId")
    public Page<PostDto> findByUserIdIsPublished(Integer userId, Integer page, Integer size, String[] sort) {
        // 创建排序
        Sort sorting = createSort(sort);
        // 创建分页请求
        PageRequest pageRequest = PageRequest.of(page, size, sorting);
        // 调用repository
        Page<Post> postPage = postRepository.findByUserIdAndIsPublishedTrueAndIsDeletedFalse(userId, pageRequest);
        return postPage.map(this::convertToPostDto);
    }

    @Transactional
    @Cacheable(key = "#postId")
    public List<PostDto> findByUserIdIsPublished(Integer userId) {
        return postRepository.findByUserIdAndIsPublishedTrueAndIsDeletedFalse(userId)
                .stream().map(this::convertToPostDto).collect(Collectors.toList());
    }

    @Transactional
    @Cacheable(key = "#postId")
    public Page<PostDto> findByCategory(String category, Integer page, Integer size, String[] sort) {
        Category targetCategory = categoryRepository.findByName(category).orElse(new Category());
        if (!categoryRepository.existsById(targetCategory.getCategoryId())) {
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size), 0);
        } else {
            PageRequest pageRequest = PageUtil.createPageRequest(page, size, sort);
            Page<Post> posts = postRepository.findByCategoryId(targetCategory.getCategoryId(), pageRequest);
            return posts.map(this::convertToPostDto);
        }
    }

    @Transactional
    @Cacheable(key = "#postId")
    public Page<PostDto> findByTag(String tag, Integer page, Integer size, String[] sort) {
        Tag targetTag = tagRepository.findByName(tag).orElse(new Tag());
        if (!tagRepository.existsById(targetTag.getTagId())) {
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size), 0);
        } else {
            Sort sorting = createSort(sort);
            PageRequest pageRequest = PageRequest.of(page, size, sorting);
            Page<Post> posts = postRepository.findByTagId(targetTag.getTagId(), pageRequest);
            return posts.map(this::convertToPostDto);
        }
    }

    @Transactional
    @Cacheable(key = "#tags")
    public Page<PostDto> findByTags(List<String> tags, Integer page, Integer size, String[] sort) {
        // 查询所有标签
        List<Tag> targetTags = tagRepository.findByNameIn(tags).orElse(new ArrayList<>());

        // 如果没有找到任何标签，直接返回空列表
        if (targetTags.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), PageRequest.of(page, size), 0);
        }

        // 提取所有标签的 ID
        Set<Integer> tagIds = targetTags.stream()
                .map(Tag::getTagId)
                .collect(Collectors.toSet());

        // 根据标签 ID 查询文章
        Sort sorting = createSort(sort);
        PageRequest pageRequest = PageRequest.of(page, size, sorting);
        Page<Post> posts = postRepository.findByTagsIn(tagIds, pageRequest);
        return posts.map(this::convertToPostDto);
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
        searchService.syncPostToEs(convertToPostDto(post));
        // 用户点赞
        if (count > 0) {
            userActivityService.create(new UserActivity(post.getUserId(), ActivityType.LIKE.name(),
                    post.getPostId(), count));
        } else {
            UserActivity userActivity = userActivityService.findPostActivityExplicit(post.getUserId(), post.getPostId(),
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
        searchService.syncPostToEs(convertToPostDto(post));
        // 用户收藏
        if (count > 0) {
            favoriteService.save(new Favorite(userId, postId));
            userActivityService.create(new UserActivity(userId, ActivityType.FAVORITE.name(),
                    postId, count));
        } else {
            Favorite favorite = favoriteService.findByUserIdAndPostId(userId, postId).getBody();
            assert favorite != null;
            favoriteService.deleteById(favorite.getFavoriteId(), userId);
            UserActivity userActivity = userActivityService.findPostActivityExplicit(userId, postId,
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
    public Page<PostDto> findByVisibility(String visibility, Integer userId, Integer page, Integer size,
            String[] sort) {
        try {
            Visibility.valueOf(visibility);
            Sort sorting = createSort(sort);
            PageRequest pageRequest = PageRequest.of(page, size, sorting);
            Page<Post> posts = postRepository.findByVisibility(visibility, userId, pageRequest);
            return posts.map(this::convertToPostDto);
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
        Post pp = postRepository.save(post);
        postVersionRepository.save(
                new PostVersion(pp.getPostId(), post.getContent(),
                        post.getUserId(), post.getVersion()));

        return new ResponseEntity<>(pp, HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<PostDto> findById(Integer postId) {
        if (!postRepository.existsById(postId)) {
            return new ResponseEntity<>(new PostDto(), HttpStatus.NOT_FOUND);
        }
        Post post = (Post) postRepository.findById(postId).orElse(new Post());
        PostDto postDto = convertToPostDto(post);
        return new ResponseEntity<>(postDto, HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public Page<PostDto> findAll(Integer page, Integer size, String[] sort) {
        // 创建分页和排序请求
        PageRequest pageRequest = PageUtil.createPageRequest(page, size, sort);

        // 直接使用 Repository 进行分页查询
        Page<Post> posts = postRepository.findByIsPublishedTrueAndVisibilityAndIsDeletedFalse(
                "PUBLIC",
                pageRequest);

        // 转换为 PostDto
        return posts.map(this::convertToPostDto);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public List<PostDto> findAll() {
        // 创建搜索条件
        PostSearchCriteria criteria = PostSearchCriteria.builder()
                .isPublished(true)
                .visibility("PUBLIC")
                .build();
        // 返回分页结果
        return searchPostsByMultipleCriteria(criteria);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> deleteById(Integer postId) {
        if (Boolean.FALSE.equals(existsById(postId).getBody())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        postRepository.findById(postId).orElse(new Post()).setDeleted(true);

        // postRepository.deleteById(postId);
        // 同步到es
        searchService.deletePostFromEs(postId);

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

    @Transactional
    public List<Post> searchPosts(PostSearchCriteria criteria) {
        Specification<Post> spec = PostSpecification.createSpecification(criteria);
        return postRepository.findAll(spec);
    }

    @Transactional
    public List<PostDto> searchPostsByMultipleCriteria(PostSearchCriteria searchCriteria) {
        // 构建搜索条件
        PostSearchCriteria.PostSearchCriteriaBuilder builder = PostSearchCriteria.builder();
        if (searchCriteria.getIsPrivate() != null && !searchCriteria.getIsPrivate()) {
            builder.visibility("PUBLIC");
            builder.isPublished(true);
        } else {
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
                .map(this::convertToPostDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Page<PostAttachmentDto> findAttachmentsByUserId(Integer userId, Integer page, Integer size, String[] sort) {
        // 1. 获取用户的所有文章
        List<Post> userPosts = postRepository.findByUserId(userId)
                .orElse(Collections.emptyList());

        Sort sorting = PageUtil.createSort(sort);
        PageRequest pageRequest = PageRequest.of(page, size, sorting);

        // 2. 收集所有文章的附件
        List<PostAttachmentDto> attachmentDtos = new ArrayList<>();
        for (Post post : userPosts) {
            Page<PostAttachment> attachments = postAttachmentRepository
                    .findByPostId(post.getPostId(), pageRequest);

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

        // 4. 创建分页结果
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), attachmentDtos.size());

        return new PageImpl<>(
                attachmentDtos.subList(start, end),
                pageRequest,
                attachmentDtos.size());
    }

    @Transactional
    public String generateAbstract(String content) {
        final String instruction = "请仔细阅读以下文章，生成一个准确、完整且简洁的摘要。摘要应当：" +
                "\\n1. 包含文章的主要论点和核心观点" +
                "\\n2. 保留关键的事实和数据\\n3. 使用简洁清晰的语言\\n4. 确保摘要的逻辑性和连贯性";
        String summary = xunfeiService.chat(instruction + content);
        log.info("生成摘要请求：{}, 结果：{}", content, summary);
        return summary;
    }

    @Transactional
    public void addViews(Integer postId) {
        // 一次性获取文章和相关标签
        Post post = postRepository.findById(postId).orElse(new Post());
        List<Tag> tags = tagRepository.findByPostId(postId).orElse(new ArrayList<>());

        // 在内存中更新数据
        post.setViews(post.getViews() + 1);
        tags.forEach(tag -> tag.setClickCount(tag.getClickCount() + 1));

        // 批量保存更新
        postRepository.save(post);
        searchService.syncPostToEs(convertToPostDto(post));
        if (!tags.isEmpty()) {
            tagRepository.saveAll(tags);
        }
    }

    /**
     * Post到PostDto的映射方法
     */
    public PostDto convertToPostDto(Post post) {
        PostDto postDto = new PostDto();
        postDto.setPost(post);
        postDto.setCategory(categoryRepository.findByPostId(post.getPostId()).orElse(new Category()));
        postDto.setTags(tagRepository.findByPostId(post.getPostId()).orElse(new ArrayList<>()));
        postDto.setComments(commentRepository.findByPostId(post.getPostId()).orElse(new ArrayList<>()));
        User author = userRepository.findById(post.getUserId()).orElse(null);
        if (author != null) {
            postDto.setAuthor(author.getUsername());
        }
        postDto.setAttachments(postAttachmentRepository.findByPostId(post.getPostId()).orElse(new ArrayList<>()));
        return postDto;
    }

    private Sort createSort(String[] sort) {
        return sort != null && sort.length > 0 ? Sort.by(Arrays.stream(sort)
                .map(str -> {
                    String[] parts = str.split(",");
                    return parts[1].equalsIgnoreCase("desc") ? Sort.Order.desc(parts[0]) : Sort.Order.asc(parts[0]);
                })
                .collect(Collectors.toList()))
                : Sort.by(Sort.Direction.DESC, "createdAt");
    }
}