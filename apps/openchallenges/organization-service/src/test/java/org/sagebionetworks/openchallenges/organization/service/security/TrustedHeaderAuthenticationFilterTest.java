package org.sagebionetworks.openchallenges.organization.service.security;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class TrustedHeaderAuthenticationFilterTest {

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  private TrustedHeaderAuthenticationFilter filter;

  @BeforeEach
  void setUp() {
    filter = new TrustedHeaderAuthenticationFilter();
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldAuthenticateWithValidTrustedHeaders() throws ServletException, IOException {
    // Given
    UUID userId = UUID.randomUUID();
    when(request.getHeader("X-Authenticated-User-Id")).thenReturn(userId.toString());
    when(request.getHeader("X-Authenticated-User")).thenReturn("testuser");
    when(request.getHeader("X-Authenticated-Roles")).thenReturn("ADMIN");
    when(request.getHeader("X-Scopes")).thenReturn("read,write");

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(auth);
    assertTrue(auth.getPrincipal() instanceof AuthenticatedUser);
    
    AuthenticatedUser user = (AuthenticatedUser) auth.getPrincipal();
    assertEquals(userId, user.getUserId());
    assertEquals("testuser", user.getUsername());
    assertEquals("ADMIN", user.getRole());
    assertEquals(2, user.getScopes().size());
    assertTrue(user.getScopes().contains("read"));
    assertTrue(user.getScopes().contains("write"));

    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldNotAuthenticateWithMissingUserId() throws ServletException, IOException {
    // Given
    when(request.getHeader("X-Authenticated-User-Id")).thenReturn(null);
    when(request.getHeader("X-Authenticated-User")).thenReturn("testuser");

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNull(auth);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldNotAuthenticateWithMissingUsername() throws ServletException, IOException {
    // Given
    when(request.getHeader("X-Authenticated-User-Id")).thenReturn(UUID.randomUUID().toString());
    when(request.getHeader("X-Authenticated-User")).thenReturn(null);

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNull(auth);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldHandleInvalidUserIdFormat() throws ServletException, IOException {
    // Given
    when(request.getHeader("X-Authenticated-User-Id")).thenReturn("invalid-uuid");
    when(request.getHeader("X-Authenticated-User")).thenReturn("testuser");

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNull(auth);
    verify(filterChain).doFilter(request, response);
  }

  @Test
  void shouldUseDefaultRoleWhenMissing() throws ServletException, IOException {
    // Given
    UUID userId = UUID.randomUUID();
    when(request.getHeader("X-Authenticated-User-Id")).thenReturn(userId.toString());
    when(request.getHeader("X-Authenticated-User")).thenReturn("testuser");
    when(request.getHeader("X-Authenticated-Roles")).thenReturn(null);

    // When
    filter.doFilterInternal(request, response, filterChain);

    // Then
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertNotNull(auth);
    
    AuthenticatedUser user = (AuthenticatedUser) auth.getPrincipal();
    assertEquals("USER", user.getRole());
    verify(filterChain).doFilter(request, response);
  }
}
