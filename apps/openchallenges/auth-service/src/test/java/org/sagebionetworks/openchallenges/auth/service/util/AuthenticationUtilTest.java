package org.sagebionetworks.openchallenges.auth.service.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.service.UserLookupService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Test for AuthenticationUtil to verify it correctly handles JwtAuthenticationToken.
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationUtilTest {

  @Mock
  private UserLookupService userLookupService;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Jwt jwt;

  @InjectMocks
  private AuthenticationUtil authenticationUtil;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser = User.builder()
      .id(UUID.randomUUID())
      .username("testuser")
      .role(User.Role.user)
      .build();

    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  void shouldExtractUserFromJwtAuthenticationTokenDetails() {
    // Arrange
    JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(
      jwt,
      Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
    );
    jwtAuthToken.setDetails(testUser); // User stored in details

    when(securityContext.getAuthentication()).thenReturn(jwtAuthToken);

    // Act
    User result = authenticationUtil.getAuthenticatedUser();

    // Assert
    assertThat(result).isEqualTo(testUser);
  }

  @Test
  void shouldExtractUserFromJwtTokenWhenDetailsNotSet() {
    // Arrange
    String userId = testUser.getId().toString();
    JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(
      jwt,
      Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
    );
    // No details set, should fallback to JWT subject

    when(securityContext.getAuthentication()).thenReturn(jwtAuthToken);
    when(jwt.getSubject()).thenReturn(userId);
    when(userLookupService.findUserBySubject(userId)).thenReturn(Optional.of(testUser));

    // Act
    User result = authenticationUtil.getAuthenticatedUser();

    // Assert
    assertThat(result).isEqualTo(testUser);
  }

  @Test
  void shouldReturnNullWhenNoAuthentication() {
    // Arrange
    when(securityContext.getAuthentication()).thenReturn(null);

    // Act
    User result = authenticationUtil.getAuthenticatedUser();

    // Assert
    assertThat(result).isNull();
  }
}
