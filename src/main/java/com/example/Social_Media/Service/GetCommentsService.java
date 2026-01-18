package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Comment;
import com.example.Social_Media.Repository.CommentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("getComments")
public class GetCommentsService implements Action {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("contentId")) {
            return "{\"error\": \"contentId is required\"}";
        }

        Long contentId = node.get("contentId").asLong();
        List<Comment> comments = commentRepository.findByContentId(contentId);

        return objectMapper.writeValueAsString(comments);
    }
}
