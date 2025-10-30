package org.sagebionetworks.bixarena.api.gateway.ratelimit;

import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Resolves the rate limit key for a request based on identity.
 *
 * <p>Resolution strategy:
 *
 * <ol>
 *   <li><b>Authenticated requests</b> (with session cookie): Use session ID
 *   <li><b>Anonymous requests</b> (no session): Use client IP address
 *   <li><b>Fallback</b>: Use "unknown" (should rarely happen)
 * </ol>
 *
 * <p>This ensures:
 *
 * <ul>
 *   <li>Authenticated users are tracked individually (prevents one user from exhausting shared IP
 *       limits)
 *   <li>Anonymous users share rate limits by IP (prevents abuse while allowing legitimate browsing)
 *   <li>Session-based tracking works even before JWT is minted
 * </ul>
 */
@Slf4j
@Component
public class RateLimitKeyResolver {

  private static final String SESSION_COOKIE_NAME = "JSESSIONID";
  private static final String KEY_PREFIX = "bixarena:gateway:ratelimit";

  /**
   * Resolve the rate limit key for the given request.
   *
   * @param exchange Server web exchange
   * @param keyPrefix Custom key prefix (optional, defaults to standard prefix)
   * @return Rate limit key (e.g., "bixarena:gateway:ratelimit:session:abc123" or
   *     "bixarena:gateway:ratelimit:ip:192.168.1.1")
   */
  public Mono<String> resolve(ServerWebExchange exchange, String keyPrefix) {
    String prefix = (keyPrefix != null && !keyPrefix.isBlank()) ? keyPrefix : KEY_PREFIX;

    // Try to get session ID from cookie
    return extractSessionId(exchange)
        .map(sessionId -> prefix + ":session:" + sessionId)
        .switchIfEmpty(
            // Fall back to IP address for anonymous requests
            Mono.fromCallable(() -> {
              String ip = extractClientIp(exchange.getRequest());
              return prefix + ":ip:" + ip;
            }))
        .doOnNext(key -> log.debug("Resolved rate limit key: {}", maskKey(key)));
  }

  /**
   * Extract session ID from JSESSIONID cookie.
   *
   * @param exchange Server web exchange
   * @return Mono of session ID, or empty if no session cookie found
   */
  private Mono<String> extractSessionId(ServerWebExchange exchange) {
    List<HttpCookie> cookies = exchange.getRequest().getCookies().get(SESSION_COOKIE_NAME);
    if (cookies != null && !cookies.isEmpty()) {
      String sessionId = cookies.get(0).getValue();
      if (sessionId != null && !sessionId.isBlank()) {
        return Mono.just(sessionId);
      }
    }
    return Mono.empty();
  }

  /**
   * Extract client IP address from request.
   *
   * <p>Checks headers in order:
   *
   * <ol>
   *   <li>X-Forwarded-For (if behind proxy/load balancer)
   *   <li>X-Real-IP (alternative proxy header)
   *   <li>Remote address (direct connection)
   * </ol>
   *
   * @param request HTTP request
   * @return Client IP address
   */
  private String extractClientIp(ServerHttpRequest request) {
    // Check X-Forwarded-For header (standard for proxies)
    String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isBlank()) {
      // X-Forwarded-For can contain multiple IPs: "client, proxy1, proxy2"
      // Take the first one (original client)
      String clientIp = xForwardedFor.split(",")[0].trim();
      if (!clientIp.isBlank()) {
        return clientIp;
      }
    }

    // Check X-Real-IP header (alternative proxy header)
    String xRealIp = request.getHeaders().getFirst("X-Real-IP");
    if (xRealIp != null && !xRealIp.isBlank()) {
      return xRealIp;
    }

    // Fall back to remote address
    return Objects.requireNonNullElse(
            request.getRemoteAddress(),
            new java.net.InetSocketAddress("0.0.0.0", 0))
        .getAddress()
        .getHostAddress();
  }

  /**
   * Mask sensitive parts of the key for logging.
   *
   * @param key Rate limit key
   * @return Masked key string
   */
  private String maskKey(String key) {
    if (key == null || key.length() <= 16) {
      return "***";
    }
    // Show prefix + first few chars + last few chars
    int prefixEnd = key.indexOf(":session:") != -1
        ? key.indexOf(":session:") + 9
        : key.indexOf(":ip:") != -1
        ? key.indexOf(":ip:") + 4
        : 8;

    if (key.length() <= prefixEnd + 8) {
      return key.substring(0, prefixEnd) + "***";
    }

    return key.substring(0, prefixEnd + 4) + "***" + key.substring(key.length() - 4);
  }
}
