package org.sagebionetworks.openchallenges.api.gateway.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

/**
 * Integration tests for the complete authentication filter chain.
 * Tests the interaction between JWT and API Key filters.
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationFilterChainIntegrationTest {

  private final GatewayAuthenticationService authenticationService = mock(GatewayAuthenticationService.class);
  private final AuthConfiguration authConfiguration = mock(AuthConfiguration.class);
  private final WebFilterChain chain = mock(WebFilterChain.class);

  @Test
  @DisplayName("should authenticate with JWT when both JWT and API key are present")
  void shouldAuthenticateWithJwtWhenBothJwtAndApiKeyArePresent() {
    // given
    when(chain.filter(any())).thenReturn(Mono.empty());
    when(authConfiguration.getRealm()).thenReturn("OpenChallenges");
    
    JwtAuthenticationGatewayFilter jwtFilter = 
        new JwtAuthenticationGatewayFilter(authenticationService, authConfiguration);
    ApiKeyAuthenticationGatewayFilter apiKeyFilter = 
        new ApiKeyAuthenticationGatewayFilter(authenticationService, authConfiguration);
    
    String validToken = "valid.jwt.token";
    MockServerHttpRequest request = MockServerHttpRequest
        .post("/api/v1/organizations")
        .header("Authorization", "Bearer " + validToken)
        .header("X-API-Key", "some-api-key") // This should be processed by API key filter
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);
    
    GatewayAuthenticationService.JwtValidationResponse validJwtResponse = 
        new GatewayAuthenticationService.JwtValidationResponse();
    validJwtResponse.setValid(true);
    validJwtResponse.setUserId("jwt-user-123");
    validJwtResponse.setUsername("jwtuser");
    validJwtResponse.setRole("USER");
    
    when(authenticationService.validateJwt(validToken))
        .thenReturn(Mono.just(validJwtResponse));

    // Simulate filter chain: JWT filter -> API key filter -> downstream
    WebFilterChain jwtToApiKeyChain = (ex) -> apiKeyFilter.filter(ex, chain);

    // when
    jwtFilter.filter(exchange, jwtToApiKeyChain).block();

    // then
    verify(authenticationService).validateJwt(validToken);
    verify(authenticationService, never()).validateApiKey(anyString()); // API key should be skipped due to JWT auth headers
    verify(chain).filter(any()); // Should reach the end of the chain
    assertThat(exchange.getResponse().getStatusCode()).isNull(); // No error status
  }

  @Test
  @DisplayName("should fall back to API key authentication when no JWT token")
  void shouldFallBackToApiKeyAuthenticationWhenNoJwtToken() {
    // given
    when(chain.filter(any())).thenReturn(Mono.empty());
    when(authConfiguration.getRealm()).thenReturn("OpenChallenges");
    
    JwtAuthenticationGatewayFilter jwtFilter = 
        new JwtAuthenticationGatewayFilter(authenticationService, authConfiguration);
    ApiKeyAuthenticationGatewayFilter apiKeyFilter = 
        new ApiKeyAuthenticationGatewayFilter(authenticationService, authConfiguration);
    
    String validApiKey = "valid-api-key";
    MockServerHttpRequest request = MockServerHttpRequest
        .post("/api/v1/organizations")
        .header("X-API-Key", validApiKey)
        .build(); // No Authorization header
    MockServerWebExchange exchange = MockServerWebExchange.from(request);
    
    GatewayAuthenticationService.ApiKeyValidationResponse validApiKeyResponse = 
        new GatewayAuthenticationService.ApiKeyValidationResponse();
    validApiKeyResponse.setValid(true);
    validApiKeyResponse.setUserId("api-user-456");
    validApiKeyResponse.setUsername("apiuser");
    validApiKeyResponse.setRole("SERVICE");
    validApiKeyResponse.setScopes(new String[]{"read", "write"});
    
    when(authenticationService.validateApiKey(validApiKey))
        .thenReturn(Mono.just(validApiKeyResponse));

    // Simulate filter chain: JWT filter -> API key filter -> downstream
    WebFilterChain jwtToApiKeyChain = (ex) -> apiKeyFilter.filter(ex, chain);

    // when
    jwtFilter.filter(exchange, jwtToApiKeyChain).block();

    // then
    verify(authenticationService, never()).validateJwt(anyString()); // JWT should be skipped
    verify(authenticationService).validateApiKey(validApiKey);
    verify(chain).filter(any()); // Should reach the end of the chain
    assertThat(exchange.getResponse().getStatusCode()).isNull(); // No error status
  }

  @Test
  @DisplayName("should fail authentication when both JWT and API key are invalid")
  void shouldFailAuthenticationWhenBothJwtAndApiKeyAreInvalid() {
    // given
    when(authConfiguration.getRealm()).thenReturn("OpenChallenges");
    
    JwtAuthenticationGatewayFilter jwtFilter = 
        new JwtAuthenticationGatewayFilter(authenticationService, authConfiguration);
    ApiKeyAuthenticationGatewayFilter apiKeyFilter = 
        new ApiKeyAuthenticationGatewayFilter(authenticationService, authConfiguration);
    
    String invalidToken = "invalid.jwt.token";
    MockServerHttpRequest request = MockServerHttpRequest
        .post("/api/v1/organizations")
        .header("Authorization", "Bearer " + invalidToken)
        .header("X-API-Key", "invalid-api-key")
        .build();
    MockServerWebExchange exchange = MockServerWebExchange.from(request);
    
    GatewayAuthenticationService.JwtValidationResponse invalidJwtResponse = 
        new GatewayAuthenticationService.JwtValidationResponse();
    invalidJwtResponse.setValid(false);
    
    when(authenticationService.validateJwt(invalidToken))
        .thenReturn(Mono.just(invalidJwtResponse));

    // Simulate filter chain: JWT filter -> API key filter -> downstream
    WebFilterChain jwtToApiKeyChain = (ex) -> apiKeyFilter.filter(ex, chain);

    // when
    jwtFilter.filter(exchange, jwtToApiKeyChain).block();

    // then
    verify(authenticationService).validateJwt(invalidToken);
    verify(authenticationService, never()).validateApiKey(anyString()); // Should fail before reaching API key
    verify(chain, never()).filter(any()); // Should not reach the end of the chain
    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(exchange.getResponse().getHeaders().getFirst(HttpHeaders.WWW_AUTHENTICATE))
        .isEqualTo("Bearer realm=\"OpenChallenges\"");
  }

  @Test
  @DisplayName("should continue to Spring Security when no authentication provided")
  void shouldContinueToSpringSecurityWhenNoAuthenticationProvided() {
    // given
    when(chain.filter(any())).thenReturn(Mono.empty());
    
    JwtAuthenticationGatewayFilter jwtFilter = 
        new JwtAuthenticationGatewayFilter(authenticationService, authConfiguration);
    ApiKeyAuthenticationGatewayFilter apiKeyFilter = 
        new ApiKeyAuthenticationGatewayFilter(authenticationService, authConfiguration);
    
    MockServerHttpRequest request = MockServerHttpRequest
        .post("/api/v1/organizations")
        .build(); // No authentication headers
    MockServerWebExchange exchange = MockServerWebExchange.from(request);

    // Simulate filter chain: JWT filter -> API key filter -> downstream
    WebFilterChain jwtToApiKeyChain = (ex) -> apiKeyFilter.filter(ex, chain);

    // when
    jwtFilter.filter(exchange, jwtToApiKeyChain).block();

    // then
    verify(authenticationService, never()).validateJwt(anyString());
    verify(authenticationService, never()).validateApiKey(anyString());
    verify(chain).filter(any()); // Should reach Spring Security for authorization decision
    assertThat(exchange.getResponse().getStatusCode()).isNull(); // No error status
  }
}
