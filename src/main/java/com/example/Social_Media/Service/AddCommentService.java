package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Comment;
import com.example.Social_Media.Entity.Content;
import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Repository.CommentRepository;
import com.example.Social_Media.Repository.ContentRepository;
import com.example.Social_Media.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("addComment")
public class AddCommentService implements Action {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("contentId") || !node.has("userId") || !node.has("text")) {
            return "{\"error\": \"contentId, userId, and text are required\"}";
        }

        Long contentId = node.get("contentId").asLong();
        Long userId = node.get("userId").asLong();
        String text = node.get("text").asText();

        if (text.trim().isEmpty() || text.length() > 500) {
            return "{\"error\": \"Comment must be between 1 and 500 characters\"}";
        }

        Content content = contentRepository.findById(contentId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (content == null || user == null) {
            return "{\"error\": \"Content or User not found\"}";
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setText(text);
        comment.setCreatedAt(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);
        return objectMapper.writeValueAsString(saved);
    }
}
