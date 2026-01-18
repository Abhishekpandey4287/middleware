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
import java.util.List;

@Service("withdrawMoney")
public class WithdrawMoneyService implements Action {

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

        // Check balance
        Double balance = getWalletBalance(userId);
        if (balance < amount) {
            return "{\"error\": \"Insufficient balance\"}";
        }

        WalletTransaction transaction = new WalletTransaction();
        transaction.setUser(user);
        transaction.setAmount(amount);
        transaction.setTxnType("debit");
        transaction.setStatus("success");
        transaction.setCreatedAt(LocalDateTime.now());

        WalletTransaction saved = walletRepository.save(transaction);
        return objectMapper.writeValueAsString(saved);
    }

    private Double getWalletBalance(Long userId) {
        List<WalletTransaction> transactions = walletRepository.findAll()
                .stream()
                .filter(txn -> txn.getUser().getId().equals(userId))
                .toList();

        return transactions.stream()
                .mapToDouble(txn -> {
                    if (txn.getTxnType().equals("credit")) {
                        return txn.getAmount();
                    } else {
                        return -txn.getAmount();
                    }
                })
                .sum();
    }
}
