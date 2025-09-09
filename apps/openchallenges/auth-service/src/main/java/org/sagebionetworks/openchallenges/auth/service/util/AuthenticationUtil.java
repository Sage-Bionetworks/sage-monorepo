package org.sagebionetworks.openchallenges.auth.service.util;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.repository.UserRepository;
import org.sagebionetworks.openchallenges.auth.service.service.ApiKeyService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Utility class for authentication-related operations.
 * 
 * Provides common methods for extracting user information from Spring Security
 * authentication contexts across different authentication flows.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationUtil {

  private final UserRepository userRepository;
  private final ApiKeyService apiKeyService;

  /**
   * Get the authenticated user from the current security context.
   * 
   * This method handles multiple authentication principal types:
   * - User entity (from OAuth2WebAuthenticationFilter for browser login)
   * - JWT (from Spring Security OAuth2 Resource Server for API calls)
   * 
   * @return the authenticated User entity, or null if no authenticated user found
   */
  public User getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return null;
    }

    Object principal = authentication.getPrincipal();

    // If principal is a User object (browser OAuth2 flow via web authentication filter)
    // This happens when OAuth2WebAuthenticationFilter processes JWT from HTTP-only cookies
    // and creates UsernamePasswordAuthenticationToken with User entity as principal
    if (principal instanceof User) {
      return (User) principal;
    }

    // If principal is a JWT (from API gateway or direct JWT authentication)
    if (principal instanceof Jwt) {
      Jwt jwt = (Jwt) principal;

      // Use the subject claim to identify the user
      String subject = jwt.getSubject();
      if (subject != null) {
        // Try to parse as UUID first (user ID)
        try {
          UUID userId = UUID.fromString(subject);
          return userRepository.findById(userId).orElse(null);
        } catch (IllegalArgumentException e) {
          // Not a UUID, assume it's a client_id - look up via API key
          return apiKeyService.findByClientId(subject).map(ApiKey::getUser).orElse(null);
        }
      }

      return null;
    }

    // Unsupported principal type - log for debugging
    log.warn("Unsupported principal type in security context: {}", principal.getClass().getName());
    return null;
  }
}
