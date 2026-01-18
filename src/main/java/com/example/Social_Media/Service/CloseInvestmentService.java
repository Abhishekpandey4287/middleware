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

        if (!node.has("investmentId")) {
            return "{\"error\": \"investmentId is required\"}";
        }

        Long investmentId = node.get("investmentId").asLong();

        Investment investment = investmentRepository.findById(investmentId)
                .orElse(null);

        if (investment == null) {
            return "{\"error\": \"Investment not found\"}";
        }

        investment.setStatus("closed");
        Investment updated = investmentRepository.save(investment);

        return objectMapper.writeValueAsString(updated);
    }
}
