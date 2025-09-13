package org.sagebionetworks.openchallenges.auth.service.util;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Utility class for extracting and handling JWT claims in a consistent manner.
 *
 * This utility consolidates all JWT claim extraction logic that was previously
 * scattered across multiple components, providing a single source of truth for
 * claim handling patterns.
 *
 * Key features:
 * - Standardized claim extraction methods
 * - Null-safe operations with proper error handling
 * - Consistent logging for debugging
 * - Support for common JWT claim types
 */
@Component
@Slf4j
public class JwtClaimUtil {

  /**
   * Extract the subject claim from JWT token.
   *
   * @param jwt the JWT token
   * @return the subject claim, or null if not found or JWT is null
   */
  public String extractSubject(Jwt jwt) {
    if (jwt == null) {
      log.debug("JWT is null, cannot extract subject");
      return null;
    }

    String subject = jwt.getSubject();
    if (StringUtils.hasText(subject)) {
      log.debug("Extracted subject from JWT: {}", subject);
      return subject.trim();
    }

    log.debug("No subject claim found in JWT");
    return null;
  }

  /**
   * Extract scopes from JWT token.
   *
   * Looks for scopes in the standard "scp" claim used by Spring Security OAuth2.
   *
   * @param jwt the JWT token
   * @return list of scopes, empty list if none found or JWT is null
   */
  public List<String> extractScopes(Jwt jwt) {
    if (jwt == null) {
      log.debug("JWT is null, cannot extract scopes");
      return Collections.emptyList();
    }

    List<String> scopes = jwt.getClaimAsStringList("scp");
    if (scopes != null && !scopes.isEmpty()) {
      // Trim whitespace from scopes and filter out empty ones
      List<String> trimmedScopes = scopes
        .stream()
        .filter(StringUtils::hasText)
        .map(String::trim)
        .toList();

      log.debug("Extracted {} scopes from JWT: {}", trimmedScopes.size(), trimmedScopes);
      return trimmedScopes;
    }

    log.debug("No scopes found in JWT scp claim");
    return Collections.emptyList();
  }

  /**
   * Extract a string claim from JWT token.
   *
   * @param jwt the JWT token
   * @param claimName the name of the claim to extract
   * @return the claim value, or empty Optional if not found
   */
  public Optional<String> extractStringClaim(Jwt jwt, String claimName) {
    if (jwt == null || !StringUtils.hasText(claimName)) {
      log.debug("JWT is null or claim name is empty, cannot extract claim: {}", claimName);
      return Optional.empty();
    }

    String claimValue = jwt.getClaimAsString(claimName);
    if (StringUtils.hasText(claimValue)) {
      log.debug("Extracted claim '{}' from JWT: {}", claimName, claimValue);
      return Optional.of(claimValue.trim());
    }

    log.debug("Claim '{}' not found or empty in JWT", claimName);
    return Optional.empty();
  }

  /**
   * Extract a list claim from JWT token.
   *
   * @param jwt the JWT token
   * @param claimName the name of the claim to extract
   * @return list of claim values, empty list if not found
   */
  public List<String> extractStringListClaim(Jwt jwt, String claimName) {
    if (jwt == null || !StringUtils.hasText(claimName)) {
      log.debug("JWT is null or claim name is empty, cannot extract claim: {}", claimName);
      return Collections.emptyList();
    }

    List<String> claimValues = jwt.getClaimAsStringList(claimName);
    if (claimValues != null && !claimValues.isEmpty()) {
      // Trim whitespace and filter out empty values
      List<String> trimmedValues = claimValues
        .stream()
        .filter(StringUtils::hasText)
        .map(String::trim)
        .toList();

      log.debug(
        "Extracted {} values for claim '{}' from JWT: {}",
        trimmedValues.size(),
        claimName,
        trimmedValues
      );
      return trimmedValues;
    }

    log.debug("Claim '{}' not found or empty in JWT", claimName);
    return Collections.emptyList();
  }

  /**
   * Check if JWT contains a specific scope.
   *
   * @param jwt the JWT token
   * @param scope the scope to check for
   * @return true if the scope is present, false otherwise
   */
  public boolean hasScope(Jwt jwt, String scope) {
    if (!StringUtils.hasText(scope)) {
      return false;
    }

    List<String> scopes = extractScopes(jwt);
    boolean hasScope = scopes.contains(scope.trim());

    log.debug("Checking if JWT has scope '{}': {}", scope, hasScope);
    return hasScope;
  }

  /**
   * Check if JWT contains any of the specified scopes.
   *
   * @param jwt the JWT token
   * @param requiredScopes the scopes to check for
   * @return true if any of the required scopes are present, false otherwise
   */
  public boolean hasAnyScope(Jwt jwt, String... requiredScopes) {
    if (requiredScopes == null || requiredScopes.length == 0) {
      return false;
    }

    List<String> jwtScopes = extractScopes(jwt);
    for (String requiredScope : requiredScopes) {
      if (StringUtils.hasText(requiredScope) && jwtScopes.contains(requiredScope.trim())) {
        log.debug("JWT has required scope: {}", requiredScope);
        return true;
      }
    }

    log.debug("JWT does not have any of the required scopes: {}", List.of(requiredScopes));
    return false;
  }

  /**
   * Check if JWT contains all of the specified scopes.
   *
   * @param jwt the JWT token
   * @param requiredScopes the scopes to check for
   * @return true if all required scopes are present, false otherwise
   */
  public boolean hasAllScopes(Jwt jwt, String... requiredScopes) {
    if (requiredScopes == null || requiredScopes.length == 0) {
      return true; // No scopes required
    }

    List<String> jwtScopes = extractScopes(jwt);
    for (String requiredScope : requiredScopes) {
      if (!StringUtils.hasText(requiredScope) || !jwtScopes.contains(requiredScope.trim())) {
        log.debug("JWT missing required scope: {}", requiredScope);
        return false;
      }
    }

    log.debug("JWT has all required scopes: {}", List.of(requiredScopes));
    return true;
  }
}
