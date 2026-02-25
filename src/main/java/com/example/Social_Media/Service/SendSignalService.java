package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.CallSignal;
import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Repository.CallSignalRepository;
import com.example.Social_Media.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("sendSignal")
public class SendSignalService implements Action {

    @Autowired
    private CallSignalRepository callSignalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        // FIXED: Proper parameter mapping validation with detailed error messages
        if (!node.has("title")) {
            return "{\"error\": \"roomId (mapped to 'title' field) is required\"}";
        }
        if (!node.has("userId")) {
            return "{\"error\": \"senderId (mapped to 'userId' field) is required\"}";
        }
        if (!node.has("followingId")) {
            return "{\"error\": \"receiverId (mapped to 'followingId' field) is required\"}";
        }
        if (!node.has("text")) {
            return "{\"error\": \"signalType (mapped to 'text' field) is required\"}";
        }
        if (!node.has("description")) {
            return "{\"error\": \"signalData (mapped to 'description' field) is required\"}";
        }

        String roomId = node.get("title").asText();
        Long senderId = node.get("userId").asLong();
        Long receiverId = node.get("followingId").asLong();
        String signalType = node.get("text").asText();
        String signalData = node.get("description").asText();

        // Validate users exist
        User sender = userRepository.findById(senderId).orElse(null);
        User receiver = userRepository.findById(receiverId).orElse(null);

        if (sender == null) {
            return "{\"error\": \"Sender with ID " + senderId + " not found\"}";
        }
        if (receiver == null) {
            return "{\"error\": \"Receiver with ID " + receiverId + " not found\"}";
        }

        // FIXED: Validate signal type
        if (!signalType.equals("offer") && !signalType.equals("answer") &&
                !signalType.equals("ice-candidate") && !signalType.equals("hang-up")) {
            return "{\"error\": \"Invalid signalType: " + signalType + ". Must be offer, answer, ice-candidate, or hang-up\"}";
        }

        // Create signal
        CallSignal signal = new CallSignal();
        signal.setRoomId(roomId);
        signal.setSender(sender);
        signal.setReceiver(receiver);
        signal.setSignalType(signalType);
        signal.setSignalData(signalData);
        signal.setIsProcessed(false);
        signal.setCreatedAt(LocalDateTime.now());

        CallSignal savedSignal = callSignalRepository.save(signal);

        // Log for debugging
        System.out.println("✅ Signal saved: " + signalType + " from User " + senderId + " to User " + receiverId + " in room " + roomId);

        return objectMapper.writeValueAsString(savedSignal);
    }
}