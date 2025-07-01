package org.sagebionetworks.openchallenges.auth.service.api;

import java.util.List;
import java.util.Optional;
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
import org.springframework.stereotype.Component;

@Component
public class ApiKeysApiDelegateImpl implements ApiKeysApiDelegate {

  private final ApiKeyService apiKeyService;
  private final UserService userService;

  public ApiKeysApiDelegateImpl(ApiKeyService apiKeyService, UserService userService) {
    this.apiKeyService = apiKeyService;
    this.userService = userService;
  }

  @Override
  public ResponseEntity<CreateApiKeyResponseDto> createApiKey(
    CreateApiKeyRequestDto createApiKeyRequestDto
  ) {
    try {
      // TODO: Get authenticated user from security context
      // For now, we'll use a hardcoded user for testing
      // In a real implementation, you would get this from SecurityContextHolder
      Optional<User> userOptional = userService.findByUsername("admin");

      if (userOptional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      User user = userOptional.get();

      // Create the API key
      String keyName = createApiKeyRequestDto.getName();
      Integer expiresInDays = createApiKeyRequestDto.getExpiresIn();

      ApiKey apiKey = apiKeyService.createApiKey(user, keyName, expiresInDays);

      // Build response
      CreateApiKeyResponseDto response = new CreateApiKeyResponseDto()
        .id(apiKey.getId())
        .key(apiKey.getKeyHash()) // The service temporarily stores the plain key here
        .name(apiKey.getName())
        .prefix(apiKey.getKeyPrefix())
        .createdAt(apiKey.getCreatedAt())
        .expiresAt(apiKey.getExpiresAt());

      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      // Log the error in a real application
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @Override
  public ResponseEntity<Void> deleteApiKey(UUID keyId) {
    try {
      // TODO: Get authenticated user from security context
      // For now, we'll use a hardcoded user for testing
      Optional<User> userOptional = userService.findByUsername("admin");

      if (userOptional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      User user = userOptional.get();

      // Delete the API key
      boolean deleted = apiKeyService.deleteApiKey(keyId, user);

      if (deleted) {
        return ResponseEntity.noContent().build();
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (Exception e) {
      // Log the error in a real application
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @Override
  public ResponseEntity<List<ApiKeyDto>> listApiKeys() {
    try {
      // TODO: Get authenticated user from security context
      // For now, we'll use a hardcoded user for testing
      Optional<User> userOptional = userService.findByUsername("admin");

      if (userOptional.isEmpty()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      User user = userOptional.get();

      // Get user's API keys
      List<ApiKey> apiKeys = apiKeyService.getUserApiKeys(user);

      // Convert to DTOs (without exposing actual key values)
      List<ApiKeyDto> apiKeyDtos = apiKeys
        .stream()
        .map(this::convertToDto)
        .collect(java.util.stream.Collectors.toList());

      return ResponseEntity.ok(apiKeyDtos);
    } catch (Exception e) {
      // Log the error in a real application
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
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
