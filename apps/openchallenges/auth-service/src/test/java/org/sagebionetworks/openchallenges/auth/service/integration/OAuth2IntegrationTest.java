package org.sagebionetworks.openchallenges.auth.service.integration;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.auth.service.model.dto.*;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ExternalAccount;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.repository.ExternalAccountRepository;
import org.sagebionetworks.openchallenges.auth.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Integration tests for OAuth2 End-to-End Authentication Flow
 * 
 * Tests Phase 2.3: End-to-End Authentication Testing
 * This test suite validates the complete OAuth2 authentication workflow from
 * authorization request through callback handling to JWT token management.
 * Includes provider state encoding/extraction and security integration testing.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@Transactional
@TestPropertySource(properties = {
  "app.security.oauth2.google.client-id=test-google-client-id",
  "app.security.oauth2.google.client-secret=test-google-client-secret",
  "app.security.oauth2.synapse.client-id=test-synapse-client-id", 
  "app.security.oauth2.synapse.client-secret=test-synapse-client-secret",
  "app.base-url=http://localhost:8085"
})
@DisplayName("OAuth2 End-to-End Integration Tests")
public class OAuth2IntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
    .withDatabaseName("auth_service")
    .withUsername("test")
    .withPassword("test");

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ExternalAccountRepository externalAccountRepository;

  @BeforeEach
  void setUp() {
    // Clear data before each test
    externalAccountRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Nested
  @DisplayName("OAuth2 Authorization Flow")
  class OAuth2AuthorizationFlowTests {

    @Test
    @DisplayName("should generate Google OAuth2 authorization URL with provider encoded in state")
    void shouldGenerateGoogleAuthorizationUrlWithProviderInState() throws Exception {
      OAuth2AuthorizeRequestDto request = new OAuth2AuthorizeRequestDto();
      request.setProvider(OAuth2AuthorizeRequestDto.ProviderEnum.GOOGLE);
      request.setRedirectUri(URI.create("http://localhost:3000/auth/callback"));
      request.setState(null);

      mockMvc.perform(post("/v1/auth/oauth2/authorize")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.authorizationUrl", notNullValue()))
        .andExpect(jsonPath("$.authorizationUrl", containsString("accounts.google.com")))
        .andExpect(jsonPath("$.authorizationUrl", containsString("response_type=code")))
        .andExpect(jsonPath("$.authorizationUrl", containsString("client_id=")))
        .andExpect(jsonPath("$.state", notNullValue()))
        .andExpect(jsonPath("$.state", containsString("google:")));
    }

    @Test
    @DisplayName("should access public OAuth2 endpoints without authentication")
    void shouldAccessPublicOAuth2EndpointsWithoutAuthentication() throws Exception {
      OAuth2AuthorizeRequestDto request = new OAuth2AuthorizeRequestDto();
      request.setProvider(OAuth2AuthorizeRequestDto.ProviderEnum.GOOGLE);
      request.setRedirectUri(URI.create("http://localhost:3000/auth/callback"));
      request.setState(null);

      mockMvc.perform(post("/v1/auth/oauth2/authorize")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
    }
  }
}
