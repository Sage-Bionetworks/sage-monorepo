package org.sagebionetworks.openchallenges.auth.service.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.sagebionetworks.openchallenges.auth.service.configuration.ApiKeyProperties;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.repository.ApiKeyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ApiKeyService {

  private static final Logger logger = LoggerFactory.getLogger(ApiKeyService.class);
  private static final String CLIENT_ID_PREFIX = "oc_api_key_";

  private final ApiKeyRepository apiKeyRepository;
  private final PasswordEncoder passwordEncoder;
  private final ApiKeyProperties apiKeyProperties;
  private final RegisteredClientRepository registeredClientRepository;
  private final JdbcTemplate jdbcTemplate;
  private final SecureRandom secureRandom = new SecureRandom();

  /**
   * Create a new API key for a user
   */
  public ApiKey createApiKey(User user, String name, Integer expiresInDays) {
    // Generate random API key
    String plainApiKey = generateApiKey();
    String keyHash = passwordEncoder.encode(plainApiKey);

    logger.debug("Creating API key for user: {}", user.getUsername());
    logger.debug(
      "Generated API key with prefix: {}",
      plainApiKey.substring(0, apiKeyProperties.getPrefix().length())
    );

    // Calculate expiration
    OffsetDateTime expiresAt = null;
    if (expiresInDays != null && expiresInDays > 0) {
      expiresAt = OffsetDateTime.now().plusDays(expiresInDays);
    } // Create entity with the HASHED key
    ApiKey apiKeyEntity = ApiKey.builder()
      .user(user)
      .keyHash(keyHash)
      .keyPrefix(apiKeyProperties.getPrefix())
      .name(name)
      .expiresAt(expiresAt)
      .build();
    
    // Save the API key first
    ApiKey saved = apiKeyRepository.save(apiKeyEntity);

    // Now create the OAuth2 service principal for this API key
    createOAuth2ServicePrincipal(saved, plainApiKey, user);

    // Create a temporary field to store the plain key for the response
    saved.setPlainKey(plainApiKey); // This is a transient field

    return saved;
  }

  /**
   * Generate a new API key for a user (used for login sessions)
   */
  public ApiKey generateApiKey(User user, String name) {
    return createApiKey(user, name, null); // No expiration for login session keys
  }

  /**
   * Find API key by key value
   */
  @Transactional(readOnly = true)
  public Optional<ApiKey> findByKeyValue(String apiKeyValue) {
    return validateApiKey(apiKeyValue);
  }

  /**
   * Update last used timestamp for an API key
   */
  @Transactional
  public void updateLastUsed(ApiKey apiKey) {
    apiKey.updateLastUsed();
    apiKeyRepository.save(apiKey);
  }

  /**
   * Get all API keys for a user (without the actual key values)
   */
  @Transactional(readOnly = true)
  public List<ApiKey> getUserApiKeys(User user) {
    return apiKeyRepository.findByUserOrderByCreatedAtDesc(user);
  }

  /**
   * Validate an API key and return the associated user
   */
  @Transactional
  public Optional<ApiKey> validateApiKey(String apiKey) {
    logger.debug(
      "Validating API key with prefix: {}",
      apiKey != null && apiKey.length() >= apiKeyProperties.getPrefix().length()
        ? apiKey.substring(0, apiKeyProperties.getPrefix().length())
        : "null"
    );

    if (apiKey == null || !apiKey.startsWith(apiKeyProperties.getPrefix())) {
      logger.debug(
        "API key is null or doesn't have correct prefix: {}",
        apiKeyProperties.getPrefix()
      );
      return Optional.empty();
    }

    // Find all API keys and check against each hash
    // In a high-performance system, you might want to use a different approach
    List<ApiKey> allKeys = apiKeyRepository.findAll();
    logger.debug("Found {} API keys in database", allKeys.size());

    for (ApiKey key : allKeys) {
      logger.debug("Checking key ID: {}", key.getId());

      boolean matches = passwordEncoder.matches(apiKey, key.getKeyHash());
      logger.debug("Password matches for key {}: {}", key.getId(), matches);

      if (matches) {
        logger.debug("Found matching API key for user: {}", key.getUser().getUsername());
        // Check if expired
        if (key.isExpired()) {
          logger.debug("API key is expired");
          return Optional.empty();
        }

        // Update last used timestamp
        key.updateLastUsed();
        apiKeyRepository.save(key);

        return Optional.of(key);
      }
    }

    logger.debug("No matching API key found");
    return Optional.empty();
  }

  /**
   * Delete an API key and its corresponding OAuth2 client
   */
  @Transactional
  public boolean deleteApiKey(UUID keyId, User user) {
    logger.debug("Attempting to delete API key {} for user {}", keyId, user.getId());
    
    try {
      // Use DELETE with RETURNING to get the client_id and name in one atomic operation
      String deleteQuery = "DELETE FROM api_key WHERE id = ? AND user_id = ? RETURNING client_id, name";
      
      List<Map<String, Object>> deletedRows = jdbcTemplate.queryForList(deleteQuery, keyId, user.getId());
      
      if (!deletedRows.isEmpty()) {
        Map<String, Object> deletedApiKey = deletedRows.get(0);
        String clientId = (String) deletedApiKey.get("client_id");
        String keyName = (String) deletedApiKey.get("name");
        
        logger.info("Deleted API key: {} ({})", keyName, keyId);
        
        // Now delete the OAuth2 registered client if it exists
        if (clientId != null) {
          try {
            int oauthDeletedRows = jdbcTemplate.update(
              "DELETE FROM oauth2_registered_client WHERE client_id = ?", 
              clientId
            );
            if (oauthDeletedRows > 0) {
              logger.info("Deleted OAuth2 client: {}", clientId);
            } else {
              logger.warn("OAuth2 client not found in database: {}", clientId);
            }
          } catch (Exception e) {
            logger.warn("Failed to delete OAuth2 client '{}': {}", clientId, e.getMessage());
            // Don't fail the entire operation if OAuth2 cleanup fails
          }
        }
        
        return true;
      } else {
        logger.warn("API key {} not found or not owned by user {}", keyId, user.getId());
        return false;
      }
      
    } catch (Exception e) {
      logger.error("Error during API key deletion for {} (user {}): {}", keyId, user.getId(), e.getMessage());
      return false;
    }
  }

  /**
   * Clean up expired API keys and their corresponding OAuth2 clients
   */
  @Transactional
  public void cleanupExpiredApiKeys() {
    OffsetDateTime now = OffsetDateTime.now();
    
    // Find expired API keys before deleting them
    List<ApiKey> expiredKeys = apiKeyRepository.findExpiredApiKeys(now);
    
    // Delete corresponding OAuth2 clients for expired API keys
    for (ApiKey expiredKey : expiredKeys) {
      if (expiredKey.getClientId() != null) {
        try {
          int deletedRows = jdbcTemplate.update(
            "DELETE FROM oauth2_registered_client WHERE client_id = ?", 
            expiredKey.getClientId()
          );
          if (deletedRows > 0) {
            logger.debug("Deleted OAuth2 client for expired API key: {}", expiredKey.getClientId());
          }
        } catch (Exception e) {
          logger.warn("Failed to delete OAuth2 client '{}' during cleanup: {}", 
                     expiredKey.getClientId(), e.getMessage());
        }
      }
    }
    
    // Delete expired API keys using JDBC to avoid optimistic locking issues
    int deletedCount = jdbcTemplate.update(
      "DELETE FROM api_key WHERE expires_at IS NOT NULL AND expires_at < ?", 
      now
    );
    
    if (deletedCount > 0) {
      logger.info("Deleted {} expired API keys", deletedCount);
    }
  }

  /**
   * Generate a secure random API key in the format: {prefix}{suffix}.{secret}
   */
  private String generateApiKey() {
    // Generate suffix (identifier part)
    byte[] suffixBytes = new byte[16]; // 16 bytes = ~22 chars in base64url
    secureRandom.nextBytes(suffixBytes);
    String suffix = Base64.getUrlEncoder()
      .withoutPadding()
      .encodeToString(suffixBytes);
    
    // Generate secret (authentication part)  
    byte[] secretBytes = new byte[24]; // 24 bytes = 32 chars in base64url
    secureRandom.nextBytes(secretBytes);
    String secret = Base64.getUrlEncoder()
      .withoutPadding()
      .encodeToString(secretBytes);

    return apiKeyProperties.getPrefix() + suffix + "." + secret;
  }

  /**
   * Create OAuth2 service principal for an API key
   */
  private void createOAuth2ServicePrincipal(ApiKey apiKey, String plainApiKey, User user) {
    // Parse the API key to extract the client ID suffix
    String suffix = extractSuffix(plainApiKey);
    String secret = extractSecret(plainApiKey);
    String clientId = CLIENT_ID_PREFIX + suffix;
    
    logger.info("Creating OAuth2 client '{}' for API key: {}", clientId, apiKey.getName());
    
    // Determine scopes based on user role (simplified for now)
    Set<String> scopes = Set.of("api:read", "api:write");
    
    // Create RegisteredClient for this API key
    RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId(clientId)
            .clientSecret(passwordEncoder.encode(secret)) // Hash the secret
            .clientName("API Key: " + apiKey.getName() + " (" + user.getUsername() + ")")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .scopes(scopeSet -> scopeSet.addAll(scopes))
            .clientSettings(ClientSettings.builder()
                    .requireAuthorizationConsent(false) // No consent for API keys
                    .requireProofKey(false) // No PKCE for client credentials
                    .build())
            .tokenSettings(TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofMinutes(15)) // Short-lived tokens
                    .refreshTokenTimeToLive(Duration.ofHours(1)) // Refresh tokens
                    .reuseRefreshTokens(false) // No refresh tokens for client credentials
                    .build())
            .build();
            
    // Save the registered client
    registeredClientRepository.save(registeredClient);
    
    // Update the API key with OAuth2 info
    apiKey.setClientId(clientId);
    apiKey.setAllowedScopes(String.join(",", scopes));
    apiKeyRepository.save(apiKey);
    
    logger.info("Created OAuth2 client {} for API key: {}", clientId, apiKey.getName());
  }

  /**
   * Extract suffix from API key (everything between prefix and last dot)
   */
  private String extractSuffix(String apiKey) {
    String withoutPrefix = apiKey.substring(apiKeyProperties.getPrefix().length());
    int lastDot = withoutPrefix.lastIndexOf('.');
    if (lastDot == -1) {
      throw new IllegalArgumentException("Invalid API key format: missing secret separator");
    }
    return withoutPrefix.substring(0, lastDot);
  }

  /**
   * Extract secret from API key (everything after the last dot)
   */
  private String extractSecret(String apiKey) {
    int lastDot = apiKey.lastIndexOf('.');
    if (lastDot == -1) {
      throw new IllegalArgumentException("Invalid API key format: missing secret separator");
    }
    return apiKey.substring(lastDot + 1);
  }
}
