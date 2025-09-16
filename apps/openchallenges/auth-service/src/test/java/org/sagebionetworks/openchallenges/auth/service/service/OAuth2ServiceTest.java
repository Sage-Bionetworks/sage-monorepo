package org.sagebionetworks.openchallenges.auth.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2TokenResponse;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2UserInfo;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ExternalAccount;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class OAuth2ServiceTest {

  @Mock
  private WebClient webClient;

  @Mock
  private OAuth2ConfigurationService oAuth2ConfigurationService;

  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;

  @Mock
  private WebClient.RequestBodySpec requestBodySpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Mock
  private Mono<String> stringMono;

  @Mock
  private Mono<Map> mapMono;

  private OAuth2Service oAuth2Service;

  @BeforeEach
  void setUp() {
    oAuth2Service = new OAuth2Service(webClient, oAuth2ConfigurationService);

    // Setup lenient stubs for WebClient chain to avoid PotentialStubbingProblem
    lenient().when(webClient.post()).thenReturn(requestBodyUriSpec);
    lenient().when(webClient.get()).thenReturn(requestHeadersUriSpec);
    lenient().when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
    lenient().when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
    lenient().when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
    lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
    lenient()
      .when(requestHeadersSpec.header(anyString(), anyString()))
      .thenReturn(requestHeadersSpec);
    lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

    // Setup lenient stubs for OAuth2ConfigurationService discovery endpoints
    lenient()
      .when(oAuth2ConfigurationService.getTokenEndpoint(any()))
      .thenReturn("https://example.com/token");
    lenient()
      .when(oAuth2ConfigurationService.getUserInfoEndpoint(any()))
      .thenReturn("https://example.com/userinfo");

    // Setup lenient stubs for responseSpec methods used in getUserInfo
    lenient().when(responseSpec.bodyToMono(String.class)).thenReturn(stringMono);
    lenient()
      .when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
      .thenReturn(mapMono);

    // Setup lenient stubs for Mono chain
    lenient().when(stringMono.timeout(any(Duration.class))).thenReturn(stringMono);
    lenient().when(stringMono.block()).thenReturn("{}");
    lenient().when(mapMono.timeout(any(Duration.class))).thenReturn(mapMono);
    lenient().when(mapMono.block()).thenReturn(Map.of());
  }

  @Test
  @SuppressWarnings("unchecked")
  void exchangeCodeForToken_GoogleProvider_ShouldReturnTokenResponse() {
    // Given
    ExternalAccount.Provider provider = ExternalAccount.Provider.google;
    String code = "auth-code-123";
    String redirectUri = "https://example.com/callback";

    lenient().when(oAuth2ConfigurationService.getClientId(provider)).thenReturn("google-client-id");
    lenient()
      .when(oAuth2ConfigurationService.getClientSecret(provider))
      .thenReturn("google-client-secret");
    lenient()
      .when(oAuth2ConfigurationService.getTokenEndpoint(provider))
      .thenReturn("https://oauth2.googleapis.com/token");

    // Mock the actual response as a Map (as the service expects)
    Map<String, Object> tokenResponseMap = Map.of(
      "access_token",
      "access-token-123",
      "token_type",
      "Bearer",
      "expires_in",
      3600L,
      "refresh_token",
      "refresh-token-123"
    );
    when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(
      Mono.just(tokenResponseMap)
    );

    // When
    OAuth2TokenResponse result = oAuth2Service.exchangeCodeForToken(provider, code, redirectUri);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getAccessToken()).isEqualTo("access-token-123");
    assertThat(result.getTokenType()).isEqualTo("Bearer");
    assertThat(result.getExpiresIn()).isEqualTo(3600L);
    assertThat(result.getRefreshToken()).isEqualTo("refresh-token-123");
  }

  @Test
  @SuppressWarnings("unchecked")
  void exchangeCodeForToken_SynapseProvider_ShouldReturnTokenResponse() {
    // Given
    ExternalAccount.Provider provider = ExternalAccount.Provider.synapse;
    String code = "auth-code-456";
    String redirectUri = "https://example.com/callback";

    lenient()
      .when(oAuth2ConfigurationService.getClientId(provider))
      .thenReturn("synapse-client-id");
    lenient()
      .when(oAuth2ConfigurationService.getClientSecret(provider))
      .thenReturn("synapse-client-secret");
    lenient()
      .when(oAuth2ConfigurationService.getTokenEndpoint(provider))
      .thenReturn("https://repo-prod.prod.sagebase.org/auth/v1/oauth2/token");

    Map<String, Object> tokenResponseMap = Map.of(
      "access_token",
      "synapse-access-token",
      "token_type",
      "Bearer",
      "expires_in",
      7200L
    );
    when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(
      Mono.just(tokenResponseMap)
    );

    // When
    OAuth2TokenResponse result = oAuth2Service.exchangeCodeForToken(provider, code, redirectUri);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getAccessToken()).isEqualTo("synapse-access-token");
    assertThat(result.getTokenType()).isEqualTo("Bearer");
    assertThat(result.getExpiresIn()).isEqualTo(7200L);
  }

  @Test
  @SuppressWarnings("unchecked")
  void exchangeCodeForToken_WebClientError_ShouldThrowRuntimeException() {
    // Given
    ExternalAccount.Provider provider = ExternalAccount.Provider.google;
    String code = "invalid-code";
    String redirectUri = "https://example.com/callback";

    lenient().when(oAuth2ConfigurationService.getClientId(provider)).thenReturn("google-client-id");
    lenient()
      .when(oAuth2ConfigurationService.getClientSecret(provider))
      .thenReturn("google-client-secret");
    lenient()
      .when(oAuth2ConfigurationService.getTokenEndpoint(provider))
      .thenReturn("https://oauth2.googleapis.com/token");

    when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(
      Mono.error(new WebClientResponseException(400, "Bad Request", null, null, null))
    );

    // When/Then
    assertThatThrownBy(() -> oAuth2Service.exchangeCodeForToken(provider, code, redirectUri))
      .isInstanceOf(RuntimeException.class)
      .hasMessage("Failed to exchange authorization code for token");
  }

  @Test
  @SuppressWarnings("unchecked")
  void getUserInfo_GoogleProvider_ShouldReturnUserInfo() {
    // Given
    ExternalAccount.Provider provider = ExternalAccount.Provider.google;
    String accessToken = "access-token-123";

    lenient()
      .when(oAuth2ConfigurationService.getUserInfoEndpoint(provider))
      .thenReturn("https://www.googleapis.com/oauth2/v2/userinfo");

    // Mock the actual response as a Map (as the service expects)
    Map<String, Object> userInfoMap = Map.of(
      "sub",
      "google-user-sub", // This will become providerId for Google
      "id",
      "google-user-id",
      "email",
      "test@example.com",
      "name",
      "Test User", // This will become displayName
      "given_name",
      "Test", // This will become givenName
      "family_name",
      "User", // This will become familyName
      "picture",
      "https://example.com/picture.jpg",
      "email_verified",
      true
    );

    // Mock the Mono chain for this specific test
    when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(mapMono);
    when(mapMono.timeout(any(Duration.class))).thenReturn(mapMono);
    when(mapMono.block()).thenReturn(userInfoMap);

    // When
    OAuth2UserInfo result = oAuth2Service.getUserInfo(provider, accessToken);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo("google-user-sub"); // For Google, id comes from 'sub'
    assertThat(result.getEmail()).isEqualTo("test@example.com");
    assertThat(result.getName()).isEqualTo("Test User");
    assertThat(result.getGivenName()).isEqualTo("Test");
    assertThat(result.getFamilyName()).isEqualTo("User");
    assertThat(result.getProviderId()).isEqualTo("google-user-sub"); // For Google, providerId comes from 'sub'
    assertThat(result.getDisplayName()).isEqualTo("Test User"); // For Google, displayName comes from 'name'
  }

  @Test
  @SuppressWarnings("unchecked")
  void getUserInfo_SynapseProvider_ShouldReturnUserInfo() {
    // Given
    ExternalAccount.Provider provider = ExternalAccount.Provider.synapse;
    String accessToken = "synapse-access-token";

    lenient()
      .when(oAuth2ConfigurationService.getUserInfoEndpoint(provider))
      .thenReturn("https://repo-prod.prod.sagebase.org/auth/v1/oauth2/userinfo");

    // Mock the actual response as a Map (as the service expects)
    Map<String, Object> userInfoMap = Map.of(
      "sub",
      "synapse-user-sub",
      "ownerId",
      "synapse-owner-id", // Fallback for providerId
      "email",
      "synapse@example.com",
      "user_name",
      "synapse_user", // Service expects user_name (snake_case)
      "displayName",
      "Synapse User", // This field is ignored by service, uses user_name instead
      "given_name",
      "Synapse", // Service expects given_name (snake_case)
      "family_name",
      "User" // Service expects family_name (snake_case)
    );

    // Mock the String response first (for debugging call)
    when(responseSpec.bodyToMono(String.class)).thenReturn(stringMono);
    when(stringMono.timeout(any(Duration.class))).thenReturn(stringMono);
    when(stringMono.block()).thenReturn("{\"sub\":\"synapse-user-sub\"}");

    // Mock the Map response (for actual parsing)
    when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(mapMono);
    when(mapMono.timeout(any(Duration.class))).thenReturn(mapMono);
    when(mapMono.block()).thenReturn(userInfoMap);

    // When
    OAuth2UserInfo result = oAuth2Service.getUserInfo(provider, accessToken);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getSub()).isEqualTo("synapse-user-sub");
    assertThat(result.getEmail()).isEqualTo("synapse@example.com");
    assertThat(result.getUsername()).isEqualTo("synapse_user"); // From user_name field
    assertThat(result.getProviderId()).isEqualTo("synapse-user-sub"); // For Synapse, providerId comes from 'sub'
    assertThat(result.getDisplayName()).isEqualTo("synapse_user"); // For Synapse, displayName comes from 'user_name'
    assertThat(result.getGivenName()).isEqualTo("Synapse"); // From given_name field
    assertThat(result.getFamilyName()).isEqualTo("User"); // From family_name field
  }

  @Test
  @SuppressWarnings("unchecked")
  void getUserInfo_InvalidAccessToken_ShouldThrowRuntimeException() {
    // Given
    ExternalAccount.Provider provider = ExternalAccount.Provider.google;
    String accessToken = "invalid-token";

    lenient()
      .when(oAuth2ConfigurationService.getUserInfoEndpoint(provider))
      .thenReturn("https://www.googleapis.com/oauth2/v2/userinfo");

    when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(
      Mono.error(new WebClientResponseException(401, "Unauthorized", null, null, null))
    );

    // When/Then
    assertThatThrownBy(() -> oAuth2Service.getUserInfo(provider, accessToken))
      .isInstanceOf(RuntimeException.class)
      .hasMessage("Failed to fetch user information");
  }
}
