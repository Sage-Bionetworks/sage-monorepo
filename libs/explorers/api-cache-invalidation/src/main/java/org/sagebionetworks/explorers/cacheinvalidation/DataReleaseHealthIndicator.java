package org.sagebionetworks.explorers.cacheinvalidation;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

/**
 * Reports the data release tracker's state in `/actuator/health`. The status is always UP: a
 * database outage that makes the refresh fail must not get every API task reported unhealthy, and
 * replaced, at once.
 */
public class DataReleaseHealthIndicator implements HealthIndicator {

  private final DataReleaseTracker tracker;

  public DataReleaseHealthIndicator(DataReleaseTracker tracker) {
    this.tracker = tracker;
  }

  @Override
  public Health health() {
    DataReleaseState state = tracker.getState();
    // Health.Builder.withDetail rejects null values, but an unknown release is reported as null.
    Map<String, Object> details = new LinkedHashMap<>();
    details.put("release", state.release());
    details.put("lastRefreshedAt", state.lastRefreshedAt());
    details.put("consecutiveFailures", state.consecutiveFailures());
    return Health.up().withDetails(details).build();
  }
}
