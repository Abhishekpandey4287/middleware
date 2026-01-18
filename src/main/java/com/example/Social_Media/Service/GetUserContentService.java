package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Content;
import com.example.Social_Media.Repository.ContentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("getUserContent")
public class GetUserContentService implements Action {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("userId")) {
            return "{\"error\": \"userId is required\"}";
        }

        Long userId = node.get("userId").asLong();
        List<Content> content = contentRepository.findByCreatorId(userId);

        return objectMapper.writeValueAsString(content);
    }
}
