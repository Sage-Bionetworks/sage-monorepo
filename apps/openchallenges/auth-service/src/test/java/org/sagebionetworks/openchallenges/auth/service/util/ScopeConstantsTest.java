package org.sagebionetworks.openchallenges.auth.service.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.sagebionetworks.openchallenges.auth.service.util.ScopeConstants.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test for ScopeConstants to ensure all scope constants are correctly defined
 * and match the expected patterns.
 */
@DisplayName("ScopeConstants Tests")
class ScopeConstantsTest {

  @Test
  @DisplayName("should prevent instantiation")
  void shouldPreventInstantiation() {
    // Verify that the constructor throws an exception
    Exception exception = assertThrows(Exception.class, () -> {
      // Use reflection to access the private constructor
      var constructor = ScopeConstants.class.getDeclaredConstructor();
      constructor.setAccessible(true);
      constructor.newInstance();
    });

    // The root cause should be UnsupportedOperationException
    Throwable rootCause = exception.getCause();
    assertNotNull(rootCause);
    assertEquals(UnsupportedOperationException.class, rootCause.getClass());
  }

  @Test
  @DisplayName("should have correctly formatted profile scopes")
  void shouldHaveCorrectlyFormattedProfileScopes() {
    assertEquals("read:profile", READ_PROFILE);
    assertEquals("update:profile", UPDATE_PROFILE);
  }

  @Test
  @DisplayName("should have correctly formatted API key scopes")
  void shouldHaveCorrectlyFormattedApiKeyScopes() {
    assertEquals("read:api-key", READ_API_KEY);
    assertEquals("create:api-key", CREATE_API_KEY);
    assertEquals("update:api-key", UPDATE_API_KEY);
    assertEquals("delete:api-key", DELETE_API_KEY);
    assertEquals("rotate:api-key", ROTATE_API_KEY);
  }

  @Test
  @DisplayName("should have correctly formatted organization scopes")
  void shouldHaveCorrectlyFormattedOrganizationScopes() {
    assertEquals("read:organizations", READ_ORGANIZATIONS);
    assertEquals("create:organizations", CREATE_ORGANIZATIONS);
    assertEquals("update:organizations", UPDATE_ORGANIZATIONS);
    assertEquals("delete:organizations", DELETE_ORGANIZATIONS);
  }

  @Test
  @DisplayName("should have correctly formatted challenge scopes")
  void shouldHaveCorrectlyFormattedChallengeScopes() {
    assertEquals("read:challenges", READ_CHALLENGES);
    assertEquals("create:challenges", CREATE_CHALLENGES);
    assertEquals("update:challenges", UPDATE_CHALLENGES);
    assertEquals("delete:challenges", DELETE_CHALLENGES);
    assertEquals("read:challenges-analytics", READ_CHALLENGES_ANALYTICS);
  }

  @Test
  @DisplayName("should have correctly formatted challenge platform scopes")
  void shouldHaveCorrectlyFormattedChallengePlatformScopes() {
    assertEquals("read:challenge-platforms", READ_CHALLENGE_PLATFORMS);
    assertEquals("create:challenge-platforms", CREATE_CHALLENGE_PLATFORMS);
    assertEquals("update:challenge-platforms", UPDATE_CHALLENGE_PLATFORMS);
    assertEquals("delete:challenge-platforms", DELETE_CHALLENGE_PLATFORMS);
  }

  @Test
  @DisplayName("should have correctly formatted EDAM and admin scopes")
  void shouldHaveCorrectlyFormattedEdamAndAdminScopes() {
    assertEquals("read:edam-concepts", READ_EDAM_CONCEPTS);
    assertEquals("admin:panel", ADMIN_PANEL);
  }

  @Test
  @DisplayName("should have correctly formatted OpenID Connect scopes")
  void shouldHaveCorrectlyFormattedOpenIdConnectScopes() {
    assertEquals("openid", OPENID);
    assertEquals("profile", PROFILE);
    assertEquals("email", EMAIL);
  }

  @Test
  @DisplayName("should have all constants as non-null values")
  void shouldHaveAllConstantsAsNonNullValues() {
    // Profile scopes
    assertNotNull(READ_PROFILE);
    assertNotNull(UPDATE_PROFILE);

    // API key scopes
    assertNotNull(READ_API_KEY);
    assertNotNull(CREATE_API_KEY);
    assertNotNull(UPDATE_API_KEY);
    assertNotNull(DELETE_API_KEY);
    assertNotNull(ROTATE_API_KEY);

    // Organization scopes
    assertNotNull(READ_ORGANIZATIONS);
    assertNotNull(CREATE_ORGANIZATIONS);
    assertNotNull(UPDATE_ORGANIZATIONS);
    assertNotNull(DELETE_ORGANIZATIONS);

    // Challenge scopes
    assertNotNull(READ_CHALLENGES);
    assertNotNull(CREATE_CHALLENGES);
    assertNotNull(UPDATE_CHALLENGES);
    assertNotNull(DELETE_CHALLENGES);
    assertNotNull(READ_CHALLENGES_ANALYTICS);

    // Challenge platform scopes
    assertNotNull(READ_CHALLENGE_PLATFORMS);
    assertNotNull(CREATE_CHALLENGE_PLATFORMS);
    assertNotNull(UPDATE_CHALLENGE_PLATFORMS);
    assertNotNull(DELETE_CHALLENGE_PLATFORMS);

    // EDAM and admin scopes
    assertNotNull(READ_EDAM_CONCEPTS);
    assertNotNull(ADMIN_PANEL);

    // OpenID Connect scopes
    assertNotNull(OPENID);
    assertNotNull(PROFILE);
    assertNotNull(EMAIL);
  }

  @Test
  @DisplayName("should have all scope strings follow expected patterns")
  void shouldHaveAllScopeStringsFollowExpectedPatterns() {
    // Most scopes should follow the pattern "action:resource"
    assertTrue(READ_PROFILE.matches("^[a-z]+:[a-z\\-]+$"));
    assertTrue(UPDATE_PROFILE.matches("^[a-z]+:[a-z\\-]+$"));
    assertTrue(READ_API_KEY.matches("^[a-z]+:[a-z\\-]+$"));
    assertTrue(CREATE_ORGANIZATIONS.matches("^[a-z]+:[a-z\\-]+$"));
    assertTrue(DELETE_CHALLENGES.matches("^[a-z]+:[a-z\\-]+$"));

    // OpenID Connect standard scopes are single words
    assertTrue(OPENID.matches("^[a-z]+$"));
    assertTrue(PROFILE.matches("^[a-z]+$"));
    assertTrue(EMAIL.matches("^[a-z]+$"));
  }
}
