package org.sagebionetworks.bixarena.api.api;

import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.UserStatsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Implementation of the User API delegate.
 * Handles user-specific operations requiring authentication.
 */
@Slf4j
@Component
public class UserApiDelegateImpl implements UserApiDelegate {

  /**
   * Get current user's battle statistics.
   * Requires USER role (any authenticated user).
   *
   * <p>Currently returns mock data for testing purposes.
   * Real implementation will query the Battle entity for actual stats.</p>
   *
   * @return User statistics response
   */
  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<UserStatsDto> getUserStats() {
    // Extract authenticated user from Spring Security context
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth != null ? auth.getName() : "unknown";

    log.info("Fetching stats for user: {} (roles: {})",
      username,
      auth != null ? auth.getAuthorities() : "none"
    );

    // TODO: Replace with actual database query
    // Query battles table: SELECT COUNT(*), MIN(created_at), MAX(created_at)
    // WHERE user_id = current_user_id
    // GROUP BY ended_at IS NULL

    UserStatsDto mockStats = UserStatsDto.builder()
      .totalBattles(42L)
      .completedBattles(38L)
      .activeBattles(4L)
      .firstBattleAt(OffsetDateTime.parse("2024-01-15T10:30:00Z"))
      .latestBattleAt(OffsetDateTime.parse("2024-10-26T14:23:00Z"))
      .build();

    log.info("Returning mock stats for user: {}", username);
    return ResponseEntity.ok(mockStats);
  }
}
