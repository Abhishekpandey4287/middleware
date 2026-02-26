package com.example.Social_Media.Repository;

import com.example.Social_Media.Entity.Content;
import com.example.Social_Media.Entity.ContentLike;
import com.example.Social_Media.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface ContentLikeRepository extends JpaRepository<ContentLike, Long> {

    boolean existsByContentAndUser(Content content, User user);

    ContentLike findByContentAndUser(Content content, User user);

    Long countByContentId(Long contentId);

    // Bulk check: which contentIds has this user liked?
    @Query("SELECT cl.content.id FROM ContentLike cl WHERE cl.user.id = :userId AND cl.content.id IN :contentIds")
    Set<Long> findLikedContentIdsByUser(@Param("userId") Long userId, @Param("contentIds") List<Long> contentIds);

    @Modifying
    @Transactional
    @Query("DELETE FROM ContentLike cl WHERE cl.content.id = :contentId AND cl.user.id = :userId")
    void deleteByContentIdAndUserId(@Param("contentId") Long contentId, @Param("userId") Long userId);
}