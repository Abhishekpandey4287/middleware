package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Chat;
import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Repository.ChatRepository;
import com.example.Social_Media.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service("getOrCreateChat")
public class GetOrCreateChatService implements Action {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("userId") || !node.has("followingId")) {
            return "{\"error\": \"userId1 and followingId are required\"}";
        }

        Long userId1 = node.get("userId").asLong();
        Long userId2 = node.get("followingId").asLong();

        if (userId1.equals(userId2)) {
            return "{\"error\": \"Cannot create chat with yourself\"}";
        }

        User user1 = userRepository.findById(userId1).orElse(null);
        User user2 = userRepository.findById(userId2).orElse(null);

        if (user1 == null || user2 == null) {
            return "{\"error\": \"One or both users not found\"}";
        }

        // Check if chat already exists
        Optional<Chat> existingChat = chatRepository.findChatBetweenUsers(userId1, userId2);

        if (existingChat.isPresent()) {
            return objectMapper.writeValueAsString(existingChat.get());
        }

        // Create new chat
        Chat chat = new Chat();
        chat.setUser1(user1);
        chat.setUser2(user2);
        chat.setCreatedAt(LocalDateTime.now());
        chat.setUpdatedAt(LocalDateTime.now());
        chat.setUser1UnreadCount(0);
        chat.setUser2UnreadCount(0);

        Chat savedChat = chatRepository.save(chat);
        return objectMapper.writeValueAsString(savedChat);
    }
}