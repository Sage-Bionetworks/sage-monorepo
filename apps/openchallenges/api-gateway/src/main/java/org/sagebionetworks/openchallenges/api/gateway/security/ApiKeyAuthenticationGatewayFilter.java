package org.sagebionetworks.openchallenges.api.gateway.security;

import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.api.gateway.configuration.AppProperties;
import org.sagebionetworks.openchallenges.api.gateway.service.GatewayAuthenticationService;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
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
 * WebFilter that handles API key authentication.
 * Runs AFTER anonymous access filter but BEFORE Spring Security authorization filters.
 *
 * This filter focuses solely on API key authentication:
 * 1. Extracts API keys from X-API-Key header
 * 2. Exchanges API keys for JWT tokens via OAuth2 client credentials flow
 * 3. Adds JWT to Authorization header for backend services
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 2) // Run after anonymous access filter
@RequiredArgsConstructor
public class ApiKeyAuthenticationGatewayFilter implements WebFilter {

  private static final String API_KEY_HEADER = "X-API-Key";

  private final GatewayAuthenticationService authenticationService;
  private final AppProperties appProperties;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getPath().toString();
    String method = request.getMethod().name();

    log.info("=== API KEY FILTER: Processing request {} {} ===", method, path);

    // Skip if already authenticated by JWT filter
    if (exchange.getRequest().getHeaders().containsKey("Authorization")) {
      log.info(
        "Request already has Authorization header, skipping API key validation for: {}",
        path
      );
      return chain.filter(exchange);
    }

    // Extract API key from X-API-Key header
    String apiKey = request.getHeaders().getFirst(API_KEY_HEADER);
    log.info("API Key header present: {}", apiKey != null && !apiKey.trim().isEmpty());

    if (apiKey == null || apiKey.trim().isEmpty()) {
      log.info("No API key found for request to: {} - continuing without API key auth", path);
      // No API key - let Spring Security decide if this endpoint requires authentication
      return chain.filter(exchange);
    }

    log.info("Found API key, starting exchange for JWT for request to: {}", path);

    // Exchange API key for JWT using OAuth2 Client Credentials flow
    return authenticationService
      .exchangeApiKeyForJwt(apiKey, method, path)
      .flatMap(tokenResponse -> {
        log.info("=== API KEY FILTER: JWT exchange successful ===");
        log.info("Token scope: {}", tokenResponse.scope());

        // Replace the API key with the JWT in the Authorization header
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

        log.info("Spring Security authorities created: {}", authorities);

        // Create authentication for the service principal (API key client)
        var authentication = new UsernamePasswordAuthenticationToken(
          "api-key-client", // principal name for API key clients
          null,
          authorities
        );

        log.info("=== API KEY FILTER: Forwarding request with JWT Authorization header ===");

        return chain
          .filter(exchange.mutate().request(modifiedRequest).build())
          .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
      })
      .onErrorResume(GatewayAuthenticationService.AuthenticationException.class, ex -> {
        log.error("=== API KEY FILTER: Authentication failed ===");
        log.error("Error for request to {}: {}", path, ex.getMessage(), ex);
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange
          .getResponse()
          .getHeaders()
          .add(
            "WWW-Authenticate",
            String.format("ApiKey realm=\"%s\"", appProperties.auth().realm())
          );
        return exchange.getResponse().setComplete();
      })
      .onErrorResume(Exception.class, ex -> {
        log.error(
          "Unexpected error during API key authentication for request to {}: {}",
          path,
          ex.getMessage()
        );
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return exchange.getResponse().setComplete();
      });
  }
}
