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

        if (!node.has("refreshToken")) {
            return "{\"error\": \"refreshToken is required\"}";
        }

        String refreshToken = node.get("refreshToken").asText();

        if (!jwtUtil.validateRefreshToken(refreshToken)) {

            if (jwtUtil.isRefreshTokenExpired(refreshToken)) {
                return "{\"error\": \"REFRESH_TOKEN_EXPIRED\", " +
                        "\"message\": \"Session has expired. Please log in again.\"}";
            }

            return "{\"error\": \"INVALID_REFRESH_TOKEN\", " +
                    "\"message\": \"Invalid refresh token.\"}";
        }

        String email = jwtUtil.extractEmailFromRefreshToken(refreshToken);
        String newAccessToken = jwtUtil.generateAccessToken(email);

        ObjectNode response = objectMapper.createObjectNode();
        response.put("accessToken", newAccessToken);
        response.put("expiresIn", jwtUtil.getAccessTokenValidityMs() / 1000); // seconds

        return objectMapper.writeValueAsString(response);
    }
}