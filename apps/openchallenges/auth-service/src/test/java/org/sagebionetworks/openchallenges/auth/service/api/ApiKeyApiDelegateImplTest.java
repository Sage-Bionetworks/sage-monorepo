package org.sagebionetworks.openchallenges.auth.service.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.auth.service.model.dto.CreateApiKeyRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.service.ApiKeyService;
import org.sagebionetworks.openchallenges.auth.service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Pure unit tests for ApiKeyApiDelegateImpl focusing on business logic without Spring context.
 * 
 * This test class complements ApiKeyApiDelegateImplWebTest.java (web layer tests with @WebMvcTest)
 * by providing focused unit testing of the delegate's internal logic, authentication handling,
 * and edge cases that are harder to test through the web layer.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ApiKeyApiDelegateImpl Unit Tests")
class ApiKeyApiDelegateImplTest {

  @Mock
  private ApiKeyService apiKeyService;

  @Mock
  private UserService userService;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private ApiKeyApiDelegateImpl apiKeyApiDelegateImpl;

  private User testUser;
  private ApiKey testApiKey;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
      .id(UUID.randomUUID())
      .username("testuser")
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
      .lastUsedAt(OffsetDateTime.now().minusHours(2))
      .build();
    testApiKey.setPlainKey("oc_dev_1234567890abcdef");

