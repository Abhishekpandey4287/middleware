package com.example.Social_Media.Repository;

import com.example.Social_Media.Entity.Content;
import com.example.Social_Media.Entity.ContentLike;
import com.example.Social_Media.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContentLikeRepository extends JpaRepository<ContentLike, Long> {

    // Check if user already liked a content
    boolean existsByContentAndUser(Content content, User user);

    // Find specific like by content and user
    Optional<ContentLike> findByContentAndUser(Content content, User user);

    // Find all likes by a user
    List<ContentLike> findByUserId(Long userId);

    // Find all likes on a content
    List<ContentLike> findByContentId(Long contentId);

    // Count likes on content
    Long countByContentId(Long contentId);

    // Delete like by content and user (for unlike)
    void deleteByContentAndUser(Content content, User user);
}