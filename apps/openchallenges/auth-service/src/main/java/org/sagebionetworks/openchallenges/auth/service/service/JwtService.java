package org.sagebionetworks.openchallenges.auth.service.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtService {

    private final SecretKey jwtSigningKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;
    private final String issuer;

    public JwtService(
            @Value("${app.security.jwt.secret-key:openchallenges-default-jwt-secret-key-change-in-production}") String jwtSecretKey,
            @Value("${app.security.jwt.access-token-expiration-ms:3600000}") long accessTokenExpirationMs, // 1 hour
            @Value("${app.security.jwt.refresh-token-expiration-ms:604800000}") long refreshTokenExpirationMs, // 7 days
            @Value("${app.security.jwt.issuer:openchallenges-auth-service}") String issuer) {
        
        this.jwtSigningKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
        this.issuer = issuer;
        
        log.info("JWT Service initialized with access token expiration: {}ms, refresh token expiration: {}ms", 
                 accessTokenExpirationMs, refreshTokenExpirationMs);
    }

    /**
     * Generate JWT access token for user
     */
    public String generateAccessToken(User user) {
        return generateToken(user, accessTokenExpirationMs, TokenType.ACCESS);
    }

    /**
     * Generate JWT refresh token for user
     */
    public String generateRefreshToken(User user) {
        return generateToken(user, refreshTokenExpirationMs, TokenType.REFRESH);
    }

    /**
     * Generate JWT token with specified expiration and type
     */
    private String generateToken(User user, long expirationMs, TokenType tokenType) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime expiration = now.plusSeconds(expirationMs / 1000);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().toString());
        claims.put("username", user.getUsername());
        claims.put("role", user.getRole().name());
        claims.put("tokenType", tokenType.name());
        
        if (user.getEmail() != null) {
            claims.put("email", user.getEmail());
        }

        return Jwts.builder()
                .claims(claims)
                .subject(user.getId().toString())
                .issuer(issuer)
                .issuedAt(Date.from(now.toInstant()))
                .expiration(Date.from(expiration.toInstant()))
                .signWith(jwtSigningKey, Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Validate and parse JWT token
     */
    public JwtValidationResult validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtSigningKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return JwtValidationResult.builder()
                    .valid(true)
                    .userId(UUID.fromString(claims.getSubject()))
                    .username(claims.get("username", String.class))
                    .role(User.Role.valueOf(claims.get("role", String.class)))
                    .email(claims.get("email", String.class))
                    .tokenType(TokenType.valueOf(claims.get("tokenType", String.class)))
                    .issuedAt(claims.getIssuedAt().toInstant().atOffset(ZoneOffset.UTC))
                    .expiresAt(claims.getExpiration().toInstant().atOffset(ZoneOffset.UTC))
                    .build();

        } catch (JwtException | IllegalArgumentException e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return JwtValidationResult.builder()
                    .valid(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }

    /**
     * Extract user ID from token without full validation
     */
    public UUID extractUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtSigningKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            return UUID.fromString(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Failed to extract user ID from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtSigningKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            return claims.get("username", String.class);
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Check if token is valid for the given username
     */
    public boolean isTokenValid(String token, String username) {
        try {
            JwtValidationResult result = validateToken(token);
            return result.isValid() && username.equals(result.getUsername());
        } catch (Exception e) {
            log.debug("Token validation failed for username {}: {}", username, e.getMessage());
            return false;
        }
    }

    /**
     * Check if token is expired without throwing exceptions
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(jwtSigningKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true; // Consider invalid tokens as expired
        }
    }

    /**
     * Get access token expiration time in seconds
     */
    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationMs / 1000;
    }

    /**
     * Get refresh token expiration time in seconds
     */
    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpirationMs / 1000;
    }

    /**
     * Token type enumeration
     */
    public enum TokenType {
        ACCESS,
        REFRESH
    }

    /**
     * JWT validation result
     */
    @lombok.Builder
    @lombok.Data
    public static class JwtValidationResult {
        private boolean valid;
        private UUID userId;
        private String username;
        private User.Role role;
        private String email;
        private TokenType tokenType;
        private OffsetDateTime issuedAt;
        private OffsetDateTime expiresAt;
        private String errorMessage;
    }
}
