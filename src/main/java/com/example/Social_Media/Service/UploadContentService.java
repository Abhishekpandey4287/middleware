package com.example.Social_Media.Service;

import com.example.Social_Media.Entity.Content;
import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Repository.ContentRepository;
import com.example.Social_Media.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Social_Media.Action.Action;

@Service("uploadContent")
public class UploadContentService implements Action {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        Long userId = node.get("userId").asLong();
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Content content = new Content();
        content.setCreator(creator);
        content.setTitle(node.get("title").asText());
        content.setDescription(node.get("description").asText());
        content.setVideoUrl(node.get("videoUrl").asText());
        content.setThumbnailUrl(node.get("thumbnailUrl").asText());

        Content saved = contentRepository.save(content);
        return objectMapper.writeValueAsString(saved);
    }
}
