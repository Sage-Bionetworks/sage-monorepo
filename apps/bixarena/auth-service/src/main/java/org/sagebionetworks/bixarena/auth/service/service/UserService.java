package org.sagebionetworks.bixarena.auth.service.service;

import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.auth.service.event.EventPublisher;
import org.sagebionetworks.bixarena.auth.service.model.entity.ExternalAccountEntity;
import org.sagebionetworks.bixarena.auth.service.model.entity.ExternalAccountEntity.Provider;
import org.sagebionetworks.bixarena.auth.service.model.entity.UserEntity;
import org.sagebionetworks.bixarena.auth.service.model.repository.ExternalAccountRepository;
import org.sagebionetworks.bixarena.auth.service.model.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing user accounts and authentication state.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserRepository userRepository;
  private final ExternalAccountRepository externalAccountRepository;
  private final EventPublisher eventPublisher;

  /**
   * Handles user login from an external OAuth2 provider (e.g., Synapse).
   * Creates or updates the user account and external account linkage,
   * and records the login timestamp.
   *
   * @param provider The OAuth2 provider (e.g., synapse)
   * @param externalId The user's ID from the external provider
   * @param username The preferred username
   * @param email The user's email address
   * @param firstName The user's first name (given name)
   * @param lastName The user's last name (family name)
   * @return The UserEntity representing the logged-in user
   */
  @Transactional
  public UserEntity handleUserLogin(
    Provider provider,
    String externalId,
    String username,
    String email,
    String firstName,
    String lastName
  ) {
    // Try to find existing external account first
    Optional<ExternalAccountEntity> existingExtAccount =
      externalAccountRepository.findByProviderAndExternalId(provider, externalId);

    UserEntity user;

    if (existingExtAccount.isPresent()) {
      // User has logged in before with this provider
      user = existingExtAccount.get().getUser();
      log.info(
        "Existing user login: userId={} username={} provider={} externalId={}",
        user.getId(),
        user.getUsername(),
        provider,
        externalId
      );

      // Update user information in case it changed at the provider
      boolean updated = false;
      if (email != null && !email.equals(user.getEmail())) {
        user.setEmail(email);
        updated = true;
      }
      if (firstName != null && !firstName.equals(user.getFirstName())) {
        user.setFirstName(firstName);
        updated = true;
      }
      if (lastName != null && !lastName.equals(user.getLastName())) {
        user.setLastName(lastName);
        updated = true;
      }

      // Update last login timestamp
      user.setLastLoginAt(OffsetDateTime.now());
      user = userRepository.save(user);

      if (updated) {
        log.info("Updated user information for userId={}", user.getId());
      }

      // Update external account information
      ExternalAccountEntity extAccount = existingExtAccount.get();
      boolean extUpdated = false;
      if (username != null && !username.equals(extAccount.getExternalUsername())) {
        extAccount.setExternalUsername(username);
        extUpdated = true;
      }
      if (email != null && !email.equals(extAccount.getExternalEmail())) {
        extAccount.setExternalEmail(email);
        extUpdated = true;
      }
      if (extUpdated) {
        externalAccountRepository.save(extAccount);
      }
    } else {
      // New user - check if username already exists (shouldn't happen, but defensive)
      Optional<UserEntity> existingUser = userRepository.findByUsername(username);

      if (existingUser.isPresent()) {
        user = existingUser.get();
        log.info(
          "Linking existing user to new provider: userId={} username={} provider={} externalId={}",
          user.getId(),
          user.getUsername(),
          provider,
          externalId
        );
      } else {
        // Create new user
        user = UserEntity.builder()
          .username(username)
          .email(email)
          .firstName(firstName)
          .lastName(lastName)
          .lastLoginAt(OffsetDateTime.now())
          .build();
        user = userRepository.save(user);
        log.info(
          "Created new user: userId={} username={} email={}",
          user.getId(),
          user.getUsername(),
          email
        );

        // Publish event after new user is persisted (affects total user count)
        eventPublisher.publishUserRegistered(user.getId());
      }

      // Create external account link
      ExternalAccountEntity extAccount = ExternalAccountEntity.builder()
        .user(user)
        .provider(provider)
        .externalId(externalId)
        .externalUsername(username)
        .externalEmail(email)
        .build();
      externalAccountRepository.save(extAccount);
      log.info(
        "Created external account link: provider={} externalId={} userId={}",
        provider,
        externalId,
        user.getId()
      );
    }

    return user;
  }

  /**
   * Records a login timestamp for an existing user.
   * Useful for updating last login time without full OAuth flow.
   *
   * @param userId The user's ID
   */
  @Transactional
  public void recordLogin(java.util.UUID userId) {
    userRepository
      .findById(userId)
      .ifPresent(user -> {
        user.setLastLoginAt(OffsetDateTime.now());
        userRepository.save(user);
        log.debug("Recorded login for userId={}", userId);
      });
  }
}
