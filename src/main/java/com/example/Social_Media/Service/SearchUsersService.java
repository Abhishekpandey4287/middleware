package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("searchUsers")
public class SearchUsersService implements Action {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("query")) {
            return "{\"error\": \"search query is required\"}";
        }

        String query = node.get("query").asText();
        List<User> results = userRepository.findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(query, query);

        // Remove passwords from response
        results.forEach(user -> user.setPassword(null));

        return objectMapper.writeValueAsString(results);
    }
}
