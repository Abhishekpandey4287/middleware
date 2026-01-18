package com.example.Social_Media.Repository;

import com.example.Social_Media.Entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find notifications by user ID
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Find unread notifications
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId);

    // Count unread notifications
    Long countByUserIdAndIsReadFalse(Long userId);
}
