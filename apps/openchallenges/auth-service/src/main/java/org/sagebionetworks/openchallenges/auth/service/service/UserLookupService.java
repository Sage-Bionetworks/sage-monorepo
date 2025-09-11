package org.sagebionetworks.openchallenges.auth.service.service;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

/**
 * Shared service for user lookup operations to eliminate duplicate database queries.
 *
 * This service consolidates all user lookup logic that was previously duplicated across
 * multiple components like AuthenticationUtil and OAuth2WebAuthenticationFilter.
 *
 * Key features:
 * - Centralized user lookup logic
 * - Caching to reduce database queries
 * - Comprehensive error handling
 * - Support for multiple identifier types (UUID, username, client_id)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserLookupService {

  private final UserRepository userRepository;
  private final ApiKeyService apiKeyService;

  /**
   * Find user by subject claim from JWT token.
   *
   * This method handles the most common user lookup scenario in authentication flows.
   * It tries multiple lookup strategies in order of efficiency and likelihood:
   * 1. UUID lookup (most common for authenticated users)
   * 2. Username lookup (fallback for legacy tokens)
   * 3. API key client_id lookup (for service accounts)
   *
   * Results are cached to avoid repeated database queries during a single request.
   *
   * @param subject the subject claim from JWT (could be UUID, username, or client_id)
   * @return Optional containing the User if found, empty otherwise
   */
  @Cacheable(value = "userBySubject", key = "#subject")
  public Optional<User> findUserBySubject(String subject) {
    if (subject == null || subject.trim().isEmpty()) {
      log.debug("Subject claim is null or empty");
      return Optional.empty();
    }

    String trimmedSubject = subject.trim();

    // Strategy 1: Try parsing as UUID (most common case - user ID)
    // If it's a valid UUID format, only try UUID lookup (don't fallback to other methods)
    try {
      UUID.fromString(trimmedSubject); // Just validate it's a UUID, don't use the result yet
      return findUserByUuid(trimmedSubject); // If it's a UUID, only try UUID lookup
    } catch (IllegalArgumentException e) {
      log.debug("'{}' is not a valid UUID, trying other strategies", trimmedSubject);
    }

    // Strategy 2: Try finding by username (legacy tokens or OAuth2 provider usernames)
    Optional<User> user = findUserByUsername(trimmedSubject);
    if (user.isPresent()) {
      return user;
    }

    // Strategy 3: Try finding by API key client_id (service accounts)
    user = findUserByClientId(trimmedSubject);
    if (user.isPresent()) {
      return user;
    }

    log.debug("No user found for subject: {}", trimmedSubject);
    return Optional.empty();
  }

  /**
   * Find user by UUID with caching and error handling.
   *
   * @param potentialUuid string that might be a UUID
   * @return Optional containing User if found, empty if not found or not a valid UUID
   */
  @Cacheable(value = "userById", key = "#potentialUuid")
  public Optional<User> findUserByUuid(String potentialUuid) {
    try {
      UUID userId = UUID.fromString(potentialUuid);
      Optional<User> userOpt = userRepository.findById(userId);
      if (userOpt.isPresent()) {
        log.debug("Found user by UUID: {}", userId);
        return userOpt;
      } else {
        log.debug("No user found for UUID: {}", userId);
        return Optional.empty();
      }
    } catch (IllegalArgumentException e) {
      log.debug("'{}' is not a valid UUID", potentialUuid);
      return Optional.empty();
    } catch (DataAccessException e) {
      log.error(
        "Database error while looking up user by UUID '{}': {}",
        potentialUuid,
        e.getMessage()
      );
      return Optional.empty();
    }
  }

  /**
   * Find user by username with caching and error handling.
   *
   * @param username the username to search for
   * @return Optional containing User if found, empty otherwise
   */
  @Cacheable(value = "userByUsername", key = "#username")
  public Optional<User> findUserByUsername(String username) {
    try {
      Optional<User> userOpt = userRepository.findByUsernameIgnoreCase(username);
      if (userOpt.isPresent()) {
        log.debug("Found user by username: {}", username);
        return userOpt;
      } else {
        log.debug("No user found for username: {}", username);
        return Optional.empty();
      }
    } catch (DataAccessException e) {
      log.error(
        "Database error while looking up user by username '{}': {}",
        username,
        e.getMessage()
      );
      return Optional.empty();
    }
  }

  /**
   * Find user by API key client_id with caching and error handling.
   *
   * @param clientId the client_id to search for
   * @return Optional containing User if found, empty otherwise
   */
  @Cacheable(value = "userByClientId", key = "#clientId")
  public Optional<User> findUserByClientId(String clientId) {
    try {
      Optional<ApiKey> apiKeyOpt = apiKeyService.findByClientId(clientId);
      if (apiKeyOpt.isPresent()) {
        User user = apiKeyOpt.get().getUser();
        log.debug("Found user by client_id: {} -> user: {}", clientId, user.getUsername());
        return Optional.of(user);
      } else {
        log.debug("No API key found for client_id: {}", clientId);
        return Optional.empty();
      }
    } catch (Exception e) {
      log.error("Error while looking up user by client_id '{}': {}", clientId, e.getMessage());
      return Optional.empty();
    }
  }

  /**
   * Clear all user lookup caches.
   * This method can be called when user data changes to ensure cache consistency.
   */
  public void clearUserCaches() {
    log.debug("Clearing user lookup caches");
    // Note: In a full implementation, you would inject CacheManager and clear specific caches
    // For now, this is a placeholder for explicit cache management
  }
}
