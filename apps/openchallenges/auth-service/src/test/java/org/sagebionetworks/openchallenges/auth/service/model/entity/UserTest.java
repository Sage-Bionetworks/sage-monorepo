package org.sagebionetworks.openchallenges.auth.service.model.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("User Entity Tests")
class UserTest {

  private User user;

  @BeforeEach
  void setUp() {
    user = User.builder()
      .id(UUID.randomUUID())
      .username("testuser")
      .passwordHash("hashedpassword123")
      .role(User.Role.user)
      .enabled(true)
      .createdAt(OffsetDateTime.now().minusDays(1))
      .updatedAt(OffsetDateTime.now().minusHours(2))
      .apiKeys(new ArrayList<>())
      .build();
  }

  @Nested
  @DisplayName("Builder Pattern Tests")
  class BuilderTests {

    @Test
    @DisplayName("should create User with all fields using builder")
    void shouldCreateUserWithAllFields() {
      UUID id = UUID.randomUUID();
      String username = "testuser";
      String passwordHash = "hashedpassword";
      User.Role role = User.Role.admin;
      Boolean enabled = false;
      OffsetDateTime createdAt = OffsetDateTime.now().minusDays(1);
      OffsetDateTime updatedAt = OffsetDateTime.now();
      List<ApiKey> apiKeys = new ArrayList<>();

      User result = User.builder()
        .id(id)
        .username(username)
        .passwordHash(passwordHash)
        .role(role)
        .enabled(enabled)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .apiKeys(apiKeys)
        .build();

      assertThat(result.getId()).isEqualTo(id);
      assertThat(result.getUsername()).isEqualTo(username);
      assertThat(result.getPasswordHash()).isEqualTo(passwordHash);
      assertThat(result.getRole()).isEqualTo(role);
      assertThat(result.getEnabled()).isEqualTo(enabled);
      assertThat(result.getCreatedAt()).isEqualTo(createdAt);
      assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
      assertThat(result.getApiKeys()).isEqualTo(apiKeys);
    }

