package com.example.Social_Media.Repository;

import com.example.Social_Media.Entity.Chat;
import com.example.Social_Media.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    // Find chat between two users
    @Query("SELECT c FROM Chat c WHERE (c.user1.id = :userId1 AND c.user2.id = :userId2) OR (c.user1.id = :userId2 AND c.user2.id = :userId1)")
    Optional<Chat> findChatBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Find all chats for a user, ordered by last message time
    @Query("SELECT c FROM Chat c WHERE c.user1.id = :userId OR c.user2.id = :userId ORDER BY c.lastMessageTime DESC")
    List<Chat> findAllChatsForUser(@Param("userId") Long userId);

    // Count unread chats for a user
    @Query("SELECT COUNT(c) FROM Chat c WHERE (c.user1.id = :userId AND c.user1UnreadCount > 0) OR (c.user2.id = :userId AND c.user2UnreadCount > 0)")
    Long countUnreadChats(@Param("userId") Long userId);
}