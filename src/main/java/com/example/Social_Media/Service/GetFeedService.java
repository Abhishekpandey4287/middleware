package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Content;
import com.example.Social_Media.Entity.ContentLike;
import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Repository.ContentLikeRepository;
import com.example.Social_Media.Repository.ContentRepository;
import com.example.Social_Media.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("getFeed")
public class GetFeedService implements Action {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentLikeRepository contentLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        Long currentUserId = null;
        if (node.has("userId")) {
            currentUserId = node.get("userId").asLong();
        }

        List<Content> feed = contentRepository.findAll(
                PageRequest.of(0, 20, Sort.by("id").descending())
        ).getContent();

        ArrayNode feedArray = objectMapper.createArrayNode();

        // Resolve user once outside the loop for efficiency
        User currentUser = null;
        if (currentUserId != null) {
            currentUser = userRepository.findById(currentUserId).orElse(null);
        }

        for (Content content : feed) {
            ObjectNode contentNode = objectMapper.createObjectNode();
            contentNode.put("id", content.getId());
            contentNode.put("title", content.getTitle());
            contentNode.put("description", content.getDescription());
            contentNode.put("videoUrl", content.getVideoUrl());
            contentNode.put("thumbnailUrl", content.getThumbnailUrl());
            contentNode.put("views", content.getViews());
            contentNode.put("likes", content.getLikes());

            boolean isLiked = false;
            if (currentUser != null) {
                isLiked = contentLikeRepository.existsByContentAndUser(content, currentUser);
            }
            contentNode.put("isLiked", isLiked);

            if (content.getCreator() != null) {
                ObjectNode creatorNode = objectMapper.createObjectNode();
                creatorNode.put("id", content.getCreator().getId());
                creatorNode.put("name", content.getCreator().getName());
                creatorNode.put("email", content.getCreator().getEmail());
                contentNode.set("creator", creatorNode);
            }

            feedArray.add(contentNode);
        }

        return objectMapper.writeValueAsString(feedArray);
    }
}