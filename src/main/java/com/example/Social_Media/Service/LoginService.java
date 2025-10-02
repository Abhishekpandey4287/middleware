package com.example.Social_Media.Service;

import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Repository.UserRepository;
import com.example.Social_Media.Utility.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

    @Autowired
    private JwtUtil jwtUtil;
    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);
        String email = node.get("email").asText();
        String password = node.get("password").asText();

        // Find user by email
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            User user = userOpt.get();

            String accessToken = jwtUtil.generateAccessToken(email);
            String refreshToken = jwtUtil.generateRefreshToken(email);

            ObjectNode response = objectMapper.createObjectNode();
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("accessToken", accessToken);
            response.put("refreshToken", refreshToken);

            return objectMapper.writeValueAsString(response);
          //  return objectMapper.writeValueAsString(userOpt.get());
        } else {
            return "{}";
        }
    }
}
