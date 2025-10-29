package org.sagebionetworks.bixarena.api.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.service.StatsCacheService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Subscribes to cross-service events via Redis Pub/Sub.
 *
 * <p>Handles cache invalidation when relevant events occur in other services (e.g., when a new
 * user is registered in the auth service).
 */
@Component
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
@RequiredArgsConstructor
@Slf4j
public class EventSubscriber implements MessageListener {

  private final RedisMessageListenerContainer listenerContainer;
  private final StatsCacheService statsCacheService;
  private final ObjectMapper objectMapper;

  /**
   * Subscribe to events channel when application is ready.
   * This ensures all beans are initialized before we start receiving events.
   */
  @EventListener(ApplicationReadyEvent.class)
  public void subscribeToEvents() {
    listenerContainer.addMessageListener(this, new ChannelTopic(EventChannels.BIXARENA_EVENTS));
    log.info("Subscribed to {} channel", EventChannels.BIXARENA_EVENTS);
  }

  /**
   * Handle incoming messages from Redis Pub/Sub.
   *
   * @param message The message received
   * @param pattern The pattern that matched (unused)
   */
  @Override
  public void onMessage(Message message, byte[] pattern) {
    try {
      String json = new String(message.getBody());
      BixArenaEvent event = objectMapper.readValue(json, BixArenaEvent.class);

      log.debug("Received event: type={} service={}", event.type(), event.service());

      switch (event.type()) {
        case "user.registered":
          handleUserRegistered(event);
          break;
        default:
          log.debug("Ignoring event type: {}", event.type());
      }
    } catch (Exception e) {
      log.error("Failed to handle event", e);
    }
  }

  /**
   * Handle user.registered event by invalidating the public stats cache.
   *
   * @param event The user registration event
   */
  private void handleUserRegistered(BixArenaEvent event) {
    log.info("User registered event received, invalidating stats cache");
    statsCacheService.invalidatePublicStats();
  }
}
