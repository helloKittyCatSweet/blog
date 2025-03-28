package com.kitty.blog.service;

import com.kitty.blog.model.Favorite;
import com.kitty.blog.model.Post;
import com.kitty.blog.repository.FavoriteRepository;
import com.kitty.blog.repository.PostRepository;
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
    public ResponseEntity<List<Post>> findByUserId(Integer userId) {
        if (!userRepository.existsById(userId)){
            return new ResponseEntity<>(new ArrayList<>(),HttpStatus.NOT_FOUND);
        }else {
            List<Favorite> favorites = favoriteRepository.findByUserId(userId).orElse(new ArrayList<>());
            List<Post> posts = new ArrayList<>();
            for (Favorite favorite : favorites) {
                posts.add(postRepository.findById(favorite.getPostId()).orElse(new Post()));
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
}
