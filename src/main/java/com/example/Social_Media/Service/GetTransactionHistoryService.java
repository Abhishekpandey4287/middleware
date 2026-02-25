package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.WalletTransaction;
import com.example.Social_Media.Repository.WalletRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service("getTransactionHistory")
public class GetTransactionHistoryService implements Action {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("userId")) {
            return "{\"error\": \"userId is required\"}";
        }

        Long userId = node.get("userId").asLong();

        // FIXED: Use repository method and sort by date (newest first)
        List<WalletTransaction> transactions = walletRepository.findByUserId(userId)
                .stream()
                .sorted(Comparator.comparing(WalletTransaction::getCreatedAt).reversed())
                .collect(Collectors.toList());

        return objectMapper.writeValueAsString(transactions);
    }
}