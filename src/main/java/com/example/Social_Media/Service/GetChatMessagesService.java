package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Message;
import com.example.Social_Media.Repository.MessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("getChatMessages")
public class GetChatMessagesService implements Action {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("contentId")) {
            return "{\"error\": \"chatId is required\"}";
        }

        Long chatId = node.get("contentId").asLong();

        List<Message> messages = messageRepository.findMessagesByChatId(chatId);

        return objectMapper.writeValueAsString(messages);
    }
}