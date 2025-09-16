package org.sagebionetworks.openchallenges.auth.service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ExternalAccount;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalAccountRepository extends JpaRepository<ExternalAccount, UUID> {
  /**
   * Find external account by provider and external ID
   */
  Optional<ExternalAccount> findByProviderAndExternalId(
    ExternalAccount.Provider provider,
    String externalId
  );
  /**
   * Find external account by provider and external email
   */
  // Optional<ExternalAccount> findByProviderAndExternalEmail(
  //   ExternalAccount.Provider provider,
  //   String externalEmail
  // );

  /**
   * Find all external accounts for a user
   */
  // List<ExternalAccount> findByUserOrderByCreatedAtDesc(User user);

  /**
   * Find external account for a specific user and provider
   */
  // Optional<ExternalAccount> findByUserAndProvider(User user, ExternalAccount.Provider provider);

  /**
   * Check if external account exists for provider and external ID
   */
  // boolean existsByProviderAndExternalId(ExternalAccount.Provider provider, String externalId);

  /**
   * Check if external account exists for provider and external email
   */
  // boolean existsByProviderAndExternalEmail(ExternalAccount.Provider provider, String externalEmail);

  /**
   * Find external accounts with valid (non-expired) access tokens
   */
  // @Query(
  //   "SELECT ea FROM ExternalAccount ea WHERE ea.user = :user AND ea.accessTokenHash IS NOT NULL AND " +
  //   "(ea.expiresAt IS NULL OR ea.expiresAt > CURRENT_TIMESTAMP)"
  // )
  // List<ExternalAccount> findValidExternalAccountsForUser(@Param("user") User user);

  /**
   * Find external accounts that have refresh tokens
   */
  // @Query(
  //   "SELECT ea FROM ExternalAccount ea WHERE ea.user = :user AND ea.refreshTokenHash IS NOT NULL"
  // )
  // List<ExternalAccount> findExternalAccountsWithRefreshTokensForUser(@Param("user") User user);
}
