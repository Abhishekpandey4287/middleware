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

@Service("unfollowUser")
public class UnfollowUserService implements Action {

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

        User follower = userRepository.findById(followerId).orElse(null);
        User following = userRepository.findById(followingId).orElse(null);

        if (follower == null || following == null) {
            return "{\"error\": \"User not found\"}";
        }

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following);
        if (follow == null) {
            return "{\"error\": \"Not following this user\"}";
        }

        followRepository.delete(follow);
        return "{\"message\": \"Unfollowed successfully\"}";
    }
}
