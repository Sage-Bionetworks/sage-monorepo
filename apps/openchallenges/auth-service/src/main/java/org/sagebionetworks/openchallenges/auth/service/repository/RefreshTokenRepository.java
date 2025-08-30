package org.sagebionetworks.openchallenges.auth.service.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.sagebionetworks.openchallenges.auth.service.model.entity.RefreshToken;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
  
  /**
   * Find refresh token by token hash
   */
  Optional<RefreshToken> findByTokenHash(String tokenHash);
  
  /**
   * Find refresh token by token hash and user ID
   */
  Optional<RefreshToken> findByTokenHashAndUserId(String tokenHash, UUID userId);
  
  /**
   * Delete refresh token by token hash and user ID
   */
  void deleteByTokenHashAndUserId(String tokenHash, UUID userId);
  
  /**
   * Find valid (non-expired, non-revoked) refresh token by token hash
   */
  @Query(
    "SELECT rt FROM RefreshToken rt WHERE rt.tokenHash = :tokenHash AND " +
    "rt.expiresAt > :now AND rt.revoked = false"
  )
  Optional<RefreshToken> findValidRefreshToken(
    @Param("tokenHash") String tokenHash,
    @Param("now") OffsetDateTime now
  );
  
  /**
   * Find all refresh tokens for a user (ordered by creation date, newest first)
   */
  List<RefreshToken> findByUserOrderByCreatedAtDesc(User user);
  
  /**
   * Find all valid refresh tokens for a user
   */
  @Query(
    "SELECT rt FROM RefreshToken rt WHERE rt.user = :user AND " +
    "rt.expiresAt > :now AND rt.revoked = false ORDER BY rt.createdAt DESC"
  )
  List<RefreshToken> findValidRefreshTokensForUser(
    @Param("user") User user,
    @Param("now") OffsetDateTime now
  );
  
  /**
   * Find expired refresh tokens
   */
  @Query(
    "SELECT rt FROM RefreshToken rt WHERE rt.expiresAt <= :now AND rt.revoked = false"
  )
  List<RefreshToken> findExpiredRefreshTokens(@Param("now") OffsetDateTime now);
  
  /**
   * Revoke all refresh tokens for a user
   */
  @Modifying
  @Query(
    "UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.user = :user AND rt.revoked = false"
  )
  int revokeAllTokensForUser(@Param("user") User user);
  
  /**
   * Revoke refresh token by token hash
   */
  @Modifying
  @Query(
    "UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.tokenHash = :tokenHash"
  )
  int revokeTokenByHash(@Param("tokenHash") String tokenHash);
  
  /**
   * Delete expired and revoked refresh tokens (cleanup)
   */
  @Modifying
  @Query(
    "DELETE FROM RefreshToken rt WHERE (rt.expiresAt <= :expiredBefore OR rt.revoked = true) " +
    "AND rt.createdAt <= :createdBefore"
  )
  int deleteExpiredAndRevokedTokens(
    @Param("expiredBefore") OffsetDateTime expiredBefore,
    @Param("createdBefore") OffsetDateTime createdBefore
  );
  
  /**
   * Count valid refresh tokens for a user
   */
  @Query(
    "SELECT COUNT(rt) FROM RefreshToken rt WHERE rt.user = :user AND " +
    "rt.expiresAt > :now AND rt.revoked = false"
  )
  long countValidTokensForUser(@Param("user") User user, @Param("now") OffsetDateTime now);
}
