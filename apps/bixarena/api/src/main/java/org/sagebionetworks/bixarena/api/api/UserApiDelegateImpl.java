package org.sagebionetworks.bixarena.api.api;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.model.dto.UserStatsDto;
import org.sagebionetworks.bixarena.api.service.UserStatsService;
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
@RequiredArgsConstructor
@Component
public class UserApiDelegateImpl implements UserApiDelegate {

  private final UserStatsService userStatsService;

  /**
   * Get current user's battle statistics.
   * Requires USER role (any authenticated user).
   *
   * @return User statistics response
   */
  @Override
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<UserStatsDto> getUserStats() {
    // Extract authenticated user from Spring Security context
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String sub = auth != null ? auth.getName() : null;

    if (sub == null) {
      log.warn("No authentication context found");
      throw new IllegalStateException("User not authenticated");
    }

    log.info(
        "Fetching stats for user: {} (roles: {})",
        sub,
        auth.getAuthorities()
    );

    // Parse user ID from JWT sub claim
    UUID userId;
    try {
      userId = UUID.fromString(sub);
    } catch (IllegalArgumentException e) {
      log.error("Invalid UUID format in JWT sub claim: {}", sub);
      throw new IllegalStateException("Invalid user ID in authentication token");
    }

    // Fetch real stats from database
    UserStatsDto stats = userStatsService.getUserStats(userId);

    log.info("Returning stats for user {}: {} total battles", userId, stats.getTotalBattles());
    return ResponseEntity.ok(stats);
  }
}
