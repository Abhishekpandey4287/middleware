package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("users")
public class UserService implements Action {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String handle(String requestJson) throws Exception {
        User user = objectMapper.readValue(requestJson, User.class);

        // Check if user already exists
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return "{\"error\": \"User with this email already exists\"}";
        }

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default role if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        User savedUser = userRepository.save(user);

        // Don't return password in response
        savedUser.setPassword(null);
        return objectMapper.writeValueAsString(savedUser);
    }
}