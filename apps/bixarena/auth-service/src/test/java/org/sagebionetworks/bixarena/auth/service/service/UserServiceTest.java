package org.sagebionetworks.bixarena.auth.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.bixarena.auth.service.model.entity.ExternalAccountEntity;
import org.sagebionetworks.bixarena.auth.service.model.entity.ExternalAccountEntity.Provider;
import org.sagebionetworks.bixarena.auth.service.model.entity.UserEntity;
import org.sagebionetworks.bixarena.auth.service.model.repository.ExternalAccountRepository;
import org.sagebionetworks.bixarena.auth.service.model.repository.UserRepository;
import org.sagebionetworks.bixarena.auth.service.event.EventPublisher;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ExternalAccountRepository externalAccountRepository;

  @Mock
  private EventPublisher eventPublisher;

  @InjectMocks
  private UserService userService;

  private static final String EXTERNAL_ID = "12345";
  private static final String USERNAME = "testuser";
  private static final String EMAIL = "test@example.com";
  private static final String FIRST_NAME = "Test";
  private static final String LAST_NAME = "User";

  @BeforeEach
  void setUp() {
    reset(userRepository, externalAccountRepository, eventPublisher);
  }

  @Test
  @DisplayName("Should create new user and external account on first login")
  void handleUserLogin_NewUser_CreatesUserAndExternalAccount() {
    // Arrange
    when(
      externalAccountRepository.findByProviderAndExternalId(Provider.synapse, EXTERNAL_ID)
    ).thenReturn(Optional.empty());
    when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

    UserEntity savedUser = UserEntity.builder()
      .id(UUID.randomUUID())
      .username(USERNAME)
      .email(EMAIL)
      .firstName(FIRST_NAME)
      .lastName(LAST_NAME)
      .build();

    when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);
    when(externalAccountRepository.save(any(ExternalAccountEntity.class))).thenReturn(
      ExternalAccountEntity.builder().build()
    );

    // Act
    UserEntity result = userService.handleUserLogin(
      Provider.synapse,
      EXTERNAL_ID,
      USERNAME,
      EMAIL,
      true,
      FIRST_NAME,
      LAST_NAME
    );

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getUsername()).isEqualTo(USERNAME);
    assertThat(result.getEmail()).isEqualTo(EMAIL);

    // Verify user was saved with lastLoginAt set
    ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
    verify(userRepository).save(userCaptor.capture());
    UserEntity capturedUser = userCaptor.getValue();
    assertThat(capturedUser.getUsername()).isEqualTo(USERNAME);
    assertThat(capturedUser.getEmail()).isEqualTo(EMAIL);
    assertThat(capturedUser.getFirstName()).isEqualTo(FIRST_NAME);
    assertThat(capturedUser.getLastName()).isEqualTo(LAST_NAME);
    assertThat(capturedUser.getLastLoginAt()).isNotNull();

    // Verify external account was created
    ArgumentCaptor<ExternalAccountEntity> extAccountCaptor = ArgumentCaptor.forClass(
      ExternalAccountEntity.class
    );
    verify(externalAccountRepository).save(extAccountCaptor.capture());
    ExternalAccountEntity capturedExtAccount = extAccountCaptor.getValue();
    assertThat(capturedExtAccount.getProvider()).isEqualTo(Provider.synapse);
    assertThat(capturedExtAccount.getExternalId()).isEqualTo(EXTERNAL_ID);
    assertThat(capturedExtAccount.getExternalUsername()).isEqualTo(USERNAME);
    assertThat(capturedExtAccount.getExternalEmail()).isEqualTo(EMAIL);
  }

  @Test
  @DisplayName("Should update existing user information and record login on subsequent login")
  void handleUserLogin_ExistingUser_UpdatesUserAndRecordsLogin() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UserEntity existingUser = UserEntity.builder()
      .id(userId)
      .username(USERNAME)
      .email("old@example.com")
      .firstName("OldFirst")
      .lastName("OldLast")
      .build();

    ExternalAccountEntity existingExtAccount = ExternalAccountEntity.builder()
      .id(UUID.randomUUID())
      .user(existingUser)
      .provider(Provider.synapse)
      .externalId(EXTERNAL_ID)
      .externalUsername("oldusername")
      .externalEmail("old@example.com")
      .build();

    when(
      externalAccountRepository.findByProviderAndExternalId(Provider.synapse, EXTERNAL_ID)
    ).thenReturn(Optional.of(existingExtAccount));

    UserEntity updatedUser = UserEntity.builder()
      .id(userId)
      .username(USERNAME)
      .email(EMAIL)
      .firstName(FIRST_NAME)
      .lastName(LAST_NAME)
      .lastLoginAt(OffsetDateTime.now())
      .build();

    when(userRepository.save(any(UserEntity.class))).thenReturn(updatedUser);
    when(externalAccountRepository.save(any(ExternalAccountEntity.class))).thenReturn(
      existingExtAccount
    );

    // Act
    UserEntity result = userService.handleUserLogin(
      Provider.synapse,
      EXTERNAL_ID,
      USERNAME,
      EMAIL,
      true,
      FIRST_NAME,
      LAST_NAME
    );

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(userId);

    // Verify user was updated
    ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
    verify(userRepository).save(userCaptor.capture());
    UserEntity capturedUser = userCaptor.getValue();
    assertThat(capturedUser.getEmail()).isEqualTo(EMAIL);
    assertThat(capturedUser.getFirstName()).isEqualTo(FIRST_NAME);
    assertThat(capturedUser.getLastName()).isEqualTo(LAST_NAME);
    assertThat(capturedUser.getLastLoginAt()).isNotNull();

    // Verify external account was updated
    ArgumentCaptor<ExternalAccountEntity> extAccountCaptor = ArgumentCaptor.forClass(
      ExternalAccountEntity.class
    );
    verify(externalAccountRepository).save(extAccountCaptor.capture());
    ExternalAccountEntity capturedExtAccount = extAccountCaptor.getValue();
    assertThat(capturedExtAccount.getExternalUsername()).isEqualTo(USERNAME);
    assertThat(capturedExtAccount.getExternalEmail()).isEqualTo(EMAIL);
  }

  @Test
  @DisplayName("Should not update user if information hasn't changed")
  void handleUserLogin_ExistingUserNoChanges_DoesNotUpdateUnnecessarily() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UserEntity existingUser = UserEntity.builder()
      .id(userId)
      .username(USERNAME)
      .email(EMAIL)
      .firstName(FIRST_NAME)
      .lastName(LAST_NAME)
      .build();

    ExternalAccountEntity existingExtAccount = ExternalAccountEntity.builder()
      .id(UUID.randomUUID())
      .user(existingUser)
      .provider(Provider.synapse)
      .externalId(EXTERNAL_ID)
      .externalUsername(USERNAME)
      .externalEmail(EMAIL)
      .build();

    when(
      externalAccountRepository.findByProviderAndExternalId(Provider.synapse, EXTERNAL_ID)
    ).thenReturn(Optional.of(existingExtAccount));

    UserEntity savedUser = UserEntity.builder()
      .id(userId)
      .username(USERNAME)
      .email(EMAIL)
      .firstName(FIRST_NAME)
      .lastName(LAST_NAME)
      .lastLoginAt(OffsetDateTime.now())
      .build();

    when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

    // Act
    UserEntity result = userService.handleUserLogin(
      Provider.synapse,
      EXTERNAL_ID,
      USERNAME,
      EMAIL,
      true,
      FIRST_NAME,
      LAST_NAME
    );

    // Assert
    assertThat(result).isNotNull();

    // User should still be saved to update lastLoginAt
    verify(userRepository).save(any(UserEntity.class));

    // External account should not be updated since nothing changed
    verify(externalAccountRepository, never()).save(any(ExternalAccountEntity.class));
  }

  @Test
  @DisplayName("Should link new provider to existing user when username matches")
  void handleUserLogin_ExistingUserNewProvider_LinksNewProvider() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UserEntity existingUser = UserEntity.builder()
      .id(userId)
      .username(USERNAME)
      .email(EMAIL)
      .firstName(FIRST_NAME)
      .lastName(LAST_NAME)
      .build();

    when(
      externalAccountRepository.findByProviderAndExternalId(Provider.synapse, EXTERNAL_ID)
    ).thenReturn(Optional.empty());
    when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(existingUser));
    when(externalAccountRepository.findByUserIdAndProvider(userId, Provider.synapse))
      .thenReturn(Optional.empty());
    when(userRepository.save(any(UserEntity.class))).thenReturn(existingUser);
    when(externalAccountRepository.save(any(ExternalAccountEntity.class))).thenReturn(
      ExternalAccountEntity.builder().build()
    );

    // Act
    UserEntity result = userService.handleUserLogin(
      Provider.synapse,
      EXTERNAL_ID,
      USERNAME,
      EMAIL,
      true,
      FIRST_NAME,
      LAST_NAME
    );

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(userId);

    // Verify external account was created for the existing user
    ArgumentCaptor<ExternalAccountEntity> extAccountCaptor = ArgumentCaptor.forClass(
      ExternalAccountEntity.class
    );
    verify(externalAccountRepository).save(extAccountCaptor.capture());
    ExternalAccountEntity capturedExtAccount = extAccountCaptor.getValue();
    assertThat(capturedExtAccount.getUser().getId()).isEqualTo(userId);
    assertThat(capturedExtAccount.getProvider()).isEqualTo(Provider.synapse);
    assertThat(capturedExtAccount.getExternalId()).isEqualTo(EXTERNAL_ID);

    // User should be saved to update lastLoginAt
    verify(userRepository).save(any(UserEntity.class));
  }

  @Test
  @DisplayName("Should update external account when user exists with different external ID")
  void handleUserLogin_ExistingUserExistingProvider_UpdatesExternalId() {
    // Arrange
    UUID userId = UUID.randomUUID();
    String oldExternalId = "old-external-id";
    String newExternalId = "new-external-id";

    UserEntity existingUser = UserEntity.builder()
      .id(userId)
      .username(USERNAME)
      .email(EMAIL)
      .firstName(FIRST_NAME)
      .lastName(LAST_NAME)
      .build();

    ExternalAccountEntity existingExtAccount = ExternalAccountEntity.builder()
      .id(UUID.randomUUID())
      .user(existingUser)
      .provider(Provider.synapse)
      .externalId(oldExternalId)
      .externalUsername(USERNAME)
      .externalEmail(EMAIL)
      .build();

    when(
      externalAccountRepository.findByProviderAndExternalId(Provider.synapse, newExternalId)
    ).thenReturn(Optional.empty());
    when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(existingUser));
    when(externalAccountRepository.findByUserIdAndProvider(userId, Provider.synapse))
      .thenReturn(Optional.of(existingExtAccount));
    when(userRepository.save(any(UserEntity.class))).thenReturn(existingUser);
    when(externalAccountRepository.save(any(ExternalAccountEntity.class))).thenReturn(
      existingExtAccount
    );

    // Act
    UserEntity result = userService.handleUserLogin(
      Provider.synapse,
      newExternalId,
      USERNAME,
      EMAIL,
      true,
      FIRST_NAME,
      LAST_NAME
    );

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(userId);

    // Verify external account was updated (not created)
    ArgumentCaptor<ExternalAccountEntity> extAccountCaptor = ArgumentCaptor.forClass(
      ExternalAccountEntity.class
    );
    verify(externalAccountRepository).save(extAccountCaptor.capture());
    ExternalAccountEntity capturedExtAccount = extAccountCaptor.getValue();
    assertThat(capturedExtAccount.getExternalId()).isEqualTo(newExternalId);
    assertThat(capturedExtAccount.getExternalUsername()).isEqualTo(USERNAME);
    assertThat(capturedExtAccount.getExternalEmail()).isEqualTo(EMAIL);

    // User should be saved to update lastLoginAt
    verify(userRepository).save(any(UserEntity.class));
  }

  @Test
  @DisplayName("Should record login timestamp for existing user")
  void recordLogin_ExistingUser_UpdatesLastLoginAt() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UserEntity existingUser = UserEntity.builder()
      .id(userId)
      .username(USERNAME)
      .email(EMAIL)
      .build();

    when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
    when(userRepository.save(any(UserEntity.class))).thenReturn(existingUser);

    // Act
    userService.recordLogin(userId);

    // Assert
    ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
    verify(userRepository).save(userCaptor.capture());
    UserEntity capturedUser = userCaptor.getValue();
    assertThat(capturedUser.getLastLoginAt()).isNotNull();
  }

  @Test
  @DisplayName("Should handle gracefully when user not found for recordLogin")
  void recordLogin_UserNotFound_DoesNotThrowException() {
    // Arrange
    UUID userId = UUID.randomUUID();
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    // Act & Assert - should not throw exception
    userService.recordLogin(userId);

    // Verify save was never called
    verify(userRepository, never()).save(any(UserEntity.class));
  }
}
