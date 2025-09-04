package org.sagebionetworks.openchallenges.organization.service.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ApiKeyAuthenticationFilterTest {

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  private ApiKeyAuthenticationFilter filter;

  @BeforeEach
  void setUp() {
    // Updated: ApiKeyAuthenticationFilter is now a placeholder with no-arg constructor
    filter = new ApiKeyAuthenticationFilter();
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldPassThroughWithoutAuthentication() throws ServletException, IOException {
    // Given - API key authentication is now a placeholder (no need to mock headers)

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then - placeholder implementation just passes through
    verify(filterChain).doFilter(request, response);
    // No authentication should be set since it's a placeholder
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }

  @Test
  void shouldPassThroughWithoutApiKey() throws ServletException, IOException {
    // Given - no API key (no need to mock headers)

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then - placeholder implementation just passes through
    verify(filterChain).doFilter(request, response);
    assertNull(SecurityContextHolder.getContext().getAuthentication());
  }
}
