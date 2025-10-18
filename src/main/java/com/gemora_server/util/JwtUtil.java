package com.gemora_server.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret; // Must be at least 32 characters long for HMAC

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ✅ Generate token with claims
    public String generateToken(Long userId, String email) {
        SecretKey key = getSigningKey();
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .claims(Map.of(
                        "userId", userId,
                        "email", email
                ))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Extract all claims from the token
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ✅ Extract user ID claim
    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }

    // ✅ Extract email (subject)
    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    // ✅ Validate token expiration
    public boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    // ✅ Validate token signature + expiry
    public boolean validateToken(String token) {
        try {
            extractAllClaims(token); // will throw if invalid
            return !isTokenExpired(token);
        } catch (Exception e) {
            System.out.println("Invalid JWT: " + e.getMessage());
            return false;
        }
    }
}
