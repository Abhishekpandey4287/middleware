package com.example.Social_Media.Service;

import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Repository.UserRepository;
import com.example.Social_Media.Utility.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.Social_Media.Action.Action;

import java.util.Optional;

@Service("loginUser")
public class LoginService implements Action {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        String identifier = null;
        if (node.has("email"))    identifier = node.get("email").asText();
        if (node.has("username")) identifier = node.get("username").asText();

        if (identifier == null || !node.has("password")) {
            return "{\"error\": \"Identifier (email or username) and password are required\"}";
        }

        String password = node.get("password").asText();

        Optional<User> userOpt = userRepository.findByEmail(identifier);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByUsername(identifier);
        }

        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            User user = userOpt.get();

            String accessToken  = jwtUtil.generateAccessToken(user.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

            ObjectNode response = objectMapper.createObjectNode();
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);
            return objectMapper.writeValueAsString(response);
        } else {
            return "{\"error\": \"Invalid email or password\"}";
        }
    }
}