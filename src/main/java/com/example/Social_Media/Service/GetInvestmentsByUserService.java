package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Entity.Investment;
import com.example.Social_Media.Repository.InvestmentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service("getInvestmentsByUser")
public class GetInvestmentsByUserService implements Action {

    @Autowired
    private InvestmentRepository investmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);

        if (!node.has("userId")) {
            return "{\"error\": \"userId is required\"}";
        }

        Long userId = node.get("userId").asLong();

        List<Investment> investments = investmentRepository.findAll()
                .stream()
                .filter(inv -> inv.getInvestor().getId().equals(userId) ||
                        inv.getCreator().getId().equals(userId))
                .toList();

        return objectMapper.writeValueAsString(investments);
    }
}