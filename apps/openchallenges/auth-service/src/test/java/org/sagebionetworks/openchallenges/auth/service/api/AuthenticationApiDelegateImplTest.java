package org.sagebionetworks.openchallenges.auth.service.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LoginRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LoginResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ValidateApiKeyRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ValidateApiKeyResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.service.ApiKeyService;
import org.sagebionetworks.openchallenges.auth.service.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AuthenticationApiDelegateImplTest {

  @Mock
  private ApiKeyService apiKeyService;

  @Mock
  private AuthenticationService authenticationService;

  private AuthenticationApiDelegateImpl authenticationApiDelegate;

  private User testUser;
  private ApiKey testApiKey;
  private LoginRequestDto loginRequest;
  private ValidateApiKeyRequestDto validateApiKeyRequest;

  @BeforeEach
  void setUp() {
    authenticationApiDelegate = new AuthenticationApiDelegateImpl(
      apiKeyService,
      authenticationService
    );

    testUser = User.builder()
      .id(UUID.randomUUID())
      .username("testuser")
      .passwordHash("hashedpassword")
      .role(User.Role.user)
      .build();

    testApiKey = ApiKey.builder()
      .id(UUID.randomUUID())
      .user(testUser)
      .name("Test API Key")
      .keyPrefix("oc_dev_")
      .keyHash("hashedkey")
      .createdAt(OffsetDateTime.now())
      .expiresAt(OffsetDateTime.now().plusDays(30))
      .build();
    testApiKey.setPlainKey("oc_dev_1234567890abcdef");

    loginRequest = new LoginRequestDto()
      .username("testuser")
      .password("plainpassword");

    validateApiKeyRequest = new ValidateApiKeyRequestDto()
      .apiKey("oc_dev_1234567890abcdef");
  }

  // ========== Login Tests ==========

  @Test
  void shouldReturnSuccessResponseWhenValidCredentials() {
    // Arrange
    when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches("plainpassword", "hashedpassword")).thenReturn(true);
    when(apiKeyService.generateApiKey(testUser, "Login Session")).thenReturn(testApiKey);

    // Act
    ResponseEntity<LoginResponseDto> response = authenticationApiDelegate.login(loginRequest);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getUserId()).isEqualTo(testUser.getId());
    assertThat(response.getBody().getUsername()).isEqualTo("testuser");
    assertThat(response.getBody().getRole()).isEqualTo(LoginResponseDto.RoleEnum.USER);
    assertThat(response.getBody().getApiKey()).isEqualTo("oc_dev_1234567890abcdef");

    verify(userService).findByUsername("testuser");
    verify(passwordEncoder).matches("plainpassword", "hashedpassword");
    verify(apiKeyService).generateApiKey(testUser, "Login Session");
  }

  @Test
  void shouldLoginReturnUnauthorizedWhenUserNotFound() {
    // Arrange
    when(userService.findByUsername("testuser")).thenReturn(Optional.empty());

    // Act
    ResponseEntity<LoginResponseDto> response = authenticationApiDelegate.login(loginRequest);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).isNull();

    verify(userService).findByUsername("testuser");
    verify(passwordEncoder, never()).matches(any(), any());
    verify(apiKeyService, never()).generateApiKey(any(), any());
  }

  @Test
  void shouldLoginReturnUnauthorizedWhenInvalidPassword() {
    // Arrange
    when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches("plainpassword", "hashedpassword")).thenReturn(false);

    // Act
    ResponseEntity<LoginResponseDto> response = authenticationApiDelegate.login(loginRequest);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).isNull();

    verify(userService).findByUsername("testuser");
    verify(passwordEncoder).matches("plainpassword", "hashedpassword");
    verify(apiKeyService, never()).generateApiKey(any(), any());
  }

  @Test
  void shouldLoginReturnInternalServerErrorWhenExceptionThrown() {
    // Arrange
    when(userService.findByUsername("testuser")).thenThrow(new RuntimeException("Database error"));

    // Act
    ResponseEntity<LoginResponseDto> response = authenticationApiDelegate.login(loginRequest);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNull();

    verify(userService).findByUsername("testuser");
  }

  @Test
  void shouldLoginHandleAdminRoleWhenUserIsAdmin() {
    // Arrange
    User adminUser = User.builder()
      .id(UUID.randomUUID())
      .username("admin")
      .passwordHash("hashedpassword")
      .role(User.Role.admin)
      .build();

    LoginRequestDto adminLoginRequest = new LoginRequestDto()
      .username("admin")
      .password("adminpassword");

    when(userService.findByUsername("admin")).thenReturn(Optional.of(adminUser));
    when(passwordEncoder.matches("adminpassword", "hashedpassword")).thenReturn(true);
    when(apiKeyService.generateApiKey(adminUser, "Login Session")).thenReturn(testApiKey);

    // Act
    ResponseEntity<LoginResponseDto> response = authenticationApiDelegate.login(adminLoginRequest);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRole()).isEqualTo(LoginResponseDto.RoleEnum.ADMIN);
  }

  @Test
  void shouldLoginHandleReadonlyRoleWhenUserIsReadonly() {
    // Arrange
    User readonlyUser = User.builder()
      .id(UUID.randomUUID())
      .username("readonly")
      .passwordHash("hashedpassword")
      .role(User.Role.readonly)
      .build();

    LoginRequestDto readonlyLoginRequest = new LoginRequestDto()
      .username("readonly")
      .password("readonlypassword");

    when(userService.findByUsername("readonly")).thenReturn(Optional.of(readonlyUser));
    when(passwordEncoder.matches("readonlypassword", "hashedpassword")).thenReturn(true);
    when(apiKeyService.generateApiKey(readonlyUser, "Login Session")).thenReturn(testApiKey);

    // Act
    ResponseEntity<LoginResponseDto> response = authenticationApiDelegate.login(readonlyLoginRequest);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRole()).isEqualTo(LoginResponseDto.RoleEnum.READONLY);
  }

  @Test
  void shouldLoginHandleServiceRoleWhenUserIsService() {
    // Arrange
    User serviceUser = User.builder()
      .id(UUID.randomUUID())
      .username("service")
      .passwordHash("hashedpassword")
      .role(User.Role.service)
      .build();

    LoginRequestDto serviceLoginRequest = new LoginRequestDto()
      .username("service")
      .password("servicepassword");

    when(userService.findByUsername("service")).thenReturn(Optional.of(serviceUser));
    when(passwordEncoder.matches("servicepassword", "hashedpassword")).thenReturn(true);
    when(apiKeyService.generateApiKey(serviceUser, "Login Session")).thenReturn(testApiKey);

    // Act
    ResponseEntity<LoginResponseDto> response = authenticationApiDelegate.login(serviceLoginRequest);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getRole()).isEqualTo(LoginResponseDto.RoleEnum.SERVICE);
  }

  // ========== Validate API Key Tests ==========

  @Test
  void shouldValidateApiKeyReturnValidResponseWhenValidApiKey() {
    // Arrange
    when(apiKeyService.findByKeyValue("oc_dev_1234567890abcdef")).thenReturn(Optional.of(testApiKey));

    // Act
    ResponseEntity<ValidateApiKeyResponseDto> response = authenticationApiDelegate.validateApiKey(
      validateApiKeyRequest
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getValid()).isTrue();
    assertThat(response.getBody().getUserId()).isEqualTo(testUser.getId());
    assertThat(response.getBody().getUsername()).isEqualTo("testuser");
    assertThat(response.getBody().getRole()).isEqualTo(ValidateApiKeyResponseDto.RoleEnum.USER);
    assertThat(response.getBody().getScopes()).containsExactlyInAnyOrder(
      "organizations:read",
      "challenges:read"
    );

    verify(apiKeyService).findByKeyValue("oc_dev_1234567890abcdef");
    verify(apiKeyService).updateLastUsed(testApiKey);
  }

  @Test
  void shouldValidateApiKeyReturnInvalidResponseWhenApiKeyNotFound() {
    // Arrange
    when(apiKeyService.findByKeyValue("oc_dev_1234567890abcdef")).thenReturn(Optional.empty());

    // Act
    ResponseEntity<ValidateApiKeyResponseDto> response = authenticationApiDelegate.validateApiKey(
      validateApiKeyRequest
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getValid()).isFalse();

    verify(apiKeyService).findByKeyValue("oc_dev_1234567890abcdef");
    verify(apiKeyService, never()).updateLastUsed(any());
  }

  @Test
  void shouldValidateApiKeyReturnInvalidResponseWhenApiKeyExpired() {
    // Arrange
    ApiKey expiredApiKey = ApiKey.builder()
      .id(UUID.randomUUID())
      .user(testUser)
      .name("Expired API Key")
      .keyPrefix("oc_dev_")
      .keyHash("expiredkey")
      .createdAt(OffsetDateTime.now().minusDays(60))
      .expiresAt(OffsetDateTime.now().minusDays(30))
      .build();

    when(apiKeyService.findByKeyValue("oc_dev_1234567890abcdef")).thenReturn(Optional.of(expiredApiKey));

    // Act
    ResponseEntity<ValidateApiKeyResponseDto> response = authenticationApiDelegate.validateApiKey(
      validateApiKeyRequest
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getValid()).isFalse();

    verify(apiKeyService).findByKeyValue("oc_dev_1234567890abcdef");
    verify(apiKeyService, never()).updateLastUsed(any());
  }

  @Test
  void shouldValidateApiKeyReturnUnauthorizedWhenApiKeyIsNull() {
    // Arrange
    ValidateApiKeyRequestDto nullApiKeyRequest = new ValidateApiKeyRequestDto().apiKey(null);

    // Act
    ResponseEntity<ValidateApiKeyResponseDto> response = authenticationApiDelegate.validateApiKey(
      nullApiKeyRequest
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).isNull();

    verify(apiKeyService, never()).findByKeyValue(any());
  }

  @Test
  void shouldValidateApiKeyReturnUnauthorizedWhenApiKeyIsEmpty() {
    // Arrange
    ValidateApiKeyRequestDto emptyApiKeyRequest = new ValidateApiKeyRequestDto().apiKey("");

    // Act
    ResponseEntity<ValidateApiKeyResponseDto> response = authenticationApiDelegate.validateApiKey(
      emptyApiKeyRequest
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).isNull();

    verify(apiKeyService, never()).findByKeyValue(any());
  }

  @Test
  void shouldValidateApiKeyReturnUnauthorizedWhenApiKeyIsWhitespace() {
    // Arrange
    ValidateApiKeyRequestDto whitespaceApiKeyRequest = new ValidateApiKeyRequestDto().apiKey("   ");

    // Act
    ResponseEntity<ValidateApiKeyResponseDto> response = authenticationApiDelegate.validateApiKey(
      whitespaceApiKeyRequest
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).isNull();

    verify(apiKeyService, never()).findByKeyValue(any());
  }

  @Test
  void shouldValidateApiKeyReturnInternalServerErrorWhenExceptionThrown() {
    // Arrange
    when(apiKeyService.findByKeyValue("oc_dev_1234567890abcdef"))
      .thenThrow(new RuntimeException("Database error"));

    // Act
    ResponseEntity<ValidateApiKeyResponseDto> response = authenticationApiDelegate.validateApiKey(
      validateApiKeyRequest
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNull();

    verify(apiKeyService).findByKeyValue("oc_dev_1234567890abcdef");
  }

  @Test
  void shouldValidateApiKeyReturnValidResponseWithAdminScopesWhenUserIsAdmin() {
    // Arrange
    User adminUser = User.builder()
      .id(UUID.randomUUID())
      .username("admin")
      .passwordHash("hashedpassword")
      .role(User.Role.admin)
      .build();

    ApiKey adminApiKey = ApiKey.builder()
      .id(UUID.randomUUID())
      .user(adminUser)
      .name("Admin API Key")
      .keyPrefix("oc_dev_")
      .keyHash("adminkey")
      .createdAt(OffsetDateTime.now())
      .expiresAt(OffsetDateTime.now().plusDays(30))
      .build();

    when(apiKeyService.findByKeyValue("oc_dev_1234567890abcdef")).thenReturn(Optional.of(adminApiKey));

    // Act
    ResponseEntity<ValidateApiKeyResponseDto> response = authenticationApiDelegate.validateApiKey(
      validateApiKeyRequest
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getValid()).isTrue();
    assertThat(response.getBody().getRole()).isEqualTo(ValidateApiKeyResponseDto.RoleEnum.ADMIN);
    assertThat(response.getBody().getScopes()).containsExactlyInAnyOrder(
      "organizations:read",
      "organizations:write",
      "organizations:delete",
      "challenges:read",
      "challenges:write",
      "challenges:delete",
      "users:read",
      "users:write"
    );
  }

  @Test
  void shouldValidateApiKeyReturnValidResponseWithReadonlyScopesWhenUserIsReadonly() {
    // Test that readonly role gets default scopes since it's not explicitly handled in getDefaultScopes
    // The readonly role should fall through to the default case
    User readonlyUser = User.builder()
      .id(UUID.randomUUID())
      .username("readonly")
      .passwordHash("hashedpassword")
      .role(User.Role.readonly)
      .build();

    ApiKey readonlyApiKey = ApiKey.builder()
      .id(UUID.randomUUID())
      .user(readonlyUser)
      .name("Readonly API Key")
      .keyPrefix("oc_dev_")
      .keyHash("readonlykey")
      .createdAt(OffsetDateTime.now())
      .expiresAt(OffsetDateTime.now().plusDays(30))
      .build();

    when(apiKeyService.findByKeyValue("oc_dev_1234567890abcdef")).thenReturn(Optional.of(readonlyApiKey));

    // Act
    ResponseEntity<ValidateApiKeyResponseDto> response = authenticationApiDelegate.validateApiKey(
      validateApiKeyRequest
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getValid()).isTrue();
    assertThat(response.getBody().getRole()).isEqualTo(ValidateApiKeyResponseDto.RoleEnum.READONLY);
    // readonly role should get default scopes since it's not in the switch statement
    assertThat(response.getBody().getScopes()).containsExactly("organizations:read");
  }

  @Test
  void shouldValidateApiKeyReturnValidResponseWithServiceScopesWhenUserIsService() {
    // Arrange
    User serviceUser = User.builder()
      .id(UUID.randomUUID())
      .username("service")
      .passwordHash("hashedpassword")
      .role(User.Role.service)
      .build();

    ApiKey serviceApiKey = ApiKey.builder()
      .id(UUID.randomUUID())
      .user(serviceUser)
      .name("Service API Key")
      .keyPrefix("oc_dev_")
      .keyHash("servicekey")
      .createdAt(OffsetDateTime.now())
      .expiresAt(OffsetDateTime.now().plusDays(30))
      .build();

    when(apiKeyService.findByKeyValue("oc_dev_1234567890abcdef")).thenReturn(Optional.of(serviceApiKey));

    // Act
    ResponseEntity<ValidateApiKeyResponseDto> response = authenticationApiDelegate.validateApiKey(
      validateApiKeyRequest
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getValid()).isTrue();
    assertThat(response.getBody().getRole()).isEqualTo(ValidateApiKeyResponseDto.RoleEnum.SERVICE);
    assertThat(response.getBody().getScopes()).containsExactly("organizations:write");
  }

  @Test
  void shouldValidateApiKeyHandleApiKeyWithoutExpiration() {
    // Arrange
    ApiKey neverExpiresApiKey = ApiKey.builder()
      .id(UUID.randomUUID())
      .user(testUser)
      .name("Never Expires API Key")
      .keyPrefix("oc_dev_")
      .keyHash("neverexpireskey")
      .createdAt(OffsetDateTime.now())
      .expiresAt(null) // No expiration
      .build();

    when(apiKeyService.findByKeyValue("oc_dev_1234567890abcdef")).thenReturn(Optional.of(neverExpiresApiKey));

    // Act
    ResponseEntity<ValidateApiKeyResponseDto> response = authenticationApiDelegate.validateApiKey(
      validateApiKeyRequest
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getValid()).isTrue();

    verify(apiKeyService).updateLastUsed(neverExpiresApiKey);
  }

  // ========== Edge Cases and Integration Tests ==========

  @Test
  void shouldValidateApiKeyHandleUnknownRole() {
    // This test covers the default case in the switch statement for scopes
    // We can't directly test this without adding an unknown role to the enum,
    // but we can test with a mock that returns a string representation
    User mockUser = mock(User.class);
    when(mockUser.getId()).thenReturn(UUID.randomUUID());
    when(mockUser.getUsername()).thenReturn("unknownroleuser");
    when(mockUser.getRole()).thenReturn(User.Role.readonly); // Using readonly but will mock the string

    ApiKey mockApiKey = mock(ApiKey.class);
    when(mockApiKey.getUser()).thenReturn(mockUser);
    when(mockApiKey.getExpiresAt()).thenReturn(OffsetDateTime.now().plusDays(30));

    when(apiKeyService.findByKeyValue("oc_dev_1234567890abcdef")).thenReturn(Optional.of(mockApiKey));

    // Act
    ResponseEntity<ValidateApiKeyResponseDto> response = authenticationApiDelegate.validateApiKey(
      validateApiKeyRequest
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getValid()).isTrue();
    // Default scopes should be applied (organizations:read)
    assertThat(response.getBody().getScopes()).containsExactly("organizations:read");
  }

  @Test
  void shouldValidateApiKeyReturnInternalServerErrorWhenUserRoleIsNull() {
    // Test that null role causes an exception because user.getRole().name() will throw NPE
    User nullRoleUser = mock(User.class);
    lenient().when(nullRoleUser.getId()).thenReturn(UUID.randomUUID());
    lenient().when(nullRoleUser.getUsername()).thenReturn("nullroleuser");
    when(nullRoleUser.getRole()).thenReturn(null);

    ApiKey nullRoleApiKey = mock(ApiKey.class);
    when(nullRoleApiKey.getUser()).thenReturn(nullRoleUser);
    lenient().when(nullRoleApiKey.getExpiresAt()).thenReturn(OffsetDateTime.now().plusDays(30));

    when(apiKeyService.findByKeyValue("oc_dev_1234567890abcdef")).thenReturn(Optional.of(nullRoleApiKey));

    // Act
    ResponseEntity<ValidateApiKeyResponseDto> response = authenticationApiDelegate.validateApiKey(
      validateApiKeyRequest
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNull();

    // Verify updateLastUsed was called before the NPE occurred
    verify(apiKeyService).updateLastUsed(nullRoleApiKey);
  }

  @Test
  void shouldConstructorInitializeAllDependencies() {
    // This test ensures the constructor properly assigns all dependencies
    // and helps with constructor coverage
    UserService mockUserService = mock(UserService.class);
    ApiKeyService mockApiKeyService = mock(ApiKeyService.class);
    PasswordEncoder mockPasswordEncoder = mock(PasswordEncoder.class);

    AuthenticationApiDelegateImpl delegate = new AuthenticationApiDelegateImpl(
      mockApiKeyService,
      mock(org.sagebionetworks.openchallenges.auth.service.service.AuthenticationService.class)
    );

    // The delegate should be properly initialized
    assertThat(delegate).isNotNull();
    // We can't directly test the private fields, but we can verify they work by calling methods
    when(mockUserService.findByUsername("test")).thenReturn(Optional.empty());

    LoginRequestDto testLogin = new LoginRequestDto().username("test").password("test");
    ResponseEntity<LoginResponseDto> response = delegate.login(testLogin);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    verify(mockUserService).findByUsername("test");
  }
}
