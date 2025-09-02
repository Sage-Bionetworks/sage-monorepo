package org.sagebionetworks.openchallenges.auth.service.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.sagebionetworks.openchallenges.auth.service.model.dto.UpdateUserProfileRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.UserProfileDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ValidateApiKeyRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.ValidateApiKeyResponseDto;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.service.ApiKeyService;
import org.sagebionetworks.openchallenges.auth.service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class AuthenticationApiDelegateImplTest {

  @Mock
  private ApiKeyService apiKeyService;

  @Mock
  private UserService userService;

  @Mock
  private Authentication authentication;

  @Mock
  private SecurityContext securityContext;

  private AuthenticationApiDelegateImpl authenticationApiDelegate;

  private User testUser;
  private ApiKey testApiKey;

  @BeforeEach
  void setUp() {
    authenticationApiDelegate = new AuthenticationApiDelegateImpl(
      apiKeyService,
      userService
    );

    // Set up test user
    testUser = User.builder()
      .id(UUID.randomUUID())
      .username("testuser")
      .email("test@example.com")
      .firstName("Test")
      .lastName("User")
      .role(User.Role.user)
      .createdAt(OffsetDateTime.now())
      .updatedAt(OffsetDateTime.now())
      .build();

    // Set up test API key
    testApiKey = new ApiKey();
    testApiKey.setId(UUID.randomUUID());
    testApiKey.setKeyValue("test-api-key-value");
    testApiKey.setUser(testUser);
    testApiKey.setCreatedAt(OffsetDateTime.now());
  }

  @Test
  void validateApiKey_ValidKey_ShouldReturnSuccess() {
    // Arrange
    ValidateApiKeyRequestDto request = new ValidateApiKeyRequestDto()
      .apiKey("test-api-key-value");

    when(apiKeyService.findByKeyValue("test-api-key-value"))
      .thenReturn(Optional.of(testApiKey));

    // Act
    ResponseEntity<ValidateApiKeyResponseDto> response = 
      authenticationApiDelegate.validateApiKey(request);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getValid()).isTrue();
    assertThat(response.getBody().getUsername()).isEqualTo("testuser");
    assertThat(response.getBody().getUserId()).isEqualTo(testUser.getId());

    verify(apiKeyService).updateLastUsed(testApiKey);
  }

  @Test
  void validateApiKey_InvalidKey_ShouldReturnFailure() {
    // Arrange
    ValidateApiKeyRequestDto request = new ValidateApiKeyRequestDto()
      .apiKey("invalid-key");

    when(apiKeyService.findByKeyValue("invalid-key"))
      .thenReturn(Optional.empty());

    // Act
    ResponseEntity<ValidateApiKeyResponseDto> response = 
      authenticationApiDelegate.validateApiKey(request);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getValid()).isFalse();
  }

  @Test
  void getUserProfile_AuthenticatedUser_ShouldReturnProfile() {
    // Arrange
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn("testuser");
    when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));

    // Act
    ResponseEntity<UserProfileDto> response = authenticationApiDelegate.getUserProfile();

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getUsername()).isEqualTo("testuser");
    assertThat(response.getBody().getEmail()).isEqualTo("test@example.com");
    assertThat(response.getBody().getFirstName()).isEqualTo("Test");
    assertThat(response.getBody().getLastName()).isEqualTo("User");
  }

  @Test
  void getUserProfile_UnauthenticatedUser_ShouldReturnUnauthorized() {
    // Arrange
    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(null);

    // Act
    ResponseEntity<UserProfileDto> response = authenticationApiDelegate.getUserProfile();

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
  }

  @Test
  void updateUserProfile_ValidRequest_ShouldUpdateProfile() {
    // Arrange
    UpdateUserProfileRequestDto updateRequest = new UpdateUserProfileRequestDto()
      .firstName("Updated")
      .lastName("Name")
      .bio("Updated bio");

    User updatedUser = User.builder()
      .id(testUser.getId())
      .username(testUser.getUsername())
      .email(testUser.getEmail())
      .firstName("Updated")
      .lastName("Name")
      .bio("Updated bio")
      .role(testUser.getRole())
      .createdAt(testUser.getCreatedAt())
      .updatedAt(OffsetDateTime.now())
      .build();

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.isAuthenticated()).thenReturn(true);
    when(authentication.getName()).thenReturn("testuser");
    when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    when(userService.updateUserProfile(eq(testUser), any(UpdateUserProfileRequestDto.class)))
      .thenReturn(updatedUser);

    // Act
    ResponseEntity<UserProfileDto> response = 
      authenticationApiDelegate.updateUserProfile(updateRequest);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getFirstName()).isEqualTo("Updated");
    assertThat(response.getBody().getLastName()).isEqualTo("Name");
    assertThat(response.getBody().getBio()).isEqualTo("Updated bio");

    verify(userService).updateUserProfile(testUser, updateRequest);
  }
}
