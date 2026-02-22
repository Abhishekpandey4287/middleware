package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Call;
import com.example.Social_Media.Repository.CallRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("rejectCall")
public class RejectCallService implements Action {

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

        // Verify user is the receiver
        if (!call.getReceiver().getId().equals(userId)) {
            return "{\"error\": \"Unauthorized to reject this call\"}";
        }

        // Check if call is in ringing state
        if (!call.getCallStatus().equals("ringing")) {
            return "{\"error\": \"Call is not in ringing state\"}";
        }

        // Update call status
        call.setCallStatus("rejected");
        call.setEndTime(LocalDateTime.now());
        call.setUpdatedAt(LocalDateTime.now());

        Call updatedCall = callRepository.save(call);

        return objectMapper.writeValueAsString(updatedCall);
    }
}