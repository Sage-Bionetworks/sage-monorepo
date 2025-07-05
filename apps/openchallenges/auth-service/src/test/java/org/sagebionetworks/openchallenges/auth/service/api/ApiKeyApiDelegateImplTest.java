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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.sagebionetworks.openchallenges.auth.service.model.dto.CreateApiKeyRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.service.ApiKeyService;
import org.sagebionetworks.openchallenges.auth.service.service.UserService;
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
class ApiKeyApiDelegateImplTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ApiKeyService apiKeyService;

  @MockBean
  private UserService userService;

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

  @Test
  @WithMockUser("testuser")
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
      .andExpect(jsonPath("$.id").value(testApiKey.getId().toString()))
      .andExpect(jsonPath("$.name").value("Test API Key"))
      .andExpect(jsonPath("$.prefix").value("oc_dev_"))
      .andExpect(jsonPath("$.key").value("oc_dev_1234567890abcdef"));
  }

  @Test
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
  void shouldReturnUnauthorizedWhenNotAuthenticatedForListApiKeys() throws Exception {
    // Act & Assert
    mockMvc.perform(get("/v1/auth/api-keys")).andExpect(status().isForbidden());
  }

  @Test
  @WithMockUser("testuser")
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
  void shouldReturnUnauthorizedWhenNotAuthenticatedForDeleteApiKey() throws Exception {
    // Arrange
    UUID keyId = UUID.randomUUID();

    // Act & Assert
    mockMvc.perform(delete("/v1/auth/api-keys/{keyId}", keyId)).andExpect(status().isForbidden());
  }
}
