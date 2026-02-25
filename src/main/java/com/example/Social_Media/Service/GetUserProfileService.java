package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("getUserProfile")
public class GetUserProfileService implements Action {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        Optional<User> userOpt = Optional.empty();

        // Support fetching by either userId or username
        if (node.has("userId")) {
            Long userId = node.get("userId").asLong();
            userOpt = userRepository.findById(userId);
        } else if (node.has("username")) {
            String username = node.get("username").asText();
            userOpt = userRepository.findByUsername(username);
        } else {
            return "{\"error\": \"userId or username is required\"}";
        }

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Remove password from response
            user.setPassword(null);
            return objectMapper.writeValueAsString(user);
        } else {
            return "{\"error\": \"User not found\"}";
        }
    }
}