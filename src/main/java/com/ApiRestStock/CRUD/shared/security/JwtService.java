package com.ApiRestStock.CRUD.shared.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey key;
    private final long accessExpirationMinutes;
    private final long refreshExpirationDays;

public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-minutes:60}") long accessExpirationMinutes,
            @Value("${security.jwt.refresh-expiration-days:7}") long refreshExpirationDays
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationMinutes = accessExpirationMinutes;
        this.refreshExpirationDays = refreshExpirationDays;
    }

    public String generateAccessToken(String subjectUserName, String role) {
        Date now = new Date();
        long expirationMs = accessExpirationMinutes * 60 * 1000L;
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
            .subject(subjectUserName)
            .claim("role", role)
            .claim("token_type", "access")
            .issuedAt(now)
            .expiration(exp)
            .signWith(key)
            .compact();
    }

    public String generateRefreshToken(String subjectUserName, String role) {
        Date now = new Date();
        long expirationMs = refreshExpirationDays * 24L * 60L * 60L * 1000L;
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
            .subject(subjectUserName)
            .claim("role", role)
            .claim("token_type", "refresh")
            .issuedAt(now)
            .expiration(exp)
            .signWith(key)
            .compact();
    }

    /**
     * Compatibilidad: mantiene el nombre anterior como access token.
     */
    public String generateToken(String subjectUserName, String role) {
        return generateAccessToken(subjectUserName, role);
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }

    public String extractTokenType(String token) {
        Claims claims = parseClaims(token);
        return claims.get("token_type", String.class);
    }

    public boolean isAccessToken(String token) {
        String type = extractTokenType(token);
        return type == null || "access".equalsIgnoreCase(type);
    }

    public boolean isRefreshToken(String token) {
        String type = extractTokenType(token);
        return "refresh".equalsIgnoreCase(type);
    }


    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
