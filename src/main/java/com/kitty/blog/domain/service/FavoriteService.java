package com.kitty.blog.domain.service;

import com.kitty.blog.application.dto.favorite.FavoriteDto;
import com.kitty.blog.domain.model.Favorite;
import com.kitty.blog.domain.model.Post;
import com.kitty.blog.domain.repository.FavoriteRepository;
import com.kitty.blog.domain.repository.post.PostRepository;
import com.kitty.blog.domain.repository.UserRepository;
import com.kitty.blog.infrastructure.utils.UpdateUtil;
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
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "favorite")
public class FavoriteService {

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Transactional
    public ResponseEntity<Boolean> create(Favorite favorite) {
        if (!userRepository.existsById(favorite.getUserId())
                || !postRepository.existsById(favorite.getFavoriteId())) {
            return new ResponseEntity<>(false,HttpStatus.NOT_FOUND);
        }
        favoriteRepository.save(favorite);
        return new ResponseEntity<>(true, HttpStatus.CREATED);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> update(Favorite favorite) {
        if (!favoriteRepository.existsById(favorite.getFavoriteId())) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        final Favorite oldFavorite = (Favorite)
                favoriteRepository.findById(favorite.getFavoriteId()).orElse(new Favorite());
        try {
            UpdateUtil.updateNotNullProperties(favorite, oldFavorite);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        favoriteRepository.save(favorite);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#userId")
    public ResponseEntity<List<FavoriteDto>> findByUserId(Integer userId) {
        if (!userRepository.existsById(userId)){
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.NOT_FOUND);
        }else {
            List<Favorite> favorites = favoriteRepository.findByUserId(userId).orElse(new ArrayList<>());
            List<FavoriteDto> posts = new ArrayList<>();
            for (Favorite favorite : favorites) {
                posts.add(new FavoriteDto(
                        postRepository.findById(favorite.getPostId()).orElse(new Post()),
                        favorite.getFavoriteId(),
                        favorite.getFolderName()
                ));
            }
            return new ResponseEntity<>(
                    posts,
                    HttpStatus.OK);
        }
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Integer> countByUserId(Integer userId) {
        if (!userRepository.existsById(userId)){
            return new ResponseEntity<>(-1,HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(favoriteRepository.countByUserId(userId), HttpStatus.OK);
    }

//    @Transactional
//    public ResponseEntity<Favorite> findByPostId(Integer postId) {
//        if (!postRepository.existsById(postId)) {
//            return new ResponseEntity<>(new Favorite(), HttpStatus.NOT_FOUND);
//        } else {
//            return new ResponseEntity<>(
//                    favoriteRepository.findByPostId(postId).orElse(new Favorite()),
//                    HttpStatus.OK);
//        }
//    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Integer> countByPostId(Integer postId) {
        if (!postRepository.existsById(postId)){
            return new ResponseEntity<>(-1, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(favoriteRepository.countByPostId(postId), HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#userId + #postId")
    public ResponseEntity<Favorite> findByUserIdAndPostId(Integer userId, Integer postId) {
        if (!userRepository.existsById(userId) || !postRepository.existsById(postId)) {
            return new ResponseEntity<>(new Favorite(),HttpStatus.NOT_FOUND);
        }else {
            return new ResponseEntity<>(
                    favoriteRepository.findByUserIdAndPostId(userId, postId).orElse(new Favorite()),
                    HttpStatus.OK);
        }
    }

    @Transactional
    public boolean IsUser(Integer userId, Integer testId){
        return userId.equals(testId);
    }

    /**
     * Auto-generated methods
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Favorite> save(Favorite favorite) {
        return new ResponseEntity<>((Favorite) favoriteRepository.save(favorite),
                HttpStatus.OK);
    }

    @Transactional
    @Cacheable(key = "#id")
    public ResponseEntity<Post> findById(Integer id){
        Favorite favorite = (Favorite) favoriteRepository.findById(id).orElse(new Favorite());
        return new ResponseEntity<>(
                postRepository.findById(favorite.getPostId()).orElse(new Post()),
                HttpStatus.OK);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<List<Post>> findAll(){
        if (favoriteRepository.count() == 0){
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.NO_CONTENT);
        }else {
            List<Favorite> favorites = favoriteRepository.findAll();
            List<Post> posts = new ArrayList<>();
            for (Favorite favorite : favorites) {
                posts.add(postRepository.findById(favorite.getPostId()).orElse(new Post()));
            }
            return new ResponseEntity<>(posts, HttpStatus.OK);
        }

    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> deleteById(Integer id) {
        if (!existsById(id).getBody()){
            return new ResponseEntity<>(false,HttpStatus.NOT_FOUND);
        }
        favoriteRepository.deleteById(id);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Long> count() {
        return new ResponseEntity<>(favoriteRepository.count(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<Boolean> existsById(Integer id) {
        return new ResponseEntity<>(favoriteRepository.existsById(id), HttpStatus.OK);
    }

    /**
     * 获取用户的所有收藏夹名称
     */
    @Transactional
    @Cacheable(key = "'folders_' + #userId")
    public ResponseEntity<List<String>> getFolderNames(Integer userId) {
        if (!userRepository.existsById(userId)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(favoriteRepository.findFolderNamesByUserId(userId), HttpStatus.OK);
    }

    /**
     * 获取用户特定收藏夹中的收藏
     */
    @Transactional
    @Cacheable(key = "'folder_' + #userId + '_' + #folderName")
    public ResponseEntity<List<FavoriteDto>> findByUserIdAndFolderName(Integer userId, String folderName) {
        if (!userRepository.existsById(userId)) {
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.NOT_FOUND);
        }
        List<Favorite> favorites = favoriteRepository.findByUserIdAndFolderName(userId, folderName)
                .orElse(new ArrayList<>());
        List<FavoriteDto> posts = new ArrayList<>();
        for (Favorite favorite : favorites) {
            posts.add(new FavoriteDto(
                    postRepository.findById(favorite.getPostId()).orElse(new Post()),
                    favorite.getFavoriteId(),
                    favorite.getFolderName()
            ));
        }
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    /**
     * 移动收藏到指定文件夹
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> moveToFolder(Integer favoriteId, String folderName) {
        Optional<Favorite> favoriteOpt = favoriteRepository.findById(favoriteId);
        if (favoriteOpt.isEmpty()) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
        Favorite favorite = favoriteOpt.get();
        favorite.setFolderName(folderName);
        favoriteRepository.save(favorite);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    /**
     * 获取用户特定收藏夹中的收藏数量
     */
    @Transactional
    @Cacheable(key = "'folder_count_' + #userId + '_' + #folderName")
    public ResponseEntity<Integer> countByUserIdAndFolderName(Integer userId, String folderName) {
        if (!userRepository.existsById(userId)) {
            return new ResponseEntity<>(-1, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(
                favoriteRepository.countByUserIdAndFolderName(userId, folderName),
                HttpStatus.OK
        );
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public ResponseEntity<Boolean> deleteFolder(Integer userId, String folderName) {
        // 不允许删除默认收藏夹
        if ("默认收藏夹".equals(folderName)) {
            return new ResponseEntity<>(false, HttpStatus.FORBIDDEN);
        }

        // 检查文件夹是否存在
        if (!favoriteRepository.existsByUserIdAndFolderName(userId, folderName)) {
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }

        // 获取该文件夹下的所有收藏
        List<Favorite> favorites = favoriteRepository.findByUserIdAndFolderName(userId, folderName)
                .orElse(new ArrayList<>());

        // 将收藏移动到默认收藏夹
        for (Favorite favorite : favorites) {
            favorite.setFolderName("默认收藏夹");
            favoriteRepository.save(favorite);
        }

        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
