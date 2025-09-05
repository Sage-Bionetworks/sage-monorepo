package org.sagebionetworks.openchallenges.auth.service.service;

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

import java.util.Map;

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
        
        // Setup lenient stubs for WebClient chain to avoid PotentialStubbingProblem
        lenient().when(webClient.post()).thenReturn(requestBodyUriSpec);
        lenient().when(webClient.get()).thenReturn(requestHeadersUriSpec);
        lenient().when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        lenient().when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        lenient().when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.header(anyString(), anyString())).thenReturn(requestHeadersSpec);
        lenient().when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    @SuppressWarnings("unchecked")
    void exchangeCodeForToken_GoogleProvider_ShouldReturnTokenResponse() {
        // Given
        ExternalAccount.Provider provider = ExternalAccount.Provider.google;
        String code = "auth-code-123";
        String redirectUri = "https://example.com/callback";
        
        lenient().when(oAuth2ConfigurationService.getClientId(provider)).thenReturn("google-client-id");
        lenient().when(oAuth2ConfigurationService.getClientSecret(provider)).thenReturn("google-client-secret");
        
        // Mock the actual response as a Map (as the service expects)
        Map<String, Object> tokenResponseMap = Map.of(
            "access_token", "access-token-123",
            "token_type", "Bearer",
            "expires_in", 3600L,
            "refresh_token", "refresh-token-123"
        );
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(tokenResponseMap));

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
        
        lenient().when(oAuth2ConfigurationService.getClientId(provider)).thenReturn("synapse-client-id");
        lenient().when(oAuth2ConfigurationService.getClientSecret(provider)).thenReturn("synapse-client-secret");
        
        Map<String, Object> tokenResponseMap = Map.of(
            "access_token", "synapse-access-token",
            "token_type", "Bearer",
            "expires_in", 7200L
        );
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(tokenResponseMap));

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
        lenient().when(oAuth2ConfigurationService.getClientSecret(provider)).thenReturn("google-client-secret");
        
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
            .thenReturn(Mono.error(new WebClientResponseException(400, "Bad Request", null, null, null)));

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
        
        // Mock the actual response as a Map (as the service expects)
        Map<String, Object> userInfoMap = Map.of(
            "sub", "google-user-sub",  // This will become providerId for Google
            "id", "google-user-id",
            "email", "test@example.com",
            "name", "Test User",      // This will become displayName
            "given_name", "Test",     // This will become givenName  
            "family_name", "User",    // This will become familyName
            "picture", "https://example.com/picture.jpg",
            "email_verified", true
        );

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(userInfoMap));

        // When
        OAuth2UserInfo result = oAuth2Service.getUserInfo(provider, accessToken);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("google-user-id");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getName()).isEqualTo("Test User");
        assertThat(result.getGivenName()).isEqualTo("Test");
        assertThat(result.getFamilyName()).isEqualTo("User");
        assertThat(result.getProviderId()).isEqualTo("google-user-sub"); // For Google, providerId comes from 'sub'
        assertThat(result.getDisplayName()).isEqualTo("Test User");     // For Google, displayName comes from 'name'
    }

    @Test
    @SuppressWarnings("unchecked")
    void getUserInfo_SynapseProvider_ShouldReturnUserInfo() {
        // Given
        ExternalAccount.Provider provider = ExternalAccount.Provider.synapse;
        String accessToken = "synapse-access-token";
        
        // Mock the actual response as a Map (as the service expects)
        Map<String, Object> userInfoMap = Map.of(
            "sub", "synapse-user-sub",
            "ownerId", "synapse-owner-id",  // This will become providerId for Synapse
            "email", "synapse@example.com",
            "userName", "synapse_user",     // This will become username
            "displayName", "Synapse User",  // This will become displayName  
            "firstName", "Synapse",         // This will become givenName
            "lastName", "User"              // This will become familyName
        );

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class))).thenReturn(Mono.just(userInfoMap));

        // When
        OAuth2UserInfo result = oAuth2Service.getUserInfo(provider, accessToken);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSub()).isEqualTo("synapse-user-sub");
        assertThat(result.getEmail()).isEqualTo("synapse@example.com");
        assertThat(result.getUsername()).isEqualTo("synapse_user");
        assertThat(result.getProviderId()).isEqualTo("synapse-owner-id"); // For Synapse, providerId comes from 'ownerId'
        assertThat(result.getDisplayName()).isEqualTo("Synapse User");
        assertThat(result.getGivenName()).isEqualTo("Synapse");          // For Synapse, givenName comes from 'firstName'
        assertThat(result.getFamilyName()).isEqualTo("User");            // For Synapse, familyName comes from 'lastName'
    }

    @Test
    @SuppressWarnings("unchecked")
    void getUserInfo_InvalidAccessToken_ShouldThrowRuntimeException() {
        // Given
        ExternalAccount.Provider provider = ExternalAccount.Provider.google;
        String accessToken = "invalid-token";

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
            .thenReturn(Mono.error(new WebClientResponseException(401, "Unauthorized", null, null, null)));

        // When/Then
        assertThatThrownBy(() -> oAuth2Service.getUserInfo(provider, accessToken))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Failed to fetch user information");
    }
}
