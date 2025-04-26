package com.kitty.blog.domain.repository;

import com.kitty.blog.domain.model.UserFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFollowRepository extends BaseRepository<UserFollow, Integer> {

    Page<List<UserFollow>> findByFollowerId(Integer followerId, Pageable pageable);

    List<UserFollow> findByFollowerId(Integer followerId);

    Page<UserFollow> findByFollowingId(Integer followingId, Pageable pageable);

    Page<Optional<UserFollow>> findByFollowerIdAndFollowingId
            (Integer followerId, Integer followingId, Pageable pageable);

    Optional<UserFollow> findByFollowerIdAndFollowingId
            (Integer followerId, Integer followingId);

    boolean existsByFollowerIdAndFollowingId(Integer followerId, Integer followingId);
}