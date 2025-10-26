package org.sagebionetworks.bixarena.api.gateway.filter;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.bixarena.api.gateway.routing.RouteConfigRegistry;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFilter that validates session cookies, mints JWTs for downstream services,
 * and sets authentication context for Spring Security.
 *
 * <p>This filter runs BEFORE Spring Security's authorization check
 * and implements Pattern B (Gateway-Minted JWTs).</p>
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@RequiredArgsConstructor
public class SessionToJwtFilter implements WebFilter {

  private static final String SESSION_COOKIE_NAME = "JSESSIONID";
  private static final String AUTH_SERVICE_AUDIENCE = "urn:bixarena:auth";

  private final RouteConfigRegistry routeConfigRegistry;
  private final WebClient authServiceClient;
  private final Cache<String, UserInfo> sessionCache;
  private final Cache<JwtCacheKey, String> jwtCache;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String method = exchange.getRequest().getMethod().name();
    String path = exchange.getRequest().getPath().value();

    log.debug("Processing request: {} {}", method, path);

    // Step 1: Check if route allows anonymous access
    if (routeConfigRegistry.isAnonymousAccessAllowed(method, path)) {
      log.debug("Anonymous access allowed for: {} {}", method, path);
      var anonymousAuth = new UsernamePasswordAuthenticationToken(
        "anonymous", null, List.of()
      );
      return chain.filter(exchange)
        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(anonymousAuth));
    }

    // Step 2: Extract session cookie
    String sessionId = extractSessionCookie(exchange);
    if (sessionId == null) {
      log.debug("Session cookie missing for: {} {}", method, path);
      return unauthorized(exchange, "Session cookie missing");
    }

    // Step 3: Get target audience
    Optional<String> audience = routeConfigRegistry.getAudienceForRoute(method, path);
    if (audience.isEmpty()) {
      log.error("No audience configured for route: {} {}", method, path);
      return Mono.error(new IllegalStateException("No audience for route: " + method + " " + path));
    }

    // Step 4: Special handling for auth-service endpoints (skip JWT minting)
    if (AUTH_SERVICE_AUDIENCE.equals(audience.get())) {
      log.debug("Auth service endpoint, validating session only: {} {}", method, path);
      return validateSession(sessionId)
        .flatMap(userInfo -> {
          var userAuth = createAuthentication(userInfo);
          return chain.filter(exchange)
            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(userAuth));
        })
        .onErrorResume(e -> handleAuthError(exchange, e, method, path));
    }

    // Step 5: For other services, validate session, mint JWT, and strip session cookie
    log.debug("Resource service endpoint, minting JWT with audience: {}", audience.get());
    return validateSession(sessionId)
      .flatMap(userInfo -> mintJwt(sessionId, audience.get())
        .flatMap(jwt -> {
          // Build modified request:
          // 1. Add Authorization header with JWT
          // 2. Remove session cookie (resource services shouldn't see it)
          ServerHttpRequest mutated = exchange.getRequest().mutate()
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
            .headers(headers -> headers.remove(HttpHeaders.COOKIE)) // Remove ALL cookies
            .build();

          var userAuth = createAuthentication(userInfo);
          log.debug("JWT minted and attached for user: {}", userInfo.sub());
          return chain.filter(exchange.mutate().request(mutated).build())
            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(userAuth));
        }))
      .onErrorResume(e -> handleAuthError(exchange, e, method, path));
  }

  /**
   * Creates Spring Security authentication from user info.
   * Maps JWT roles to Spring authorities with ROLE_ prefix.
   */
  private UsernamePasswordAuthenticationToken createAuthentication(UserInfo userInfo) {
    // JWT: "roles": ["admin", "user"]
    // Spring: ROLE_ADMIN, ROLE_USER
    List<SimpleGrantedAuthority> authorities = userInfo.roles().stream()
      .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
      .collect(Collectors.toList());
    return new UsernamePasswordAuthenticationToken(userInfo.sub(), null, authorities);
  }

  /**
   * Extracts session cookie from request.
   */
  private String extractSessionCookie(ServerWebExchange exchange) {
    var cookie = exchange.getRequest().getCookies().getFirst(SESSION_COOKIE_NAME);
    return cookie != null ? cookie.getValue() : null;
  }

  /**
   * Validates session by calling auth service /userinfo endpoint.
   * Uses cache to reduce load on auth service.
   */
  private Mono<UserInfo> validateSession(String sessionId) {
    // Check cache first
    UserInfo cached = sessionCache.getIfPresent(sessionId);
    if (cached != null) {
      log.debug("Session cache hit for sessionId: {}", maskSessionId(sessionId));
      return Mono.just(cached);
    }

    // Call auth service /userinfo endpoint with session cookie
    log.debug("Session cache miss, calling auth service /userinfo");
    return authServiceClient.get()
      .uri("/userinfo")
      .cookie(SESSION_COOKIE_NAME, sessionId)
      .retrieve()
      .bodyToMono(UserInfo.class)
      .doOnNext(info -> {
        sessionCache.put(sessionId, info);
        log.debug("Session validated for user: {}", info.sub());
      })
      .doOnError(e -> log.error("Session validation failed: {}", e.getMessage()));
  }

  /**
   * Mints JWT by calling auth service /oauth2/token with audience parameter.
   * Uses cache to reduce token minting calls.
   */
  private Mono<String> mintJwt(String sessionId, String audience) {
    JwtCacheKey key = new JwtCacheKey(sessionId, audience);

    // Check cache first
    String cached = jwtCache.getIfPresent(key);
    if (cached != null) {
      log.debug("JWT cache hit for audience: {}", audience);
      return Mono.just(cached);
    }

    // Call auth service /oauth2/token with audience parameter
    log.debug("JWT cache miss, minting new JWT for audience: {}", audience);
    return authServiceClient.post()
      .uri(uriBuilder -> uriBuilder
        .path("/oauth2/token")
        .queryParam("audience", audience)
        .build())
      .cookie(SESSION_COOKIE_NAME, sessionId)
      .retrieve()
      .bodyToMono(TokenResponse.class)
      .map(TokenResponse::accessToken)
      .doOnNext(jwt -> {
        jwtCache.put(key, jwt);
        log.debug("JWT minted and cached for audience: {}", audience);
      })
      .doOnError(e -> log.error("JWT minting failed for audience {}: {}", audience, e.getMessage()));
  }

  /**
   * Returns 401 Unauthorized response.
   */
  private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    exchange.getResponse().getHeaders().add(HttpHeaders.WWW_AUTHENTICATE,
      "Cookie realm=\"BixArena\", redirect=\"/auth/login\"");
    log.debug("Unauthorized: {}", message);
    return exchange.getResponse().setComplete();
  }

  /**
   * Handles authentication errors (session validation or JWT minting failures).
   */
  private Mono<Void> handleAuthError(ServerWebExchange exchange, Throwable e, String method, String path) {
    log.error("Authentication error for {} {}: {}", method, path, e.getMessage());

    // Handle WebClient errors from auth service
    if (e instanceof WebClientResponseException webClientEx) {
      int statusCode = webClientEx.getStatusCode().value();
      // Auth service returned 401 or 403 -> invalid/expired session
      if (statusCode == 401 || statusCode == 403) {
        return unauthorized(exchange, "Session invalid or expired");
      }
      // Auth service returned other 4xx -> client error
      if (statusCode >= 400 && statusCode < 500) {
        return unauthorized(exchange, "Authentication failed");
      }
      // Auth service returned 5xx -> service unavailable
      log.error("Auth service error: {} {}", statusCode, webClientEx.getResponseBodyAsString());
    }

    // Network errors, timeouts, or other issues
    exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
    return exchange.getResponse().setComplete();
  }

  /**
   * Masks session ID for logging (security best practice).
   */
  private String maskSessionId(String sessionId) {
    if (sessionId == null || sessionId.length() < 8) {
      return "****";
    }
    return sessionId.substring(0, 4) + "****" + sessionId.substring(sessionId.length() - 4);
  }

  /**
   * User information from /userinfo endpoint.
   */
  public record UserInfo(
    String sub,      // BixArena user ID (UUID from auth.user.id)
    String username, // from auth.user.username
    String email,    // from auth.user.email
    List<String> roles // from auth.user.role
  ) {}

  /**
   * Token response from /oauth2/token endpoint.
   */
  public record TokenResponse(
    @com.fasterxml.jackson.annotation.JsonProperty("access_token") String accessToken,
    @com.fasterxml.jackson.annotation.JsonProperty("token_type") String tokenType,
    @com.fasterxml.jackson.annotation.JsonProperty("expires_in") int expiresIn
  ) {}

  /**
   * Cache key for JWT tokens (sessionId + audience).
   */
  public record JwtCacheKey(String sessionId, String audience) {}
}
