package org.sagebionetworks.openchallenges.auth.service.util;

import static org.sagebionetworks.openchallenges.auth.service.util.ScopeConstants.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.dto.AuthScopeDto;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Utility class for handling OAuth2 scopes across different authentication methods.
 *
 * This utility consolidates scope handling logic that was previously scattered across
 * multiple components, providing consistent scope extraction, validation, and
 * role-based scope determination.
 *
 * Key features:
 * - Standardized scope extraction from different sources (JWT, API keys)
 * - Role-based scope determination for users
 * - Conversion between scope formats (String, AuthScopeDto, Set)
 * - Consistent logging and error handling
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ScopeUtil {

  private final JwtClaimUtil jwtClaimUtil;

  /**
   * Extract scopes from JWT token and convert to AuthScopeDto list.
   *
   * @param jwt the JWT token
   * @return list of AuthScopeDto objects, empty list if none found
   */
  public List<AuthScopeDto> extractScopesFromJwt(Jwt jwt) {
    List<String> scopes = jwtClaimUtil.extractScopes(jwt);
    return convertToAuthScopeDtos(scopes);
  }

  /**
   * Extract scopes from API key and convert to AuthScopeDto list.
   *
   * @param apiKey the API key entity
   * @return list of AuthScopeDto objects, empty list if none found
   */
  public List<AuthScopeDto> extractScopesFromApiKey(ApiKey apiKey) {
    if (apiKey == null) {
      log.debug("API key is null, cannot extract scopes");
      return Collections.emptyList();
    }

    String allowedScopes = apiKey.getAllowedScopes();
    if (!StringUtils.hasText(allowedScopes)) {
      log.debug("API key '{}' has no allowed scopes", apiKey.getName());
      return Collections.emptyList();
    }

    List<String> scopes = Arrays.stream(allowedScopes.split(","))
      .filter(StringUtils::hasText)
      .map(String::trim)
      .toList();

    log.debug("Extracted {} scopes from API key '{}': {}", scopes.size(), apiKey.getName(), scopes);
    return convertToAuthScopeDtos(scopes);
  }

  /**
   * Determine default scopes for a user based on their role.
   *
   * @param user the user entity
   * @return set of default scopes for the user's role
   */
  public Set<String> getDefaultScopesForUser(User user) {
    if (user == null) {
      log.debug("User is null, returning empty scopes");
      return Collections.emptySet();
    }

    if (user.isAdmin()) {
      Set<String> adminScopes = Set.of(
        READ_PROFILE,
        UPDATE_PROFILE,
        READ_API_KEY,
        CREATE_API_KEY,
        DELETE_API_KEY,
        READ_ORGANIZATIONS,
        CREATE_ORGANIZATIONS,
        UPDATE_ORGANIZATIONS,
        DELETE_ORGANIZATIONS,
        READ_CHALLENGES,
        CREATE_CHALLENGES,
        UPDATE_CHALLENGES,
        DELETE_CHALLENGES,
        READ_CHALLENGES_ANALYTICS,
        READ_CHALLENGE_PLATFORMS,
        CREATE_CHALLENGE_PLATFORMS,
        UPDATE_CHALLENGE_PLATFORMS,
        DELETE_CHALLENGE_PLATFORMS,
        READ_EDAM_CONCEPTS
      );

      log.debug("Assigned {} admin scopes to user '{}'", adminScopes.size(), user.getUsername());
      return adminScopes;
    } else {
      Set<String> userScopes = Set.of(
        READ_PROFILE,
        UPDATE_PROFILE,
        READ_API_KEY,
        CREATE_API_KEY,
        DELETE_API_KEY,
        READ_ORGANIZATIONS,
        CREATE_ORGANIZATIONS,
        UPDATE_ORGANIZATIONS,
        READ_CHALLENGES,
        READ_CHALLENGE_PLATFORMS,
        READ_EDAM_CONCEPTS
      );

      log.debug("Assigned {} user scopes to user '{}'", userScopes.size(), user.getUsername());
      return userScopes;
    }
  }

  /**
   * Convert list of scope strings to AuthScopeDto list.
   *
   * @param scopes the scope strings
   * @return list of AuthScopeDto objects
   */
  public List<AuthScopeDto> convertToAuthScopeDtos(List<String> scopes) {
    if (scopes == null || scopes.isEmpty()) {
      return Collections.emptyList();
    }

    List<AuthScopeDto> scopeDtos = scopes
      .stream()
      .filter(StringUtils::hasText)
      .map(scope -> {
        try {
          return AuthScopeDto.fromValue(scope.trim());
        } catch (IllegalArgumentException e) {
          log.warn("Invalid scope value '{}', skipping", scope);
          return null;
        }
      })
      .filter(scopeDto -> scopeDto != null)
      .collect(Collectors.toList());

    log.debug("Converted {} scope strings to AuthScopeDto objects", scopeDtos.size());
    return scopeDtos;
  }

  /**
   * Convert set of scope strings to comma-separated string.
   *
   * @param scopes the scope strings
   * @return comma-separated scope string
   */
  public String convertScopesToString(Set<String> scopes) {
    if (scopes == null || scopes.isEmpty()) {
      return "";
    }

    String scopeString = String.join(",", scopes);
    log.debug("Converted {} scopes to string: {}", scopes.size(), scopeString);
    return scopeString;
  }

  /**
   * Parse comma-separated scope string to list of strings.
   *
   * @param scopeString the comma-separated scope string
   * @return list of scope strings
   */
  public List<String> parseScopeString(String scopeString) {
    if (!StringUtils.hasText(scopeString)) {
      return Collections.emptyList();
    }

    List<String> scopes = Arrays.stream(scopeString.split(","))
      .filter(StringUtils::hasText)
      .map(String::trim)
      .toList();

    log.debug("Parsed scope string '{}' into {} scopes", scopeString, scopes.size());
    return scopes;
  }

  /**
   * Check if a user has a specific scope through their role.
   *
   * @param user the user entity
   * @param requiredScope the scope to check for
   * @return true if the user's role grants the required scope
   */
  public boolean userHasScope(User user, String requiredScope) {
    if (!StringUtils.hasText(requiredScope)) {
      return false;
    }

    Set<String> userScopes = getDefaultScopesForUser(user);
    boolean hasScope = userScopes.contains(requiredScope.trim());

    log.debug(
      "User '{}' has scope '{}': {}",
      user != null ? user.getUsername() : "null",
      requiredScope,
      hasScope
    );
    return hasScope;
  }

  /**
   * Check if a user has any of the required scopes through their role.
   *
   * @param user the user entity
   * @param requiredScopes the scopes to check for
   * @return true if the user has any of the required scopes
   */
  public boolean userHasAnyScope(User user, String... requiredScopes) {
    if (requiredScopes == null || requiredScopes.length == 0) {
      return false;
    }

    Set<String> userScopes = getDefaultScopesForUser(user);
    for (String requiredScope : requiredScopes) {
      if (StringUtils.hasText(requiredScope) && userScopes.contains(requiredScope.trim())) {
        log.debug(
          "User '{}' has required scope: {}",
          user != null ? user.getUsername() : "null",
          requiredScope
        );
        return true;
      }
    }

    log.debug(
      "User '{}' does not have any of the required scopes: {}",
      user != null ? user.getUsername() : "null",
      Arrays.asList(requiredScopes)
    );
    return false;
  }
}
