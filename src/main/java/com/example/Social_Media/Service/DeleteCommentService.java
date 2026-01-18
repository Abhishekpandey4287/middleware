package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Comment;
import com.example.Social_Media.Repository.CommentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("deleteComment")
public class DeleteCommentService implements Action {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("commentId") || !node.has("userId")) {
            return "{\"error\": \"commentId and userId are required\"}";
        }

        Long commentId = node.get("commentId").asLong();
        Long userId = node.get("userId").asLong();

        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment == null) {
            return "{\"error\": \"Comment not found\"}";
        }

        // Check if user is the comment author
        if (!comment.getUser().getId().equals(userId)) {
            return "{\"error\": \"Unauthorized to delete this comment\"}";
        }

        commentRepository.delete(comment);
        return "{\"message\": \"Comment deleted successfully\"}";
    }
}
