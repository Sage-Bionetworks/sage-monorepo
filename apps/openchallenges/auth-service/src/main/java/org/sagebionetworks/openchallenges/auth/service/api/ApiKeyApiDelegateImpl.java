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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ApiKeyApiDelegateImpl implements ApiKeyApiDelegate {

  private static final Logger logger = LoggerFactory.getLogger(ApiKeyApiDelegateImpl.class);

  private final ApiKeyService apiKeyService;
  private final UserService userService;

  public ApiKeyApiDelegateImpl(ApiKeyService apiKeyService, UserService userService) {
    this.apiKeyService = apiKeyService;
    this.userService = userService;
  }

  @Override
  public ResponseEntity<CreateApiKeyResponseDto> createApiKey(
    CreateApiKeyRequestDto createApiKeyRequestDto
  ) {
    logger.info("Creating new API key with name: {}", createApiKeyRequestDto.getName());
    try {
      // Get authenticated user from security context
      User authenticatedUser = getAuthenticatedUser();
      if (authenticatedUser == null) {
        logger.warn("API key creation failed: no authenticated user");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      // Create the API key
      String keyName = createApiKeyRequestDto.getName();
      Integer expiresInDays = createApiKeyRequestDto.getExpiresIn();

      ApiKey apiKey = apiKeyService.createApiKey(authenticatedUser, keyName, expiresInDays);

      logger.info(
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
      logger.error(
        "Unexpected error while creating API key: {}",
        createApiKeyRequestDto.getName(),
        e
      );
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @Override
  public ResponseEntity<Void> deleteApiKey(UUID keyId) {
    logger.info("Deleting API key with ID: {}", keyId);
    try {
      // Get authenticated user from security context
      User authenticatedUser = getAuthenticatedUser();
      if (authenticatedUser == null) {
        logger.warn("API key deletion failed: no authenticated user");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      // Delete the API key (only if it belongs to the authenticated user)
      boolean deleted = apiKeyService.deleteApiKey(keyId, authenticatedUser);

      if (deleted) {
        logger.info(
          "Successfully deleted API key {} for user: {}",
          keyId,
          authenticatedUser.getUsername()
        );
        return ResponseEntity.noContent().build();
      } else {
        logger.warn(
          "API key {} not found or does not belong to user: {}",
          keyId,
          authenticatedUser.getUsername()
        );
        return ResponseEntity.notFound().build();
      }
    } catch (Exception e) {
      logger.error("Unexpected error while deleting API key: {}", keyId, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @Override
  public ResponseEntity<List<ApiKeyDto>> listApiKeys() {
    logger.info("Listing API keys");
    try {
      // Get authenticated user from security context
      User authenticatedUser = getAuthenticatedUser();
      if (authenticatedUser == null) {
        logger.warn("API keys listing failed: no authenticated user");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      // Get user's API keys
      List<ApiKey> apiKeys = apiKeyService.getUserApiKeys(authenticatedUser);

      // Convert to DTOs (without exposing actual key values)
      List<ApiKeyDto> apiKeyDtos = apiKeys
        .stream()
        .map(this::convertToDto)
        .collect(java.util.stream.Collectors.toList());

      logger.info(
        "Successfully listed {} API keys for user: {}",
        apiKeyDtos.size(),
        authenticatedUser.getUsername()
      );
      return ResponseEntity.ok(apiKeyDtos);
    } catch (Exception e) {
      logger.error("Unexpected error while listing API keys", e);
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
