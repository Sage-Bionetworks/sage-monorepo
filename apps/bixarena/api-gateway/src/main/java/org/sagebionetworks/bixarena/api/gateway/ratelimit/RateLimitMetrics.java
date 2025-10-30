package org.sagebionetworks.bixarena.api.gateway.ratelimit;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Metrics for rate limiting operations.
 *
 * <p>Provides metrics for monitoring rate limiting effectiveness and performance:
 *
 * <ul>
 *   <li>Rate limit checks (allowed vs blocked)
 *   <li>Valkey operation latency
 *   <li>Valkey errors
 *   <li>Rate limit type (session vs IP)
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class RateLimitMetrics {

  private final MeterRegistry meterRegistry;

  private static final String METRIC_PREFIX = "bixarena.gateway.ratelimit";

  /**
   * Record a rate limit check result.
   *
   * @param allowed Whether the request was allowed
   * @param keyType Type of rate limit key (session or ip)
   * @param route Route path or null if not available
   */
  public void recordRateLimitCheck(boolean allowed, String keyType, String route) {
    Counter.builder(METRIC_PREFIX + ".checks")
        .tag("allowed", String.valueOf(allowed))
        .tag("key_type", keyType != null ? keyType : "unknown")
        .tag("route", route != null ? sanitizeRoute(route) : "unknown")
        .description("Number of rate limit checks")
        .register(meterRegistry)
        .increment();
  }

  /**
   * Record a Valkey operation latency.
   *
   * @param operation Operation name (e.g., "isAllowed")
   * @return Timer.Sample to stop and record
   */
  public Timer.Sample startValkeyOperation(String operation) {
    return Timer.start(meterRegistry);
  }

  /**
   * Stop and record a Valkey operation latency.
   *
   * @param sample Timer sample from startValkeyOperation
   * @param operation Operation name
   * @param success Whether the operation succeeded
   */
  public void stopValkeyOperation(Timer.Sample sample, String operation, boolean success) {
    sample.stop(
        Timer.builder(METRIC_PREFIX + ".valkey.operation")
            .tag("operation", operation)
            .tag("success", String.valueOf(success))
            .description("Valkey operation latency")
            .register(meterRegistry));
  }

  /**
   * Record a Valkey error.
   *
   * @param operation Operation name
   * @param errorType Error type (e.g., "timeout", "connection_refused")
   */
  public void recordValkeyError(String operation, String errorType) {
    Counter.builder(METRIC_PREFIX + ".valkey.errors")
        .tag("operation", operation)
        .tag("error_type", errorType != null ? errorType : "unknown")
        .description("Number of Valkey errors")
        .register(meterRegistry)
        .increment();
  }

  /**
   * Record rate limit exceeded (429 response).
   *
   * @param keyType Type of rate limit key (session or ip)
   * @param route Route path or null if not available
   */
  public void recordRateLimitExceeded(String keyType, String route) {
    Counter.builder(METRIC_PREFIX + ".exceeded")
        .tag("key_type", keyType != null ? keyType : "unknown")
        .tag("route", route != null ? sanitizeRoute(route) : "unknown")
        .description("Number of rate limit exceeded responses (429)")
        .register(meterRegistry)
        .increment();
  }

  /**
   * Sanitize route path for metrics (replace path parameters with placeholders).
   *
   * <p>Examples:
   *
   * <ul>
   *   <li>/api/v1/battles/123 → /api/v1/battles/{id}
   *   <li>/api/v1/leaderboards/elo/history/model-1 → /api/v1/leaderboards/{id}/history/{id}
   * </ul>
   *
   * @param route Route path
   * @return Sanitized route path
   */
  private String sanitizeRoute(String route) {
    if (route == null || route.isBlank()) {
      return "unknown";
    }

    // Simple sanitization: replace UUIDs and numeric IDs with {id}
    return route
        .replaceAll(
            "/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", "/{id}") // UUIDs
        .replaceAll("/\\d+", "/{id}") // Numeric IDs
        .replaceAll("/[a-zA-Z0-9_-]{8,}", "/{id}"); // Long alphanumeric IDs
  }
}
