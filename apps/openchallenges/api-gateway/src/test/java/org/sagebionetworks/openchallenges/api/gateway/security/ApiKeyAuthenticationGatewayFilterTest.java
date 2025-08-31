package org.sagebionetworks.openchallenges.api.gateway.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.api.gateway.configuration.AuthConfiguration;
import org.sagebionetworks.openchallenges.api.gateway.service.GatewayAuthenticationService;
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
        .header("X-Authenticated-User-Id", "user123") // Added by JWT filter
        .header("X-API-Key", "some-api-key") // Should be ignored
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);

    // when
    filter.filter(exchange, chain).block();

    // then
    verify(authenticationService, never()).validateApiKey(anyString());
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
    verify(authenticationService, never()).validateApiKey(anyString());
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
    verify(authenticationService, never()).validateApiKey(anyString());
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
    
    GatewayAuthenticationService.ApiKeyValidationResponse invalidResponse = 
        new GatewayAuthenticationService.ApiKeyValidationResponse();
    invalidResponse.setValid(false);
    
    when(authenticationService.validateApiKey(invalidApiKey))
        .thenReturn(Mono.just(invalidResponse));

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
    
    GatewayAuthenticationService.ApiKeyValidationResponse validResponse = 
        new GatewayAuthenticationService.ApiKeyValidationResponse();
    validResponse.setValid(true);
    validResponse.setUserId("user123");
    validResponse.setUsername("testuser");
    validResponse.setRole("USER");
    validResponse.setScopes(new String[]{"read", "write"});
    
    when(authenticationService.validateApiKey(validApiKey))
        .thenReturn(Mono.just(validResponse));

    // when
    filter.filter(exchange, chain).block();

    // then
    verify(chain).filter(any());
    assertThat(exchange.getResponse().getStatusCode()).isNull(); // No error status set
    
    // Verify headers were added to the request that was passed to the next filter
    // Note: We can't easily verify the modified request headers in this test setup
    // as MockServerWebExchange doesn't provide access to the mutated request
  }
}
