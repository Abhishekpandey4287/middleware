package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Call;
import com.example.Social_Media.Repository.CallRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("endCall")
public class EndCallService implements Action {

    @Autowired
    private CallRepository callRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("contentId") || !node.has("userId")) {
            return "{\"error\": \"callId and userId are required\"}";
        }

        Long callId = node.get("contentId").asLong();
        Long userId = node.get("userId").asLong();

        Call call = callRepository.findById(callId).orElse(null);

        if (call == null) {
            return "{\"error\": \"Call not found\"}";
        }

        // Verify user is part of the call
        if (!call.getCaller().getId().equals(userId) && !call.getReceiver().getId().equals(userId)) {
            return "{\"error\": \"Unauthorized to end this call\"}";
        }

        // Update call status based on current state
        if (call.getCallStatus().equals("ongoing")) {
            call.setCallStatus("completed");
            call.setEndTime(LocalDateTime.now());
            call.calculateDuration();
        } else if (call.getCallStatus().equals("ringing")) {
            // If call was ringing and caller ends it, mark as cancelled
            // If receiver ends it, mark as rejected
            if (call.getCaller().getId().equals(userId)) {
                call.setCallStatus("cancelled");
            } else {
                call.setCallStatus("rejected");
            }
            call.setEndTime(LocalDateTime.now());
        }

        call.setUpdatedAt(LocalDateTime.now());

        Call updatedCall = callRepository.save(call);

        return objectMapper.writeValueAsString(updatedCall);
    }
}