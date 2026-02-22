package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.WalletTransaction;
import com.example.Social_Media.Repository.WalletRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("getWalletBalance")
public class GetWalletBalanceService implements Action {

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

        // Get all transactions for user
        List<WalletTransaction> transactions = walletRepository.findByUserId(userId);

        // Calculate balance from successful transactions only
        Double balance = transactions.stream()
                .filter(txn -> "success".equals(txn.getStatus())) // Only count successful transactions
                .mapToDouble(txn -> {
                    if ("credit".equals(txn.getTxnType())) {
                        return txn.getAmount();
                    } else if ("debit".equals(txn.getTxnType())) {
                        return -txn.getAmount();
                    }
                    return 0.0;
                })
                .sum();

        // FIXED: Return proper object structure matching WalletBalance data class
        Map<String, Object> walletBalance = new HashMap<>();
        walletBalance.put("userId", userId);
        walletBalance.put("balance", balance);

        return objectMapper.writeValueAsString(walletBalance);
    }
}