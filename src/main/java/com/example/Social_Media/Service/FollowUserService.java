package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Follow;
import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Repository.FollowRepository;
import com.example.Social_Media.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("followUser")
public class FollowUserService implements Action {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("followerId") || !node.has("followingId")) {
            return "{\"error\": \"followerId and followingId are required\"}";
        }

        Long followerId = node.get("followerId").asLong();
        Long followingId = node.get("followingId").asLong();

        if (followerId.equals(followingId)) {
            return "{\"error\": \"Cannot follow yourself\"}";
        }

        User follower = userRepository.findById(followerId).orElse(null);
        User following = userRepository.findById(followingId).orElse(null);

        if (follower == null || following == null) {
            return "{\"error\": \"User not found\"}";
        }

        // Check if already following
        boolean alreadyFollowing = followRepository.existsByFollowerAndFollowing(follower, following);
        if (alreadyFollowing) {
            return "{\"error\": \"Already following this user\"}";
        }

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        follow.setCreatedAt(LocalDateTime.now());

        Follow saved = followRepository.save(follow);
        return objectMapper.writeValueAsString(saved);
    }
}
