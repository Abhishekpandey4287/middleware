package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Message;
import com.example.Social_Media.Repository.MessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("deleteMessage")
public class DeleteMessageService implements Action {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("messageId") || !node.has("userId")) {
            return "{\"error\": \"messageId and userId are required\"}";
        }

        Long messageId = node.get("messageId").asLong();
        Long userId = node.get("userId").asLong();

        Message message = messageRepository.findById(messageId).orElse(null);

        if (message == null) {
            return "{\"error\": \"Message not found\"}";
        }

        // Only sender can delete the message
        if (!message.getSender().getId().equals(userId)) {
            return "{\"error\": \"Unauthorized to delete this message\"}";
        }

        message.setIsDeleted(true);
        messageRepository.save(message);

        return "{\"message\": \"Message deleted successfully\"}";
    }
}