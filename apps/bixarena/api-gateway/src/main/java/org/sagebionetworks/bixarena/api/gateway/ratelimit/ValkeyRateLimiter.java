package org.sagebionetworks.bixarena.api.gateway.ratelimit;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * Distributed rate limiter using Valkey (Redis-compatible) with sliding window algorithm.
 *
 * <p>This implementation uses a sliding window counter approach which provides:
 *
 * <ul>
 *   <li>Accurate rate limiting across distributed gateway instances
 *   <li>Memory-efficient storage (one key per identity per time window)
 *   <li>Automatic expiration of old rate limit data
 *   <li>Atomic operations via Lua scripts
 * </ul>
 *
 * <p><b>Algorithm:</b> Sliding window counter using sorted sets where:
 *
 * <ul>
 *   <li>Key: {@code bixarena:gateway:ratelimit:<identity>}
 *   <li>Score: timestamp (milliseconds since epoch)
 *   <li>Member: timestamp + random component (to allow duplicate timestamps)
 *   <li>Window: remove entries older than (now - window duration)
 *   <li>Count: count remaining entries after cleanup
 *   <li>Compare: check if count <= limit
 * </ul>
 *
 * <p><b>Example:</b> For a 100 req/min limit:
 *
 * <pre>
 * Time: 12:00:00 - Request 1-50 → Allowed (50/100)
 * Time: 12:00:30 - Request 51-100 → Allowed (100/100)
 * Time: 12:00:45 - Request 101 → DENIED (100/100)
 * Time: 12:01:00 - Request 102 → Allowed (51/100, first 50 expired)
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ValkeyRateLimiter {

  private final ReactiveStringRedisTemplate redisTemplate;
  private final RateLimitMetrics metrics;

  /**
   * Lua script for atomic rate limiting check.
   *
   * <p>KEYS[1]: Rate limit key (e.g., "bixarena:gateway:ratelimit:user:123")
   *
   * <p>ARGV[1]: Current timestamp (milliseconds since epoch)
   *
   * <p>ARGV[2]: Window start timestamp (current - window duration)
   *
   * <p>ARGV[3]: Maximum allowed requests
   *
   * <p>ARGV[4]: Unique request ID (timestamp + random)
   *
   * <p>ARGV[5]: Key TTL in seconds (window duration + buffer)
   *
   * <p>Returns array: [allowed (1/0), current_count, ttl_milliseconds]
   */
  private static final String RATE_LIMIT_SCRIPT =
      """
      -- Remove entries older than the window
      redis.call('ZREMRANGEBYSCORE', KEYS[1], '-inf', ARGV[2])

      -- Get current count
      local current = redis.call('ZCARD', KEYS[1])

      -- Check if we can allow this request
      local allowed = 0
      if current < tonumber(ARGV[3]) then
        -- Add the new request timestamp
        redis.call('ZADD', KEYS[1], ARGV[1], ARGV[4])
        allowed = 1
        current = current + 1
      end

      -- Set expiration on the key (window duration + small buffer)
      redis.call('EXPIRE', KEYS[1], ARGV[5])

      -- Get TTL of the oldest entry to calculate retry-after
      local oldest = redis.call('ZRANGE', KEYS[1], 0, 0, 'WITHSCORES')
      local ttl_ms = 0
      if #oldest > 0 then
        local oldest_time = tonumber(oldest[2])
        local window_duration_ms = tonumber(ARGV[1]) - tonumber(ARGV[2])
        ttl_ms = math.max(0, (oldest_time + window_duration_ms) - tonumber(ARGV[1]))
      end

      return {allowed, current, ttl_ms}
      """;

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static final RedisScript<List> SCRIPT =
      RedisScript.of(RATE_LIMIT_SCRIPT, List.class);

  /**
   * Check if a request is allowed under the rate limit.
   *
   * @param key Unique identifier for the rate limit bucket (e.g., user ID, IP address)
   * @param limit Maximum number of requests allowed in the window
   * @param window Duration of the sliding window
   * @return RateLimitResult indicating if request is allowed and rate limit status
   */
  public Mono<RateLimitResult> isAllowed(String key, int limit, Duration window) {
    var timerSample = metrics.startValkeyOperation("isAllowed");

    long nowMillis = Instant.now().toEpochMilli();
    long windowStartMillis = nowMillis - window.toMillis();
    String uniqueRequestId = nowMillis + ":" + Math.random();
    long ttlSeconds = window.toSeconds() + 60; // Add 60s buffer for cleanup

    List<String> keys = List.of(key);
    List<String> args =
        List.of(
            String.valueOf(nowMillis),
            String.valueOf(windowStartMillis),
            String.valueOf(limit),
            uniqueRequestId,
            String.valueOf(ttlSeconds));

    return redisTemplate
        .execute(SCRIPT, keys, args)
        .next()
        .map(
            result -> {
              // Cast raw List elements to Long
              boolean allowed = ((Number) result.get(0)).longValue() == 1;
              long currentCount = ((Number) result.get(1)).longValue();
              long ttlMillis = ((Number) result.get(2)).longValue();

              // Record metrics
              String keyType = extractKeyType(key);
              metrics.stopValkeyOperation(timerSample, "isAllowed", true);
              metrics.recordRateLimitCheck(allowed, keyType, null);

              if (allowed) {
                log.debug(
                    "Rate limit ALLOWED for key={}, count={}/{}, window={}",
                    maskKey(key),
                    currentCount,
                    limit,
                    window);
              } else {
                log.warn(
                    "Rate limit EXCEEDED for key={}, count={}/{}, window={}, retry_after={}ms",
                    maskKey(key),
                    currentCount,
                    limit,
                    window,
                    ttlMillis);
              }

              return new RateLimitResult(allowed, currentCount, limit, ttlMillis);
            })
        .onErrorResume(
            e -> {
              // Record error metrics
              metrics.stopValkeyOperation(timerSample, "isAllowed", false);
              metrics.recordValkeyError("isAllowed", e.getClass().getSimpleName());

              log.error("Rate limit check failed for key={}, error: {}", maskKey(key), e.getMessage(), e);
              // Fail open: allow request if Redis/Valkey is unavailable
              return Mono.just(
                  new RateLimitResult(
                      true, // allowed
                      -1, // unknown count
                      limit,
                      0 // no retry-after
                      ));
            });
  }

  /**
   * Extract the key type from the rate limit key.
   *
   * @param key Rate limit key (e.g., "bixarena:gateway:ratelimit:session:abc123")
   * @return Key type ("session" or "ip")
   */
  private String extractKeyType(String key) {
    if (key != null) {
      if (key.contains(":session:")) {
        return "session";
      } else if (key.contains(":ip:")) {
        return "ip";
      }
    }
    return "unknown";
  }

  /**
   * Mask sensitive parts of the key for logging.
   *
   * @param key Rate limit key
   * @return Masked key string
   */
  private String maskKey(String key) {
    if (key == null || key.length() <= 8) {
      return "***";
    }
    return key.substring(0, 4) + "***" + key.substring(key.length() - 4);
  }

  /**
   * Result of a rate limit check.
   *
   * @param allowed Whether the request is allowed
   * @param currentCount Current number of requests in the window
   * @param limit Maximum allowed requests
   * @param retryAfterMillis Milliseconds until the oldest request expires (for 429 Retry-After
   *     header)
   */
  public record RateLimitResult(
      boolean allowed, long currentCount, long limit, long retryAfterMillis) {

    /**
     * Get the remaining number of requests allowed in the current window.
     *
     * @return Remaining requests (0 if limit exceeded, -1 if unknown)
     */
    public long remaining() {
      if (currentCount < 0) {
        return -1; // Unknown (e.g., Redis error)
      }
      return Math.max(0, limit - currentCount);
    }

    /**
     * Get retry-after duration in seconds (rounded up).
     *
     * @return Seconds to wait before retrying
     */
    public long retryAfterSeconds() {
      return (retryAfterMillis + 999) / 1000; // Round up
    }
  }
}
