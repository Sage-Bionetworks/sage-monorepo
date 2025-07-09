package org.sagebionetworks.openchallenges.organization.service.security;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AuthenticatedUserTest {

  @Test
  void shouldHaveCorrectAuthorities() {
    // Given
    List<String> scopes = List.of(
      "organizations:read",
      "organizations:write",
      "organizations:delete"
    );
    AuthenticatedUser user = new AuthenticatedUser(UUID.randomUUID(), "testuser", "admin", scopes);

    // When & Then
    assertEquals(3, user.getAuthorities().size());
    assertTrue(
      user
        .getAuthorities()
        .stream()
        .anyMatch(auth -> auth.getAuthority().equals("organizations:delete"))
    );
  }

  @Test
  void shouldDetectAdminRole() {
    // Given
    AuthenticatedUser adminUser = new AuthenticatedUser(
      UUID.randomUUID(),
      "admin",
      "admin",
      List.of()
    );
    AuthenticatedUser regularUser = new AuthenticatedUser(
      UUID.randomUUID(),
      "user",
      "user",
      List.of()
    );

    // When & Then
    assertTrue(adminUser.isAdmin());
    assertFalse(regularUser.isAdmin());
  }

  @Test
  void shouldCheckScopes() {
    // Given
    List<String> scopes = List.of("organizations:read", "organizations:write");
    AuthenticatedUser user = new AuthenticatedUser(UUID.randomUUID(), "testuser", "user", scopes);

    // When & Then
    assertTrue(user.hasScope("organizations:read"));
    assertTrue(user.hasScope("organizations:write"));
    assertFalse(user.hasScope("organizations:delete"));
  }

  @Test
  void shouldHandleNullScopes() {
    // Given
    AuthenticatedUser user = new AuthenticatedUser(UUID.randomUUID(), "testuser", "user", null);

    // When & Then
    assertFalse(user.hasScope("organizations:read"));
    assertEquals(0, user.getAuthorities().size());
  }

  @Test
  void shouldImplementUserDetailsCorrectly() {
    // Given
    AuthenticatedUser user = new AuthenticatedUser(
      UUID.randomUUID(),
      "testuser",
      "user",
      List.of()
    );

    // When & Then
    assertEquals("testuser", user.getUsername());
    assertNull(user.getPassword());
    assertTrue(user.isAccountNonExpired());
    assertTrue(user.isAccountNonLocked());
    assertTrue(user.isCredentialsNonExpired());
    assertTrue(user.isEnabled());
  }
}
