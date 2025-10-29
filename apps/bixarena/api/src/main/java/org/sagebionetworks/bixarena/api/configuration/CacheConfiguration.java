package org.sagebionetworks.bixarena.api.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Configuration for Redis-backed caching in the BixArena API service.
 * All cache names are namespaced with "bixarena:api:" prefix to avoid collisions
 * with other services (auth, ai, etc.) using the shared Valkey instance.
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

  @Bean
  public RedisCacheManager cacheManager(
      RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {

    // Default cache configuration
    RedisCacheConfiguration defaultConfig =
        RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(5)) // Default 5 minutes
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new StringRedisSerializer()))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer(objectMapper)))
            .disableCachingNullValues();

    return RedisCacheManager.builder(connectionFactory).cacheDefaults(defaultConfig).build();
  }
}
