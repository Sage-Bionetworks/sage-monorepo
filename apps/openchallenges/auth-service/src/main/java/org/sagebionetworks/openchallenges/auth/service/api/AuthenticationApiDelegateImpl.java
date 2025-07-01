package org.sagebionetworks.openchallenges.auth.service.api;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LoginRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LoginResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ValidateApiKeyRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ValidateApiKeyResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.service.ApiKeyService;
import org.sagebionetworks.openchallenges.auth.service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationApiDelegateImpl implements AuthenticationApiDelegate {

  private final UserService userService;
  private final ApiKeyService apiKeyService;
  private final PasswordEncoder passwordEncoder;

  public AuthenticationApiDelegateImpl(
    UserService userService,
    ApiKeyService apiKeyService,
    PasswordEncoder passwordEncoder
  ) {
    this.userService = userService;
    this.apiKeyService = apiKeyService;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public ResponseEntity<LoginResponseDto> login(LoginRequestDto loginRequestDto) {
    try {
      // TEMPORARY: Generate correct hashes for debugging
      System.out.println("=== DEBUG: Generating BCrypt hashes ===");
      System.out.println("admin123 hash: " + passwordEncoder.encode("admin123"));
      System.out.println("researcher123 hash: " + passwordEncoder.encode("researcher123"));
      System.out.println("=====================================");

      // Find user by username
      Optional<User> userOptional = userService.findByUsername(loginRequestDto.getUsername());

      if (userOptional.isEmpty()) {
        System.out.println("DEBUG: User not found: " + loginRequestDto.getUsername());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      User user = userOptional.get();
      System.out.println("DEBUG: Found user: " + user.getUsername());
      System.out.println("DEBUG: Stored hash: " + user.getPasswordHash());
      System.out.println("DEBUG: Input password: " + loginRequestDto.getPassword());

      // Verify password
      boolean passwordMatches = passwordEncoder.matches(
        loginRequestDto.getPassword(),
        user.getPasswordHash()
      );
      System.out.println("DEBUG: Password matches: " + passwordMatches);

      if (!passwordMatches) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      // Generate a new API key for this login session
      ApiKey apiKey = apiKeyService.generateApiKey(user, "Login Session");

      // Build response
      LoginResponseDto response = new LoginResponseDto()
        .userId(user.getId())
        .username(user.getUsername())
        .role(convertToRoleEnum(user.getRole()))
        .apiKey(apiKey.getPlainKey()); // Use the transient field with the plain key

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      // Log the error in a real application
      System.out.println("DEBUG: Exception in login: " + e.getMessage());
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @Override
  public ResponseEntity<ValidateApiKeyResponseDto> validateApiKey(
    ValidateApiKeyRequestDto validateApiKeyRequestDto
  ) {
    try {
      String apiKeyValue = validateApiKeyRequestDto.getApiKey();
      
      System.out.println("=== DEBUG: API Key Validation ===");
      System.out.println("Input API key: " + apiKeyValue);

      if (apiKeyValue == null || apiKeyValue.trim().isEmpty()) {
        System.out.println("DEBUG: API key is null or empty");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }

      // Find and validate API key
      Optional<ApiKey> apiKeyOptional = apiKeyService.findByKeyValue(apiKeyValue);
      
      System.out.println("DEBUG: API key found in service: " + apiKeyOptional.isPresent());

      if (apiKeyOptional.isEmpty()) {
        // API key not found
        System.out.println("DEBUG: API key not found - returning valid=false");
        ValidateApiKeyResponseDto response = new ValidateApiKeyResponseDto().valid(false);
        return ResponseEntity.ok(response);
      }

      ApiKey apiKey = apiKeyOptional.get();
      System.out.println("DEBUG: Found API key with ID: " + apiKey.getId());
      System.out.println("DEBUG: API key expires at: " + apiKey.getExpiresAt());

      // Check if API key is expired
      if (apiKey.getExpiresAt() != null && apiKey.getExpiresAt().isBefore(OffsetDateTime.now())) {
        System.out.println("DEBUG: API key is expired - returning valid=false");
        ValidateApiKeyResponseDto response = new ValidateApiKeyResponseDto().valid(false);
        return ResponseEntity.ok(response);
      }

      // Update last used timestamp
      apiKeyService.updateLastUsed(apiKey);

      User user = apiKey.getUser();
      System.out.println("DEBUG: API key belongs to user: " + user.getUsername());

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
      // Log the error in a real application
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
      default -> new String[] { "organizations:read" };
    };
  }

  private LoginResponseDto.RoleEnum convertToRoleEnum(User.Role role) {
    return switch (role) {
      case admin -> LoginResponseDto.RoleEnum.ADMIN;
      case user -> LoginResponseDto.RoleEnum.USER;
      case readonly -> LoginResponseDto.RoleEnum.READONLY;
    };
  }

  private ValidateApiKeyResponseDto.RoleEnum convertToValidateRoleEnum(User.Role role) {
    return switch (role) {
      case admin -> ValidateApiKeyResponseDto.RoleEnum.ADMIN;
      case user -> ValidateApiKeyResponseDto.RoleEnum.USER;
      case readonly -> ValidateApiKeyResponseDto.RoleEnum.READONLY;
    };
  }
}
