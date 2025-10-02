package com.example.Social_Media.Service;

import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Social_Media.Action.Action;

import java.util.Optional;

@Service("loginUser")
public class LoginService implements Action {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);
        String email = node.get("email").asText();
        String password = node.get("password").asText();

        // Find user by email
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            return objectMapper.writeValueAsString(userOpt.get());
        } else {
            return "{}";
        }
    }
}
