package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Chat;
import com.example.Social_Media.Entity.Message;
import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Repository.ChatRepository;
import com.example.Social_Media.Repository.MessageRepository;
import com.example.Social_Media.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service("sendMessage")
public class SendMessageService implements Action {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("contentId") || !node.has("userId") || !node.has("followingId")) {
            return "{\"error\": \"contentId, userId, and followingId are required\"}";
        }

        Long chatId = node.get("contentId").asLong();
        Long senderId = node.get("userId").asLong();
        Long receiverId = node.get("followingId").asLong();

        // At least one of messageText or mediaUrl must be present
        if (!node.has("text") && !node.has("mediaUrl")) {
            return "{\"error\": \"messageText or mediaUrl is required\"}";
        }

        Chat chat = chatRepository.findById(chatId).orElse(null);
        User sender = userRepository.findById(senderId).orElse(null);
        User receiver = userRepository.findById(receiverId).orElse(null);

        if (chat == null || sender == null || receiver == null) {
            return "{\"error\": \"Chat, sender, or receiver not found\"}";
        }

        // Create message
        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMessageText(node.has("text") ? node.get("text").asText() : null);
        message.setMessageType(node.has("title") ? node.get("title").asText() : "text");
        message.setMediaUrl(node.has("mediaUrl") ? node.get("mediaUrl").asText() : null);
        message.setIsRead(false);
        message.setIsDeleted(false);
        message.setCreatedAt(LocalDateTime.now());

        // Handle reply
        if (node.has("replyToMessageId")) {
            Long replyToId = node.get("replyToMessageId").asLong();
            Message replyToMessage = messageRepository.findById(replyToId).orElse(null);
            if (replyToMessage != null) {
                message.setReplyToMessage(replyToMessage);
            }
        }

        Message savedMessage = messageRepository.save(message);

        // Update chat's last message info
        chat.setLastMessage(message.getMessageText() != null ? message.getMessageText() : "Media");
        chat.setLastMessageTime(LocalDateTime.now());
        chat.setLastMessageSenderId(senderId);
        chat.setUpdatedAt(LocalDateTime.now());

        // Increment unread count for receiver
        if (chat.getUser1().getId().equals(receiverId)) {
            chat.setUser1UnreadCount(chat.getUser1UnreadCount() + 1);
        } else {
            chat.setUser2UnreadCount(chat.getUser2UnreadCount() + 1);
        }

        chatRepository.save(chat);

        return objectMapper.writeValueAsString(savedMessage);
    }
}