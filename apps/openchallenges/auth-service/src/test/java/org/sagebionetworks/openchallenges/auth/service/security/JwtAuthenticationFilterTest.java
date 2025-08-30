package org.sagebionetworks.openchallenges.auth.service.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.repository.UserRepository;
import org.sagebionetworks.openchallenges.auth.service.service.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("JWT Authentication Filter Tests")
class JwtAuthenticationFilterTest {

  @Mock
  private JwtService jwtService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private FilterChain filterChain;

  private JwtAuthenticationFilter jwtAuthenticationFilter;
  private User testUser;

  @BeforeEach
  void setUp() {
    jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtService, userRepository);
    
    testUser = User.builder()
        .id(UUID.randomUUID())
        .username("testuser")
        .passwordHash("hashedpassword")
        .role(User.Role.user)
        .enabled(true)
        .build();

    // Clear security context before each test
    SecurityContextHolder.clearContext();
  }

  @Nested
  @DisplayName("Public Endpoints")
  class PublicEndpointsTests {

    @Test
    void shouldSkipJwtAuthenticationForLoginEndpoint() throws Exception {
      // Arrange
      when(request.getRequestURI()).thenReturn("/api/v1/auth/login");

      // Act
      jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(filterChain).doFilter(request, response);
      verify(jwtService, never()).extractUsername(anyString());
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldSkipJwtAuthenticationForOAuth2Endpoints() throws Exception {
      // Arrange
      when(request.getRequestURI()).thenReturn("/api/v1/auth/oauth2/authorize");

      // Act
      jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(filterChain).doFilter(request, response);
      verify(jwtService, never()).extractUsername(anyString());
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldSkipJwtAuthenticationForValidateEndpoint() throws Exception {
      // Arrange
      when(request.getRequestURI()).thenReturn("/api/v1/auth/validate");

      // Act
      jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(filterChain).doFilter(request, response);
      verify(jwtService, never()).extractUsername(anyString());
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldSkipJwtAuthenticationForActuatorEndpoints() throws Exception {
      // Arrange
      when(request.getRequestURI()).thenReturn("/actuator/health");

      // Act
      jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(filterChain).doFilter(request, response);
      verify(jwtService, never()).extractUsername(anyString());
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldSkipJwtAuthenticationForSwaggerEndpoints() throws Exception {
      // Arrange
      when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");

      // Act
      jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(filterChain).doFilter(request, response);
      verify(jwtService, never()).extractUsername(anyString());
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
  }

  @Nested
  @DisplayName("JWT Token Processing")
  class JwtTokenProcessingTests {

    @Test
    void shouldContinueWithoutAuthenticationWhenNoAuthorizationHeader() throws Exception {
      // Arrange
      when(request.getRequestURI()).thenReturn("/api/v1/auth/user/profile");
      when(request.getHeader("Authorization")).thenReturn(null);

      // Act
      jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(filterChain).doFilter(request, response);
      verify(jwtService, never()).extractUsername(anyString());
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenAuthorizationHeaderDoesNotStartWithBearer() throws Exception {
      // Arrange
      when(request.getRequestURI()).thenReturn("/api/v1/auth/user/profile");
      when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

      // Act
      jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(filterChain).doFilter(request, response);
      verify(jwtService, never()).extractUsername(anyString());
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenJwtTokenIsInvalid() throws Exception {
      // Arrange
      when(request.getRequestURI()).thenReturn("/api/v1/auth/user/profile");
      when(request.getHeader("Authorization")).thenReturn("Bearer invalid.jwt.token");
      when(jwtService.extractUsername("invalid.jwt.token")).thenReturn(null);

      // Act
      jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(filterChain).doFilter(request, response);
      verify(jwtService).extractUsername("invalid.jwt.token");
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenJwtTokenValidationFails() throws Exception {
      // Arrange
      when(request.getRequestURI()).thenReturn("/api/v1/auth/user/profile");
      when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
      when(jwtService.extractUsername("valid.jwt.token")).thenReturn("testuser");
      when(jwtService.isTokenValid("valid.jwt.token", "testuser")).thenReturn(false);

      // Act
      jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(filterChain).doFilter(request, response);
      verify(jwtService).extractUsername("valid.jwt.token");
      verify(jwtService).isTokenValid("valid.jwt.token", "testuser");
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
  }

  @Nested
  @DisplayName("Valid JWT Authentication")
  class ValidJwtAuthenticationTests {

    @Test
    void shouldSetupAuthenticationContextWithValidJwtToken() throws Exception {
      // Arrange
      when(request.getRequestURI()).thenReturn("/api/v1/auth/user/profile");
      when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
      when(jwtService.extractUsername("valid.jwt.token")).thenReturn("testuser");
      when(jwtService.isTokenValid("valid.jwt.token", "testuser")).thenReturn(true);
      when(userRepository.findByUsernameIgnoreCase("testuser")).thenReturn(Optional.of(testUser));

      // Act
      jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(filterChain).doFilter(request, response);
      
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      assertThat(authentication).isNotNull();
      assertThat(authentication.getPrincipal()).isEqualTo(testUser);
      assertThat(authentication.getCredentials()).isEqualTo("valid.jwt.token");
      assertThat(authentication.getAuthorities()).hasSize(1);
      assertThat(authentication.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test
    void shouldSetupAuthenticationWithCorrectRoleForAdminUser() throws Exception {
      // Arrange
      User adminUser = User.builder()
          .id(UUID.randomUUID())
          .username("admin")
          .passwordHash("hashedpassword")
          .role(User.Role.admin)
          .enabled(true)
          .build();

      when(request.getRequestURI()).thenReturn("/api/v1/auth/admin/users");
      when(request.getHeader("Authorization")).thenReturn("Bearer admin.jwt.token");
      when(jwtService.extractUsername("admin.jwt.token")).thenReturn("admin");
      when(jwtService.isTokenValid("admin.jwt.token", "admin")).thenReturn(true);
      when(userRepository.findByUsernameIgnoreCase("admin")).thenReturn(Optional.of(adminUser));

      // Act
      jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(filterChain).doFilter(request, response);
      
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      assertThat(authentication).isNotNull();
      assertThat(authentication.getPrincipal()).isEqualTo(adminUser);
      assertThat(authentication.getAuthorities()).hasSize(1);
      assertThat(authentication.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void shouldNotSetupAuthenticationWhenUserNotFoundInDatabase() throws Exception {
      // Arrange
      when(request.getRequestURI()).thenReturn("/api/v1/auth/user/profile");
      when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
      when(jwtService.extractUsername("valid.jwt.token")).thenReturn("nonexistentuser");
      when(jwtService.isTokenValid("valid.jwt.token", "nonexistentuser")).thenReturn(true);
      when(userRepository.findByUsernameIgnoreCase("nonexistentuser")).thenReturn(Optional.empty());

      // Act
      jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(filterChain).doFilter(request, response);
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldNotSetupAuthenticationWhenUserAccountIsDisabled() throws Exception {
      // Arrange
      User disabledUser = User.builder()
          .id(UUID.randomUUID())
          .username("disabled")
          .passwordHash("hashedpassword")
          .role(User.Role.user)
          .enabled(false)
          .build();

      when(request.getRequestURI()).thenReturn("/api/v1/auth/user/profile");
      when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
      when(jwtService.extractUsername("valid.jwt.token")).thenReturn("disabled");
      when(jwtService.isTokenValid("valid.jwt.token", "disabled")).thenReturn(true);
      when(userRepository.findByUsernameIgnoreCase("disabled")).thenReturn(Optional.of(disabledUser));

      // Act
      jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(filterChain).doFilter(request, response);
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }
  }

  @Nested
  @DisplayName("Exception Handling")
  class ExceptionHandlingTests {

    @Test
    void shouldContinueFilterChainWhenExceptionOccurs() throws Exception {
      // Arrange
      when(request.getRequestURI()).thenReturn("/api/v1/auth/user/profile");
      when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
      when(jwtService.extractUsername("valid.jwt.token")).thenThrow(new RuntimeException("JWT parsing error"));

      // Act
      jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(filterChain).doFilter(request, response);
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void shouldNotSetupAuthenticationWhenAlreadyAuthenticated() throws Exception {
      // Arrange
      Authentication existingAuth = mock(Authentication.class);
      SecurityContextHolder.getContext().setAuthentication(existingAuth);

      when(request.getRequestURI()).thenReturn("/api/v1/auth/user/profile");
      when(request.getHeader("Authorization")).thenReturn("Bearer valid.jwt.token");
      when(jwtService.extractUsername("valid.jwt.token")).thenReturn("testuser");
      when(jwtService.isTokenValid("valid.jwt.token", "testuser")).thenReturn(true);

      // Act
      jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

      // Assert
      verify(filterChain).doFilter(request, response);
      verify(userRepository, never()).findByUsernameIgnoreCase(any());
      assertThat(SecurityContextHolder.getContext().getAuthentication()).isEqualTo(existingAuth);
    }
  }
}
