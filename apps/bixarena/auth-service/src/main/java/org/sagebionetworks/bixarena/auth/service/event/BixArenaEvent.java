package org.sagebionetworks.bixarena.auth.service.event;

import java.time.Instant;
import java.util.Map;
import lombok.Builder;

/**
 * Standard event format for cross-service communication in BixArena.
 *
 * <p>Events are published to Redis Pub/Sub channel "bixarena:events" and consumed by other
 * services (e.g., API service) to trigger actions like cache invalidation.
 */
@Builder
public record BixArenaEvent(
    String type, String service, Instant timestamp, Map<String, Object> payload) {}
