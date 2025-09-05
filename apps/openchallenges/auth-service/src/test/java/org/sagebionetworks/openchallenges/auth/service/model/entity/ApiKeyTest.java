package org.sagebionetworks.openchallenges.auth.service.model.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ApiKey Entity Tests")
class ApiKeyTest {

  private ApiKey apiKey;
  private User user;

  @BeforeEach
  void setUp() {
    user = User.builder()
      .id(UUID.randomUUID())
      .username("testuser")
      .passwordHash("hashedpassword")
      .build();

    apiKey = ApiKey.builder()
      .id(UUID.randomUUID())
      .user(user)
      .keyHash("hashedkey123")
      .keyPrefix("ak_12345")
      .name("Test API Key")
      .expiresAt(OffsetDateTime.now().plusDays(30))
      .lastUsedAt(OffsetDateTime.now().minusHours(1))
      .createdAt(OffsetDateTime.now().minusDays(1))
      .updatedAt(OffsetDateTime.now().minusHours(2))
      .plainKey("plain_key_value")
      .build();
  }

  @Nested
  @DisplayName("Builder Pattern Tests")
  class BuilderTests {

    @Test
    @DisplayName("should create ApiKey with all fields using builder")
    void shouldCreateApiKeyWithAllFields() {
      UUID id = UUID.randomUUID();
      String keyHash = "test_hash";
      String keyPrefix = "ak_test";
      String name = "Test Key";
      OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(30);
      OffsetDateTime lastUsedAt = OffsetDateTime.now().minusHours(1);
      OffsetDateTime createdAt = OffsetDateTime.now().minusDays(1);
      OffsetDateTime updatedAt = OffsetDateTime.now();
      String plainKey = "plain_key";

      ApiKey result = ApiKey.builder()
        .id(id)
        .user(user)
        .keyHash(keyHash)
        .keyPrefix(keyPrefix)
        .name(name)
        .expiresAt(expiresAt)
        .lastUsedAt(lastUsedAt)
        .createdAt(createdAt)
        .updatedAt(updatedAt)
        .plainKey(plainKey)
        .build();

      assertThat(result.getId()).isEqualTo(id);
      assertThat(result.getUser()).isEqualTo(user);
      assertThat(result.getKeyHash()).isEqualTo(keyHash);
      assertThat(result.getKeyPrefix()).isEqualTo(keyPrefix);
      assertThat(result.getName()).isEqualTo(name);
      assertThat(result.getExpiresAt()).isEqualTo(expiresAt);
      assertThat(result.getLastUsedAt()).isEqualTo(lastUsedAt);
      assertThat(result.getCreatedAt()).isEqualTo(createdAt);
      assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
      assertThat(result.getPlainKey()).isEqualTo(plainKey);
    }

    @Test
    @DisplayName("should create ApiKey with minimum required fields")
    void shouldCreateApiKeyWithMinimumFields() {
      ApiKey result = ApiKey.builder()
        .user(user)
        .keyHash("hash")
        .keyPrefix("prefix")
        .name("name")
        .build();

      assertThat(result.getUser()).isEqualTo(user);
      assertThat(result.getKeyHash()).isEqualTo("hash");
      assertThat(result.getKeyPrefix()).isEqualTo("prefix");
      assertThat(result.getName()).isEqualTo("name");
      assertThat(result.getExpiresAt()).isNull();
      assertThat(result.getLastUsedAt()).isNull();
      assertThat(result.getPlainKey()).isNull();
    }
  }

  @Nested
  @DisplayName("No-Args Constructor Tests")
  class NoArgsConstructorTests {

    @Test
    @DisplayName("should create empty ApiKey using no-args constructor")
    void shouldCreateEmptyApiKey() {
      ApiKey result = new ApiKey();

      assertThat(result.getId()).isNull();
      assertThat(result.getUser()).isNull();
      assertThat(result.getKeyHash()).isNull();
      assertThat(result.getKeyPrefix()).isNull();
      assertThat(result.getName()).isNull();
      assertThat(result.getExpiresAt()).isNull();
      assertThat(result.getLastUsedAt()).isNull();
      assertThat(result.getCreatedAt()).isNull();
      assertThat(result.getUpdatedAt()).isNull();
      assertThat(result.getPlainKey()).isNull();
    }
  }

