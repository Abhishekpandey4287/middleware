package com.example.Social_Media.Repository;

import com.example.Social_Media.Entity.Follow;
import com.example.Social_Media.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(User follower, User following);

    // Find specific follow relationship
    Follow findByFollowerAndFollowing(User follower, User following);

    // Get all followers of a user
    List<Follow> findByFollowingId(Long followingId);

    // Get all users that a user is following
    List<Follow> findByFollowerId(Long followerId);

    // Count followers
    Long countByFollowingId(Long followingId);

    // Count following
    Long countByFollowerId(Long followerId);
}