package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("checkUsernameAvailability")
public class CheckUsernameAvailabilityService implements Action {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("username")) {
            return "{\"error\": \"Username is required\"}";
        }

        String username = node.get("username").asText();

        // Validate username format
        if (!username.matches("^[a-zA-Z0-9._]+$")) {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("available", false);
            response.put("message", "Username can only contain letters, numbers, dots and underscores");
            return objectMapper.writeValueAsString(response);
        }

        // Validate username length
        if (username.length() < 3 || username.length() > 30) {
            ObjectNode response = objectMapper.createObjectNode();
            response.put("available", false);
            response.put("message", "Username must be between 3 and 30 characters");
            return objectMapper.writeValueAsString(response);
        }

        boolean exists = userRepository.existsByUsername(username);

        ObjectNode response = objectMapper.createObjectNode();
        response.put("available", !exists);
        response.put("username", username);

        if (exists) {
            response.put("message", "Username is already taken");
        } else {
            response.put("message", "Username is available");
        }

        return objectMapper.writeValueAsString(response);
    }
}