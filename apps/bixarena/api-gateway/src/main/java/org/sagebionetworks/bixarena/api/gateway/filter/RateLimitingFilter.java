package org.sagebionetworks.bixarena.api.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.gateway.configuration.AppProperties;
import org.sagebionetworks.bixarena.api.gateway.model.dto.RateLimitErrorDto;
import org.sagebionetworks.bixarena.api.gateway.ratelimit.RateLimitKeyResolver;
import org.sagebionetworks.bixarena.api.gateway.ratelimit.RateLimitMetrics;
import org.sagebionetworks.bixarena.api.gateway.ratelimit.ValkeyRateLimiter;
import org.sagebionetworks.bixarena.api.gateway.routing.RouteConfigRegistry;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFilter that enforces rate limiting for all requests.
 *
 * <p>This filter runs BEFORE authentication (SessionToJwtFilter) to prevent resource exhaustion
 * from unauthenticated requests.
 *
 * <p><b>Rate Limiting Strategy:</b>
 *
 * <ul>
 *   <li>Authenticated requests (with session): Rate limited by session ID
 *   <li>Anonymous requests (no session): Rate limited by client IP address
 *   <li>Per-route limits: Configurable via routes.yml (rateLimitRequestsPerMinute)
 *   <li>Default limit: Configurable via application.yml (app.rate-limit.default-requests-per-minute)
 *   <li>Fail-open: If Valkey is unavailable, requests are allowed (configurable)
 * </ul>
 *
 * <p><b>Response Headers:</b>
 *
 * <ul>
 *   <li>X-RateLimit-Limit: Maximum requests allowed per minute
 *   <li>X-RateLimit-Remaining: Remaining requests in current window
 *   <li>X-RateLimit-Reset: Seconds until window resets
 *   <li>Retry-After: (429 only) Seconds to wait before retrying
 * </ul>
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class RateLimitingFilter implements WebFilter {

  private final ValkeyRateLimiter rateLimiter;
  private final RateLimitKeyResolver keyResolver;
  private final RouteConfigRegistry routeConfigRegistry;
  private final AppProperties appProperties;
  private final RateLimitMetrics metrics;
  private final ObjectMapper objectMapper;

  private static final Duration WINDOW = Duration.ofMinutes(1);

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    // Skip rate limiting if disabled
    if (!appProperties.rateLimit().enabled()) {
      return chain.filter(exchange);
    }

    // Skip rate limiting for health checks
    String path = exchange.getRequest().getPath().value();
    if (isHealthCheckEndpoint(path)) {
      return chain.filter(exchange);
    }

    String method = exchange.getRequest().getMethod().name();
    int limit = getRouteRateLimit(method, path);

    return keyResolver
        .resolve(exchange, appProperties.rateLimit().keyPrefix())
        .flatMap(key -> rateLimiter.isAllowed(key, limit, WINDOW))
        .flatMap(
            result -> {
              // Add rate limit headers to response
              addRateLimitHeaders(exchange, result);

              if (result.allowed()) {
                // Request allowed, proceed with filter chain
                return chain.filter(exchange);
              } else {
                // Rate limit exceeded, return 429 Too Many Requests
                return rateLimitExceeded(exchange, result);
              }
            });
  }

  /**
   * Get the rate limit for the current route.
   *
   * @param method HTTP method
   * @param path Request path
   * @return Requests per minute limit
   */
  private int getRouteRateLimit(String method, String path) {
    return routeConfigRegistry
        .getRouteConfig(method, path)
        .filter(config -> config.rateLimitRequestsPerMinute() != null)
        .map(config -> config.rateLimitRequestsPerMinute())
        .orElse(appProperties.rateLimit().defaultRequestsPerMinute());
  }

  /**
   * Check if path is a health check endpoint that should bypass rate limiting.
   *
   * @param path Request path
   * @return true if health check endpoint
   */
  private boolean isHealthCheckEndpoint(String path) {
    return path.startsWith("/actuator/health")
        || path.equals("/health")
        || path.equals("/actuator/metrics");
  }

  /**
   * Add rate limit headers to the response.
   *
   * @param exchange Server web exchange
   * @param result Rate limit result
   */
  private void addRateLimitHeaders(
      ServerWebExchange exchange, ValkeyRateLimiter.RateLimitResult result) {
    var headers = exchange.getResponse().getHeaders();

    headers.add("X-RateLimit-Limit", String.valueOf(result.limit()));

    // Only add remaining/reset if we have valid count data
    if (result.currentCount() >= 0) {
      headers.add("X-RateLimit-Remaining", String.valueOf(result.remaining()));
    }

    // Add reset time if we have valid retry-after data
    if (result.retryAfterMillis() > 0) {
      headers.add("X-RateLimit-Reset", String.valueOf(result.retryAfterSeconds()));
    }
  }

  /**
   * Return 429 Too Many Requests response.
   *
   * @param exchange Server web exchange
   * @param result Rate limit result
   * @return Mono that completes the response
   */
  private Mono<Void> rateLimitExceeded(
      ServerWebExchange exchange, ValkeyRateLimiter.RateLimitResult result) {
    var response = exchange.getResponse();
    response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);

    // Record rate limit exceeded metrics
    String path = exchange.getRequest().getPath().value();
    String keyType = extractKeyTypeFromExchange(exchange);
    metrics.recordRateLimitExceeded(keyType, path);

    // Add Retry-After header (required by HTTP spec for 429)
    if (result.retryAfterSeconds() > 0) {
      response.getHeaders().add("Retry-After", String.valueOf(result.retryAfterSeconds()));
    }

    // Return JSON error response using RateLimitErrorDto schema
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

    // Build error response matching OpenAPI schema
    RateLimitErrorDto errorDto =
        RateLimitErrorDto.builder()
            .title("Rate Limit Exceeded")
            .status(429)
            .detail("Too many requests. Please try again later.")
            .limit((int) result.limit())
            .window("1 minute")
            .retryAfterSeconds((int) result.retryAfterSeconds())
            .build();

    try {
      String body = objectMapper.writeValueAsString(errorDto);
      var buffer = response.bufferFactory().wrap(body.getBytes());
      return response.writeWith(Mono.just(buffer));
    } catch (JsonProcessingException e) {
      log.error("Failed to serialize rate limit error response", e);
      // Fallback to simple error message
      var buffer =
          response
              .bufferFactory()
              .wrap("{\"error\":\"rate_limit_exceeded\"}".getBytes());
      return response.writeWith(Mono.just(buffer));
    }
  }

  /**
   * Extract key type from exchange (session vs IP).
   *
   * @param exchange Server web exchange
   * @return Key type ("session" or "ip")
   */
  private String extractKeyTypeFromExchange(ServerWebExchange exchange) {
    var cookies = exchange.getRequest().getCookies().get("JSESSIONID");
    return (cookies != null && !cookies.isEmpty()) ? "session" : "ip";
  }
}
