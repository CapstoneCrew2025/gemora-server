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
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }


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


    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    public Long extractUserId(String token) {
        return extractAllClaims(token).get("userId", Long.class);
    }


    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }


    public String extractUsername(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = extractAllClaims(token);
        return claims.getSubject(); // subject = email or username
    }

    public boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }


    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            System.out.println("Invalid JWT: " + e.getMessage());
            return false;
        }
    }
}
