package org.sagebionetworks.openchallenges.auth.service.service;

import java.util.Optional;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Authenticate a user with username and password
   */
  @Transactional(readOnly = true)
  public Optional<User> authenticate(String username, String password) {
    Optional<User> userOpt = userRepository.findByUsernameAndEnabled(username, true);

    if (userOpt.isPresent()) {
      User user = userOpt.get();
      if (passwordEncoder.matches(password, user.getPasswordHash())) {
        return Optional.of(user);
      }
    }

    return Optional.empty();
  }

  /**
   * Find user by username
   */
  @Transactional(readOnly = true)
  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  /**
   * Create a new user (for administrative purposes)
   */
  public User createUser(String username, String password, User.Role role) {
    if (userRepository.existsByUsername(username)) {
      throw new IllegalArgumentException("Username already exists: " + username);
    }

    String hashedPassword = passwordEncoder.encode(password);
    User user = new User(username, hashedPassword, role);

    return userRepository.save(user);
  }

  /**
   * Update user password
   */
  public void updatePassword(User user, String newPassword) {
    user.setPasswordHash(passwordEncoder.encode(newPassword));
    userRepository.save(user);
  }

  /**
   * Enable or disable a user
   */
  public void setUserEnabled(User user, boolean enabled) {
    user.setEnabled(enabled);
    userRepository.save(user);
  }
}
