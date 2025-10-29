package org.sagebionetworks.bixarena.api.event;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;

/**
 * Standard event format for cross-service communication in BixArena.
 *
 * <p>Events are received from Redis Pub/Sub channel "bixarena:events" and published by other
 * services (e.g., auth service) to trigger actions like cache invalidation.
 */
@Builder
public record BixArenaEvent(
    String type, String service, Instant timestamp, Map<String, Object> payload) {}