  @Nested
  @DisplayName("All-Args Constructor Tests")
  class AllArgsConstructorTests {

    @Test
    @DisplayName("should create ApiKey using all-args constructor")
    void shouldCreateApiKeyWithAllArgsConstructor() {
      UUID id = UUID.randomUUID();
      String keyHash = "test_hash";
      String keyPrefix = "ak_test";
      String name = "Test Key";
      OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(30);
      OffsetDateTime lastUsedAt = OffsetDateTime.now().minusHours(1);
      OffsetDateTime createdAt = OffsetDateTime.now().minusDays(1);
      OffsetDateTime updatedAt = OffsetDateTime.now();
      String plainKey = "plain_key";

      ApiKey result = new ApiKey(
        id,
        user,
        keyHash,
        keyPrefix,
        name,
        expiresAt,
        lastUsedAt,
        createdAt,
        updatedAt,
        null, // clientId
        null, // allowedScopes
        plainKey
      );

      assertThat(result.getId()).isEqualTo(id);
      assertThat(result.getUser()).isEqualTo(user);
      assertThat(result.getKeyHash()).isEqualTo(keyHash);
      assertThat(result.getKeyPrefix()).isEqualTo(keyPrefix);
      assertThat(result.getName()).isEqualTo(name);
      assertThat(result.getExpiresAt()).isEqualTo(expiresAt);
      assertThat(result.getLastUsedAt()).isEqualTo(lastUsedAt);
      assertThat(result.getCreatedAt()).isEqualTo(createdAt);
      assertThat(result.getUpdatedAt()).isEqualTo(updatedAt);
      assertThat(result.getPlainKey()).isEqualTo(plainKey);
    }
  }

  @Nested
  @DisplayName("Getters and Setters Tests")
  class GettersAndSettersTests {

    @Test
    @DisplayName("should get and set all properties correctly")
    void shouldGetAndSetAllProperties() {
      ApiKey testKey = new ApiKey();
      UUID id = UUID.randomUUID();
      String keyHash = "new_hash";
      String keyPrefix = "ak_new";
      String name = "New Name";
      OffsetDateTime expiresAt = OffsetDateTime.now().plusDays(60);
      OffsetDateTime lastUsedAt = OffsetDateTime.now();
      OffsetDateTime createdAt = OffsetDateTime.now().minusDays(2);
      OffsetDateTime updatedAt = OffsetDateTime.now();
      String plainKey = "new_plain_key";

      testKey.setId(id);
      testKey.setUser(user);
      testKey.setKeyHash(keyHash);
      testKey.setKeyPrefix(keyPrefix);
      testKey.setName(name);
      testKey.setExpiresAt(expiresAt);
      testKey.setLastUsedAt(lastUsedAt);
      testKey.setCreatedAt(createdAt);
      testKey.setUpdatedAt(updatedAt);
      testKey.setPlainKey(plainKey);

      assertThat(testKey.getId()).isEqualTo(id);
      assertThat(testKey.getUser()).isEqualTo(user);
      assertThat(testKey.getKeyHash()).isEqualTo(keyHash);
      assertThat(testKey.getKeyPrefix()).isEqualTo(keyPrefix);
      assertThat(testKey.getName()).isEqualTo(name);
      assertThat(testKey.getExpiresAt()).isEqualTo(expiresAt);
      assertThat(testKey.getLastUsedAt()).isEqualTo(lastUsedAt);
      assertThat(testKey.getCreatedAt()).isEqualTo(createdAt);
      assertThat(testKey.getUpdatedAt()).isEqualTo(updatedAt);
      assertThat(testKey.getPlainKey()).isEqualTo(plainKey);
    }
  }

  @Nested
  @DisplayName("Helper Methods Tests")
  class HelperMethodsTests {

    @Nested
    @DisplayName("isExpired() Tests")
    class IsExpiredTests {

      @Test
      @DisplayName("should return false when expiresAt is null")
      void shouldReturnFalseWhenExpiresAtIsNull() {
        apiKey.setExpiresAt(null);

        assertThat(apiKey.isExpired()).isFalse();
      }

      @Test
      @DisplayName("should return false when expiresAt is in the future")
      void shouldReturnFalseWhenExpiresAtIsInFuture() {
        apiKey.setExpiresAt(OffsetDateTime.now().plusDays(1));

        assertThat(apiKey.isExpired()).isFalse();
      }

