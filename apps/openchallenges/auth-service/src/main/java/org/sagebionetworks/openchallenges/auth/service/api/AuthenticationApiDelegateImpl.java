package org.sagebionetworks.openchallenges.auth.service.api;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.List;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

  /**
   * Get the authenticated user's profile.
   */
  @Override
  public ResponseEntity<UserProfileDto> getUserProfile() {
    log.debug("Getting user profile for authenticated user");

    try {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null || !authentication.isAuthenticated()) {
        log.debug("No authenticated user found");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      // Get the user from the authentication principal (set by JwtAuthenticationFilter)
      Object principal = authentication.getPrincipal();
      if (!(principal instanceof User)) {
        log.debug("Authentication principal is not a User entity: {}", principal.getClass());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      User user = (User) principal;
      log.debug("Getting profile for user with ID: {} and username: {}", user.getId(), user.getUsername());
      
      // Get API key scopes from authentication details if available
      List<AuthScopeDto> scopes = Collections.emptyList();
      Object details = authentication.getDetails();
      if (details instanceof ApiKey) {
        ApiKey apiKey = (ApiKey) details;
        String allowedScopes = apiKey.getAllowedScopes();
        if (allowedScopes != null && !allowedScopes.trim().isEmpty()) {
          String[] scopeArray = allowedScopes.split(",");
          scopes = Arrays.stream(scopeArray)
              .map(scope -> AuthScopeDto.fromValue(scope.trim()))
              .collect(Collectors.toList());
        }
        log.debug("Retrieved {} scopes from API key: {}", scopes.size(), allowedScopes);
      }
      
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
   * Update the authenticated user's profile.
   */
  @Override
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
