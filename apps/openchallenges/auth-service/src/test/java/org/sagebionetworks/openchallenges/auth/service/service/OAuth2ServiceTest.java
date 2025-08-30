package org.sagebionetworks.openchallenges.auth.service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2TokenResponse;
import org.sagebionetworks.openchallenges.auth.service.model.dto.OAuth2UserInfo;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ExternalAccount;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    private OAuth2Service oAuth2Service;

    @BeforeEach
    void setUp() {
        oAuth2Service = new OAuth2Service(webClient, oAuth2ConfigurationService);
    }

    @Test
    void exchangeCodeForToken_GoogleProvider_ShouldReturnTokenResponse() {
        // Given
        ExternalAccount.Provider provider = ExternalAccount.Provider.google;
        String code = "auth-code-123";
        String redirectUri = "https://example.com/callback";
        
        OAuth2TokenResponse expectedResponse = OAuth2TokenResponse.builder()
            .accessToken("access-token-123")
            .tokenType("Bearer")
            .expiresIn(3600L)
            .refreshToken("refresh-token-123")
            .build();

        when(oAuth2ConfigurationService.getClientId(provider)).thenReturn("google-client-id");
        when(oAuth2ConfigurationService.getClientSecret(provider)).thenReturn("google-client-secret");
        
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("https://oauth2.googleapis.com/token")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OAuth2TokenResponse.class)).thenReturn(Mono.just(expectedResponse));

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
    void exchangeCodeForToken_SynapseProvider_ShouldReturnTokenResponse() {
        // Given
        ExternalAccount.Provider provider = ExternalAccount.Provider.synapse;
        String code = "auth-code-456";
        String redirectUri = "https://example.com/callback";
        
        OAuth2TokenResponse expectedResponse = OAuth2TokenResponse.builder()
            .accessToken("synapse-access-token")
            .tokenType("Bearer")
            .expiresIn(7200L)
            .build();

        when(oAuth2ConfigurationService.getClientId(provider)).thenReturn("synapse-client-id");
        when(oAuth2ConfigurationService.getClientSecret(provider)).thenReturn("synapse-client-secret");
        
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("https://repo-prod.prod.sagebase.org/auth/v1/oauth2/token")).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OAuth2TokenResponse.class)).thenReturn(Mono.just(expectedResponse));

        // When
        OAuth2TokenResponse result = oAuth2Service.exchangeCodeForToken(provider, code, redirectUri);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo("synapse-access-token");
        assertThat(result.getTokenType()).isEqualTo("Bearer");
        assertThat(result.getExpiresIn()).isEqualTo(7200L);
    }

    @Test
    void exchangeCodeForToken_WebClientError_ShouldThrowRuntimeException() {
        // Given
        ExternalAccount.Provider provider = ExternalAccount.Provider.google;
        String code = "invalid-code";
        String redirectUri = "https://example.com/callback";

        when(oAuth2ConfigurationService.getClientId(provider)).thenReturn("google-client-id");
        when(oAuth2ConfigurationService.getClientSecret(provider)).thenReturn("google-client-secret");
        
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OAuth2TokenResponse.class))
            .thenReturn(Mono.error(new WebClientResponseException(400, "Bad Request", null, null, null)));

        // When/Then
        assertThatThrownBy(() -> oAuth2Service.exchangeCodeForToken(provider, code, redirectUri))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to exchange authorization code for token");
    }

    @Test
    void getUserInfo_GoogleProvider_ShouldReturnUserInfo() {
        // Given
        ExternalAccount.Provider provider = ExternalAccount.Provider.google;
        String accessToken = "access-token-123";
        
        OAuth2UserInfo expectedUserInfo = OAuth2UserInfo.builder()
            .id("google-user-id")
            .email("test@example.com")
            .name("Test User")
            .givenName("Test")
            .familyName("User")
            .picture("https://example.com/picture.jpg")
            .emailVerified(true)
            .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("https://www.googleapis.com/oauth2/v2/userinfo")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header("Authorization", "Bearer " + accessToken)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OAuth2UserInfo.class)).thenReturn(Mono.just(expectedUserInfo));

        // When
        OAuth2UserInfo result = oAuth2Service.getUserInfo(provider, accessToken);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("google-user-id");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getGivenName()).isEqualTo("Test");
        assertThat(result.getFamilyName()).isEqualTo("User");
        assertThat(result.getProviderId()).isEqualTo("google-user-id");
        assertThat(result.getDisplayName()).isEqualTo("Test User");
    }

    @Test
    void getUserInfo_SynapseProvider_ShouldReturnUserInfo() {
        // Given
        ExternalAccount.Provider provider = ExternalAccount.Provider.synapse;
        String accessToken = "synapse-access-token";
        
        OAuth2UserInfo expectedUserInfo = OAuth2UserInfo.builder()
            .sub("synapse-user-sub")
            .email("synapse@example.com")
            .username("synapse_user")
            .displayName("Synapse User")
            .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("https://repo-prod.prod.sagebase.org/auth/v1/oauth2/userinfo")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header("Authorization", "Bearer " + accessToken)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OAuth2UserInfo.class)).thenReturn(Mono.just(expectedUserInfo));

        // When
        OAuth2UserInfo result = oAuth2Service.getUserInfo(provider, accessToken);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSub()).isEqualTo("synapse-user-sub");
        assertThat(result.getEmail()).isEqualTo("synapse@example.com");
        assertThat(result.getUsername()).isEqualTo("synapse_user");
        assertThat(result.getProviderId()).isEqualTo("synapse-user-sub"); // Should use 'sub' field
        assertThat(result.getDisplayName()).isEqualTo("Synapse User");
    }

    @Test
    void getUserInfo_InvalidAccessToken_ShouldThrowRuntimeException() {
        // Given
        ExternalAccount.Provider provider = ExternalAccount.Provider.google;
        String accessToken = "invalid-token";

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(OAuth2UserInfo.class))
            .thenReturn(Mono.error(new WebClientResponseException(401, "Unauthorized", null, null, null)));

        // When/Then
        assertThatThrownBy(() -> oAuth2Service.getUserInfo(provider, accessToken))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to fetch user information");
    }
}