      @Test
      @DisplayName("should return true when expiresAt is in the past")
      void shouldReturnTrueWhenExpiresAtIsInPast() {
        apiKey.setExpiresAt(OffsetDateTime.now().minusDays(1));

        assertThat(apiKey.isExpired()).isTrue();
      }

      @Test
      @DisplayName("should return true when expiresAt is exactly now")
      void shouldReturnTrueWhenExpiresAtIsNow() {
        // Set to a time slightly in the past to account for execution time
        apiKey.setExpiresAt(OffsetDateTime.now().minusNanos(1000));

        assertThat(apiKey.isExpired()).isTrue();
      }
    }

    @Nested
    @DisplayName("updateLastUsed() Tests")
    class UpdateLastUsedTests {

      @Test
      @DisplayName("should update lastUsedAt to current time")
      void shouldUpdateLastUsedAtToCurrentTime() {
        OffsetDateTime beforeUpdate = OffsetDateTime.now().minusSeconds(1);
        apiKey.setLastUsedAt(OffsetDateTime.now().minusHours(5));

        apiKey.updateLastUsed();

        OffsetDateTime afterUpdate = OffsetDateTime.now().plusSeconds(1);
        assertThat(apiKey.getLastUsedAt()).isAfter(beforeUpdate);
        assertThat(apiKey.getLastUsedAt()).isBefore(afterUpdate);
      }

      @Test
      @DisplayName("should update lastUsedAt when it was null")
      void shouldUpdateLastUsedAtWhenNull() {
        apiKey.setLastUsedAt(null);
        OffsetDateTime beforeUpdate = OffsetDateTime.now().minusSeconds(1);

        apiKey.updateLastUsed();

        OffsetDateTime afterUpdate = OffsetDateTime.now().plusSeconds(1);
        assertThat(apiKey.getLastUsedAt()).isNotNull();
        assertThat(apiKey.getLastUsedAt()).isAfter(beforeUpdate);
        assertThat(apiKey.getLastUsedAt()).isBefore(afterUpdate);
      }

      @Test
      @DisplayName("should update lastUsedAt to a newer time")
      void shouldUpdateLastUsedAtToNewerTime() {
        OffsetDateTime originalTime = OffsetDateTime.now().minusHours(2);
        apiKey.setLastUsedAt(originalTime);

        apiKey.updateLastUsed();

        assertThat(apiKey.getLastUsedAt()).isAfter(originalTime);
      }
    }
  }

  @Nested
  @DisplayName("Equals and HashCode Tests")
  class EqualsAndHashCodeTests {

    @Test
    @DisplayName("should be equal to itself")
    void shouldBeEqualToItself() {
      assertThat(apiKey).isEqualTo(apiKey);
      assertThat(apiKey.hashCode()).isEqualTo(apiKey.hashCode());
    }

    @Test
    @DisplayName("should be equal to another ApiKey with same values")
    void shouldBeEqualToAnotherApiKeyWithSameValues() {
      ApiKey other = ApiKey.builder()
        .id(apiKey.getId())
        .user(apiKey.getUser())
        .keyHash(apiKey.getKeyHash())
        .keyPrefix(apiKey.getKeyPrefix())
        .name(apiKey.getName())
        .expiresAt(apiKey.getExpiresAt())
        .lastUsedAt(apiKey.getLastUsedAt())
        .createdAt(apiKey.getCreatedAt())
        .updatedAt(apiKey.getUpdatedAt())
        .plainKey(apiKey.getPlainKey())
        .build();

      assertThat(apiKey).isEqualTo(other);
      assertThat(apiKey.hashCode()).isEqualTo(other.hashCode());
    }

    @Test
    @DisplayName("should not be equal to null")
    void shouldNotBeEqualToNull() {
      assertThat(apiKey).isNotEqualTo(null);
    }

    @Test
    @DisplayName("should not be equal to different type")
    void shouldNotBeEqualToDifferentType() {
      assertThat(apiKey).isNotEqualTo("string");
    }

