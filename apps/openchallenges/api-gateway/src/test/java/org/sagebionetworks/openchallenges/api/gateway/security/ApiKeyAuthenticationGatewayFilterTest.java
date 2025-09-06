package org.sagebionetworks.openchallenges.api.gateway.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.api.gateway.configuration.AuthConfiguration;
import org.sagebionetworks.openchallenges.api.gateway.service.GatewayAuthenticationService;
import org.sagebionetworks.openchallenges.api.gateway.service.OAuth2TokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

/**
 * Unit tests for ApiKeyAuthenticationGatewayFilter.
 */
@ExtendWith(MockitoExtension.class)
class ApiKeyAuthenticationGatewayFilterTest {

  @Mock
  private GatewayAuthenticationService authenticationService;

  @Mock
  private AuthConfiguration authConfiguration;

  @Mock
  private WebFilterChain chain;

  @InjectMocks
  private ApiKeyAuthenticationGatewayFilter filter;

  @Test
  @DisplayName("should skip validation when request already has authenticated user header from JWT filter")
  void shouldSkipValidationWhenRequestAlreadyHasAuthenticatedUserHeaderFromJwtFilter() {
    // given
    when(chain.filter(any())).thenReturn(Mono.empty());
    
    MockServerHttpRequest request = MockServerHttpRequest
        .post("/api/v1/organizations")
        .header("Authorization", "Bearer jwt-token") // Added by JWT filter - this should trigger skip
        .header("X-API-Key", "some-api-key") // Should be ignored
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);

    // when
    filter.filter(exchange, chain).block();

    // then
    verify(authenticationService, never()).exchangeApiKeyForJwt(anyString(), anyString(), anyString());
    verify(chain).filter(exchange);
  }

  @Test
  @DisplayName("should continue chain when no api key header is present")
  void shouldContinueChainWhenNoApiKeyHeaderIsPresent() {
    // given
    when(chain.filter(any())).thenReturn(Mono.empty());
    
    MockServerHttpRequest request = MockServerHttpRequest
        .post("/api/v1/organizations")
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);

    // when
    filter.filter(exchange, chain).block();

    // then
    verify(chain).filter(exchange);
  }

  @Test
  @DisplayName("should continue chain when api key header is empty")
  void shouldContinueChainWhenApiKeyHeaderIsEmpty() {
    // given
    when(chain.filter(any())).thenReturn(Mono.empty());
    
    MockServerHttpRequest request = MockServerHttpRequest
        .post("/api/v1/organizations")
        .header("X-API-Key", "")
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);

    // when
    filter.filter(exchange, chain).block();

    // then
    verify(chain).filter(exchange);
  }

  @Test
  @DisplayName("should return unauthorized with realm when api key is invalid")
  void shouldReturnUnauthorizedWithRealmWhenApiKeyIsInvalid() {
    // given
    when(authConfiguration.getRealm()).thenReturn("OpenChallenges");
    
    String invalidApiKey = "invalid-key";
    MockServerHttpRequest request = MockServerHttpRequest
        .post("/api/v1/organizations")
        .header("X-API-Key", invalidApiKey)
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);
    
    // Mock OAuth2 exchange failure for invalid API key
    when(authenticationService.exchangeApiKeyForJwt(invalidApiKey, "POST", "/api/v1/organizations"))
        .thenReturn(Mono.error(new GatewayAuthenticationService.AuthenticationException("API key authentication failed", new RuntimeException())));

    // when
    filter.filter(exchange, chain).block();

    // then
    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(exchange.getResponse().getHeaders().getFirst(HttpHeaders.WWW_AUTHENTICATE))
        .isEqualTo("ApiKey realm=\"OpenChallenges\"");
    verify(chain, never()).filter(exchange);
  }

  @Test
  @DisplayName("should add authentication headers when api key is valid")
  void shouldAddAuthenticationHeadersWhenApiKeyIsValid() {
    // given
    when(chain.filter(any())).thenReturn(Mono.empty());
    
    String validApiKey = "valid-key";
    MockServerHttpRequest request = MockServerHttpRequest
        .post("/api/v1/organizations")
        .header("X-API-Key", validApiKey)
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);
    
    // Mock successful OAuth2 token exchange
    OAuth2TokenResponse tokenResponse = new OAuth2TokenResponse(
        "jwt-access-token", 
        "Bearer", 
        3600, 
        "read:orgs create:orgs update:orgs read:profile update:profile"
    );
    
    when(authenticationService.exchangeApiKeyForJwt(validApiKey, "POST", "/api/v1/organizations"))
        .thenReturn(Mono.just(tokenResponse));

    // when
    filter.filter(exchange, chain).block();

    // then
    verify(chain).filter(any());
    assertThat(exchange.getResponse().getStatusCode()).isNull(); // No error status set
    
    // Verify the token exchange was called
    verify(authenticationService).exchangeApiKeyForJwt(validApiKey, "POST", "/api/v1/organizations");
  }
}
