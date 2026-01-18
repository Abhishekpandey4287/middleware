package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Entity.WalletTransaction;
import com.example.Social_Media.Repository.UserRepository;
import com.example.Social_Media.Repository.WalletRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service("addMoney")
public class AddMoneyService implements Action {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("userId") || !node.has("amount")) {
            return "{\"error\": \"userId and amount are required\"}";
        }

        Long userId = node.get("userId").asLong();
        Double amount = node.get("amount").asDouble();

        if (amount <= 0) {
            return "{\"error\": \"Amount must be positive\"}";
        }

        User user = userRepository.findById(userId)
                .orElse(null);

        if (user == null) {
            return "{\"error\": \"User not found\"}";
        }

        WalletTransaction transaction = new WalletTransaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setTxnType("credit");
        transaction.setStatus("success");
        transaction.setCreatedAt(LocalDateTime.now());

        WalletTransaction saved = walletRepository.save(transaction);
        return objectMapper.writeValueAsString(saved);
    }
}