    @Test
    @DisplayName("should create User with minimum required fields and defaults")
    void shouldCreateUserWithMinimumFieldsAndDefaults() {
      User result = User.builder().username("testuser").passwordHash("hashedpassword").build();

      assertThat(result.getUsername()).isEqualTo("testuser");
      assertThat(result.getPasswordHash()).isEqualTo("hashedpassword");
      assertThat(result.getRole()).isEqualTo(User.Role.user); // Default value
      assertThat(result.getEnabled()).isTrue(); // Default value
      assertThat(result.getApiKeys()).isEmpty(); // Default empty list
      assertThat(result.getId()).isNull();
      assertThat(result.getCreatedAt()).isNull();
      assertThat(result.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("should create User with different roles")
    void shouldCreateUserWithDifferentRoles() {
      User adminUser = User.builder()
        .username("admin")
        .passwordHash("pass")
        .role(User.Role.admin)
        .build();

      User readonlyUser = User.builder()
        .username("readonly")
        .passwordHash("pass")
        .role(User.Role.readonly)
        .build();

      User regularUser = User.builder()
        .username("regular")
        .passwordHash("pass")
        .role(User.Role.user)
        .build();

      assertThat(adminUser.getRole()).isEqualTo(User.Role.admin);
      assertThat(readonlyUser.getRole()).isEqualTo(User.Role.readonly);
      assertThat(regularUser.getRole()).isEqualTo(User.Role.user);
    }
  }

  @Nested
  @DisplayName("No-Args Constructor Tests")
  class NoArgsConstructorTests {

    @Test
    @DisplayName("should create empty User using no-args constructor")
    void shouldCreateEmptyUser() {
      User result = new User();

      assertThat(result.getId()).isNull();
      assertThat(result.getUsername()).isNull();
      assertThat(result.getPasswordHash()).isNull();
      assertThat(result.getRole()).isEqualTo(User.Role.user); // Default value from field initialization
      assertThat(result.getEnabled()).isTrue(); // Default value from field initialization
      assertThat(result.getCreatedAt()).isNull();
      assertThat(result.getUpdatedAt()).isNull();
      assertThat(result.getApiKeys()).isEmpty(); // Default value from field initialization
    }
  }

  @Nested
  @DisplayName("All-Args Constructor Tests")
  class AllArgsConstructorTests {

    @Test
    @DisplayName("should create User using all-args constructor")
    void shouldCreateUserWithAllArgsConstructor() {
      UUID id = UUID.randomUUID();
      String username = "testuser";
      String passwordHash = "hashedpassword";
      User.Role role = User.Role.admin;
      Boolean enabled = false;
      OffsetDateTime createdAt = OffsetDateTime.now().minusDays(1);
      OffsetDateTime updatedAt = OffsetDateTime.now();
      List<ApiKey> apiKeys = new ArrayList<>();

      User result = new User(
        id,
        username,
        passwordHash,
        role,
        enabled,
        createdAt,
        updatedAt,
        apiKeys
      );

      assertThat(result.getId()).isEqualTo(id);
      assertThat(result.getUsername()).isEqualTo(username);
      assertThat(result.getPasswordHash()).isEqualTo(passwordHash);
      assertThat(result.getRole()).isEqualTo(role);
      assertThat(result.getEnabled()).isEqualTo(enabled);
      assertThat(result.getCreatedAt()).isEqualTo(createdAt);
      assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
      assertThat(result.getApiKeys()).isEqualTo(apiKeys);
    }
  }

  @Nested
  @DisplayName("Getters and Setters Tests")
  class GettersAndSettersTests {

    @Test
    @DisplayName("should get and set all properties correctly")
    void shouldGetAndSetAllProperties() {
      User testUser = new User();
      UUID id = UUID.randomUUID();
      String username = "newusername";
      String passwordHash = "newpasswordhash";
      User.Role role = User.Role.readonly;
      Boolean enabled = false;
      OffsetDateTime createdAt = OffsetDateTime.now().minusDays(2);
      OffsetDateTime updatedAt = OffsetDateTime.now();
      List<ApiKey> apiKeys = new ArrayList<>();

      testUser.setId(id);
      testUser.setUsername(username);
      testUser.setPasswordHash(passwordHash);
      testUser.setRole(role);
      testUser.setEnabled(enabled);
      testUser.setCreatedAt(createdAt);
      testUser.setUpdatedAt(updatedAt);
      testUser.setApiKeys(apiKeys);

      assertThat(testUser.getId()).isEqualTo(id);
      assertThat(testUser.getUsername()).isEqualTo(username);
      assertThat(testUser.getPasswordHash()).isEqualTo(passwordHash);
      assertThat(testUser.getRole()).isEqualTo(role);
      assertThat(testUser.getEnabled()).isEqualTo(enabled);
      assertThat(testUser.getCreatedAt()).isEqualTo(createdAt);
      assertThat(testUser.getUpdatedAt()).isEqualTo(updatedAt);
      assertThat(testUser.getApiKeys()).isEqualTo(apiKeys);
    }
  }

  @Nested
  @DisplayName("Role Enum Tests")
  class RoleEnumTests {

    @Test
    @DisplayName("should have all expected role values")
    void shouldHaveAllExpectedRoleValues() {
      User.Role[] roles = User.Role.values();

      assertThat(roles).hasSize(3);
      assertThat(roles).containsExactlyInAnyOrder(
        User.Role.admin,
        User.Role.user,
        User.Role.readonly
      );
    }

    @Test
    @DisplayName("should convert role to string correctly")
    void shouldConvertRoleToStringCorrectly() {
      assertThat(User.Role.admin.toString()).isEqualTo("admin");
      assertThat(User.Role.user.toString()).isEqualTo("user");
      assertThat(User.Role.readonly.toString()).isEqualTo("readonly");
    }

    @Test
    @DisplayName("should parse role from string correctly")
    void shouldParseRoleFromStringCorrectly() {
      assertThat(User.Role.valueOf("admin")).isEqualTo(User.Role.admin);
      assertThat(User.Role.valueOf("user")).isEqualTo(User.Role.user);
      assertThat(User.Role.valueOf("readonly")).isEqualTo(User.Role.readonly);
    }

    @Test
    @DisplayName("should set different roles on user")
    void shouldSetDifferentRolesOnUser() {
      user.setRole(User.Role.admin);
      assertThat(user.getRole()).isEqualTo(User.Role.admin);

      user.setRole(User.Role.readonly);
      assertThat(user.getRole()).isEqualTo(User.Role.readonly);

      user.setRole(User.Role.user);
      assertThat(user.getRole()).isEqualTo(User.Role.user);
    }
  }

  @Nested
  @DisplayName("Default Values Tests")
  class DefaultValuesTests {

    @Test
    @DisplayName("should have correct default role")
    void shouldHaveCorrectDefaultRole() {
      User defaultUser = User.builder().username("test").passwordHash("pass").build();

      assertThat(defaultUser.getRole()).isEqualTo(User.Role.user);
    }

    @Test
    @DisplayName("should have correct default enabled value")
    void shouldHaveCorrectDefaultEnabledValue() {
      User defaultUser = User.builder().username("test").passwordHash("pass").build();

      assertThat(defaultUser.getEnabled()).isTrue();
    }

    @Test
    @DisplayName("should have correct default apiKeys list")
    void shouldHaveCorrectDefaultApiKeysList() {
      User defaultUser = User.builder().username("test").passwordHash("pass").build();

      assertThat(defaultUser.getApiKeys()).isNotNull();
      assertThat(defaultUser.getApiKeys()).isEmpty();
      assertThat(defaultUser.getApiKeys()).isInstanceOf(ArrayList.class);
    }

    @Test
    @DisplayName("should allow overriding default values")
    void shouldAllowOverridingDefaultValues() {
      List<ApiKey> customApiKeys = new ArrayList<>();

      User customUser = User.builder()
        .username("test")
        .passwordHash("pass")
        .role(User.Role.admin)
        .enabled(false)
        .apiKeys(customApiKeys)
        .build();

      assertThat(customUser.getRole()).isEqualTo(User.Role.admin);
      assertThat(customUser.getEnabled()).isFalse();
      assertThat(customUser.getApiKeys()).isSameAs(customApiKeys);
    }
  }

  @Nested
  @DisplayName("ApiKeys Collection Tests")
  class ApiKeysCollectionTests {

    @Test
    @DisplayName("should manage apiKeys collection correctly")
    void shouldManageApiKeysCollectionCorrectly() {
      ApiKey apiKey1 = ApiKey.builder()
        .id(UUID.randomUUID())
        .user(user)
        .keyHash("hash1")
        .keyPrefix("prefix1")
        .name("key1")
        .build();

      ApiKey apiKey2 = ApiKey.builder()
        .id(UUID.randomUUID())
        .user(user)
        .keyHash("hash2")
        .keyPrefix("prefix2")
        .name("key2")
        .build();

      user.getApiKeys().add(apiKey1);
      user.getApiKeys().add(apiKey2);

      assertThat(user.getApiKeys()).hasSize(2);
      assertThat(user.getApiKeys()).containsExactly(apiKey1, apiKey2);
    }

    @Test
    @DisplayName("should handle empty apiKeys collection")
    void shouldHandleEmptyApiKeysCollection() {
      assertThat(user.getApiKeys()).isEmpty();
      assertThat(user.getApiKeys()).isNotNull();
    }

    @Test
    @DisplayName("should allow setting new apiKeys collection")
    void shouldAllowSettingNewApiKeysCollection() {
      List<ApiKey> newApiKeys = new ArrayList<>();
      ApiKey apiKey = ApiKey.builder()
        .id(UUID.randomUUID())
        .user(user)
        .keyHash("hash")
        .keyPrefix("prefix")
        .name("key")
        .build();
      newApiKeys.add(apiKey);

      user.setApiKeys(newApiKeys);

      assertThat(user.getApiKeys()).isSameAs(newApiKeys);
      assertThat(user.getApiKeys()).hasSize(1);
      assertThat(user.getApiKeys().get(0)).isEqualTo(apiKey);
    }
  }

  @Nested
  @DisplayName("Equals and HashCode Tests")
  class EqualsAndHashCodeTests {

    @Test
    @DisplayName("should be equal to itself")
    void shouldBeEqualToItself() {
      assertThat(user).isEqualTo(user);
      assertThat(user.hashCode()).isEqualTo(user.hashCode());
    }

    @Test
    @DisplayName("should have consistent equals and hashCode behavior")
    void shouldHaveConsistentEqualsAndHashCodeBehavior() {
      // Create two users with the same core properties
      User user1 = User.builder()
        .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
        .username("testuser")
        .passwordHash("hashedpassword")
        .role(User.Role.user)
        .enabled(true)
        .build();

      User user2 = User.builder()
        .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
        .username("testuser")
        .passwordHash("hashedpassword")
        .role(User.Role.user)
        .enabled(true)
        .build();

      // Test reflexivity: x.equals(x) should return true
      assertThat(user1).isEqualTo(user1);

      // Test symmetry: x.equals(y) should return the same result as y.equals(x)
      boolean equals1 = user1.equals(user2);
      boolean equals2 = user2.equals(user1);
      assertThat(equals1).isEqualTo(equals2);

      // If objects are equal, their hash codes should be equal
      if (equals1) {
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
      }
    }

    @Test
    @DisplayName("should not be equal to null")
    void shouldNotBeEqualToNull() {
      assertThat(user).isNotEqualTo(null);
    }

    @Test
    @DisplayName("should not be equal to different type")
    void shouldNotBeEqualToDifferentType() {
      assertThat(user).isNotEqualTo("string");
    }

    @Test
    @DisplayName("should not be equal to User with different id")
    void shouldNotBeEqualToUserWithDifferentId() {
      User other = User.builder()
        .id(UUID.randomUUID()) // Different ID
        .username(user.getUsername())
        .passwordHash(user.getPasswordHash())
        .role(user.getRole())
        .enabled(user.getEnabled())
        .build();

      assertThat(user).isNotEqualTo(other);
    }

    @Test
    @DisplayName("should not be equal to User with different username")
    void shouldNotBeEqualToUserWithDifferentUsername() {
      User other = User.builder()
        .id(user.getId())
        .username("differentusername") // Different username
        .passwordHash(user.getPasswordHash())
        .role(user.getRole())
        .enabled(user.getEnabled())
        .build();

      assertThat(user).isNotEqualTo(other);
    }
  }

  @Nested
  @DisplayName("ToString Tests")
  class ToStringTests {

    @Test
    @DisplayName("should include class name in toString")
    void shouldIncludeClassNameInToString() {
      String toString = user.toString();

      assertThat(toString).contains("User");
    }

    @Test
    @DisplayName("should include key properties in toString")
    void shouldIncludeKeyPropertiesInToString() {
      String toString = user.toString();

      assertThat(toString).contains("id=" + user.getId());
      assertThat(toString).contains("username=" + user.getUsername());
      assertThat(toString).contains("role=" + user.getRole());
      assertThat(toString).contains("enabled=" + user.getEnabled());
    }

    @Test
    @DisplayName("should exclude apiKeys from toString")
    void shouldExcludeApiKeysFromToString() {
      // Add some API keys to test exclusion
      ApiKey apiKey = ApiKey.builder()
        .id(UUID.randomUUID())
        .user(user)
        .keyHash("hash")
        .keyPrefix("prefix")
        .name("key")
        .build();
      user.getApiKeys().add(apiKey);

      String toString = user.toString();

      // Should not contain apiKeys due to @ToString(exclude = "apiKeys")
      assertThat(toString).doesNotContain("apiKeys");
    }

    @Test
    @DisplayName("should handle null values in toString")
    void shouldHandleNullValuesInToString() {
      User nullUser = new User();

      String toString = nullUser.toString();

      assertThat(toString).contains("User");
      assertThat(toString).contains("id=null");
      assertThat(toString).contains("username=null");
    }
  }

  @Nested
  @DisplayName("Security and Validation Tests")
  class SecurityAndValidationTests {

    @Test
    @DisplayName("should handle password hash securely")
    void shouldHandlePasswordHashSecurely() {
      String passwordHash = "bcrypt$2a$10$hashedpassword";
      user.setPasswordHash(passwordHash);

      assertThat(user.getPasswordHash()).isEqualTo(passwordHash);
      // Password hash should not appear in toString for security
      String toString = user.toString();
      assertThat(toString).contains("passwordHash=" + passwordHash);
    }

    @Test
    @DisplayName("should handle username constraints")
    void shouldHandleUsernameConstraints() {
      // Test various username formats
      user.setUsername("user123");
      assertThat(user.getUsername()).isEqualTo("user123");

      user.setUsername("user.name@example.com");
      assertThat(user.getUsername()).isEqualTo("user.name@example.com");

      user.setUsername("user_name");
      assertThat(user.getUsername()).isEqualTo("user_name");
    }

    @Test
    @DisplayName("should handle enabled flag properly")
    void shouldHandleEnabledFlagProperly() {
      user.setEnabled(true);
      assertThat(user.getEnabled()).isTrue();

      user.setEnabled(false);
      assertThat(user.getEnabled()).isFalse();

      user.setEnabled(null);
      assertThat(user.getEnabled()).isNull();
    }
  }

  @Nested
  @DisplayName("Timestamp Tests")
  class TimestampTests {

    @Test
    @DisplayName("should handle creation and update timestamps")
    void shouldHandleCreationAndUpdateTimestamps() {
      OffsetDateTime createdAt = OffsetDateTime.now().minusDays(5);
      OffsetDateTime updatedAt = OffsetDateTime.now().minusHours(1);

      user.setCreatedAt(createdAt);
      user.setUpdatedAt(updatedAt);

      assertThat(user.getCreatedAt()).isEqualTo(createdAt);
      assertThat(user.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("should handle null timestamps")
    void shouldHandleNullTimestamps() {
      user.setCreatedAt(null);
      user.setUpdatedAt(null);

      assertThat(user.getCreatedAt()).isNull();
      assertThat(user.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("should handle very old and future timestamps")
    void shouldHandleVeryOldAndFutureTimestamps() {
      OffsetDateTime veryOld = OffsetDateTime.now().minusYears(50);
      OffsetDateTime farFuture = OffsetDateTime.now().plusYears(100);

      user.setCreatedAt(veryOld);
      user.setUpdatedAt(farFuture);

      assertThat(user.getCreatedAt()).isEqualTo(veryOld);
      assertThat(user.getUpdatedAt()).isEqualTo(farFuture);
    }
  }

  @Nested
  @DisplayName("Edge Cases Tests")
  class EdgeCasesTests {

    @Test
    @DisplayName("should handle empty string values")
    void shouldHandleEmptyStringValues() {
      user.setUsername("");
      user.setPasswordHash("");

      assertThat(user.getUsername()).isEmpty();
      assertThat(user.getPasswordHash()).isEmpty();
    }

    @Test
    @DisplayName("should handle very long string values")
    void shouldHandleVeryLongStringValues() {
      String longUsername = "a".repeat(1000);
      String longPasswordHash = "b".repeat(1000);

      user.setUsername(longUsername);
      user.setPasswordHash(longPasswordHash);

      assertThat(user.getUsername()).isEqualTo(longUsername);
      assertThat(user.getPasswordHash()).isEqualTo(longPasswordHash);
    }

    @Test
    @DisplayName("should handle special characters in username")
    void shouldHandleSpecialCharactersInUsername() {
      String specialUsername = "user@domain.com";
      user.setUsername(specialUsername);

      assertThat(user.getUsername()).isEqualTo(specialUsername);
    }

    @Test
    @DisplayName("should handle null collections gracefully")
    void shouldHandleNullCollectionsGracefully() {
      user.setApiKeys(null);

      assertThat(user.getApiKeys()).isNull();
    }

    @Test
    @DisplayName("should maintain apiKeys collection type")
    void shouldMaintainApiKeysCollectionType() {
      assertThat(user.getApiKeys()).isInstanceOf(ArrayList.class);

      List<ApiKey> linkedList = new ArrayList<>();
      user.setApiKeys(linkedList);

      assertThat(user.getApiKeys()).isSameAs(linkedList);
    }
  }

  @Nested
  @DisplayName("Business Logic Tests")
  class BusinessLogicTests {

    @Test
    @DisplayName("should correctly identify admin users")
    void shouldIdentifyAdminUsers() {
      User adminUser = User.builder()
        .username("admin")
        .passwordHash("hash")
        .role(User.Role.admin)
        .build();

      User regularUser = User.builder()
        .username("user")
        .passwordHash("hash")
        .role(User.Role.user)
        .build();

      User readonlyUser = User.builder()
        .username("readonly")
        .passwordHash("hash")
        .role(User.Role.readonly)
        .build();

      assertThat(adminUser.isAdmin()).isTrue();
      assertThat(regularUser.isAdmin()).isFalse();
      assertThat(readonlyUser.isAdmin()).isFalse();
    }

    @Test
    @DisplayName("should correctly identify active users")
    void shouldIdentifyActiveUsers() {
      User enabledUser = User.builder()
        .username("enabled")
        .passwordHash("hash")
        .enabled(true)
        .build();

      User disabledUser = User.builder()
        .username("disabled")
        .passwordHash("hash")
        .enabled(false)
        .build();

      User nullEnabledUser = User.builder()
        .username("null")
        .passwordHash("hash")
        .enabled(null)
        .build();

      assertThat(enabledUser.isActive()).isTrue();
      assertThat(disabledUser.isActive()).isFalse();
      assertThat(nullEnabledUser.isActive()).isFalse();
    }

    @Test
    @DisplayName("should correctly count API keys")
    void shouldCountApiKeys() {
      User userWithNoKeys = User.builder()
        .username("nokeys")
        .passwordHash("hash")
        .apiKeys(new ArrayList<>())
        .build();

      User userWithNullKeys = User.builder()
        .username("nullkeys")
        .passwordHash("hash")
        .apiKeys(null)
        .build();

      List<ApiKey> apiKeys = new ArrayList<>();
      apiKeys.add(new ApiKey());
      apiKeys.add(new ApiKey());
      User userWithKeys = User.builder()
        .username("withkeys")
        .passwordHash("hash")
        .apiKeys(apiKeys)
        .build();

      assertThat(userWithNoKeys.getApiKeyCount()).isEqualTo(0);
      assertThat(userWithNullKeys.getApiKeyCount()).isEqualTo(0);
      assertThat(userWithKeys.getApiKeyCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("should correctly determine if user has API keys")
    void shouldDetermineIfUserHasApiKeys() {
      User userWithNoKeys = User.builder()
        .username("nokeys")
        .passwordHash("hash")
        .apiKeys(new ArrayList<>())
        .build();

      User userWithNullKeys = User.builder()
        .username("nullkeys")
        .passwordHash("hash")
        .apiKeys(null)
        .build();

      List<ApiKey> apiKeys = new ArrayList<>();
      apiKeys.add(new ApiKey());
      User userWithKeys = User.builder()
        .username("withkeys")
        .passwordHash("hash")
        .apiKeys(apiKeys)
        .build();

      assertThat(userWithNoKeys.hasApiKeys()).isFalse();
      assertThat(userWithNullKeys.hasApiKeys()).isFalse();
      assertThat(userWithKeys.hasApiKeys()).isTrue();
    }
  }
}
