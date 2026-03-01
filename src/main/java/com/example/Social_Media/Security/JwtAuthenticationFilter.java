package com.example.Social_Media.Security;

import com.example.Social_Media.Utility.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (!request.getRequestURI().contains("/api/app/process")) {
            filterChain.doFilter(request, response);
            return;
        }

        String action = extractAction(request);

        if (PublicActions.isPublic(action)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "MISSING_TOKEN", "Authorization header required");
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateAccessToken(token)) {
            if (jwtUtil.isAccessTokenExpired(token)) {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "TOKEN_EXPIRED", "Access token expired. Please refresh.");
            } else {
                sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                        "INVALID_TOKEN", "Invalid or malformed token");
            }
            return;
        }

        try {
            String email = jwtUtil.extractEmailFromAccessToken(token);
            request.setAttribute("userEmail", email);

            var auth = new UsernamePasswordAuthenticationToken(
                    email, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED,
                    "INVALID_TOKEN", "Token processing error: " + e.getMessage());
        }
    }

    /**
     * Unwraps the request chain until it finds our CachedBodyHttpServletRequest,
     * then reads the cached body bytes. This is necessary because Spring Security
     * wraps the request multiple times after ContentCachingFilter runs, so a
     * direct cast always throws ClassCastException.
     */
    private String extractAction(HttpServletRequest request) {
        try {
            // Walk the wrapper chain to find our cached body wrapper
            HttpServletRequest current = request;
            while (current != null) {
                if (current instanceof CachedBodyHttpServletRequest cached) {
                    byte[] body = cached.getBody();
                    if (body == null || body.length == 0) return null;
                    JsonNode node = objectMapper.readTree(body);
                    return node.has("action") ? node.get("action").asText() : null;
                }
                // Unwrap one level
                if (current instanceof HttpServletRequestWrapper wrapper) {
                    current = (HttpServletRequest) wrapper.getRequest();
                } else {
                    break;
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private void sendError(HttpServletResponse response,
                           int status,
                           String errorCode,
                           String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String body = String.format(
                "{\"status\":false,\"code\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
                status, errorCode, message);
        response.getWriter().write(body);
    }
}