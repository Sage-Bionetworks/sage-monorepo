package org.sagebionetworks.openchallenges.auth.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.auth.service.configuration.AuthServiceProperties;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.repository.ApiKeyRepository;
import org.sagebionetworks.openchallenges.auth.service.util.ScopeUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ApiKeyService Tests")
class ApiKeyServiceTest {

  @Mock
  private ApiKeyRepository apiKeyRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private AuthServiceProperties authServiceProperties;

  @Mock
  private AuthServiceProperties.ApiKeyConfig apiKeyConfig;

  @Mock
  private RegisteredClientRepository registeredClientRepository;

  @Mock
  private JdbcTemplate jdbcTemplate;

  @Mock
  private ScopeUtil scopeUtil;

  private ApiKeyService apiKeyService;

  private User testUser;
  private ApiKey testApiKey;

  @BeforeEach
  void setUp() {
    // Setup lenient stubs for properties that are commonly used
    lenient().when(authServiceProperties.getApiKey()).thenReturn(apiKeyConfig);
    lenient().when(apiKeyConfig.getPrefix()).thenReturn("oc_test_");
    lenient().when(apiKeyConfig.getLength()).thenReturn(40);

    // Setup lenient stubs for OAuth2 repository operations
    lenient().doNothing().when(registeredClientRepository).save(any());
    lenient().when(jdbcTemplate.queryForList(anyString(), (Object[]) any())).thenReturn(List.of());
    lenient().when(jdbcTemplate.update(anyString(), (Object[]) any())).thenReturn(0);

    apiKeyService = new ApiKeyService(
      apiKeyRepository,
      passwordEncoder,
      authServiceProperties,
      registeredClientRepository,
      jdbcTemplate,
      scopeUtil
    );

    testUser = User.builder()
      .id(UUID.randomUUID())
      .username("testuser")
      .passwordHash("hashedpassword")
      .role(User.Role.user)
      .build();

    testApiKey = ApiKey.builder()
      .id(UUID.randomUUID())
      .user(testUser)
      .name("Test API Key")
      .keyPrefix("oc_test_")
      .keyHash("hashedkey")
      .createdAt(OffsetDateTime.now())
      .expiresAt(OffsetDateTime.now().plusDays(30))
      .build();
  }

  @DisplayName("Constructor Tests")
  @Nested
  class ConstructorTests {

    @Test
    @DisplayName("should initialize all dependencies correctly")
    void shouldInitializeAllDependenciesCorrectly() {
      // When
      ApiKeyService service = new ApiKeyService(
        apiKeyRepository,
        passwordEncoder,
        authServiceProperties,
        registeredClientRepository,
        jdbcTemplate,
        scopeUtil
      );

      // Then
      assertThat(service).isNotNull();
      // The service should be properly initialized - we verify this by testing that methods work
      when(apiKeyRepository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(List.of());

      List<ApiKey> result = service.getUserApiKeys(testUser);
      assertThat(result).isEmpty();
      verify(apiKeyRepository).findByUserOrderByCreatedAtDesc(testUser);
    }
  }

  @DisplayName("Create API Key Tests")
  @Nested
  class CreateApiKeyTests {

    @Test
    @DisplayName("should create API key with expiration")
    void shouldCreateApiKeyWithExpiration() {
      // Arrange
      String name = "Test API Key";
      Integer expiresInDays = 30;
      String hashedKey = "hashedkey";

      when(passwordEncoder.encode(anyString())).thenReturn(hashedKey);
      when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(invocation -> {
        ApiKey apiKey = invocation.getArgument(0);
        apiKey.setId(UUID.randomUUID());
        return apiKey;
      });

      // Act
      ApiKey result = apiKeyService.createApiKey(testUser, name, expiresInDays);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.getUser()).isEqualTo(testUser);
      assertThat(result.getName()).isEqualTo(name);
      assertThat(result.getKeyPrefix()).isEqualTo("oc_test_");
      assertThat(result.getKeyHash()).isEqualTo(hashedKey);
      assertThat(result.getExpiresAt()).isNotNull();
      assertThat(result.getExpiresAt()).isAfter(OffsetDateTime.now().plusDays(29));
      assertThat(result.getExpiresAt()).isBefore(OffsetDateTime.now().plusDays(31));
      assertThat(result.getPlainKey()).isNotNull();
      assertThat(result.getPlainKey()).startsWith("oc_test_");

