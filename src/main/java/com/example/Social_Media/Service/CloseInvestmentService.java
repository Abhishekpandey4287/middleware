package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Investment;
import com.example.Social_Media.Repository.InvestmentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("closeInvestment")
public class CloseInvestmentService implements Action {

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        // FIXED: Map from contentId to investmentId
        if (!node.has("contentId")) {
            return "{\"error\": \"contentId (investmentId) is required\"}";
        }

        Long investmentId = node.get("contentId").asLong(); // contentId is mapped to investmentId

        Investment investment = investmentRepository.findById(investmentId).orElse(null);

        if (investment == null) {
            return "{\"error\": \"Investment not found\"}";
        }

        // Check if already closed
        if ("closed".equals(investment.getStatus())) {
            return "{\"error\": \"Investment is already closed\"}";
        }

        investment.setStatus("closed");
        Investment updated = investmentRepository.save(investment);

        return objectMapper.writeValueAsString(updated);
    }
}