package org.sagebionetworks.openchallenges.auth.service.service;

import org.sagebionetworks.openchallenges.auth.service.model.dto.CreateApiKeyRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.CreateApiKeyResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing API keys as OAuth2 service principals.
 * Each API key corresponds to a RegisteredClient that can use client_credentials flow.
 */
@Service
public class ApiKeyAsServicePrincipalService {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyAsServicePrincipalService.class);
    
    private static final String CLIENT_ID_PREFIX = "oc-ak_";
    // API key prefixes are environment-specific: oc_dev_, oc_stage_, oc_prod_
    // The actual prefix will be determined at runtime based on configuration
    private static final int KEY_SUFFIX_LENGTH = 20; // Length of random suffix after environment prefix
    private static final int SECRET_LENGTH = 32;
    
    private final RegisteredClientRepository registeredClientRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom;
    private final String environmentPrefix;

    public ApiKeyAsServicePrincipalService(
            RegisteredClientRepository registeredClientRepository,
            PasswordEncoder passwordEncoder,
            @Value("${openchallenges.api-key.environment-prefix:oc_dev_}") String environmentPrefix) {
        this.registeredClientRepository = registeredClientRepository;
        this.passwordEncoder = passwordEncoder;
        this.secureRandom = new SecureRandom();
        this.environmentPrefix = environmentPrefix;
    }

    /**
     * Creates a new API key as an OAuth2 service principal.
     * The user information is obtained from the current security context.
     * 
     * @param request The API key creation request
     * @return Response containing the API key (shown only once)
     */
    public CreateApiKeyResponseDto createApiKey(CreateApiKeyRequestDto request) {
        // TODO: Get user context from Spring Security
        // For now, we'll use placeholder values
        String username = "current-user"; // TODO: Get from SecurityContextHolder
        String userRole = "user"; // TODO: Get from SecurityContextHolder
        
        logger.info("Creating API key '{}' for user: {}", request.getName(), username);
        
        // Generate API key components
        String keySuffix = generateBase32String(KEY_SUFFIX_LENGTH);
        String secret = generateBase64UrlString(SECRET_LENGTH);
        String fullApiKey = environmentPrefix + keySuffix + "." + secret;
        String clientId = CLIENT_ID_PREFIX + keySuffix;
        
        // Determine scopes based on user role
        List<String> scopes = determineScopesFromRole(userRole);
        
        // Create RegisteredClient for this API key
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId(clientId)
                .clientSecret(passwordEncoder.encode(secret)) // Hash the secret
                .clientName("API Key: " + request.getName() + " (" + username + ")")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scopes(scopeSet -> scopeSet.addAll(scopes))
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false) // No consent for API keys
                        .requireProofKey(false) // No PKCE for client credentials
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(15)) // Short-lived tokens
                        .reuseRefreshTokens(false) // No refresh tokens for client credentials
                        .build())
                .build();
                
        // Save the registered client
        registeredClientRepository.save(registeredClient);
        
        logger.info("Created OAuth2 client {} for API key: {}", clientId, request.getName());
        
        // TODO: Return proper CreateApiKeyResponseDto using the existing constructor
        // For now, this is a placeholder
        return new CreateApiKeyResponseDto();
    }
    
    /**
     * Revokes an API key by deleting its RegisteredClient.
     * 
     * @param clientId The client ID of the API key to revoke
     */
    public void revokeApiKey(String clientId) {
        logger.info("Revoking API key with client ID: {}", clientId);
        
        RegisteredClient client = registeredClientRepository.findByClientId(clientId);
        if (client != null) {
            // For JdbcRegisteredClientRepository, we'd need to implement delete
            // For now, this is a placeholder
            logger.warn("RegisteredClient deletion not implemented - would delete client: {}", clientId);
            // TODO: Implement deletion in repository or mark as revoked in companion table
        } else {
            logger.warn("API key client not found: {}", clientId);
        }
    }
    
    /**
     * Determines OAuth2 scopes based on user role.
     */
    private List<String> determineScopesFromRole(String role) {
        return switch (role.toLowerCase()) {
            case "admin" -> List.of("read:org", "write:org", "delete:org", "read:challenge", "write:challenge", "delete:challenge");
            case "service" -> List.of("read:org", "write:org", "read:challenge", "write:challenge");
            case "user" -> List.of("read:org", "read:challenge");
            default -> List.of("read:org");
        };
    }
    
    /**
     * Generates a base32-encoded random string for key prefixes.
     */
    private String generateBase32String(int length) {
        byte[] bytes = new byte[length];
        secureRandom.nextBytes(bytes);
        
        // Simple base32 encoding (only lowercase letters and numbers 2-7)
        StringBuilder result = new StringBuilder();
        String chars = "abcdefghijklmnopqrstuvwxyz234567";
        
        for (byte b : bytes) {
            result.append(chars.charAt((b & 0xFF) % chars.length()));
        }
        
        return result.toString();
    }
    
    /**
     * Generates a base64url-encoded random string for secrets.
     */
    private String generateBase64UrlString(int byteLength) {
        byte[] bytes = new byte[byteLength];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
