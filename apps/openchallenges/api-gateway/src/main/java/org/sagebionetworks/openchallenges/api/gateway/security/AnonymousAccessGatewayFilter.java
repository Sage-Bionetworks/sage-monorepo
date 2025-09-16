package org.sagebionetworks.openchallenges.api.gateway.security;

import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.api.gateway.routing.RouteConfigRegistry;
import org.sagebionetworks.openchallenges.api.gateway.service.GatewayAuthenticationService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFilter that handles anonymous access for public endpoints.
 * Runs BEFORE API key authentication to provide JWT tokens for anonymous access.
 *
 * This filter checks if a route allows anonymous access (x-anonymous-access: true in OpenAPI)
 * and generates JWT tokens using the anonymous OAuth2 client when:
 * 1. No Authorization header is present
 * 2. No API key header is present
 * 3. The route is configured for anonymous access
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // Run before API key filter
@RequiredArgsConstructor
public class AnonymousAccessGatewayFilter implements WebFilter {

  private static final String API_KEY_HEADER = "X-API-Key";

  private final GatewayAuthenticationService authenticationService;
  private final RouteConfigRegistry routeConfigRegistry;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getPath().toString();
    String method = request.getMethod().name();

    // Skip if already has Authorization header or API key
    if (hasExistingAuthentication(request)) {
      log.debug("Request already has authentication, skipping anonymous access for: {}", path);
      return chain.filter(exchange);
    }

    // Check if this route allows anonymous access
    if (!routeConfigRegistry.isAnonymousAccessAllowed(method, path)) {
      log.debug("Route does not allow anonymous access: {} {}", method, path);
      return chain.filter(exchange);
    }

    log.debug("Generating anonymous JWT for public endpoint: {} {}", method, path);

    // Generate anonymous JWT token using OAuth2 client credentials flow
    return authenticationService
      .generateAnonymousJwt(method, path)
      .flatMap(tokenResponse -> {
        // Add the JWT to the Authorization header for backend services
        ServerHttpRequest modifiedRequest = request
          .mutate()
          .header("Authorization", "Bearer " + tokenResponse.accessToken())
          .build();

        // Extract scopes from token response for Spring Security
        String[] scopes = tokenResponse.scope() != null
          ? tokenResponse.scope().split(" ")
          : new String[0];

        var authorities = Arrays.stream(scopes)
          .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
          .collect(Collectors.toList());

        // Create authentication for anonymous access
        var authentication = new UsernamePasswordAuthenticationToken(
          "anonymous", // principal name for anonymous access
          null,
          authorities
        );

        log.debug(
          "Anonymous JWT generated successfully: {} scopes for: {} {}",
          tokenResponse.scope(),
          method,
          path
        );

        return chain
          .filter(exchange.mutate().request(modifiedRequest).build())
          .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
      })
      .onErrorResume(GatewayAuthenticationService.AuthenticationException.class, ex -> {
        log.warn("Anonymous JWT generation failed for {}: {}", path, ex.getMessage());
        // For anonymous access failures, continue without authentication
        // The backend service will decide if the endpoint truly requires auth
        log.debug("Continuing without authentication for potentially public endpoint: {}", path);
        return chain.filter(exchange);
      })
      .onErrorResume(Exception.class, ex -> {
        log.error(
          "Unexpected error during anonymous JWT generation for {}: {}",
          path,
          ex.getMessage()
        );
        // For anonymous access, don't fail the request - let it continue without auth
        return chain.filter(exchange);
      });
  }

  /**
   * Check if the request already has authentication (Authorization header or API key).
   */
  private boolean hasExistingAuthentication(ServerHttpRequest request) {
    return (
      request.getHeaders().containsKey("Authorization") ||
      (request.getHeaders().containsKey(API_KEY_HEADER) &&
        !request.getHeaders().getFirst(API_KEY_HEADER).trim().isEmpty())
    );
  }
}
