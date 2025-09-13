package org.sagebionetworks.openchallenges.auth.service.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class JwtClaimUtilTest {

  private JwtClaimUtil jwtClaimUtil;

  @BeforeEach
  void setUp() {
    jwtClaimUtil = new JwtClaimUtil();
  }

  @Test
  void extractSubject_whenJwtIsNull_returnsNull() {
    String subject = jwtClaimUtil.extractSubject(null);
    assertNull(subject);
  }

  @Test
  void extractSubject_whenSubjectExists_returnsSubject() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getSubject()).thenReturn("test-subject");

    String subject = jwtClaimUtil.extractSubject(jwt);

    assertEquals("test-subject", subject);
  }

  @Test
  void extractSubject_whenSubjectHasWhitespace_returnsTrimmedSubject() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getSubject()).thenReturn("  test-subject  ");

    String subject = jwtClaimUtil.extractSubject(jwt);

    assertEquals("test-subject", subject);
  }

  @Test
  void extractSubject_whenSubjectIsEmpty_returnsNull() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getSubject()).thenReturn("");

    String subject = jwtClaimUtil.extractSubject(jwt);

    assertNull(subject);
  }

  @Test
  void extractScopes_whenJwtIsNull_returnsEmptyList() {
    List<String> scopes = jwtClaimUtil.extractScopes(null);
    assertTrue(scopes.isEmpty());
  }

  @Test
  void extractScopes_whenScopesExist_returnsScopes() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaimAsStringList("scp")).thenReturn(List.of("read:profile", "write:profile"));

    List<String> scopes = jwtClaimUtil.extractScopes(jwt);

    assertEquals(2, scopes.size());
    assertTrue(scopes.contains("read:profile"));
    assertTrue(scopes.contains("write:profile"));
  }

  @Test
  void extractScopes_whenScopesHaveWhitespace_returnsTrimmedScopes() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaimAsStringList("scp")).thenReturn(List.of("  read:profile  ", "write:profile"));

    List<String> scopes = jwtClaimUtil.extractScopes(jwt);

    assertEquals(2, scopes.size());
    assertTrue(scopes.contains("read:profile"));
    assertTrue(scopes.contains("write:profile"));
  }

  @Test
  void extractScopes_whenNoScopes_returnsEmptyList() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaimAsStringList("scp")).thenReturn(null);

    List<String> scopes = jwtClaimUtil.extractScopes(jwt);

    assertTrue(scopes.isEmpty());
  }

  @Test
  void extractStringClaim_whenJwtIsNull_returnsEmpty() {
    Optional<String> claim = jwtClaimUtil.extractStringClaim(null, "test");
    assertTrue(claim.isEmpty());
  }

  @Test
  void extractStringClaim_whenClaimExists_returnsClaim() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaimAsString("test")).thenReturn("test-value");

    Optional<String> claim = jwtClaimUtil.extractStringClaim(jwt, "test");

    assertTrue(claim.isPresent());
    assertEquals("test-value", claim.get());
  }

  @Test
  void extractStringClaim_whenClaimNameIsNull_returnsEmpty() {
    Jwt jwt = mock(Jwt.class);

    Optional<String> claim = jwtClaimUtil.extractStringClaim(jwt, null);

    assertTrue(claim.isEmpty());
  }

  @Test
  void hasScope_whenJwtHasScope_returnsTrue() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaimAsStringList("scp")).thenReturn(List.of("read:profile", "write:profile"));

    boolean hasScope = jwtClaimUtil.hasScope(jwt, "read:profile");

    assertTrue(hasScope);
  }

  @Test
  void hasScope_whenJwtDoesNotHaveScope_returnsFalse() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaimAsStringList("scp")).thenReturn(List.of("read:profile"));

    boolean hasScope = jwtClaimUtil.hasScope(jwt, "write:profile");

    assertFalse(hasScope);
  }

  @Test
  void hasAnyScope_whenJwtHasOneScope_returnsTrue() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaimAsStringList("scp")).thenReturn(List.of("read:profile"));

    boolean hasAnyScope = jwtClaimUtil.hasAnyScope(jwt, "read:profile", "write:profile");

    assertTrue(hasAnyScope);
  }

  @Test
  void hasAnyScope_whenJwtHasNoRequiredScopes_returnsFalse() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaimAsStringList("scp")).thenReturn(List.of("delete:profile"));

    boolean hasAnyScope = jwtClaimUtil.hasAnyScope(jwt, "read:profile", "write:profile");

    assertFalse(hasAnyScope);
  }

  @Test
  void hasAllScopes_whenJwtHasAllScopes_returnsTrue() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaimAsStringList("scp")).thenReturn(
      List.of("read:profile", "write:profile", "delete:profile")
    );

    boolean hasAllScopes = jwtClaimUtil.hasAllScopes(jwt, "read:profile", "write:profile");

    assertTrue(hasAllScopes);
  }

  @Test
  void hasAllScopes_whenJwtMissingOneScope_returnsFalse() {
    Jwt jwt = mock(Jwt.class);
    when(jwt.getClaimAsStringList("scp")).thenReturn(List.of("read:profile"));

    boolean hasAllScopes = jwtClaimUtil.hasAllScopes(jwt, "read:profile", "write:profile");

    assertFalse(hasAllScopes);
  }

  @Test
  void hasAllScopes_whenNoRequiredScopes_returnsTrue() {
    Jwt jwt = mock(Jwt.class);

    boolean hasAllScopes = jwtClaimUtil.hasAllScopes(jwt);

    assertTrue(hasAllScopes);
  }
}