      verify(passwordEncoder, times(2)).encode(anyString()); // Once for API key hash, once for OAuth2 client secret
      verify(apiKeyRepository, times(2)).save(any(ApiKey.class)); // Once for initial save, once for updating scopes
      verify(registeredClientRepository).save(any()); // OAuth2 client creation
    }

    @Test
    @DisplayName("should create API key without expiration")
    void shouldCreateApiKeyWithoutExpiration() {
      // Arrange
      String name = "Permanent API Key";
      String hashedKey = "hashedkey";

      when(passwordEncoder.encode(anyString())).thenReturn(hashedKey);
      when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(invocation -> {
        ApiKey apiKey = invocation.getArgument(0);
        apiKey.setId(UUID.randomUUID());
        return apiKey;
      });

      // Act
      ApiKey result = apiKeyService.createApiKey(testUser, name, null);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.getUser()).isEqualTo(testUser);
      assertThat(result.getName()).isEqualTo(name);
      assertThat(result.getExpiresAt()).isNull();
      assertThat(result.getPlainKey()).isNotNull();
      assertThat(result.getPlainKey()).startsWith("oc_test_");

      verify(passwordEncoder, times(2)).encode(anyString()); // Once for API key hash, once for OAuth2 client secret
      verify(apiKeyRepository, times(2)).save(any(ApiKey.class)); // Once for initial save, once for updating scopes
      verify(registeredClientRepository).save(any()); // OAuth2 client creation
    }

    @Test
    @DisplayName("should create API key with zero expiration days")
    void shouldCreateApiKeyWithZeroExpirationDays() {
      // Arrange
      String name = "No Expiration API Key";
      String hashedKey = "hashedkey";

      when(passwordEncoder.encode(anyString())).thenReturn(hashedKey);
      when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(invocation -> {
        ApiKey apiKey = invocation.getArgument(0);
        apiKey.setId(UUID.randomUUID());
        return apiKey;
      });

      // Act
      ApiKey result = apiKeyService.createApiKey(testUser, name, 0);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.getExpiresAt()).isNull(); // Zero days should result in no expiration

