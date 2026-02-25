package com.example.Social_Media.Repository;

import com.example.Social_Media.Entity.Content;
import com.example.Social_Media.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findByCreator(User creator);

    List<Content> findByCreatorId(Long creatorId);

    // Get trending content (most views/likes)
    @Query("SELECT c FROM Content c ORDER BY c.views DESC, c.likes DESC")
    Page<Content> findTrendingContent(Pageable pageable);

    List<Content> findByTitleContainingIgnoreCase(String title);
}
