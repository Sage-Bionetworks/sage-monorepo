package org.sagebionetworks.bixarena.api.gateway.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.sagebionetworks.bixarena.api.gateway.filter.SessionToJwtFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for auth service client and caching.
 */
@Configuration
public class AuthClientConfiguration {

  @Value("${app.auth-service.url:http://bixarena-auth-service:8115}")
  private String authServiceUrl;

  @Value("${app.cache.session.ttl-seconds:60}")
  private int sessionCacheTtlSeconds;

  @Value("${app.cache.session.max-size:1000}")
  private int sessionCacheMaxSize;

  @Value("${app.cache.jwt.ttl-seconds:540}")  // 9 minutes (JWT TTL - 60s)
  private int jwtCacheTtlSeconds;

  @Value("${app.cache.jwt.max-size:1000}")
  private int jwtCacheMaxSize;

  /**
   * WebClient for calling auth service endpoints.
   */
  @Bean
  public WebClient authServiceClient() {
    return WebClient.builder()
      .baseUrl(authServiceUrl)
      .build();
  }

  /**
   * Cache for session validation results.
   * TTL should be short (30-60s) to balance performance and security.
   */
  @Bean
  public Cache<String, SessionToJwtFilter.UserInfo> sessionCache() {
    return Caffeine.newBuilder()
      .expireAfterWrite(Duration.ofSeconds(sessionCacheTtlSeconds))
      .maximumSize(sessionCacheMaxSize)
      .build();
  }

  /**
   * Cache for minted JWTs.
   * TTL should be JWT expiry - 60s to prevent expired tokens.
   */
  @Bean
  public Cache<SessionToJwtFilter.JwtCacheKey, String> jwtCache() {
    return Caffeine.newBuilder()
      .expireAfterWrite(Duration.ofSeconds(jwtCacheTtlSeconds))
      .maximumSize(jwtCacheMaxSize)
      .build();
  }
}
