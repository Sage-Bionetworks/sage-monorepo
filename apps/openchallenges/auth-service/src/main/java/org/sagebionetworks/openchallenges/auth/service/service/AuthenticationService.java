package org.sagebionetworks.openchallenges.auth.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.dto.*;
import org.sagebionetworks.openchallenges.auth.service.model.entity.*;
import org.sagebionetworks.openchallenges.auth.service.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthenticationService {

    private final UserRepository userRepository;
    private final ExternalAccountRepository externalAccountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2ConfigurationService oAuth2ConfigurationService;

    /**
     * Authenticate user with username/password and generate JWT tokens
     */
    public LoginResponseDto authenticateUser(String username, String password) {
        log.debug("Authenticating user: {}", username);
        
        Optional<User> userOpt = userRepository.findByUsernameIgnoreCase(username);
        if (userOpt.isEmpty()) {
            log.debug("User not found: {}", username);
            throw new RuntimeException("Invalid credentials");
        }
        
        User user = userOpt.get();
        if (!user.isActive()) {
            log.debug("User account is disabled: {}", username);
            throw new RuntimeException("Account is disabled");
        }
        
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            log.debug("Invalid password for user: {}", username);
            throw new RuntimeException("Invalid credentials");
        }
        
        log.info("User successfully authenticated: {}", username);
        return generateTokenResponse(user);
    }

    /**
     * Generate OAuth2 authorization URL for provider
     */
    public OAuth2AuthorizeResponseDto authorizeOAuth2(OAuth2AuthorizeRequestDto request) {
        log.debug("Generating OAuth2 authorization URL for provider: {}", request.getProvider());
        
        ExternalAccount.Provider provider;
        try {
            provider = ExternalAccount.Provider.valueOf(request.getProvider().getValue());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unsupported OAuth2 provider: " + request.getProvider());
        }

        // Generate state for CSRF protection
        String state = request.getState() != null ? request.getState() : UUID.randomUUID().toString();
        
        // Get authorization URL from OAuth2 configuration service
        String authorizationUrl = oAuth2ConfigurationService.getAuthorizationUrl(provider, state);

        try {
            return OAuth2AuthorizeResponseDto.builder()
                    .authorizationUrl(new java.net.URI(authorizationUrl))
                    .state(state)
                    .build();
        } catch (java.net.URISyntaxException e) {
            throw new RuntimeException("Invalid authorization URL", e);
        }
    }

    /**
     * Handle OAuth2 callback and generate JWT tokens
     */
    public LoginResponseDto handleOAuth2Callback(String provider, String code, String state) {
        log.debug("Handling OAuth2 callback for provider: {}", provider);
        
        ExternalAccount.Provider oauthProvider;
        try {
            oauthProvider = ExternalAccount.Provider.valueOf(provider.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unsupported OAuth2 provider: " + provider);
        }

        // For now, we'll create a placeholder implementation
        // In a real implementation, this would:
        // 1. Exchange code for access token
        // 2. Fetch user info from OAuth provider
        // 3. Find or create user account
        // 4. Link external account
        // 5. Generate JWT tokens
        
        log.debug("OAuth2 callback for provider {} not yet implemented", oauthProvider);
        throw new RuntimeException("OAuth2 callback handling not yet implemented");
    }

    /**
     * Validate JWT token
     */
    public ValidateJwtResponseDto validateJwt(String token) {
        JwtService.JwtValidationResult result = jwtService.validateToken(token);
        
        return ValidateJwtResponseDto.builder()
                .valid(result.isValid())
                .userId(result.getUserId())
                .username(result.getUsername())
                .build();
    }

    /**
     * Refresh JWT tokens using refresh token
     */
    public LoginResponseDto refreshToken(String refreshToken) {
        log.debug("Refreshing JWT token");
        
        String tokenHash = hashToken(refreshToken);
        
        // Find and validate refresh token
        Optional<RefreshToken> tokenEntity = refreshTokenRepository.findByTokenHashAndUserId(
                tokenHash, extractUserIdFromRefreshToken(refreshToken));
        
        if (tokenEntity.isEmpty()) {
            log.debug("Refresh token not found");
            throw new RuntimeException("Invalid refresh token");
        }
        
        RefreshToken token = tokenEntity.get();
        if (token.isExpired() || token.getRevoked()) {
            log.debug("Refresh token is expired or revoked");
            throw new RuntimeException("Refresh token is expired or revoked");
        }
        
        User user = token.getUser();
        if (!user.isActive()) {
            log.debug("User account is disabled");
            throw new RuntimeException("User account is disabled");
        }
        
        log.info("Refresh token successfully validated for user: {}", user.getUsername());
        
        // Generate new tokens
        LoginResponseDto response = generateTokenResponse(user);
        
        // Revoke old refresh token
        refreshTokenRepository.deleteByTokenHashAndUserId(tokenHash, user.getId());
        
        return response;
    }

    /**
     * Generate JWT token response with access and refresh tokens
     */
    private LoginResponseDto generateTokenResponse(User user) {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        // Store refresh token hash
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(refreshToken))
                .expiresAt(OffsetDateTime.now().plusSeconds(jwtService.getRefreshTokenExpirationSeconds()))
                .revoked(false)
                .build();
        
        refreshTokenRepository.save(refreshTokenEntity);
        
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn((int) jwtService.getAccessTokenExpirationSeconds())
                .build();
    }

    /**
     * Hash token for secure storage
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Extract user ID from refresh token (simplified implementation)
     */
    private UUID extractUserIdFromRefreshToken(String refreshToken) {
        return jwtService.extractUserIdFromToken(refreshToken);
    }
}
