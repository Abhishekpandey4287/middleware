package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Call;
import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Repository.CallRepository;
import com.example.Social_Media.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service("initiateCall")
public class InitiateCallService implements Action {

    @Autowired
    private CallRepository callRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("userId") || !node.has("followingId") || !node.has("title")) {
            return "{\"error\": \"callerId, receiverId, and callType are required\"}";
        }

        Long callerId = node.get("userId").asLong();
        Long receiverId = node.get("followingId").asLong();
        String callType = node.get("title").asText(); // "video" or "audio"

        if (!callType.equals("video") && !callType.equals("audio")) {
            return "{\"error\": \"callType must be 'video' or 'audio'\"}";
        }

        User caller = userRepository.findById(callerId).orElse(null);
        User receiver = userRepository.findById(receiverId).orElse(null);

        if (caller == null || receiver == null) {
            return "{\"error\": \"Caller or receiver not found\"}";
        }

        // Check if caller already has an ongoing call
        if (callRepository.findOngoingCallForUser(callerId).isPresent()) {
            return "{\"error\": \"" +
                    "You already have an ongoing call\"}";
        }

        // Check if receiver already has an ongoing call
        if (callRepository.findOngoingCallForUser(receiverId).isPresent()) {
            return "{\"error\": \"User is already on another call\"}";
        }

        // Generate unique room ID
        String roomId = "room_" + UUID.randomUUID().toString();

        // Create call record
        Call call = new Call();
        call.setCaller(caller);
        call.setReceiver(receiver);
        call.setCallType(callType);
        call.setCallStatus("ringing");
        call.setRoomId(roomId);
        call.setIsGroupCall(false);
        call.setCreatedAt(LocalDateTime.now());
        call.setUpdatedAt(LocalDateTime.now());

        Call savedCall = callRepository.save(call);

        return objectMapper.writeValueAsString(savedCall);
    }
}