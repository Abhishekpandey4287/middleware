package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Message;
import com.example.Social_Media.Repository.MessageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("searchMessages")
public class SearchMessagesService implements Action {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("chatId") || !node.has("query")) {
            return "{\"error\": \"chatId and query are required\"}";
        }

        Long chatId = node.get("chatId").asLong();
        String query = node.get("query").asText();

        List<Message> messages = messageRepository.searchMessagesInChat(chatId, query);

        return objectMapper.writeValueAsString(messages);
    }
}