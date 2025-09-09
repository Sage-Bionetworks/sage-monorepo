package org.sagebionetworks.openchallenges.auth.service.api;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.dto.UserProfileDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.UpdateUserProfileRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.UserRoleDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.AuthScopeDto;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.service.UserService;
import org.sagebionetworks.openchallenges.auth.service.service.ApiKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Implementation of the Authentication API delegate.
 * 
 * This implementation focuses on user profile management and API key validation.
 * OAuth2 authentication flows are handled by Spring Authorization Server.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationApiDelegateImpl implements AuthenticationApiDelegate {

  private final UserService userService;
  private final ApiKeyService apiKeyService;

  /**
   * Get the authenticated user's profile.
   */
  @Override
  @PreAuthorize("hasAuthority('SCOPE_read:profile')")
  public ResponseEntity<UserProfileDto> getUserProfile() {
    log.debug("Getting user profile for authenticated user");

    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null || !authentication.isAuthenticated()) {
        log.debug("No authenticated user found");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      // Handle both JWT and User principals
      User user = null;
      List<AuthScopeDto> scopes = Collections.emptyList();
      
      Object principal = authentication.getPrincipal();
      
      if (principal instanceof User) {
        // Direct User principal (from API key authentication filter)
        log.debug("Authentication principal is a User entity");
        user = (User) principal;
        
        // Get API key scopes from authentication details if available
        Object details = authentication.getDetails();
        if (details instanceof ApiKey) {
          ApiKey apiKey = (ApiKey) details;
          scopes = getScopesFromApiKey(apiKey);
        }
        
      } else if (principal instanceof Jwt) {
        // JWT principal (from OAuth2 Resource Server)
        log.debug("Authentication principal is a JWT token");
        Jwt jwt = (Jwt) principal;
        
        // Extract user information from JWT claims
        String subject = jwt.getSubject(); // Can be user UUID or client_id
        String preferredUsername = jwt.getClaimAsString("preferred_username"); // Display name
        
        log.debug("JWT subject: {}, preferred_username: {}", subject, preferredUsername);
        
        // Try to parse subject as UUID first (user ID)
        try {
          UUID userId = UUID.fromString(subject);
          // Subject is a user UUID - this is a regular user authentication
          Optional<User> userOpt = userService.findByUsername(preferredUsername != null ? preferredUsername : subject);
          if (userOpt.isPresent()) {
            user = userOpt.get();
            scopes = getScopesFromJwt(jwt);
            log.debug("Found user by preferred_username for UUID subject: {}", user.getUsername());
          }
        } catch (IllegalArgumentException e) {
          // Subject is not a UUID, assume it's a client_id - find API key and user
          Optional<ApiKey> apiKeyOpt = apiKeyService.findByClientId(subject);
          if (apiKeyOpt.isPresent()) {
            ApiKey apiKey = apiKeyOpt.get();
            user = apiKey.getUser();
            scopes = getScopesFromApiKey(apiKey);
            log.debug("Found user {} for JWT client ID {}", user.getUsername(), subject);
          } else {
            log.debug("No API key found for JWT client ID: {}", subject);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
          }
        }
        
      } else {
        log.debug("Authentication principal is not a User or JWT: {}", principal.getClass());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      if (user == null) {
        log.debug("Could not resolve user from authentication");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
      
      log.debug("Getting profile for user with ID: {} and username: {}", user.getId(), user.getUsername());
      
      UserProfileDto profile = UserProfileDto.builder()
        .id(user.getId().toString())
        .username(user.getUsername())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .bio(user.getBio())
        .website(user.getWebsite() != null ? URI.create(user.getWebsite()) : null)
        .avatarUrl(user.getAvatarUrl() != null ? URI.create(user.getAvatarUrl()) : null)
        .role(UserRoleDto.fromValue(user.getRole().name()))
        .scopes(scopes)
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();

      log.debug("Returning profile for user: {}", user.getUsername());
      return ResponseEntity.ok(profile);

    } catch (Exception e) {
      log.error("Error getting user profile", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Extract scopes from an API key.
   */
  private List<AuthScopeDto> getScopesFromApiKey(ApiKey apiKey) {
    String allowedScopes = apiKey.getAllowedScopes();
    if (allowedScopes != null && !allowedScopes.trim().isEmpty()) {
      String[] scopeArray = allowedScopes.split(",");
      List<AuthScopeDto> scopes = Arrays.stream(scopeArray)
          .map(scope -> AuthScopeDto.fromValue(scope.trim()))
          .collect(Collectors.toList());
      log.debug("Retrieved {} scopes from API key: {}", scopes.size(), allowedScopes);
      return scopes;
    }
    return Collections.emptyList();
  }

  /**
   * Extract scopes from JWT token.
   */
  private List<AuthScopeDto> getScopesFromJwt(Jwt jwt) {
    List<String> scopes = jwt.getClaimAsStringList("scp");
    if (scopes != null && !scopes.isEmpty()) {
      List<AuthScopeDto> scopeDtos = scopes.stream()
          .map(scope -> AuthScopeDto.fromValue(scope.trim()))
          .collect(Collectors.toList());
      log.debug("Retrieved {} scopes from JWT: {}", scopeDtos.size(), scopes);
      return scopeDtos;
    }
    return Collections.emptyList();
  }

  /**
   * Update the authenticated user's profile.
   */
  @Override
  @PreAuthorize("hasAuthority('SCOPE_update:profile')")
  public ResponseEntity<UserProfileDto> updateUserProfile(UpdateUserProfileRequestDto updateRequest) {
    log.debug("Updating user profile for authenticated user");

    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null || !authentication.isAuthenticated()) {
        log.debug("No authenticated user found");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      String username = authentication.getName();
      log.debug("Updating profile for user: {}", username);

      Optional<User> userOpt = userService.findByUsername(username);
      if (userOpt.isEmpty()) {
        log.debug("User not found for update: {}", username);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }

      User updatedUser = userService.updateUserProfile(userOpt.get(), updateRequest);
      UserProfileDto profile = UserProfileDto.builder()
        .id(updatedUser.getId().toString())
        .username(updatedUser.getUsername())
        .email(updatedUser.getEmail())
        .firstName(updatedUser.getFirstName())
        .lastName(updatedUser.getLastName())
        .bio(updatedUser.getBio())
        .website(updatedUser.getWebsite() != null ? URI.create(updatedUser.getWebsite()) : null)
        .avatarUrl(updatedUser.getAvatarUrl() != null ? URI.create(updatedUser.getAvatarUrl()) : null)
        .role(UserRoleDto.fromValue(updatedUser.getRole().name()))
        .createdAt(updatedUser.getCreatedAt())
        .updatedAt(updatedUser.getUpdatedAt())
        .build();

      log.debug("Profile updated for user: {}", username);
      return ResponseEntity.ok(profile);

    } catch (Exception e) {
      log.error("Error updating user profile", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
