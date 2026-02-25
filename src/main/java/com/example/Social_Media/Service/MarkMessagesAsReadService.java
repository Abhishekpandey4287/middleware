package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Chat;
import com.example.Social_Media.Entity.Message;
import com.example.Social_Media.Repository.ChatRepository;
import com.example.Social_Media.Repository.MessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service("markMessagesAsRead")
public class MarkMessagesAsReadService implements Action {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("contentId") || !node.has("userId")) {
            return "{\"error\": \"chatId and userId are required\"}";
        }

        Long chatId = node.get("contentId").asLong();
        Long userId = node.get("userId").asLong();

        // Get unread messages
        List<Message> unreadMessages = messageRepository.findUnreadMessages(chatId, userId);

        // Mark all as read
        LocalDateTime now = LocalDateTime.now();
        for (Message message : unreadMessages) {
            message.setIsRead(true);
            message.setReadAt(now);
            messageRepository.save(message);
        }

        // Reset unread count in chat
        Chat chat = chatRepository.findById(chatId).orElse(null);
        if (chat != null) {
            if (chat.getUser1().getId().equals(userId)) {
                chat.setUser1UnreadCount(0);
            } else {
                chat.setUser2UnreadCount(0);
            }
            chatRepository.save(chat);
        }

        return "{\"message\": \"Messages marked as read\", \"count\": " + unreadMessages.size() + "}";
    }
}