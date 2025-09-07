package org.sagebionetworks.openchallenges.auth.service.api;

import java.util.List;
import java.util.UUID;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ApiKeyDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.CreateApiKeyRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.CreateApiKeyResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.service.ApiKeyService;
import org.sagebionetworks.openchallenges.auth.service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyApiDelegateImpl implements ApiKeyApiDelegate {

  private final ApiKeyService apiKeyService;
  private final UserService userService;

  @Override
  @PreAuthorize("hasAuthority('SCOPE_create:api-key')")
  public ResponseEntity<CreateApiKeyResponseDto> createApiKey(
    CreateApiKeyRequestDto createApiKeyRequestDto
  ) {
    log.info("Creating new API key with name: {}", createApiKeyRequestDto.getName());
    try {
      // Get authenticated user from security context
      User authenticatedUser = getAuthenticatedUser();
      if (authenticatedUser == null) {
        log.warn("API key creation failed: no authenticated user");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      // Create the API key
      String keyName = createApiKeyRequestDto.getName();
      Integer expiresInDays = createApiKeyRequestDto.getExpiresIn();

      ApiKey apiKey = apiKeyService.createApiKey(authenticatedUser, keyName, expiresInDays);

      log.info(
        "Successfully created API key '{}' for user: {}",
        keyName,
        authenticatedUser.getUsername()
      );

      // Build response
      CreateApiKeyResponseDto response = new CreateApiKeyResponseDto()
        .id(apiKey.getId())
        .key(apiKey.getPlainKey()) // Use the transient field with the plain key
        .name(apiKey.getName())
        .prefix(apiKey.getKeyPrefix())
        .createdAt(apiKey.getCreatedAt())
        .expiresAt(apiKey.getExpiresAt());

      return ResponseEntity.status(HttpStatus.CREATED)
        .header("Content-Type", "application/json")
        .body(response);
    } catch (Exception e) {
      log.error(
        "Unexpected error while creating API key: {}",
        createApiKeyRequestDto.getName(),
        e
      );
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_delete:api-key')")
  public ResponseEntity<Void> deleteApiKey(UUID keyId) {
    log.info("Deleting API key with ID: {}", keyId);
    try {
      // Get authenticated user from security context
      User authenticatedUser = getAuthenticatedUser();
      if (authenticatedUser == null) {
        log.warn("API key deletion failed: no authenticated user");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      // Delete the API key (only if it belongs to the authenticated user)
      boolean deleted = apiKeyService.deleteApiKey(keyId, authenticatedUser);

      if (deleted) {
        log.info(
          "Successfully deleted API key {} for user: {}",
          keyId,
          authenticatedUser.getUsername()
        );
        return ResponseEntity.noContent().build();
      } else {
        log.warn(
          "API key {} not found or does not belong to user: {}",
          keyId,
          authenticatedUser.getUsername()
        );
        return ResponseEntity.notFound().build();
      }
    } catch (Exception e) {
      log.error("Unexpected error while deleting API key: {}", keyId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @Override
  @PreAuthorize("hasAuthority('SCOPE_read:api-key')")
  public ResponseEntity<List<ApiKeyDto>> listApiKeys() {
    log.info("Listing API keys");
    try {
      // Get authenticated user from security context
      User authenticatedUser = getAuthenticatedUser();
      if (authenticatedUser == null) {
        log.warn("API keys listing failed: no authenticated user");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      // Get user's API keys
      List<ApiKey> apiKeys = apiKeyService.getUserApiKeys(authenticatedUser);

      // Convert to DTOs (without exposing actual key values)
      List<ApiKeyDto> apiKeyDtos = apiKeys
        .stream()
        .map(this::convertToDto)
        .collect(java.util.stream.Collectors.toList());

      log.info(
        "Successfully listed {} API keys for user: {}",
        apiKeyDtos.size(),
        authenticatedUser.getUsername()
      );
      return ResponseEntity.ok(apiKeyDtos);
    } catch (Exception e) {
      log.error("Unexpected error while listing API keys", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * Get the authenticated user from the security context
   */
  private User getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      return null;
    }

    Object principal = authentication.getPrincipal();

    // If principal is a User object (custom authentication)
    if (principal instanceof User) {
      return (User) principal;
    }

    // If principal is a JWT (from API gateway), extract client_id and find the user
    if (principal instanceof Jwt) {
      Jwt jwt = (Jwt) principal;
      String clientId = jwt.getSubject(); // client_id is stored in 'sub' claim
      
      if (clientId != null) {
        // Find the API key by client ID to get the associated user
        return apiKeyService.findByClientId(clientId)
            .map(ApiKey::getUser)
            .orElse(null);
      }
      return null;
    }

    // If principal is UserDetails (Spring Security default), get username and fetch user
    if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
      String username =
        ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
      return userService.findByUsername(username).orElse(null);
    }

    return null;
  }

  private ApiKeyDto convertToDto(ApiKey apiKey) {
    return new ApiKeyDto()
      .id(apiKey.getId())
      .name(apiKey.getName())
      .prefix(apiKey.getKeyPrefix())
      .createdAt(apiKey.getCreatedAt())
      .expiresAt(apiKey.getExpiresAt())
      .lastUsedAt(apiKey.getLastUsedAt());
  }
}
