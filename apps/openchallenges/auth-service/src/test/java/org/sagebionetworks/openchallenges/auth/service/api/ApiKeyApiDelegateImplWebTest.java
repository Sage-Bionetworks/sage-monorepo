package org.sagebionetworks.openchallenges.auth.service.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.sagebionetworks.openchallenges.auth.service.model.dto.CreateApiKeyRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.service.ApiKeyService;
import org.sagebionetworks.openchallenges.auth.service.service.UserService;
import org.sagebionetworks.openchallenges.auth.service.service.JwtService;
import org.sagebionetworks.openchallenges.auth.service.service.OAuth2ConfigurationService;
import org.sagebionetworks.openchallenges.auth.service.repository.UserRepository;
import org.sagebionetworks.openchallenges.auth.service.repository.ExternalAccountRepository;
import org.sagebionetworks.openchallenges.auth.service.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ApiKeyApiDelegateImpl.class)
@Import(org.sagebionetworks.openchallenges.auth.service.configuration.SecurityConfiguration.class)
@TestPropertySource(
  properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false",
  }
)
@MockitoSettings(strictness = Strictness.LENIENT)
class ApiKeyApiDelegateImplWebTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ApiKeyService apiKeyService;

  @MockBean
  private UserService userService;

  @MockBean
  private JwtService jwtService;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private ExternalAccountRepository externalAccountRepository;

  @MockBean
  private RefreshTokenRepository refreshTokenRepository;

  @MockBean
  private OAuth2ConfigurationService oAuth2ConfigurationService;

  @MockBean
  private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

  @Autowired
  private ObjectMapper objectMapper;

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
      .build();
    testApiKey.setPlainKey("oc_dev_1234567890abcdef");

    // Mock userService to return testUser when looking up by username
    when(userService.findByUsername("testuser")).thenReturn(java.util.Optional.of(testUser));
  }

  @Nested
  @DisplayName("Create API Key Tests")
  class CreateApiKeyTests {

    @Test
    @WithMockUser("testuser")
    @DisplayName("should create API key when valid request")
    void shouldCreateApiKeyWhenValidRequest() throws Exception {
      // Arrange
      CreateApiKeyRequestDto request = new CreateApiKeyRequestDto()
        .name("Test API Key")
        .expiresIn(30);

      when(apiKeyService.createApiKey(any(User.class), eq("Test API Key"), eq(30))).thenReturn(
        testApiKey
      );

      // Act & Assert
      mockMvc
        .perform(
          post("/v1/auth/api-keys")
            .with(user("testuser"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(header().string("Content-Type", "application/json"))
        .andExpect(jsonPath("$.id").value(testApiKey.getId().toString()))
        .andExpect(jsonPath("$.name").value("Test API Key"))
        .andExpect(jsonPath("$.prefix").value("oc_dev_"))
        .andExpect(jsonPath("$.key").value("oc_dev_1234567890abcdef"));
    }

    @Test
    @DisplayName("should return unauthorized when not authenticated")
    void shouldReturnUnauthorizedWhenNotAuthenticated() throws Exception {
      // Arrange
      CreateApiKeyRequestDto request = new CreateApiKeyRequestDto()
        .name("Test API Key")
        .expiresIn(30);

      // Act & Assert
      mockMvc
        .perform(
          post("/v1/auth/api-keys")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser("testuser")
    @DisplayName("should create API key without expiration when expiresIn is null")
    void shouldCreateApiKeyWithoutExpirationWhenExpiresInIsNull() throws Exception {
      // Arrange
      CreateApiKeyRequestDto request = new CreateApiKeyRequestDto()
        .name("Permanent API Key");
      // expiresIn is intentionally null

      when(apiKeyService.createApiKey(any(User.class), eq("Permanent API Key"), isNull())).thenReturn(
        testApiKey
      );

      // Act & Assert
      mockMvc
        .perform(
          post("/v1/auth/api-keys")
            .with(user("testuser"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Test API Key"));
    }

    @Test
    @WithMockUser("nonexistentuser")
    @DisplayName("should return unauthorized when user not found in database")
    void shouldReturnUnauthorizedWhenUserNotFoundInDatabase() throws Exception {
      // Arrange
      CreateApiKeyRequestDto request = new CreateApiKeyRequestDto()
        .name("Test API Key")
        .expiresIn(30);

      when(userService.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

      // Act & Assert
      mockMvc
        .perform(
          post("/v1/auth/api-keys")
            .with(user("nonexistentuser"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("testuser")
    @DisplayName("should return internal server error when service throws exception")
    void shouldReturnInternalServerErrorWhenServiceThrowsException() throws Exception {
      // Arrange
      CreateApiKeyRequestDto request = new CreateApiKeyRequestDto()
        .name("Test API Key")
        .expiresIn(30);

      when(apiKeyService.createApiKey(any(User.class), anyString(), anyInt()))
        .thenThrow(new RuntimeException("Database connection failed"));

      // Act & Assert
      mockMvc
        .perform(
          post("/v1/auth/api-keys")
            .with(user("testuser"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser("testuser")
    @DisplayName("should return internal server error when user service throws exception")
    void shouldReturnInternalServerErrorWhenUserServiceThrowsException() throws Exception {
      // Arrange
      CreateApiKeyRequestDto request = new CreateApiKeyRequestDto()
        .name("Test API Key")
        .expiresIn(30);

      when(userService.findByUsername("testuser"))
        .thenThrow(new RuntimeException("Database connection failed"));

      // Act & Assert
      mockMvc
        .perform(
          post("/v1/auth/api-keys")
            .with(user("testuser"))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isInternalServerError());
    }
  }

  @Nested
  @DisplayName("List API Keys Tests")
  class ListApiKeysTests {

    @Test
    @WithMockUser("testuser")
    @DisplayName("should list API keys when authenticated")
    void shouldListApiKeysWhenAuthenticated() throws Exception {
      // Arrange
      when(apiKeyService.getUserApiKeys(any(User.class))).thenReturn(List.of(testApiKey));

      // Act & Assert
      mockMvc
        .perform(get("/v1/auth/api-keys").with(user("testuser")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$[0].id").value(testApiKey.getId().toString()))
        .andExpect(jsonPath("$[0].name").value("Test API Key"))
        .andExpect(jsonPath("$[0].prefix").value("oc_dev_"));
    }

    @Test
    @DisplayName("should return unauthorized when not authenticated for list API keys")
    void shouldReturnUnauthorizedWhenNotAuthenticatedForListApiKeys() throws Exception {
      // Act & Assert
      mockMvc.perform(get("/v1/auth/api-keys")).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser("testuser")
    @DisplayName("should return empty list when user has no API keys")
    void shouldReturnEmptyListWhenUserHasNoApiKeys() throws Exception {
      // Arrange
      when(apiKeyService.getUserApiKeys(any(User.class))).thenReturn(Collections.emptyList());

      // Act & Assert
      mockMvc
        .perform(get("/v1/auth/api-keys").with(user("testuser")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser("nonexistentuser")
    @DisplayName("should return unauthorized when user not found for list API keys")
    void shouldReturnUnauthorizedWhenUserNotFoundForListApiKeys() throws Exception {
      // Arrange
      when(userService.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

      // Act & Assert
      mockMvc
        .perform(get("/v1/auth/api-keys").with(user("nonexistentuser")))
        .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("testuser")
    @DisplayName("should return internal server error when service throws exception for list")
    void shouldReturnInternalServerErrorWhenServiceThrowsExceptionForList() throws Exception {
      // Arrange
      when(apiKeyService.getUserApiKeys(any(User.class)))
        .thenThrow(new RuntimeException("Database connection failed"));

      // Act & Assert
      mockMvc
        .perform(get("/v1/auth/api-keys").with(user("testuser")))
        .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser("testuser")
    @DisplayName("should return multiple API keys when user has several")
    void shouldReturnMultipleApiKeysWhenUserHasSeveral() throws Exception {
      // Arrange
      ApiKey secondApiKey = ApiKey.builder()
        .id(UUID.randomUUID())
        .user(testUser)
        .name("Second API Key")
        .keyPrefix("oc_dev_")
        .keyHash("anotherhash")
        .createdAt(OffsetDateTime.now().minusDays(1))
        .build();

      when(apiKeyService.getUserApiKeys(any(User.class))).thenReturn(List.of(testApiKey, secondApiKey));

      // Act & Assert
      mockMvc
        .perform(get("/v1/auth/api-keys").with(user("testuser")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].name").value("Test API Key"))
        .andExpect(jsonPath("$[1].name").value("Second API Key"));
    }
  }

  @Nested
  @DisplayName("Delete API Key Tests")
  class DeleteApiKeyTests {

    @Test
    @WithMockUser("testuser")
    @DisplayName("should delete API key when valid request")
    void shouldDeleteApiKeyWhenValidRequest() throws Exception {
      // Arrange
      UUID keyId = UUID.randomUUID();
      when(apiKeyService.deleteApiKey(eq(keyId), any(User.class))).thenReturn(true);

      // Act & Assert
      mockMvc
        .perform(delete("/v1/auth/api-keys/{keyId}", keyId).with(user("testuser")))
        .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser("testuser")
    @DisplayName("should return not found when API key not found")
    void shouldReturnNotFoundWhenApiKeyNotFound() throws Exception {
      // Arrange
      UUID keyId = UUID.randomUUID();
      when(apiKeyService.deleteApiKey(eq(keyId), any(User.class))).thenReturn(false);

      // Act & Assert
      mockMvc
        .perform(delete("/v1/auth/api-keys/{keyId}", keyId).with(user("testuser")))
        .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("should return unauthorized when not authenticated for delete API key")
    void shouldReturnUnauthorizedWhenNotAuthenticatedForDeleteApiKey() throws Exception {
      // Arrange
      UUID keyId = UUID.randomUUID();

      // Act & Assert
      mockMvc.perform(delete("/v1/auth/api-keys/{keyId}", keyId)).andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser("nonexistentuser")
    @DisplayName("should return unauthorized when user not found for delete")
    void shouldReturnUnauthorizedWhenUserNotFoundForDelete() throws Exception {
      // Arrange
      UUID keyId = UUID.randomUUID();
      when(userService.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

      // Act & Assert
      mockMvc
        .perform(delete("/v1/auth/api-keys/{keyId}", keyId).with(user("nonexistentuser")))
        .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser("testuser")
    @DisplayName("should return internal server error when service throws exception for delete")
    void shouldReturnInternalServerErrorWhenServiceThrowsExceptionForDelete() throws Exception {
      // Arrange
      UUID keyId = UUID.randomUUID();
      when(apiKeyService.deleteApiKey(eq(keyId), any(User.class)))
        .thenThrow(new RuntimeException("Database connection failed"));

      // Act & Assert
      mockMvc
        .perform(delete("/v1/auth/api-keys/{keyId}", keyId).with(user("testuser")))
        .andExpect(status().isInternalServerError());
    }
  }
}
