package org.sagebionetworks.openchallenges.auth.service.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.service.UserLookupService;
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

  private final UserLookupService userLookupService;
  private final JwtClaimUtil jwtClaimUtil;

  /**
   * Get the authenticated user from the current security context.
   *
   * This method handles multiple authentication principal types:
   * - JwtAuthenticationToken (from OAuth2WebAuthenticationFilter with User in details)
   * - JWT (from Spring Security OAuth2 Resource Server for direct API calls)
   * - User entity (legacy support)
   *
   * @return the authenticated User entity, or null if no authenticated user found
   */
  public User getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return null;
    }

    // Handle JwtAuthenticationToken (from OAuth2WebAuthenticationFilter)
    if (
      authentication instanceof
      org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
    ) {
      org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken jwtAuth =
        (org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken) authentication;

      // Check if User is stored in details (set by OAuth2WebAuthenticationFilter)
      Object details = jwtAuth.getDetails();
      if (details instanceof User) {
        return (User) details;
      }

      // Fallback to extracting user from JWT principal
      Jwt jwt = jwtAuth.getToken();
      return extractUserFromJwt(jwt);
    }

    Object principal = authentication.getPrincipal();

    // If principal is a User object (legacy support)
    if (principal instanceof User) {
      return (User) principal;
    }

    // If principal is a JWT (from API gateway or direct JWT authentication)
    if (principal instanceof Jwt) {
      return extractUserFromJwt((Jwt) principal);
    }

    // Unsupported principal type - log for debugging
    log.warn(
      "Unsupported authentication type in security context: {}",
      authentication.getClass().getName()
    );
    return null;
  }

  /**
   * Extract User entity from JWT token by resolving the subject claim.
   * Uses the shared UserLookupService and JwtClaimUtil to eliminate duplicate logic.
   */
  private User extractUserFromJwt(Jwt jwt) {
    if (jwt == null) {
      return null;
    }

    // Use JwtClaimUtil to extract the subject claim consistently
    String subject = jwtClaimUtil.extractSubject(jwt);
    if (subject == null) {
      log.debug("No subject found in JWT");
      return null;
    }

    return userLookupService.findUserBySubject(subject).orElse(null);
  }
}
