package org.sagebionetworks.openchallenges.auth.service.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService")
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  private User testUser;
  private final String testUsername = "testuser";
  private final String testPassword = "password123";
  private final String hashedPassword = "hashedPassword123";

  @BeforeEach
  void setUp() {
    testUser = User.builder()
      .id(UUID.randomUUID())
      .username(testUsername)
      .passwordHash(hashedPassword)
      .role(User.Role.user)
      .enabled(true)
      .build();
  }

  @Nested
  @DisplayName("authenticate")
  class AuthenticateTests {

    @Test
    @DisplayName("should return user when authentication is successful")
    void shouldReturnUserWhenAuthenticationIsSuccessful() {
      // given
      when(userRepository.findByUsernameAndEnabled(testUsername, true)).thenReturn(
        Optional.of(testUser)
      );
      when(passwordEncoder.matches(testPassword, hashedPassword)).thenReturn(true);

      // when
      Optional<User> result = userService.authenticate(testUsername, testPassword);

      // then
      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(testUser);
      verify(userRepository).findByUsernameAndEnabled(testUsername, true);
      verify(passwordEncoder).matches(testPassword, hashedPassword);
    }

    @Test
    @DisplayName("should return empty when user not found")
    void shouldReturnEmptyWhenUserNotFound() {
      // given
      when(userRepository.findByUsernameAndEnabled(testUsername, true)).thenReturn(
        Optional.empty()
      );

      // when
      Optional<User> result = userService.authenticate(testUsername, testPassword);

      // then
      assertThat(result).isEmpty();
      verify(userRepository).findByUsernameAndEnabled(testUsername, true);
      verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("should return empty when password is incorrect")
    void shouldReturnEmptyWhenPasswordIsIncorrect() {
      // given
      when(userRepository.findByUsernameAndEnabled(testUsername, true)).thenReturn(
        Optional.of(testUser)
      );
      when(passwordEncoder.matches(testPassword, hashedPassword)).thenReturn(false);

      // when
      Optional<User> result = userService.authenticate(testUsername, testPassword);

      // then
      assertThat(result).isEmpty();
      verify(userRepository).findByUsernameAndEnabled(testUsername, true);
      verify(passwordEncoder).matches(testPassword, hashedPassword);
    }

    @Test
    @DisplayName("should return empty when user is disabled")
    void shouldReturnEmptyWhenUserIsDisabled() {
      // given
      User disabledUser = User.builder()
        .id(UUID.randomUUID())
        .username(testUsername)
        .passwordHash(hashedPassword)
        .role(User.Role.user)
        .enabled(false)
        .build();

      when(userRepository.findByUsernameAndEnabled(testUsername, true)).thenReturn(
        Optional.empty()
      ); // Repository won't return disabled users

      // when
      Optional<User> result = userService.authenticate(testUsername, testPassword);

      // then
      assertThat(result).isEmpty();
      verify(userRepository).findByUsernameAndEnabled(testUsername, true);
      verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
  }

  @Nested
  @DisplayName("findByUsername")
  class FindByUsernameTests {

    @Test
    @DisplayName("should return user when username exists")
    void shouldReturnUserWhenUsernameExists() {
      // given
      when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

      // when
      Optional<User> result = userService.findByUsername(testUsername);

      // then
      assertThat(result).isPresent();
      assertThat(result.get()).isEqualTo(testUser);
      verify(userRepository).findByUsername(testUsername);
    }

    @Test
    @DisplayName("should return empty when username does not exist")
    void shouldReturnEmptyWhenUsernameDoesNotExist() {
      // given
      when(userRepository.findByUsername(testUsername)).thenReturn(Optional.empty());

      // when
      Optional<User> result = userService.findByUsername(testUsername);

      // then
      assertThat(result).isEmpty();
      verify(userRepository).findByUsername(testUsername);
    }
  }

  @Nested
  @DisplayName("createUser")
  class CreateUserTests {

    @Test
    @DisplayName("should create user successfully when username is unique")
    void shouldCreateUserSuccessfullyWhenUsernameIsUnique() {
      // given
      String newUsername = "newuser";
      String rawPassword = "newpassword";
      User.Role role = User.Role.admin;
      String encodedPassword = "encodedPassword";

      when(userRepository.existsByUsername(newUsername)).thenReturn(false);
      when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
      when(userRepository.save(any(User.class))).thenReturn(testUser);

      // when
      User result = userService.createUser(newUsername, rawPassword, role);

      // then
      assertThat(result).isEqualTo(testUser);

      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User capturedUser = userCaptor.getValue();
      assertThat(capturedUser.getUsername()).isEqualTo(newUsername);
      assertThat(capturedUser.getPasswordHash()).isEqualTo(encodedPassword);
      assertThat(capturedUser.getRole()).isEqualTo(role);

      verify(userRepository).existsByUsername(newUsername);
      verify(passwordEncoder).encode(rawPassword);
    }

    @Test
    @DisplayName("should throw exception when username already exists")
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
      // given
      String existingUsername = "existinguser";
      when(userRepository.existsByUsername(existingUsername)).thenReturn(true);

      // when & then
      assertThatThrownBy(() ->
        userService.createUser(existingUsername, testPassword, User.Role.user)
      )
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Username already exists: " + existingUsername);

      verify(userRepository).existsByUsername(existingUsername);
      verify(passwordEncoder, never()).encode(anyString());
      verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("should create user with correct default enabled status")
    void shouldCreateUserWithCorrectDefaultEnabledStatus() {
      // given
      String newUsername = "newuser";
      String rawPassword = "password";
      User.Role role = User.Role.user;

      when(userRepository.existsByUsername(newUsername)).thenReturn(false);
      when(passwordEncoder.encode(rawPassword)).thenReturn(hashedPassword);
      when(userRepository.save(any(User.class))).thenReturn(testUser);

      // when
      userService.createUser(newUsername, rawPassword, role);

      // then
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User capturedUser = userCaptor.getValue();
      assertThat(capturedUser.getEnabled()).isTrue();
    }
  }

  @Nested
  @DisplayName("updatePassword")
  class UpdatePasswordTests {

    @Test
    @DisplayName("should update password successfully")
    void shouldUpdatePasswordSuccessfully() {
      // given
      String newPassword = "newPassword123";
      String newHashedPassword = "newHashedPassword";

      when(passwordEncoder.encode(newPassword)).thenReturn(newHashedPassword);
      when(userRepository.save(testUser)).thenReturn(testUser);

      // when
      userService.updatePassword(testUser, newPassword);

      // then
      assertThat(testUser.getPasswordHash()).isEqualTo(newHashedPassword);
      verify(passwordEncoder).encode(newPassword);
      verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("should save user after password update")
    void shouldSaveUserAfterPasswordUpdate() {
      // given
      String newPassword = "anotherPassword";
      String encodedPassword = "encodedAnotherPassword";

      when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

      // when
      userService.updatePassword(testUser, newPassword);

      // then
      verify(userRepository, times(1)).save(testUser);
    }
  }

  @Nested
  @DisplayName("setUserEnabled")
  class SetUserEnabledTests {

    @Test
    @DisplayName("should enable user successfully")
    void shouldEnableUserSuccessfully() {
      // given
      testUser.setEnabled(false); // Start with disabled user
      when(userRepository.save(testUser)).thenReturn(testUser);

      // when
      userService.setUserEnabled(testUser, true);

      // then
      assertThat(testUser.getEnabled()).isTrue();
      verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("should disable user successfully")
    void shouldDisableUserSuccessfully() {
      // given
      testUser.setEnabled(true); // Start with enabled user
      when(userRepository.save(testUser)).thenReturn(testUser);

      // when
      userService.setUserEnabled(testUser, false);

      // then
      assertThat(testUser.getEnabled()).isFalse();
      verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("should save user after enabled status change")
    void shouldSaveUserAfterEnabledStatusChange() {
      // given
      boolean newEnabledStatus = !testUser.getEnabled();

      // when
      userService.setUserEnabled(testUser, newEnabledStatus);

      // then
      verify(userRepository, times(1)).save(testUser);
    }
  }

  @Nested
  @DisplayName("Edge Cases")
  class EdgeCaseTests {

    @Test
    @DisplayName("should handle null username in authenticate")
    void shouldHandleNullUsernameInAuthenticate() {
      // given
      when(userRepository.findByUsernameAndEnabled(null, true)).thenReturn(Optional.empty());

      // when
      Optional<User> result = userService.authenticate(null, testPassword);

      // then
      assertThat(result).isEmpty();
      verify(userRepository).findByUsernameAndEnabled(null, true);
    }

    @Test
    @DisplayName("should handle null password in authenticate")
    void shouldHandleNullPasswordInAuthenticate() {
      // given
      when(userRepository.findByUsernameAndEnabled(testUsername, true)).thenReturn(
        Optional.of(testUser)
      );
      when(passwordEncoder.matches(null, hashedPassword)).thenReturn(false);

      // when
      Optional<User> result = userService.authenticate(testUsername, null);

      // then
      assertThat(result).isEmpty();
      verify(passwordEncoder).matches(null, hashedPassword);
    }

    @Test
    @DisplayName("should handle empty username in findByUsername")
    void shouldHandleEmptyUsernameInFindByUsername() {
      // given
      String emptyUsername = "";
      when(userRepository.findByUsername(emptyUsername)).thenReturn(Optional.empty());

      // when
      Optional<User> result = userService.findByUsername(emptyUsername);

      // then
      assertThat(result).isEmpty();
      verify(userRepository).findByUsername(emptyUsername);
    }
  }
}
