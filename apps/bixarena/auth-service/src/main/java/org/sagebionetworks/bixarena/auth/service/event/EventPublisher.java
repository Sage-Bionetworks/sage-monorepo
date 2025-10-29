package org.sagebionetworks.bixarena.auth.service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Publishes domain events to Redis Pub/Sub for cross-service communication.
 *
 * <p>Events are published to the "bixarena:events" channel and can be consumed by other services
 * (e.g., API service) to trigger actions like cache invalidation.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

  private static final String EVENTS_CHANNEL = "bixarena:events";

  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper;

  /**
   * Publish a user.registered event when a new user is created.
   *
   * <p>This event signals that the total user count has changed, which should trigger cache
   * invalidation in services that cache user statistics.
   *
   * @param userId The ID of the newly created user
   */
  public void publishUserRegistered(UUID userId) {
    BixArenaEvent event =
        BixArenaEvent.builder()
            .type("user.registered")
            .service("auth")
            .timestamp(Instant.now())
            .payload(Map.of("userId", userId.toString()))
            .build();

    try {
      String json = objectMapper.writeValueAsString(event);
      Long subscribers = redisTemplate.convertAndSend(EVENTS_CHANNEL, json);
      log.info("Published event: {} to {} subscribers", event.type(), subscribers);
    } catch (JsonProcessingException e) {
      log.error("Failed to publish event: {}", event.type(), e);
    }
  }
}
