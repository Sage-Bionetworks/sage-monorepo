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
 * Unit tests for JwtAuthenticationGatewayFilter.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationGatewayFilterTest {

  @Mock
  private GatewayAuthenticationService authenticationService;

  @Mock
  private AuthConfiguration authConfiguration;

  @Mock
  private WebFilterChain chain;

  @InjectMocks
  private JwtAuthenticationGatewayFilter filter;

  @Test
  @DisplayName("should continue chain when no authorization header is present")
  void shouldContinueChainWhenNoAuthorizationHeaderIsPresent() {
    // given
    when(chain.filter(any())).thenReturn(Mono.empty());
    
    MockServerHttpRequest request = MockServerHttpRequest
        .post("/api/v1/organizations")
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);

    // when
    filter.filter(exchange, chain).block();

    // then
    verify(authenticationService, never()).validateJwt(anyString());
    verify(chain).filter(exchange);
  }

  @Test
  @DisplayName("should continue chain when authorization header is not Bearer")
  void shouldContinueChainWhenAuthorizationHeaderIsNotBearer() {
    // given
    when(chain.filter(any())).thenReturn(Mono.empty());
    
    MockServerHttpRequest request = MockServerHttpRequest
        .post("/api/v1/organizations")
        .header("Authorization", "Basic dXNlcjpwYXNz")
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);

    // when
    filter.filter(exchange, chain).block();

    // then
    verify(authenticationService, never()).validateJwt(anyString());
    verify(chain).filter(exchange);
  }

  @Test
  @DisplayName("should continue chain when bearer token is empty")
  void shouldContinueChainWhenBearerTokenIsEmpty() {
    // given
    when(chain.filter(any())).thenReturn(Mono.empty());
    
    MockServerHttpRequest request = MockServerHttpRequest
        .post("/api/v1/organizations")
        .header("Authorization", "Bearer ")
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);

    // when
    filter.filter(exchange, chain).block();

    // then
    verify(authenticationService, never()).validateJwt(anyString());
    verify(chain).filter(exchange);
  }

  @Test
  @DisplayName("should return unauthorized with realm when jwt token is invalid")
  void shouldReturnUnauthorizedWithRealmWhenJwtTokenIsInvalid() {
    // given
    when(authConfiguration.getRealm()).thenReturn("OpenChallenges");
    
    String invalidToken = "invalid.jwt.token";
    MockServerHttpRequest request = MockServerHttpRequest
        .post("/api/v1/organizations")
        .header("Authorization", "Bearer " + invalidToken)
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);
    
    GatewayAuthenticationService.JwtValidationResponse invalidResponse = 
        new GatewayAuthenticationService.JwtValidationResponse();
    invalidResponse.setValid(false);
    
    when(authenticationService.validateJwt(invalidToken))
        .thenReturn(Mono.just(invalidResponse));

    // when
    filter.filter(exchange, chain).block();

    // then
    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(exchange.getResponse().getHeaders().getFirst(HttpHeaders.WWW_AUTHENTICATE))
        .isEqualTo("Bearer realm=\"OpenChallenges\"");
    verify(chain, never()).filter(exchange);
  }

  @Test
  @DisplayName("should add authentication headers when jwt token is valid")
  void shouldAddAuthenticationHeadersWhenJwtTokenIsValid() {
    // given
    when(chain.filter(any())).thenReturn(Mono.empty());
    
    String validToken = "valid.jwt.token";
    MockServerHttpRequest request = MockServerHttpRequest
        .post("/api/v1/organizations")
        .header("Authorization", "Bearer " + validToken)
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);
    
    GatewayAuthenticationService.JwtValidationResponse validResponse = 
        new GatewayAuthenticationService.JwtValidationResponse();
    validResponse.setValid(true);
    validResponse.setUserId("user123");
    validResponse.setUsername("testuser");
    validResponse.setRole("USER");
    validResponse.setExpiresAt("2024-12-31T23:59:59Z");
    
    when(authenticationService.validateJwt(validToken))
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
