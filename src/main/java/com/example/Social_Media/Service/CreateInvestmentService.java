package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Investment;
import com.example.Social_Media.Entity.User;
import com.example.Social_Media.Repository.InvestmentRepository;
import com.example.Social_Media.Repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("createInvestment")
public class CreateInvestmentService implements Action {

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("investorId") || !node.has("creatorId") || !node.has("amount")) {
            return "{\"error\": \"investorId, creatorId, and amount are required\"}";
        }

        Long investorId = node.get("investorId").asLong();
        Long creatorId = node.get("creatorId").asLong();
        Double amount = node.get("amount").asDouble();

        if (amount <= 0) {
            return "{\"error\": \"Investment amount must be positive\"}";
        }

        User investor = userRepository.findById(investorId)
                .orElse(null);
        User creator = userRepository.findById(creatorId)
                .orElse(null);

        if (investor == null || creator == null) {
            return "{\"error\": \"Investor or Creator not found\"}";
        }

        Investment investment = new Investment();
        investment.setInvestor(investor);
        investment.setCreator(creator);
        investment.setAmount(amount);
        investment.setStatus("active");
        investment.setCreatedAt(LocalDateTime.now());

        Investment saved = investmentRepository.save(investment);
        return objectMapper.writeValueAsString(saved);
    }
}
