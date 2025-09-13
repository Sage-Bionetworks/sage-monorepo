package org.sagebionetworks.openchallenges.auth.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.repository.UserRepository;
import org.springframework.dao.DataAccessException;

/**
 * Test for UserLookupService to verify it correctly consolidates user lookup logic
 * and eliminates duplicate database queries.
 */
@ExtendWith(MockitoExtension.class)
class UserLookupServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private ApiKeyService apiKeyService;

  @InjectMocks
  private UserLookupService userLookupService;

  private User testUser;
  private UUID testUserId;

  @BeforeEach
  void setUp() {
    testUserId = UUID.randomUUID();
    testUser = User.builder()
      .id(testUserId)
      .username("testuser")
      .role(User.Role.user)
      .enabled(true)
      .build();
  }

  @Test
  void shouldFindUserByUuidSubject() {
    // Arrange
    String subject = testUserId.toString();
    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

    // Act
    Optional<User> result = userLookupService.findUserBySubject(subject);

    // Assert
    assertThat(result).isPresent().contains(testUser);
    verify(userRepository).findById(testUserId);
    // Should not try other lookup methods if UUID lookup succeeds
    verifyNoMoreInteractions(userRepository, apiKeyService);
  }

  @Test
  void shouldFallbackToUsernameWhenNotUuid() {
    // Arrange
    String subject = "testuser";
    when(userRepository.findByUsernameIgnoreCase(subject)).thenReturn(Optional.of(testUser));

    // Act
    Optional<User> result = userLookupService.findUserBySubject(subject);

    // Assert
    assertThat(result).isPresent().contains(testUser);
    verify(userRepository).findByUsernameIgnoreCase(subject);
    verify(userRepository, never()).findById(testUserId);
  }

  @Test
  void shouldFallbackToClientIdWhenUsernameFails() {
    // Arrange
    String subject = "client123";
    ApiKey apiKey = ApiKey.builder().user(testUser).build();

    when(userRepository.findByUsernameIgnoreCase(subject)).thenReturn(Optional.empty());
    when(apiKeyService.findByClientId(subject)).thenReturn(Optional.of(apiKey));

    // Act
    Optional<User> result = userLookupService.findUserBySubject(subject);

    // Assert
    assertThat(result).isPresent().contains(testUser);
    verify(userRepository).findByUsernameIgnoreCase(subject);
    verify(apiKeyService).findByClientId(subject);
  }

  @Test
  void shouldReturnEmptyWhenNoUserFound() {
    // Arrange
    String subject = "nonexistent";
    when(userRepository.findByUsernameIgnoreCase(subject)).thenReturn(Optional.empty());
    when(apiKeyService.findByClientId(subject)).thenReturn(Optional.empty());

    // Act
    Optional<User> result = userLookupService.findUserBySubject(subject);

    // Assert
    assertThat(result).isEmpty();
    verify(userRepository).findByUsernameIgnoreCase(subject);
    verify(apiKeyService).findByClientId(subject);
  }

  @Test
  void shouldReturnEmptyForNullSubject() {
    // Act
    Optional<User> result = userLookupService.findUserBySubject(null);

    // Assert
    assertThat(result).isEmpty();
    verifyNoMoreInteractions(userRepository, apiKeyService);
  }

  @Test
  void shouldReturnEmptyForEmptySubject() {
    // Act
    Optional<User> result = userLookupService.findUserBySubject("   ");

    // Assert
    assertThat(result).isEmpty();
    verifyNoMoreInteractions(userRepository, apiKeyService);
  }

  @Test
  void shouldHandleDatabaseErrorGracefully() {
    // Arrange
    String subject = testUserId.toString();
    when(userRepository.findById(testUserId)).thenThrow(new DataAccessException("DB Error") {});

    // Act
    Optional<User> result = userLookupService.findUserBySubject(subject);

    // Assert
    assertThat(result).isEmpty();
    verify(userRepository).findById(testUserId);
    // Database error in UUID lookup should not cause other lookup attempts
    verify(userRepository, never()).findByUsernameIgnoreCase(subject);
    verify(apiKeyService, never()).findByClientId(subject);
  }

  @Test
  void shouldFindUserByUsernameDirectly() {
    // Arrange
    String username = "testuser";
    when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.of(testUser));

    // Act
    Optional<User> result = userLookupService.findUserByUsername(username);

    // Assert
    assertThat(result).isPresent().contains(testUser);
    verify(userRepository).findByUsernameIgnoreCase(username);
  }

  @Test
  void shouldFindUserByUuidDirectly() {
    // Arrange
    String uuidString = testUserId.toString();
    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

    // Act
    Optional<User> result = userLookupService.findUserByUuid(uuidString);

    // Assert
    assertThat(result).isPresent().contains(testUser);
    verify(userRepository).findById(testUserId);
  }

  @Test
  void shouldReturnEmptyForInvalidUuid() {
    // Arrange
    String invalidUuid = "not-a-uuid";

    // Act
    Optional<User> result = userLookupService.findUserByUuid(invalidUuid);

    // Assert
    assertThat(result).isEmpty();
    verifyNoMoreInteractions(userRepository);
  }
}
