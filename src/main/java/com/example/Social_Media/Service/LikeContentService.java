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
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service("likeContent")
public class LikeContentService implements Action {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private ContentLikeRepository contentLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("contentId")) {
            return "{\"error\": \"contentId is required\"}";
        }

        Long contentId = node.get("contentId").asLong();

        Long userId = null;
        if (node.has("userId")) {
            userId = node.get("userId").asLong();
        } else {
            return "{\"error\": \"userId is required for like/unlike\"}";
        }

        Content content = contentRepository.findById(contentId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (content == null) {
            return "{\"error\": \"Content not found\"}";
        }

        if (user == null) {
            return "{\"error\": \"User not found\"}";
        }

        // TOGGLE: Check if user already liked this content
        Optional<ContentLike> existingLike = contentLikeRepository.findByContentAndUser(content, user);

        boolean isLiked;

        if (existingLike.isPresent()) {
            // Unlike
            contentLikeRepository.delete(existingLike.get());
            content.setLikes(Math.max(0, content.getLikes() - 1));
            contentRepository.save(content);
            isLiked = false;
            System.out.println("👎 User " + userId + " unliked content " + contentId + " (count: " + content.getLikes() + ")");
        } else {
            // Like
            ContentLike like = new ContentLike();
            like.setContent(content);
            like.setUser(user);
            like.setCreatedAt(LocalDateTime.now());
            contentLikeRepository.save(like);
            content.setLikes(content.getLikes() + 1);
            contentRepository.save(content);
            isLiked = true;
            System.out.println("👍 User " + userId + " liked content " + contentId + " (count: " + content.getLikes() + ")");
        }

        ObjectNode response = objectMapper.createObjectNode();
        response.put("id", content.getId());
        response.put("title", content.getTitle());
        response.put("description", content.getDescription());
        response.put("videoUrl", content.getVideoUrl());
        response.put("thumbnailUrl", content.getThumbnailUrl());
        response.put("views", content.getViews());
        response.put("likes", content.getLikes());
        response.put("isLiked", isLiked);

        if (content.getCreator() != null) {
            ObjectNode creatorNode = objectMapper.createObjectNode();
            creatorNode.put("id", content.getCreator().getId());
            creatorNode.put("name", content.getCreator().getName());
            creatorNode.put("email", content.getCreator().getEmail());
            response.set("creator", creatorNode);
        }

        return objectMapper.writeValueAsString(response);
    }
}