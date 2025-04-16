package com.kitty.blog.domain.repository;

import com.kitty.blog.domain.model.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFollowRepository extends BaseRepository<UserFollow, Integer> {
    List<UserFollow> findByFollowerId(Integer followerId);
    List<UserFollow> findByFollowingId(Integer followingId);
    Optional<UserFollow> findByFollowerIdAndFollowingId(Integer followerId, Integer followingId);
    boolean existsByFollowerIdAndFollowingId(Integer followerId, Integer followingId);
}