package org.sagebionetworks.bixarena.api.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;

/**
 * Configuration for Valkey (Redis-compatible) connectivity.
 *
 * <p>Valkey is used for distributed rate limiting across multiple gateway instances.
 */
@Configuration
public class ValkeyConfiguration {

  /**
   * Reactive Redis template for rate limiting operations.
   *
   * <p>Uses String keys and values for simplicity and compatibility with the Lua script.
   *
   * @param factory Redis connection factory (auto-configured by Spring Boot)
   * @return Reactive Redis template
   */
  @Bean
  public ReactiveStringRedisTemplate reactiveStringRedisTemplate(
      ReactiveRedisConnectionFactory factory) {
    return new ReactiveStringRedisTemplate(factory);
  }
}
