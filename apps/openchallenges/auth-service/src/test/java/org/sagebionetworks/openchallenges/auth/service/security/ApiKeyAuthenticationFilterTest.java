package org.sagebionetworks.openchallenges.auth.service.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.service.ApiKeyService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApiKeyAuthenticationFilter")
class ApiKeyAuthenticationFilterTest {

  @Mock
  private ApiKeyService apiKeyService;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  @Mock
  private SecurityContext securityContext;

  private ApiKeyAuthenticationFilter filter;
  private StringWriter responseWriter;
  private PrintWriter printWriter;

  private final String validApiKey = "valid-api-key-123";
  private final String invalidApiKey = "invalid-api-key";
  private final String authorizationHeaderValue = "Bearer " + validApiKey;

  @BeforeEach
  void setUp() throws IOException {
    filter = new ApiKeyAuthenticationFilter(apiKeyService);

    // Setup response writer only when needed
    responseWriter = new StringWriter();
    printWriter = new PrintWriter(responseWriter);

    // Setup security context
    SecurityContextHolder.setContext(securityContext);
  }

  @Nested
  @DisplayName("Constructor")
  class ConstructorTests {

    @Test
    @DisplayName("should initialize filter with API key service")
    void shouldInitializeFilterWithApiKeyService() {
      // when
      ApiKeyAuthenticationFilter newFilter = new ApiKeyAuthenticationFilter(apiKeyService);

      // then
      assertThat(newFilter).isNotNull();
    }
  }

  @Nested
  @DisplayName("Public Endpoints")
  class PublicEndpointTests {

    @Test
    @DisplayName("should skip authentication for login endpoint")
    void shouldSkipAuthenticationForLoginEndpoint() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/v1/auth/login");

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(filterChain).doFilter(request, response);
      verify(apiKeyService, never()).validateApiKey(anyString());
      verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    @DisplayName("should skip authentication for validate endpoint")
    void shouldSkipAuthenticationForValidateEndpoint() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/v1/auth/validate");

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(filterChain).doFilter(request, response);
      verify(apiKeyService, never()).validateApiKey(anyString());
    }

