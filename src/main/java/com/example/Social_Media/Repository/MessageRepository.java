package com.example.Social_Media.Repository;

import com.example.Social_Media.Entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // Find all messages in a chat, ordered by creation time
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND m.isDeleted = false ORDER BY m.createdAt ASC")
    List<Message> findMessagesByChatId(@Param("chatId") Long chatId);

    // Find unread messages for a user in a chat
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND m.receiver.id = :userId AND m.isRead = false AND m.isDeleted = false")
    List<Message> findUnreadMessages(@Param("chatId") Long chatId, @Param("userId") Long userId);

    // Count unread messages in a chat for a user
    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat.id = :chatId AND m.receiver.id = :userId AND m.isRead = false AND m.isDeleted = false")
    Long countUnreadMessages(@Param("chatId") Long chatId, @Param("userId") Long userId);

    // Get last N messages in a chat
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND m.isDeleted = false ORDER BY m.createdAt DESC")
    List<Message> findLastMessages(@Param("chatId") Long chatId);

    // Search messages in a chat
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND m.messageText LIKE %:query% AND m.isDeleted = false ORDER BY m.createdAt DESC")
    List<Message> searchMessagesInChat(@Param("chatId") Long chatId, @Param("query") String query);
}