package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.DTO.LikeData;
import com.example.Social_Media.Entity.Content;
import com.example.Social_Media.Model.LikeEvent;
import com.example.Social_Media.Repository.ContentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("likeContent")
public class LikeContentService implements Action {

    @Autowired private LikeQueueService likeQueueService;
    @Autowired private ContentRepository contentRepository;
    @Autowired private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        var node = objectMapper.readTree(requestJson);

        if (!node.has("contentId") || !node.has("userId")) {
            return "{\"error\": \"contentId and userId are required\"}";
        }

        long contentId = node.get("contentId").asLong();
        long userId    = node.get("userId").asLong();

        // Validate content exists
        Content content = contentRepository.findById(contentId).orElse(null);
        if (content == null) {
            return "{\"error\": \"Content not found\"}";
        }

        // Warm up cache from DB if this is the first request after restart
        likeQueueService.warmUpCache(contentId, content.getLikes());

        // Enqueue the event — updates Redis cache + pushes to stream
        LikeEvent event = likeQueueService.enqueue(contentId, userId);

        // Return optimistic state from Redis (no DB hit)
        int  cachedCount = likeQueueService.getLikeCount(contentId);
        boolean isLiked  = "LIKE".equals(event.getAction());

        ObjectNode response = objectMapper.createObjectNode();
        response.put("likes",   cachedCount);
        response.put("isLiked", isLiked);

        return objectMapper.writeValueAsString(response);
    }
}