    SecurityContextHolder.setContext(securityContext);
  }

  @Nested
  @DisplayName("Authentication Principal Handling")
  class AuthenticationPrincipalHandling {

    @Test
    @DisplayName("should handle User principal directly")
    void shouldHandleUserPrincipalDirectly() {
      // Arrange
      CreateApiKeyRequestDto request = new CreateApiKeyRequestDto()
        .name("Test API Key")
        .expiresIn(30);

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(testUser);
      when(apiKeyService.createApiKey(testUser, "Test API Key", 30)).thenReturn(testApiKey);

      // Act
      ResponseEntity<?> response = apiKeyApiDelegateImpl.createApiKey(request);

      // Assert
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      verify(apiKeyService).createApiKey(testUser, "Test API Key", 30);
      // Should NOT call userService when principal is already a User
      verify(userService, org.mockito.Mockito.never()).findByUsername(anyString());
    }

    @Test
    @DisplayName("should resolve UserDetails principal to User entity")
    void shouldResolveUserDetailsPrincipalToUserEntity() {
      // Arrange
      CreateApiKeyRequestDto request = new CreateApiKeyRequestDto().name("Test");
      
      UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
        .username("testuser")
        .password("password")
        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        .build();

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(userDetails);
      when(userService.findByUsername("testuser")).thenReturn(Optional.of(testUser));
      when(apiKeyService.createApiKey(testUser, "Test", null)).thenReturn(testApiKey);

      // Act
      ResponseEntity<?> response = apiKeyApiDelegateImpl.createApiKey(request);

      // Assert
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      verify(userService).findByUsername("testuser");
      verify(apiKeyService).createApiKey(testUser, "Test", null);
    }

    @Test
    @DisplayName("should reject unknown principal types")
    void shouldRejectUnknownPrincipalTypes() {
      // Arrange
      CreateApiKeyRequestDto request = new CreateApiKeyRequestDto().name("Test");

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn("invalid_principal_type");

      // Act
      ResponseEntity<?> response = apiKeyApiDelegateImpl.createApiKey(request);

      // Assert
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
      verify(userService, org.mockito.Mockito.never()).findByUsername(anyString());
      verify(apiKeyService, org.mockito.Mockito.never()).createApiKey(any(), anyString(), any());
    }

    @Test
    @DisplayName("should handle missing authentication")
    void shouldHandleMissingAuthentication() {
      // Arrange
      CreateApiKeyRequestDto request = new CreateApiKeyRequestDto().name("Test");
      when(securityContext.getAuthentication()).thenReturn(null);

      // Act
      ResponseEntity<?> response = apiKeyApiDelegateImpl.createApiKey(request);

      // Assert
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("should handle UserDetails principal when user not found in database")
    void shouldHandleUserDetailsPrincipalWhenUserNotFoundInDatabase() {
      // Arrange
      CreateApiKeyRequestDto request = new CreateApiKeyRequestDto().name("Test");
      
      UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
        .username("nonexistent")
        .password("password")
        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        .build();

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(userDetails);
      when(userService.findByUsername("nonexistent")).thenReturn(Optional.empty());

      // Act
      ResponseEntity<?> response = apiKeyApiDelegateImpl.createApiKey(request);

      // Assert
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
      verify(userService).findByUsername("nonexistent");
      verify(apiKeyService, org.mockito.Mockito.never()).createApiKey(any(), anyString(), any());
    }
  }

  @Nested
  @DisplayName("Exception Handling")
  class ExceptionHandling {

    @Test
    @DisplayName("should handle service exceptions during API key creation")
    void shouldHandleServiceExceptionsDuringApiKeyCreation() {
      // Arrange
      CreateApiKeyRequestDto request = new CreateApiKeyRequestDto().name("Test");

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(testUser);
      when(apiKeyService.createApiKey(testUser, "Test", null))
        .thenThrow(new RuntimeException("Database connection failed"));

      // Act
      ResponseEntity<?> response = apiKeyApiDelegateImpl.createApiKey(request);

      // Assert
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("should handle user service exceptions during UserDetails resolution")
    void shouldHandleUserServiceExceptionsDuringUserDetailsResolution() {
      // Arrange
      CreateApiKeyRequestDto request = new CreateApiKeyRequestDto().name("Test");
      
      UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
        .username("testuser")
        .password("password")
        .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
        .build();

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(userDetails);
      when(userService.findByUsername("testuser"))
        .thenThrow(new RuntimeException("Database connection failed"));

      // Act
      ResponseEntity<?> response = apiKeyApiDelegateImpl.createApiKey(request);

      // Assert
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("should handle exceptions during API key listing")
    void shouldHandleExceptionsDuringApiKeyListing() {
      // Arrange
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(testUser);
      when(apiKeyService.getUserApiKeys(testUser))
        .thenThrow(new RuntimeException("Database connection failed"));

      // Act
      ResponseEntity<?> response = apiKeyApiDelegateImpl.listApiKeys();

      // Assert
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("should handle exceptions during API key deletion")
    void shouldHandleExceptionsDuringApiKeyDeletion() {
      // Arrange
      UUID keyId = UUID.randomUUID();
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(testUser);
      when(apiKeyService.deleteApiKey(keyId, testUser))
        .thenThrow(new RuntimeException("Database connection failed"));

      // Act
      ResponseEntity<Void> response = apiKeyApiDelegateImpl.deleteApiKey(keyId);

      // Assert
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Nested
  @DisplayName("Business Logic Edge Cases")
  class BusinessLogicEdgeCases {

    @Test
    @DisplayName("should handle null expiration correctly")
    void shouldHandleNullExpirationCorrectly() {
      // Arrange
      CreateApiKeyRequestDto request = new CreateApiKeyRequestDto()
        .name("Permanent Key");
      // expiresIn is null

      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(testUser);
      when(apiKeyService.createApiKey(testUser, "Permanent Key", null)).thenReturn(testApiKey);

      // Act
      ResponseEntity<?> response = apiKeyApiDelegateImpl.createApiKey(request);

      // Assert
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      verify(apiKeyService).createApiKey(testUser, "Permanent Key", null);
    }

    @Test
    @DisplayName("should handle empty API key list")
    void shouldHandleEmptyApiKeyList() {
      // Arrange
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(testUser);
      when(apiKeyService.getUserApiKeys(testUser)).thenReturn(Collections.emptyList());

      // Act
      ResponseEntity<?> response = apiKeyApiDelegateImpl.listApiKeys();

      // Assert
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      verify(apiKeyService).getUserApiKeys(testUser);
    }

    @Test
    @DisplayName("should handle API key deletion when key not found")
    void shouldHandleApiKeyDeletionWhenKeyNotFound() {
      // Arrange
      UUID keyId = UUID.randomUUID();
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(testUser);
      when(apiKeyService.deleteApiKey(keyId, testUser)).thenReturn(false);

      // Act
      ResponseEntity<Void> response = apiKeyApiDelegateImpl.deleteApiKey(keyId);

      // Assert
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      verify(apiKeyService).deleteApiKey(keyId, testUser);
    }

    @Test
    @DisplayName("should handle successful API key deletion")
    void shouldHandleSuccessfulApiKeyDeletion() {
      // Arrange
      UUID keyId = UUID.randomUUID();
      when(securityContext.getAuthentication()).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(testUser);
      when(apiKeyService.deleteApiKey(keyId, testUser)).thenReturn(true);

      // Act
      ResponseEntity<Void> response = apiKeyApiDelegateImpl.deleteApiKey(keyId);

      // Assert
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
      verify(apiKeyService).deleteApiKey(keyId, testUser);
    }
  }
}
