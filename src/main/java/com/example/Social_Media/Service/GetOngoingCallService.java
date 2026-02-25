package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Call;
import com.example.Social_Media.Repository.CallRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("getOngoingCall")
public class GetOngoingCallService implements Action {

    @Autowired
    private CallRepository callRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("userId")) {
            return "{\"error\": \"userId is required\"}";
        }

        Long userId = node.get("userId").asLong();

        Optional<Call> ongoingCall = callRepository.findOngoingCallForUser(userId);

        if (ongoingCall.isPresent()) {
            return objectMapper.writeValueAsString(ongoingCall.get());
        } else {
            return "null";
        }
    }
}