      verify(passwordEncoder, times(2)).encode(anyString()); // Once for API key hash, once for OAuth2 client secret
      verify(apiKeyRepository, times(2)).save(any(ApiKey.class)); // Once for initial save, once for updating scopes
      verify(registeredClientRepository).save(any()); // OAuth2 client creation
    }

    @Test
    @DisplayName("should generate unique API keys on multiple calls")
    void shouldGenerateUniqueApiKeysOnMultipleCalls() {
      // Arrange
      String name = "Test API Key";
      String hashedKey1 = "hashedkey1";
      String hashedKey2 = "hashedkey2";

      when(passwordEncoder.encode(anyString())).thenReturn(hashedKey1, hashedKey2);
      when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(invocation -> {
        ApiKey apiKey = invocation.getArgument(0);
        apiKey.setId(UUID.randomUUID());
        return apiKey;
      });

      // Act
      ApiKey result1 = apiKeyService.createApiKey(testUser, name, 30);
      ApiKey result2 = apiKeyService.createApiKey(testUser, name, 30);

      // Assert
      assertThat(result1.getPlainKey()).isNotEqualTo(result2.getPlainKey());
      assertThat(result1.getKeyHash()).isNotEqualTo(result2.getKeyHash());

      verify(passwordEncoder, times(4)).encode(anyString()); // 2 API keys * 2 encodes each = 4 total
      verify(apiKeyRepository, times(4)).save(any(ApiKey.class)); // 2 API keys * 2 saves each = 4 total
      verify(registeredClientRepository, times(2)).save(any()); // 2 OAuth2 clients
    }
  }

  @DisplayName("Generate API Key Tests")
  @Nested
  class GenerateApiKeyTests {

    @Test
    @DisplayName("should generate API key for login session without expiration")
    void shouldGenerateApiKeyForLoginSessionWithoutExpiration() {
      // Arrange
      String name = "Login Session";
      String hashedKey = "hashedkey";

      when(passwordEncoder.encode(anyString())).thenReturn(hashedKey);
      when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(invocation -> {
        ApiKey apiKey = invocation.getArgument(0);
        apiKey.setId(UUID.randomUUID());
        return apiKey;
      });

      // Act
      ApiKey result = apiKeyService.generateApiKey(testUser, name);

      // Assert
      assertThat(result).isNotNull();
      assertThat(result.getUser()).isEqualTo(testUser);
      assertThat(result.getName()).isEqualTo(name);
      assertThat(result.getExpiresAt()).isNull(); // Login session keys should not expire
      assertThat(result.getPlainKey()).isNotNull();
      assertThat(result.getPlainKey()).startsWith("oc_test_");

      verify(passwordEncoder, times(2)).encode(anyString()); // Once for API key hash, once for OAuth2 client secret
      verify(apiKeyRepository, times(2)).save(any(ApiKey.class)); // Once for initial save, once for updating scopes
      verify(registeredClientRepository).save(any()); // OAuth2 client creation
    }
  }

  @DisplayName("Find By Key Value Tests")
  @Nested
  class FindByKeyValueTests {

    @Test
    @DisplayName("should find API key by valid key value")
    void shouldFindApiKeyByValidKeyValue() {
      // Arrange
      String apiKeyValue = "oc_test_validkey123";
      when(apiKeyRepository.findAll()).thenReturn(List.of(testApiKey));
      when(passwordEncoder.matches(apiKeyValue, testApiKey.getKeyHash())).thenReturn(true);
      when(apiKeyRepository.save(any(ApiKey.class))).thenReturn(testApiKey);

      // Act
      Optional<ApiKey> result = apiKeyService.findByKeyValue(apiKeyValue);

      // Assert
      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(testApiKey);

      verify(apiKeyRepository).findAll();
      verify(passwordEncoder).matches(apiKeyValue, testApiKey.getKeyHash());
      verify(apiKeyRepository).save(testApiKey); // Last used should be updated
    }

    @Test
    @DisplayName("should return empty when API key not found")
    void shouldReturnEmptyWhenApiKeyNotFound() {
      // Arrange
      String apiKeyValue = "oc_test_invalidkey123";
      when(apiKeyRepository.findAll()).thenReturn(List.of(testApiKey));
      when(passwordEncoder.matches(apiKeyValue, testApiKey.getKeyHash())).thenReturn(false);

      // Act
      Optional<ApiKey> result = apiKeyService.findByKeyValue(apiKeyValue);

      // Assert
      assertThat(result).isEmpty();

      verify(apiKeyRepository).findAll();
      verify(passwordEncoder).matches(apiKeyValue, testApiKey.getKeyHash());
      verify(apiKeyRepository, never()).save(any(ApiKey.class));
    }
  }

  @DisplayName("Update Last Used Tests")
  @Nested
  class UpdateLastUsedTests {

    @Test
    @DisplayName("should update last used timestamp")
    void shouldUpdateLastUsedTimestamp() {
      // Arrange
      OffsetDateTime beforeUpdate = testApiKey.getLastUsedAt();
      when(apiKeyRepository.save(testApiKey)).thenReturn(testApiKey);

      // Act
      apiKeyService.updateLastUsed(testApiKey);

      // Assert
      assertThat(testApiKey.getLastUsedAt()).isNotEqualTo(beforeUpdate);
      assertThat(testApiKey.getLastUsedAt()).isNotNull();

      verify(apiKeyRepository).save(testApiKey);
    }
  }

  @DisplayName("Get User API Keys Tests")
  @Nested
  class GetUserApiKeysTests {

    @Test
    @DisplayName("should return user's API keys ordered by creation date")
    void shouldReturnUsersApiKeysOrderedByCreationDate() {
      // Arrange
      ApiKey apiKey1 = ApiKey.builder()
        .id(UUID.randomUUID())
        .user(testUser)
        .name("Key 1")
        .createdAt(OffsetDateTime.now().minusDays(2))
        .build();

      ApiKey apiKey2 = ApiKey.builder()
        .id(UUID.randomUUID())
        .user(testUser)
        .name("Key 2")
        .createdAt(OffsetDateTime.now().minusDays(1))
        .build();

      List<ApiKey> expectedKeys = Arrays.asList(apiKey2, apiKey1); // Descending order
      when(apiKeyRepository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(expectedKeys);

      // Act
      List<ApiKey> result = apiKeyService.getUserApiKeys(testUser);

      // Assert
      assertThat(result).isEqualTo(expectedKeys);
      assertThat(result).hasSize(2);
      assertThat(result.get(0).getName()).isEqualTo("Key 2"); // Most recent first

      verify(apiKeyRepository).findByUserOrderByCreatedAtDesc(testUser);
    }

    @Test
    @DisplayName("should return empty list when user has no API keys")
    void shouldReturnEmptyListWhenUserHasNoApiKeys() {
      // Arrange
      when(apiKeyRepository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(List.of());

      // Act
      List<ApiKey> result = apiKeyService.getUserApiKeys(testUser);

      // Assert
      assertThat(result).isEmpty();

      verify(apiKeyRepository).findByUserOrderByCreatedAtDesc(testUser);
    }
  }

  @DisplayName("Validate API Key Tests")
  @Nested
  class ValidateApiKeyTests {

    @Test
    @DisplayName("should validate API key successfully")
    void shouldValidateApiKeySuccessfully() {
      // Arrange
      String apiKeyValue = "oc_test_validkey123";
      ApiKey validApiKey = ApiKey.builder()
        .id(UUID.randomUUID())
        .user(testUser)
        .name("Valid Key")
        .keyHash("hashedkey")
        .expiresAt(OffsetDateTime.now().plusDays(30)) // Not expired
        .build();

      when(apiKeyRepository.findAll()).thenReturn(List.of(validApiKey));
      when(passwordEncoder.matches(apiKeyValue, validApiKey.getKeyHash())).thenReturn(true);
      when(apiKeyRepository.save(validApiKey)).thenReturn(validApiKey);

      // Act
      Optional<ApiKey> result = apiKeyService.validateApiKey(apiKeyValue);

      // Assert
      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(validApiKey);

      verify(apiKeyRepository).findAll();
      verify(passwordEncoder).matches(apiKeyValue, validApiKey.getKeyHash());
      verify(apiKeyRepository).save(validApiKey); // Last used should be updated
    }

    @Test
    @DisplayName("should reject null API key")
    void shouldRejectNullApiKey() {
      // Act
      Optional<ApiKey> result = apiKeyService.validateApiKey(null);

      // Assert
      assertThat(result).isEmpty();

      verify(apiKeyRepository, never()).findAll();
      verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("should reject API key without correct prefix")
    void shouldRejectApiKeyWithoutCorrectPrefix() {
      // Arrange
      String invalidApiKey = "invalid_prefix_key123";

      // Act
      Optional<ApiKey> result = apiKeyService.validateApiKey(invalidApiKey);

      // Assert
      assertThat(result).isEmpty();

      verify(apiKeyRepository, never()).findAll();
      verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("should reject expired API key")
    void shouldRejectExpiredApiKey() {
      // Arrange
      String apiKeyValue = "oc_test_expiredkey123";
      ApiKey expiredApiKey = ApiKey.builder()
        .id(UUID.randomUUID())
        .user(testUser)
        .name("Expired Key")
        .keyHash("hashedkey")
        .expiresAt(OffsetDateTime.now().minusDays(1)) // Expired
        .build();

      when(apiKeyRepository.findAll()).thenReturn(List.of(expiredApiKey));
      when(passwordEncoder.matches(apiKeyValue, expiredApiKey.getKeyHash())).thenReturn(true);

      // Act
      Optional<ApiKey> result = apiKeyService.validateApiKey(apiKeyValue);

      // Assert
      assertThat(result).isEmpty();

      verify(apiKeyRepository).findAll();
      verify(passwordEncoder).matches(apiKeyValue, expiredApiKey.getKeyHash());
      verify(apiKeyRepository, never()).save(any(ApiKey.class)); // Should not update expired key
    }

    @Test
    @DisplayName("should handle API key without expiration")
    void shouldHandleApiKeyWithoutExpiration() {
      // Arrange
      String apiKeyValue = "oc_test_neverexpireskey123";
      ApiKey neverExpiresApiKey = ApiKey.builder()
        .id(UUID.randomUUID())
        .user(testUser)
        .name("Never Expires Key")
        .keyHash("hashedkey")
        .expiresAt(null) // Never expires
        .build();

      when(apiKeyRepository.findAll()).thenReturn(List.of(neverExpiresApiKey));
      when(passwordEncoder.matches(apiKeyValue, neverExpiresApiKey.getKeyHash())).thenReturn(true);
      when(apiKeyRepository.save(neverExpiresApiKey)).thenReturn(neverExpiresApiKey);

      // Act
      Optional<ApiKey> result = apiKeyService.validateApiKey(apiKeyValue);

      // Assert
      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(neverExpiresApiKey);

      verify(apiKeyRepository).findAll();
      verify(passwordEncoder).matches(apiKeyValue, neverExpiresApiKey.getKeyHash());
      verify(apiKeyRepository).save(neverExpiresApiKey);
    }

    @Test
    @DisplayName("should handle multiple API keys and find correct match")
    void shouldHandleMultipleApiKeysAndFindCorrectMatch() {
      // Arrange
      String apiKeyValue = "oc_test_correctkey123";

      ApiKey wrongKey1 = ApiKey.builder()
        .id(UUID.randomUUID())
        .user(testUser)
        .name("Wrong Key 1")
        .keyHash("wronghash1")
        .build();

      ApiKey correctKey = ApiKey.builder()
        .id(UUID.randomUUID())
        .user(testUser)
        .name("Correct Key")
        .keyHash("correcthash")
        .expiresAt(OffsetDateTime.now().plusDays(30))
        .build();

      ApiKey wrongKey2 = ApiKey.builder()
        .id(UUID.randomUUID())
        .user(testUser)
        .name("Wrong Key 2")
        .keyHash("wronghash2")
        .build();

      when(apiKeyRepository.findAll()).thenReturn(List.of(wrongKey1, correctKey, wrongKey2));
      when(passwordEncoder.matches(apiKeyValue, "wronghash1")).thenReturn(false);
      when(passwordEncoder.matches(apiKeyValue, "correcthash")).thenReturn(true);
      when(apiKeyRepository.save(correctKey)).thenReturn(correctKey);

      // Act
      Optional<ApiKey> result = apiKeyService.validateApiKey(apiKeyValue);

      // Assert
      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(correctKey);

      verify(apiKeyRepository).findAll();
      verify(passwordEncoder).matches(apiKeyValue, "wronghash1");
      verify(passwordEncoder).matches(apiKeyValue, "correcthash");
      verify(passwordEncoder, never()).matches(apiKeyValue, "wronghash2"); // Should stop after finding match
      verify(apiKeyRepository).save(correctKey);
    }
  }

  @DisplayName("Delete API Key Tests")
  @Nested
  class DeleteApiKeyTests {

    @Test
    @DisplayName("should delete API key when user owns it")
    void shouldDeleteApiKeyWhenUserOwnsIt() {
      // Arrange
      UUID keyId = testApiKey.getId();
      Map<String, Object> deletedRow = Map.of(
        "client_id",
        "oc_api_key_test123",
        "name",
        "Test API Key"
      );

      when(jdbcTemplate.queryForList(anyString(), eq(keyId), eq(testUser.getId()))).thenReturn(
        List.of(deletedRow)
      );
      when(jdbcTemplate.update(anyString(), eq("oc_api_key_test123"))).thenReturn(1);

      // Act
      boolean result = apiKeyService.deleteApiKey(keyId, testUser);

      // Assert
      assertThat(result).isTrue();

      verify(jdbcTemplate).queryForList(anyString(), eq(keyId), eq(testUser.getId()));
      verify(jdbcTemplate).update(anyString(), eq("oc_api_key_test123"));
    }

    @Test
    @DisplayName("should not delete API key when user does not own it")
    void shouldNotDeleteApiKeyWhenUserDoesNotOwnIt() {
      // Arrange
      UUID keyId = testApiKey.getId();
      User differentUser = User.builder().id(UUID.randomUUID()).username("differentuser").build();

      when(jdbcTemplate.queryForList(anyString(), eq(keyId), eq(differentUser.getId()))).thenReturn(
        List.of()
      ); // Empty list means no rows deleted

      // Act
      boolean result = apiKeyService.deleteApiKey(keyId, differentUser);

      // Assert
      assertThat(result).isFalse();

      verify(jdbcTemplate).queryForList(anyString(), eq(keyId), eq(differentUser.getId()));
      verify(jdbcTemplate, never()).update(anyString(), (Object[]) any());
    }

    @Test
    @DisplayName("should not delete API key when it does not exist")
    void shouldNotDeleteApiKeyWhenItDoesNotExist() {
      // Arrange
      UUID nonExistentKeyId = UUID.randomUUID();
      when(
        jdbcTemplate.queryForList(anyString(), eq(nonExistentKeyId), eq(testUser.getId()))
      ).thenReturn(List.of()); // Empty list means no rows deleted

      // Act
      boolean result = apiKeyService.deleteApiKey(nonExistentKeyId, testUser);

      // Assert
      assertThat(result).isFalse();

      verify(jdbcTemplate).queryForList(anyString(), eq(nonExistentKeyId), eq(testUser.getId()));
      verify(jdbcTemplate, never()).update(anyString(), (Object[]) any());
    }
  }

  @DisplayName("Cleanup Expired API Keys Tests")
  @Nested
  class CleanupExpiredApiKeysTests {

    @Test
    @DisplayName("should call repository to delete expired API keys")
    void shouldCallRepositoryToDeleteExpiredApiKeys() {
      // Arrange
      when(apiKeyRepository.findExpiredApiKeys(any(OffsetDateTime.class))).thenReturn(List.of());
      when(jdbcTemplate.update(anyString(), any(OffsetDateTime.class))).thenReturn(0);

      // Act
      apiKeyService.cleanupExpiredApiKeys();

      // Assert
      ArgumentCaptor<OffsetDateTime> timeCaptor = ArgumentCaptor.forClass(OffsetDateTime.class);
      verify(apiKeyRepository).findExpiredApiKeys(timeCaptor.capture());
      verify(jdbcTemplate).update(anyString(), any(OffsetDateTime.class));

      OffsetDateTime capturedTime = timeCaptor.getValue();
      assertThat(capturedTime).isBeforeOrEqualTo(OffsetDateTime.now());
      assertThat(capturedTime).isAfter(OffsetDateTime.now().minusMinutes(1)); // Should be very recent
    }
  }

  @DisplayName("API Key Generation Tests")
  @Nested
  class ApiKeyGenerationTests {

    @Test
    @DisplayName("should generate API key with correct prefix")
    void shouldGenerateApiKeyWithCorrectPrefix() {
      // Arrange
      String hashedKey = "hashedkey";
      when(passwordEncoder.encode(anyString())).thenReturn(hashedKey);
      when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(invocation -> {
        ApiKey apiKey = invocation.getArgument(0);
        apiKey.setId(UUID.randomUUID());
        return apiKey;
      });

      // Act
      ApiKey result = apiKeyService.createApiKey(testUser, "Test", 30);

      // Assert
      assertThat(result.getPlainKey()).startsWith("oc_test_");

      // Verify the plain key structure
      String plainKey = result.getPlainKey();
      // Format: prefix + suffix(22 chars) + "." + secret(32 chars) = prefix + 55
      assertThat(plainKey).hasSize("oc_test_".length() + 55); // prefix + suffix + "." + secret
      assertThat(plainKey.substring("oc_test_".length())).matches("[A-Za-z0-9_.-]+"); // Base64 URL-safe characters + dot
    }

    @Test
    @DisplayName("should generate API key with correct length")
    void shouldGenerateApiKeyWithCorrectLength() {
      // Arrange
      String hashedKey = "hashedkey";
      when(passwordEncoder.encode(anyString())).thenReturn(hashedKey);
      when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(invocation -> {
        ApiKey apiKey = invocation.getArgument(0);
        apiKey.setId(UUID.randomUUID());
        return apiKey;
      });

      // Act
      ApiKey result = apiKeyService.createApiKey(testUser, "Test", 30);

      // Assert
      String plainKey = result.getPlainKey();
      assertThat(plainKey).hasSize("oc_test_".length() + 55); // prefix + suffix(22) + "." + secret(32)
    }

    @Test
    @DisplayName("should use custom prefix from properties")
    void shouldUseCustomPrefixFromProperties() {
      // Arrange
      when(authServiceProperties.getApiKey()).thenReturn(apiKeyConfig);
      when(apiKeyConfig.getPrefix()).thenReturn("custom_prefix_");
      String hashedKey = "hashedkey";
      when(passwordEncoder.encode(anyString())).thenReturn(hashedKey);
      when(apiKeyRepository.save(any(ApiKey.class))).thenAnswer(invocation -> {
        ApiKey apiKey = invocation.getArgument(0);
        apiKey.setId(UUID.randomUUID());
        return apiKey;
      });

      // Act
      ApiKey result = apiKeyService.createApiKey(testUser, "Test", 30);

      // Assert
      assertThat(result.getPlainKey()).startsWith("custom_prefix_");
      assertThat(result.getKeyPrefix()).isEqualTo("custom_prefix_");
    }
  }

  @DisplayName("Edge Cases and Integration Tests")
  @Nested
  class EdgeCasesAndIntegrationTests {

    @Test
    @DisplayName("should handle empty API key database")
    void shouldHandleEmptyApiKeyDatabase() {
      // Arrange
      String apiKeyValue = "oc_test_somekey123";
      when(apiKeyRepository.findAll()).thenReturn(List.of());

      // Act
      Optional<ApiKey> result = apiKeyService.validateApiKey(apiKeyValue);

      // Assert
      assertThat(result).isEmpty();

      verify(apiKeyRepository).findAll();
      verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("should handle API key validation with short key")
    void shouldHandleApiKeyValidationWithShortKey() {
      // Arrange
      String shortKey = "oc_"; // Shorter than prefix

      // Act
      Optional<ApiKey> result = apiKeyService.validateApiKey(shortKey);

      // Assert
      assertThat(result).isEmpty();

      verify(apiKeyRepository, never()).findAll();
    }

    @Test
    @DisplayName("should properly update lastUsedAt during validation")
    void shouldProperlyUpdateLastUsedAtDuringValidation() {
      // Arrange
      String apiKeyValue = "oc_test_validkey123";
      ApiKey apiKeyWithoutLastUsed = ApiKey.builder()
        .id(UUID.randomUUID())
        .user(testUser)
        .name("Valid Key")
        .keyHash("hashedkey")
        .lastUsedAt(null) // Initially null
        .build();

      when(apiKeyRepository.findAll()).thenReturn(List.of(apiKeyWithoutLastUsed));
      when(passwordEncoder.matches(apiKeyValue, apiKeyWithoutLastUsed.getKeyHash())).thenReturn(
        true
      );
      when(apiKeyRepository.save(any(ApiKey.class))).thenReturn(apiKeyWithoutLastUsed);

      // Act
      Optional<ApiKey> result = apiKeyService.validateApiKey(apiKeyValue);

      // Assert
      assertThat(result).isPresent();

      // Verify that updateLastUsed was called on the API key
      ArgumentCaptor<ApiKey> apiKeyCaptor = ArgumentCaptor.forClass(ApiKey.class);
      verify(apiKeyRepository).save(apiKeyCaptor.capture());

      ApiKey savedApiKey = apiKeyCaptor.getValue();
      assertThat(savedApiKey.getLastUsedAt()).isNotNull();
      assertThat(savedApiKey.getLastUsedAt()).isBeforeOrEqualTo(OffsetDateTime.now());
    }
  }
}
