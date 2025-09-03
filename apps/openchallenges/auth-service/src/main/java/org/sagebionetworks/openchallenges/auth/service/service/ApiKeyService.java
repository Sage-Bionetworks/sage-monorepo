package org.sagebionetworks.openchallenges.auth.service.service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.sagebionetworks.openchallenges.auth.service.configuration.ApiKeyProperties;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.repository.ApiKeyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ApiKeyService {

  private static final Logger logger = LoggerFactory.getLogger(ApiKeyService.class);

  private final ApiKeyRepository apiKeyRepository;
  private final PasswordEncoder passwordEncoder;
  private final ApiKeyProperties apiKeyProperties;
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
    ApiKey saved = apiKeyRepository.save(apiKeyEntity);

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
   * Delete an API key
   */
  public boolean deleteApiKey(UUID keyId, User user) {
    Optional<ApiKey> apiKey = apiKeyRepository.findById(keyId);
    if (apiKey.isPresent() && apiKey.get().getUser().getId().equals(user.getId())) {
      apiKeyRepository.delete(apiKey.get());
      return true;
    }
    return false;
  }

  /**
   * Clean up expired API keys
   */
  @Transactional
  public void cleanupExpiredApiKeys() {
    apiKeyRepository.deleteExpiredApiKeys(OffsetDateTime.now());
  }

  /**
   * Generate a secure random API key
   */
  private String generateApiKey() {
    byte[] randomBytes = new byte[30]; // 30 bytes = 40 chars in base64
    secureRandom.nextBytes(randomBytes);
    String randomPart = Base64.getUrlEncoder()
      .withoutPadding()
      .encodeToString(randomBytes)
      .substring(0, apiKeyProperties.getLength());

    return apiKeyProperties.getPrefix() + randomPart;
  }
}