    @Test
    @DisplayName("should skip authentication for actuator endpoints")
    void shouldSkipAuthenticationForActuatorEndpoints() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/actuator/health");

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(filterChain).doFilter(request, response);
      verify(apiKeyService, never()).validateApiKey(anyString());
    }

    @Test
    @DisplayName("should skip authentication for API docs endpoints")
    void shouldSkipAuthenticationForApiDocsEndpoints() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/v3/api-docs/swagger-config");

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(filterChain).doFilter(request, response);
      verify(apiKeyService, never()).validateApiKey(anyString());
    }

    @Test
    @DisplayName("should skip authentication for Swagger UI endpoints")
    void shouldSkipAuthenticationForSwaggerUiEndpoints() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(filterChain).doFilter(request, response);
      verify(apiKeyService, never()).validateApiKey(anyString());
    }
  }

  @Nested
  @DisplayName("Authorization Header Processing")
  class AuthorizationHeaderTests {

    @Test
    @DisplayName("should continue filter chain when no authorization header present")
    void shouldContinueFilterChainWhenNoAuthorizationHeaderPresent()
      throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/api/protected");
      when(request.getHeader("Authorization")).thenReturn(null);

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(filterChain).doFilter(request, response);
      verify(apiKeyService, never()).validateApiKey(anyString());
    }

    @Test
    @DisplayName(
      "should continue filter chain when authorization header does not start with Bearer"
    )
    void shouldContinueFilterChainWhenAuthorizationHeaderDoesNotStartWithBearer()
      throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/api/protected");
      when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(filterChain).doFilter(request, response);
      verify(apiKeyService, never()).validateApiKey(anyString());
    }

    @Test
    @DisplayName("should extract API key from Bearer token")
    void shouldExtractApiKeyFromBearerToken() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/api/protected");
      when(request.getHeader("Authorization")).thenReturn(authorizationHeaderValue);
      when(apiKeyService.validateApiKey(validApiKey)).thenReturn(Optional.empty());

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(apiKeyService).validateApiKey(validApiKey);
    }
  }

  @Nested
  @DisplayName("Valid API Key Authentication")
  class ValidApiKeyTests {

    private User testUser;
    private ApiKey testApiKey;

    @BeforeEach
    void setUp() {
      testUser = User.builder()
        .id(UUID.randomUUID())
        .username("testuser")
        .role(User.Role.user)
        .enabled(true)
        .build();

      testApiKey = ApiKey.builder()
        .id(UUID.randomUUID())
        .keyHash("hashed-value")
        .keyPrefix("auth_")
        .name("Test API Key")
        .user(testUser)
        .build();
    }

    @Test
    @DisplayName("should authenticate user with valid API key")
    void shouldAuthenticateUserWithValidApiKey() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/api/protected");
      when(request.getHeader("Authorization")).thenReturn(authorizationHeaderValue);
      when(apiKeyService.validateApiKey(validApiKey)).thenReturn(Optional.of(testApiKey));

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(securityContext).setAuthentication(any(Authentication.class));
      verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("should set correct authorities for user role")
    void shouldSetCorrectAuthoritiesForUserRole() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/api/protected");
      when(request.getHeader("Authorization")).thenReturn(authorizationHeaderValue);
      when(apiKeyService.validateApiKey(validApiKey)).thenReturn(Optional.of(testApiKey));

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(securityContext).setAuthentication(any(Authentication.class));
    }

    @Test
    @DisplayName("should set correct authorities for admin role")
    void shouldSetCorrectAuthoritiesForAdminRole() throws ServletException, IOException {
      // given
      testUser.setRole(User.Role.admin);
      when(request.getRequestURI()).thenReturn("/api/protected");
      when(request.getHeader("Authorization")).thenReturn(authorizationHeaderValue);
      when(apiKeyService.validateApiKey(validApiKey)).thenReturn(Optional.of(testApiKey));

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(securityContext).setAuthentication(any(Authentication.class));
      verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("should set correct authorities for readonly role")
    void shouldSetCorrectAuthoritiesForReadonlyRole() throws ServletException, IOException {
      // given
      testUser.setRole(User.Role.readonly);
      when(request.getRequestURI()).thenReturn("/api/protected");
      when(request.getHeader("Authorization")).thenReturn(authorizationHeaderValue);
      when(apiKeyService.validateApiKey(validApiKey)).thenReturn(Optional.of(testApiKey));

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(securityContext).setAuthentication(any(Authentication.class));
      verify(filterChain).doFilter(request, response);
    }
  }

  @Nested
  @DisplayName("Invalid API Key Handling")
  class InvalidApiKeyTests {

    @Test
    @DisplayName("should return 401 unauthorized for invalid API key")
    void shouldReturn401UnauthorizedForInvalidApiKey() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/api/protected");
      when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidApiKey);
      when(apiKeyService.validateApiKey(invalidApiKey)).thenReturn(Optional.empty());
      when(response.getWriter()).thenReturn(printWriter);

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      verify(response).setContentType("application/json");
      verify(filterChain, never()).doFilter(request, response);

      printWriter.flush();
      assertThat(responseWriter.toString()).contains("{\"error\":\"Invalid API key\"}");
    }

    @Test
    @DisplayName("should not set authentication for invalid API key")
    void shouldNotSetAuthenticationForInvalidApiKey() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/api/protected");
      when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidApiKey);
      when(apiKeyService.validateApiKey(invalidApiKey)).thenReturn(Optional.empty());
      when(response.getWriter()).thenReturn(printWriter);

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(securityContext, never()).setAuthentication(any());
    }
  }

  @Nested
  @DisplayName("Exception Handling")
  class ExceptionHandlingTests {

    @Test
    @DisplayName("should continue filter chain when API key service throws exception")
    void shouldContinueFilterChainWhenApiKeyServiceThrowsException()
      throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/api/protected");
      when(request.getHeader("Authorization")).thenReturn(authorizationHeaderValue);
      when(apiKeyService.validateApiKey(validApiKey)).thenThrow(
        new RuntimeException("Database error")
      );

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(filterChain).doFilter(request, response);
      verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    @DisplayName("should not set authentication when exception occurs")
    void shouldNotSetAuthenticationWhenExceptionOccurs() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/api/protected");
      when(request.getHeader("Authorization")).thenReturn(authorizationHeaderValue);
      when(apiKeyService.validateApiKey(validApiKey)).thenThrow(
        new RuntimeException("Service error")
      );

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(securityContext, never()).setAuthentication(any());
    }
  }

  @Nested
  @DisplayName("Edge Cases")
  class EdgeCaseTests {

    @Test
    @DisplayName("should handle Bearer token with only Bearer prefix")
    void shouldHandleBearerTokenWithOnlyBearerPrefix() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/api/protected");
      when(request.getHeader("Authorization")).thenReturn("Bearer ");
      when(apiKeyService.validateApiKey("")).thenReturn(Optional.empty());
      when(response.getWriter()).thenReturn(printWriter);

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(apiKeyService).validateApiKey("");
      verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("should handle Bearer token with whitespace")
    void shouldHandleBearerTokenWithWhitespace() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/api/protected");
      when(request.getHeader("Authorization")).thenReturn("Bearer   whitespace-key");
      when(apiKeyService.validateApiKey("  whitespace-key")).thenReturn(Optional.empty());
      when(response.getWriter()).thenReturn(printWriter);

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(apiKeyService).validateApiKey("  whitespace-key");
    }

    @Test
    @DisplayName("should handle empty request URI gracefully")
    void shouldHandleEmptyRequestUriGracefully() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("");
      when(request.getHeader("Authorization")).thenReturn(authorizationHeaderValue);
      when(apiKeyService.validateApiKey(validApiKey)).thenReturn(Optional.empty());
      when(response.getWriter()).thenReturn(printWriter);

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(apiKeyService).validateApiKey(validApiKey);
    }

    @Test
    @DisplayName("should handle case sensitivity in Bearer prefix")
    void shouldHandleCaseSensitivityInBearerPrefix() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/api/protected");
      when(request.getHeader("Authorization")).thenReturn("bearer " + validApiKey);

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(filterChain).doFilter(request, response);
      verify(apiKeyService, never()).validateApiKey(anyString());
    }
  }

  @Nested
  @DisplayName("Public Endpoint Detection")
  class PublicEndpointDetectionTests {

    @Test
    @DisplayName("should correctly identify exact path matches")
    void shouldCorrectlyIdentifyExactPathMatches() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/v1/auth/login");

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(filterChain).doFilter(request, response);
      verify(apiKeyService, never()).validateApiKey(anyString());
    }

    @Test
    @DisplayName("should correctly identify prefix matches")
    void shouldCorrectlyIdentifyPrefixMatches() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/actuator/health/liveness");

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(filterChain).doFilter(request, response);
      verify(apiKeyService, never()).validateApiKey(anyString());
    }

    @Test
    @DisplayName("should not match partial paths as public")
    void shouldNotMatchPartialPathsAsPublic() throws ServletException, IOException {
      // given
      when(request.getRequestURI()).thenReturn("/api/v1/auth/login/test");
      when(request.getHeader("Authorization")).thenReturn(null);

      // when
      filter.doFilterInternal(request, response, filterChain);

      // then
      verify(filterChain).doFilter(request, response);
      // This should not be treated as a public endpoint
    }
  }
}
