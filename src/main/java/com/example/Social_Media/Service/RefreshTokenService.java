package com.example.Social_Media.Service;

import com.example.Social_Media.Action.Action;
import com.example.Social_Media.Utility.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("refreshToken")
public class RefreshTokenService implements Action {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String handle(String requestJson) throws Exception {
        JsonNode node = objectMapper.readTree(requestJson);
        String refreshToken = node.get("refreshToken").asText();

        if (jwtUtil.validateToken(refreshToken)) {
            String email = jwtUtil.extractEmail(refreshToken);
            String newAccessToken = jwtUtil.generateAccessToken(email);

            ObjectNode response = objectMapper.createObjectNode();
            response.put("accessToken", newAccessToken);
            return objectMapper.writeValueAsString(response);
        } else {
            return "{}";
        }
    }
}
