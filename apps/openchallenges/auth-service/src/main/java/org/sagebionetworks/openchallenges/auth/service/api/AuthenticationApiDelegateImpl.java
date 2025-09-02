package org.sagebionetworks.openchallenges.auth.service.api;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LoginRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LoginResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LogoutRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LogoutResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ValidateApiKeyRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ValidateApiKeyResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.service.ApiKeyService;
import org.sagebionetworks.openchallenges.auth.service.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationApiDelegateImpl implements AuthenticationApiDelegate {

  private static final Logger logger = LoggerFactory.getLogger(AuthenticationApiDelegateImpl.class);

  private final ApiKeyService apiKeyService;
  private final AuthenticationService authenticationService;

  public AuthenticationApiDelegateImpl(
    ApiKeyService apiKeyService,
    AuthenticationService authenticationService
  ) {
    this.apiKeyService = apiKeyService;
    this.authenticationService = authenticationService;
  }

  @Override
  public ResponseEntity<LoginResponseDto> login(LoginRequestDto loginRequestDto) {
    logger.info("JWT login attempt for username: {}", loginRequestDto.getUsername());
    try {
      // Use the AuthenticationService for JWT-based authentication
      LoginResponseDto response = authenticationService.authenticateUser(
        loginRequestDto.getUsername(), 
        loginRequestDto.getPassword()
      );
      
      logger.info("JWT login successful for username: {}", loginRequestDto.getUsername());
      return ResponseEntity.ok(response);
    } catch (RuntimeException e) {
      logger.warn("JWT login failed for username: {} - {}", loginRequestDto.getUsername(), e.getMessage());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    } catch (Exception e) {
      logger.error("Unexpected error during JWT login for username: {}", loginRequestDto.getUsername(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @Override
  public ResponseEntity<ValidateApiKeyResponseDto> validateApiKey(
    ValidateApiKeyRequestDto validateApiKeyRequestDto
  ) {
    logger.debug("API key validation request received");
    try {
      String apiKeyValue = validateApiKeyRequestDto.getApiKey();

      if (apiKeyValue == null || apiKeyValue.trim().isEmpty()) {
        logger.warn("API key validation failed: empty or null API key");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      // Find and validate API key
      Optional<ApiKey> apiKeyOptional = apiKeyService.findByKeyValue(apiKeyValue);

      if (apiKeyOptional.isEmpty()) {
        logger.warn("API key validation failed: API key not found");
        // API key not found
        ValidateApiKeyResponseDto response = new ValidateApiKeyResponseDto().valid(false);
        return ResponseEntity.ok(response);
      }

      ApiKey apiKey = apiKeyOptional.get();

      // Check if API key is expired
      if (apiKey.getExpiresAt() != null && apiKey.getExpiresAt().isBefore(OffsetDateTime.now())) {
        logger.warn(
          "API key validation failed: API key expired for user: {}",
          apiKey.getUser().getUsername()
        );
        ValidateApiKeyResponseDto response = new ValidateApiKeyResponseDto().valid(false);
        return ResponseEntity.ok(response);
      }

      // Update last used timestamp
      apiKeyService.updateLastUsed(apiKey);

      User user = apiKey.getUser();
      logger.info("API key validation successful for user: {}", user.getUsername());

      // Define basic scopes based on user role
      String[] scopes = getDefaultScopes(user.getRole().name());

      // Build success response
      ValidateApiKeyResponseDto response = new ValidateApiKeyResponseDto()
        .valid(true)
        .userId(user.getId())
        .username(user.getUsername())
        .role(convertToValidateRoleEnum(user.getRole()))
        .scopes(Arrays.asList(scopes));

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.error("Unexpected error during API key validation", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  private String[] getDefaultScopes(String role) {
    return switch (role.toLowerCase()) {
      case "admin" -> new String[] {
        "organizations:read",
        "organizations:write",
        "organizations:delete",
        "challenges:read",
        "challenges:write",
        "challenges:delete",
        "users:read",
        "users:write",
      };
      case "user" -> new String[] { "organizations:read", "challenges:read" };
      case "service" -> new String[] { "organizations:write" };
      default -> new String[] { "organizations:read" };
    };
  }

  private ValidateApiKeyResponseDto.RoleEnum convertToValidateRoleEnum(User.Role role) {
    return switch (role) {
      case admin -> ValidateApiKeyResponseDto.RoleEnum.ADMIN;
      case user -> ValidateApiKeyResponseDto.RoleEnum.USER;
      case readonly -> ValidateApiKeyResponseDto.RoleEnum.READONLY;
      case service -> ValidateApiKeyResponseDto.RoleEnum.SERVICE;
    };
  }

  @Override
  public ResponseEntity<LogoutResponseDto> logout(LogoutRequestDto logoutRequestDto) {
    logger.info("User logout request received");
    try {
      int revokedTokens = authenticationService.logout(
        logoutRequestDto.getRefreshToken(),
        logoutRequestDto.getRevokeAllTokens() != null ? logoutRequestDto.getRevokeAllTokens() : false
      );

      LogoutResponseDto response = new LogoutResponseDto()
        .message("Successfully logged out")
        .revokedTokens(revokedTokens);

      logger.info("User successfully logged out, {} tokens revoked", revokedTokens);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.error("Error during logout", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
