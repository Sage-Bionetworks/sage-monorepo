package org.sagebionetworks.openchallenges.auth.service.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.auth.service.model.dto.AuthScopeDto;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.springframework.security.oauth2.jwt.Jwt;

@ExtendWith(MockitoExtension.class)
class ScopeUtilTest {

  @Mock
  private JwtClaimUtil jwtClaimUtil;

  private ScopeUtil scopeUtil;

  @BeforeEach
  void setUp() {
    scopeUtil = new ScopeUtil(jwtClaimUtil);
  }

  @Test
  void extractScopesFromJwt_whenJwtHasScopes_returnsAuthScopeDtos() {
    Jwt jwt = mock(Jwt.class);
    when(jwtClaimUtil.extractScopes(jwt)).thenReturn(List.of("read:profile", "update:profile"));

    List<AuthScopeDto> scopes = scopeUtil.extractScopesFromJwt(jwt);

    assertEquals(2, scopes.size());
    assertTrue(scopes.contains(AuthScopeDto.READ_PROFILE));
    assertTrue(scopes.contains(AuthScopeDto.UPDATE_PROFILE));
  }

  @Test
  void extractScopesFromApiKey_whenApiKeyIsNull_returnsEmptyList() {
    List<AuthScopeDto> scopes = scopeUtil.extractScopesFromApiKey(null);
    assertTrue(scopes.isEmpty());
  }

  @Test
  void extractScopesFromApiKey_whenApiKeyHasScopes_returnsAuthScopeDtos() {
    ApiKey apiKey = new ApiKey();
    apiKey.setName("test-key");
    apiKey.setAllowedScopes("read:profile,update:profile");

    List<AuthScopeDto> scopes = scopeUtil.extractScopesFromApiKey(apiKey);

    assertEquals(2, scopes.size());
    assertTrue(scopes.contains(AuthScopeDto.READ_PROFILE));
    assertTrue(scopes.contains(AuthScopeDto.UPDATE_PROFILE));
  }

  @Test
  void extractScopesFromApiKey_whenApiKeyHasNoScopes_returnsEmptyList() {
    ApiKey apiKey = new ApiKey();
    apiKey.setName("test-key");
    apiKey.setAllowedScopes("");

    List<AuthScopeDto> scopes = scopeUtil.extractScopesFromApiKey(apiKey);

    assertTrue(scopes.isEmpty());
  }

  @Test
  void getDefaultScopesForUser_whenUserIsNull_returnsEmptySet() {
    Set<String> scopes = scopeUtil.getDefaultScopesForUser(null);
    assertTrue(scopes.isEmpty());
  }

  @Test
  void getDefaultScopesForUser_whenUserIsAdmin_returnsAdminScopes() {
    User user = new User();
    user.setUsername("admin");
    user.setRole(User.Role.admin);

    Set<String> scopes = scopeUtil.getDefaultScopesForUser(user);

    assertTrue(scopes.size() > 10); // Admin has more scopes
    assertTrue(scopes.contains("read:profile"));
    assertTrue(scopes.contains("delete:organizations")); // Admin-only scope
  }

  @Test
  void getDefaultScopesForUser_whenUserIsRegular_returnsUserScopes() {
    User user = new User();
    user.setUsername("user");
    user.setRole(User.Role.user);

    Set<String> scopes = scopeUtil.getDefaultScopesForUser(user);

    assertTrue(scopes.contains("read:profile"));
    assertFalse(scopes.contains("delete:organizations")); // Not available for regular users
  }

  @Test
  void convertToAuthScopeDtos_whenScopesAreNull_returnsEmptyList() {
    List<AuthScopeDto> scopes = scopeUtil.convertToAuthScopeDtos(null);
    assertTrue(scopes.isEmpty());
  }

  @Test
  void convertToAuthScopeDtos_whenScopesAreValid_returnsAuthScopeDtos() {
    List<String> scopes = List.of("read:profile", "update:profile");

    List<AuthScopeDto> scopeDtos = scopeUtil.convertToAuthScopeDtos(scopes);

    assertEquals(2, scopeDtos.size());
    assertTrue(scopeDtos.contains(AuthScopeDto.READ_PROFILE));
    assertTrue(scopeDtos.contains(AuthScopeDto.UPDATE_PROFILE));
  }

  @Test
  void convertScopesToString_whenScopesAreNull_returnsEmptyString() {
    String scopeString = scopeUtil.convertScopesToString(null);
    assertEquals("", scopeString);
  }

  @Test
  void convertScopesToString_whenScopesExist_returnsCommaSeparatedString() {
    Set<String> scopes = Set.of("read:profile", "write:profile");

    String scopeString = scopeUtil.convertScopesToString(scopes);

    assertTrue(scopeString.contains("read:profile"));
    assertTrue(scopeString.contains("write:profile"));
    assertTrue(scopeString.contains(","));
  }

  @Test
  void parseScopeString_whenStringIsNull_returnsEmptyList() {
    List<String> scopes = scopeUtil.parseScopeString(null);
    assertTrue(scopes.isEmpty());
  }

  @Test
  void parseScopeString_whenStringHasScopes_returnsList() {
    String scopeString = "read:profile,write:profile";

    List<String> scopes = scopeUtil.parseScopeString(scopeString);

    assertEquals(2, scopes.size());
    assertTrue(scopes.contains("read:profile"));
    assertTrue(scopes.contains("write:profile"));
  }

  @Test
  void parseScopeString_whenStringHasWhitespace_returnsTrimmedList() {
    String scopeString = " read:profile , write:profile ";

    List<String> scopes = scopeUtil.parseScopeString(scopeString);

    assertEquals(2, scopes.size());
    assertTrue(scopes.contains("read:profile"));
    assertTrue(scopes.contains("write:profile"));
  }

  @Test
  void userHasScope_whenUserHasScope_returnsTrue() {
    User user = new User();
    user.setUsername("user");
    user.setRole(User.Role.user);

    boolean hasScope = scopeUtil.userHasScope(user, "read:profile");

    assertTrue(hasScope);
  }

  @Test
  void userHasScope_whenUserDoesNotHaveScope_returnsFalse() {
    User user = new User();
    user.setUsername("user");
    user.setRole(User.Role.user);

    boolean hasScope = scopeUtil.userHasScope(user, "delete:organizations");

    assertFalse(hasScope);
  }

  @Test
  void userHasAnyScope_whenUserHasOneScope_returnsTrue() {
    User user = new User();
    user.setUsername("user");
    user.setRole(User.Role.user);

    boolean hasAnyScope = scopeUtil.userHasAnyScope(user, "read:profile", "delete:organizations");

    assertTrue(hasAnyScope);
  }

  @Test
  void userHasAnyScope_whenUserHasNoScopes_returnsFalse() {
    User user = new User();
    user.setUsername("user");
    user.setRole(User.Role.user);

    boolean hasAnyScope = scopeUtil.userHasAnyScope(user, "delete:organizations", "admin:panel");

    assertFalse(hasAnyScope);
  }
}