    @Test
    @DisplayName("should not be equal to ApiKey with different id")
    void shouldNotBeEqualToApiKeyWithDifferentId() {
      ApiKey other = ApiKey.builder()
        .id(UUID.randomUUID()) // Different ID
        .user(apiKey.getUser())
        .keyHash(apiKey.getKeyHash())
        .keyPrefix(apiKey.getKeyPrefix())
        .name(apiKey.getName())
        .build();

      assertThat(apiKey).isNotEqualTo(other);
    }
  }

  @Nested
  @DisplayName("ToString Tests")
  class ToStringTests {

    @Test
    @DisplayName("should include class name in toString")
    void shouldIncludeClassNameInToString() {
      String toString = apiKey.toString();

      assertThat(toString).contains("ApiKey");
    }

    @Test
    @DisplayName("should include key properties in toString")
    void shouldIncludeKeyPropertiesInToString() {
      String toString = apiKey.toString();

      assertThat(toString).contains("id=" + apiKey.getId());
      assertThat(toString).contains("keyHash=" + apiKey.getKeyHash());
      assertThat(toString).contains("keyPrefix=" + apiKey.getKeyPrefix());
      assertThat(toString).contains("name=" + apiKey.getName());
    }

    @Test
    @DisplayName("should handle null values in toString")
    void shouldHandleNullValuesInToString() {
      ApiKey nullApiKey = new ApiKey();

      String toString = nullApiKey.toString();

      assertThat(toString).contains("ApiKey");
      assertThat(toString).contains("id=null");
    }
  }

  @Nested
  @DisplayName("Entity Relationship Tests")
  class EntityRelationshipTests {

    @Test
    @DisplayName("should maintain user relationship")
    void shouldMaintainUserRelationship() {
      assertThat(apiKey.getUser()).isNotNull();
      assertThat(apiKey.getUser()).isEqualTo(user);
      assertThat(apiKey.getUser().getId()).isEqualTo(user.getId());
      assertThat(apiKey.getUser().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("should allow setting different user")
    void shouldAllowSettingDifferentUser() {
      User newUser = User.builder()
        .id(UUID.randomUUID())
        .username("newuser")
        .passwordHash("hashedpassword")
        .build();

      apiKey.setUser(newUser);

      assertThat(apiKey.getUser()).isEqualTo(newUser);
      assertThat(apiKey.getUser()).isNotEqualTo(user);
    }
  }

  @Nested
  @DisplayName("Transient Field Tests")
  class TransientFieldTests {

    @Test
    @DisplayName("should handle plainKey as transient field")
    void shouldHandlePlainKeyAsTransientField() {
      String plainKeyValue = "test_plain_key_12345";

      apiKey.setPlainKey(plainKeyValue);

      assertThat(apiKey.getPlainKey()).isEqualTo(plainKeyValue);
    }

    @Test
    @DisplayName("should allow null plainKey")
    void shouldAllowNullPlainKey() {
      apiKey.setPlainKey(null);

      assertThat(apiKey.getPlainKey()).isNull();
    }
  }

  @Nested
  @DisplayName("Edge Cases Tests")
  class EdgeCasesTests {

    @Test
    @DisplayName("should handle very long expiration dates")
    void shouldHandleVeryLongExpirationDates() {
      OffsetDateTime farFuture = OffsetDateTime.now().plusYears(100);
      apiKey.setExpiresAt(farFuture);

      assertThat(apiKey.getExpiresAt()).isEqualTo(farFuture);
      assertThat(apiKey.isExpired()).isFalse();
    }

    @Test
    @DisplayName("should handle very old creation dates")
    void shouldHandleVeryOldCreationDates() {
      OffsetDateTime veryOld = OffsetDateTime.now().minusYears(50);
      apiKey.setCreatedAt(veryOld);

      assertThat(apiKey.getCreatedAt()).isEqualTo(veryOld);
    }

    @Test
    @DisplayName("should handle empty string values")
    void shouldHandleEmptyStringValues() {
      apiKey.setKeyHash("");
      apiKey.setKeyPrefix("");
      apiKey.setName("");
      apiKey.setPlainKey("");

      assertThat(apiKey.getKeyHash()).isEmpty();
      assertThat(apiKey.getKeyPrefix()).isEmpty();
      assertThat(apiKey.getName()).isEmpty();
      assertThat(apiKey.getPlainKey()).isEmpty();
    }
  }
}
