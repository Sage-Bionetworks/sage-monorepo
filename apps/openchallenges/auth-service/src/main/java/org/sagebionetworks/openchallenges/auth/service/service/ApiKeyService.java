package org.sagebionetworks.openchallenges.auth.service.service;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.repository.ApiKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ApiKeyService {

  private final ApiKeyRepository apiKeyRepository;
  private final PasswordEncoder passwordEncoder;
  private final SecureRandom secureRandom;

  private static final String API_KEY_PREFIX = "oc_live_";
  private static final int API_KEY_LENGTH = 40; // characters after prefix

  @Autowired
  public ApiKeyService(ApiKeyRepository apiKeyRepository, PasswordEncoder passwordEncoder) {
    this.apiKeyRepository = apiKeyRepository;
    this.passwordEncoder = passwordEncoder;
    this.secureRandom = new SecureRandom();
  }

  /**
   * Create a new API key for a user
   */
  public ApiKey createApiKey(User user, String name, Integer expiresInDays) {
    // Generate random API key
    String apiKey = generateApiKey();
    String keyHash = passwordEncoder.encode(apiKey);

    // Calculate expiration
    OffsetDateTime expiresAt = null;
    if (expiresInDays != null && expiresInDays > 0) {
      expiresAt = OffsetDateTime.now().plusDays(expiresInDays);
    }

    // Create entity
    ApiKey apiKeyEntity = new ApiKey(user, keyHash, API_KEY_PREFIX, name, expiresAt);
    ApiKey saved = apiKeyRepository.save(apiKeyEntity);

    // Store the plain API key temporarily for returning to client
    // This is the only time the plain key will be available
    saved.setKeyHash(apiKey); // Temporarily store plain key for response

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
  @Transactional(readOnly = true)
  public Optional<ApiKey> validateApiKey(String apiKey) {
    if (apiKey == null || !apiKey.startsWith(API_KEY_PREFIX)) {
      return Optional.empty();
    }

    // Find all API keys and check against each hash
    // In a high-performance system, you might want to use a different approach
    List<ApiKey> allKeys = apiKeyRepository.findAll();

    for (ApiKey key : allKeys) {
      if (passwordEncoder.matches(apiKey, key.getKeyHash())) {
        // Check if expired
        if (key.isExpired()) {
          return Optional.empty();
        }

        // Update last used timestamp
        key.updateLastUsed();
        apiKeyRepository.save(key);

        return Optional.of(key);
      }
    }

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
      .substring(0, API_KEY_LENGTH);

    return API_KEY_PREFIX + randomPart;
  }
}
