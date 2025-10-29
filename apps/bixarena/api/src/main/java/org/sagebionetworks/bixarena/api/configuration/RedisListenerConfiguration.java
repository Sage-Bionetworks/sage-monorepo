package org.sagebionetworks.bixarena.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

/**
 * Configuration for Redis Pub/Sub message listeners.
 *
 * <p>This configuration enables the API service to subscribe to events published by other services
 * (e.g., auth service) via Redis Pub/Sub.
 */
@Configuration
public class RedisListenerConfiguration {

  /**
   * Creates a Redis message listener container for subscribing to Pub/Sub channels.
   *
   * @param connectionFactory The Redis connection factory
   * @return Configured message listener container
   */
  @Bean
  public RedisMessageListenerContainer redisContainer(
      RedisConnectionFactory connectionFactory) {
    RedisMessageListenerContainer container = new RedisMessageListenerContainer();
    container.setConnectionFactory(connectionFactory);
    return container;
  }
}
