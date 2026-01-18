package com.example.Social_Media.Repository;

import com.example.Social_Media.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Find comments by content ID
    List<Comment> findByContentId(Long contentId);

    // Find comments by user ID
    List<Comment> findByUserId(Long userId);

    // Count comments on content
    Long countByContentId(Long contentId);
}
