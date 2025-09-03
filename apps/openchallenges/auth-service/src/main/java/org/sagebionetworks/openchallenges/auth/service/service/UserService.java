package org.sagebionetworks.openchallenges.auth.service.service;

import java.util.Optional;
import org.sagebionetworks.openchallenges.auth.service.model.dto.UpdateUserProfileRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  /**
   * Authenticate a user with username and password
   */
  @Transactional(readOnly = true)
  public Optional<User> authenticate(String username, String password) {
    logger.debug("Authenticating user: {}", username);
    Optional<User> userOpt = userRepository.findByUsernameAndEnabled(username, true);

    if (userOpt.isPresent()) {
      User user = userOpt.get();
      if (passwordEncoder.matches(password, user.getPasswordHash())) {
        logger.debug("Authentication successful for user: {}", username);
        return Optional.of(user);
      } else {
        logger.debug("Authentication failed: invalid password for user: {}", username);
      }
    } else {
      logger.debug("Authentication failed: user not found or disabled: {}", username);
    }

    return Optional.empty();
  }

  /**
   * Find user by username
   */
  @Transactional(readOnly = true)
  public Optional<User> findByUsername(String username) {
    logger.debug("Finding user by username: {}", username);
    return userRepository.findByUsername(username);
  }

  /**
   * Create a new user (for administrative purposes)
   */
  public User createUser(String username, String password, User.Role role) {
    logger.info("Creating new user: {} with role: {}", username, role);
    if (userRepository.existsByUsername(username)) {
      logger.warn("User creation failed: username already exists: {}", username);
      throw new IllegalArgumentException("Username already exists: " + username);
    }

    String hashedPassword = passwordEncoder.encode(password);
    User user = User.builder().username(username).passwordHash(hashedPassword).role(role).build();

    User savedUser = userRepository.save(user);
    logger.info("Successfully created user: {}", username);
    return savedUser;
  }

  /**
   * Update user profile information
   */
  public User updateUserProfile(User user, UpdateUserProfileRequestDto updateRequest) {
    logger.info("Updating profile for user: {}", user.getUsername());
    
    if (updateRequest.getFirstName() != null) {
      user.setFirstName(updateRequest.getFirstName());
    }
    
    if (updateRequest.getLastName() != null) {
      user.setLastName(updateRequest.getLastName());
    }
    
    if (updateRequest.getBio() != null) {
      user.setBio(updateRequest.getBio());
    }
    
    if (updateRequest.getWebsite() != null) {
      user.setWebsite(updateRequest.getWebsite().toString());
    }
    
    if (updateRequest.getAvatarUrl() != null) {
      user.setAvatarUrl(updateRequest.getAvatarUrl().toString());
    }
    
    User savedUser = userRepository.save(user);
    logger.info("Successfully updated profile for user: {}", user.getUsername());
    return savedUser;
  }

  /**
   * Update user password
   */
  public void updatePassword(User user, String newPassword) {
    logger.info("Updating password for user: {}", user.getUsername());
    user.setPasswordHash(passwordEncoder.encode(newPassword));
    userRepository.save(user);
    logger.info("Successfully updated password for user: {}", user.getUsername());
  }

  /**
   * Enable or disable a user
   */
  public void setUserEnabled(User user, boolean enabled) {
    logger.info("Setting user {} enabled status to: {}", user.getUsername(), enabled);
    user.setEnabled(enabled);
    userRepository.save(user);
    logger.info("Successfully updated enabled status for user: {}", user.getUsername());
  }
}
