package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("users")
public class UserService implements Action {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        User user = objectMapper.readValue(requestJson, User.class);
        User savedUser = userRepository.save(user);

        // return saved user as JSON
        return objectMapper.writeValueAsString(savedUser);
    }
}
