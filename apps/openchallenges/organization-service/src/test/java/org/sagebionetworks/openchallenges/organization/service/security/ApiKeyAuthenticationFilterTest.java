package org.sagebionetworks.openchallenges.organization.service.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.organization.service.client.AuthServiceClient;
import org.sagebionetworks.openchallenges.organization.service.model.dto.auth.ValidateApiKeyRequestDto;
import org.sagebionetworks.openchallenges.organization.service.model.dto.auth.ValidateApiKeyResponseDto;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ApiKeyAuthenticationFilterTest {

  @Mock
  private AuthServiceClient authServiceClient;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  private ApiKeyAuthenticationFilter filter;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    filter = new ApiKeyAuthenticationFilter(authServiceClient, objectMapper);
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldAuthenticateValidApiKey() throws Exception {
    // Given
    String apiKey = "oc_dev_valid_key_123";
    UUID userId = UUID.randomUUID();

    when(request.getHeader("Authorization")).thenReturn("Bearer " + apiKey);

    ValidateApiKeyResponseDto validResponse = new ValidateApiKeyResponseDto(
      true,
      userId,
      "testuser",
      "admin",
      List.of("organizations:read", "organizations:delete")
    );
    when(authServiceClient.validateApiKey(any(ValidateApiKeyRequestDto.class))).thenReturn(
      validResponse
    );

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
    assertNotNull(SecurityContextHolder.getContext().getAuthentication());

    AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
      .getAuthentication()
      .getPrincipal();
    assertEquals("testuser", user.getUsername());
    assertEquals("admin", user.getRole());
    assertTrue(user.hasScope("organizations:delete"));
  }

  @Test
  void shouldRejectInvalidApiKey() throws Exception {
    // Given
    String apiKey = "invalid_key";
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);

    when(request.getHeader("Authorization")).thenReturn("Bearer " + apiKey);
    when(response.getWriter()).thenReturn(writer);

    ValidateApiKeyResponseDto invalidResponse = new ValidateApiKeyResponseDto(
      false,
      null,
      null,
      null,
      null
    );
    when(authServiceClient.validateApiKey(any(ValidateApiKeyRequestDto.class))).thenReturn(
      invalidResponse
    );

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(filterChain, never()).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void shouldHandleAuthServiceUnavailable() throws Exception {
    // Given
    String apiKey = "oc_dev_valid_key_123";
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);

    when(request.getHeader("Authorization")).thenReturn("Bearer " + apiKey);
    when(response.getWriter()).thenReturn(writer);
    when(authServiceClient.validateApiKey(any(ValidateApiKeyRequestDto.class))).thenThrow(
      FeignException.ServiceUnavailable.class
    );

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(response).setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
    verify(filterChain, never()).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void shouldPassThroughRequestsWithoutAuthHeader() throws Exception {
    // Given
    when(request.getHeader("Authorization")).thenReturn(null);

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
    verify(authServiceClient, never()).validateApiKey(any());
  }

  @Test
  void shouldIgnoreNonBearerTokens() throws Exception {
    // Given
    when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    verify(filterChain).doFilter(request, response);
    verify(authServiceClient, never()).validateApiKey(any());
  }
}
