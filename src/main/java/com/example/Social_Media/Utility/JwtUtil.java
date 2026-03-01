package com.example.Social_Media.Utility;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key accessKey  = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final Key refreshKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Access token: 15 minutes
    private final long accessTokenValidity  = 1000L * 60 * 15;

    // Refresh token: 30 days
    private final long refreshTokenValidity = 1000L * 60 * 60 * 24 * 30;

    // ── Token generation ───────────────────────────────────────────────────

    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "access")
                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 10_000))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(accessKey)
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "refresh")
                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + 20_000))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(refreshKey)
                .compact();
    }

    // ── Token validation ───────────────────────────────────────────────────

    /**
     * Validates an ACCESS token. Returns false for refresh tokens even if valid.
     */
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return "access".equals(claims.get("type", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Validates a REFRESH token. Returns false for access tokens even if valid.
     */
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(refreshKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return "refresh".equals(claims.get("type", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Generic validate — tries access key first, then refresh key.
     * Used by the JWT filter for backward compatibility.
     */
    public boolean validateToken(String token) {
        return validateAccessToken(token) || validateRefreshToken(token);
    }

    // ── Email extraction ───────────────────────────────────────────────────

    public String extractEmailFromAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String extractEmailFromRefreshToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(refreshKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Tries access key first; falls back to refresh key.
     */
    public String extractEmail(String token) {
        try {
            return extractEmailFromAccessToken(token);
        } catch (JwtException e) {
            return extractEmailFromRefreshToken(token);
        }
    }

    // ── Expiry helpers ─────────────────────────────────────────────────────

    public boolean isAccessTokenExpired(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(accessKey).build().parseClaimsJws(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            return false; // not an access token
        }
    }

    public boolean isRefreshTokenExpired(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public long getAccessTokenValidityMs()  { return accessTokenValidity;  }
    public long getRefreshTokenValidityMs() { return refreshTokenValidity; }
}