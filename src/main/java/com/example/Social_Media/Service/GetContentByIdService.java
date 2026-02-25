package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Content;
import com.example.Social_Media.Repository.ContentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("getContentById")
public class GetContentByIdService implements Action {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("contentId")) {
            return "{\"error\": \"contentId is required\"}";
        }

        Long contentId = node.get("contentId").asLong();
        Content content = contentRepository.findById(contentId)
                .orElse(null);

        if (content == null) {
            return "{\"error\": \"Content not found\"}";
        }

        return objectMapper.writeValueAsString(content);
    }
}