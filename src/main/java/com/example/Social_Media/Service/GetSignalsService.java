package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.CallSignal;
import com.example.Social_Media.Repository.CallSignalRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("getSignals")
public class GetSignalsService implements Action {

    @Autowired
    private CallSignalRepository callSignalRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    @Transactional
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("userId")) {
            return "{\"error\": \"userId is required\"}";
        }

        Long userId = node.get("userId").asLong();

        // FIXED: Get unprocessed signals for the user
        List<CallSignal> signals = callSignalRepository.getUnprocessedSignals(userId);

        // CRITICAL FIX: Mark signals as processed AFTER retrieval
        // This allows the client to receive them first
        if (!signals.isEmpty()) {
            for (CallSignal signal : signals) {
                signal.setIsProcessed(true);
            }
            callSignalRepository.saveAll(signals);
        }

        // Return signals (even if empty list)
        return objectMapper.writeValueAsString(signals);
    }
}