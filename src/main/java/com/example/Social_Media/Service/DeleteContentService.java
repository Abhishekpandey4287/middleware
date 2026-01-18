package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Content;
import com.example.Social_Media.Repository.ContentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("deleteContent")
public class DeleteContentService implements Action {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("contentId") || !node.has("userId")) {
            return "{\"error\": \"contentId and userId are required\"}";
        }

        Long contentId = node.get("contentId").asLong();
        Long userId = node.get("userId").asLong();

        Content content = contentRepository.findById(contentId)
                .orElse(null);

        if (content == null) {
            return "{\"error\": \"Content not found\"}";
        }

        // Check if user is the creator
        if (!content.getCreator().getId().equals(userId)) {
            return "{\"error\": \"Unauthorized to delete this content\"}";
        }

        contentRepository.delete(content);
        return "{\"message\": \"Content deleted successfully\"}";
    }
}