package org.sagebionetworks.openchallenges.api.gateway.security;

import org.sagebionetworks.openchallenges.api.gateway.configuration.AuthConfiguration;
import org.sagebionetworks.openchallenges.api.gateway.configuration.RouteScopeConfiguration;
import org.sagebionetworks.openchallenges.api.gateway.service.GatewayAuthenticationService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WebFilter that handles API key authentication.
 * Runs BEFORE Spring Security authorization filters.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationGatewayFilter implements WebFilter {
  
  private static final String API_KEY_HEADER = "X-API-Key";

  private final GatewayAuthenticationService authenticationService;
  private final AuthConfiguration authConfiguration;
  private final RouteScopeConfiguration routeScopeConfiguration;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getPath().toString();

    // Skip if already authenticated by JWT filter
    if (exchange.getRequest().getHeaders().containsKey("Authorization")) {
      log.debug("Request already has Authorization header, skipping API key validation for: {}", path);
      return chain.filter(exchange);
    }

    // Extract API key from X-API-Key header
    String apiKey = request.getHeaders().getFirst(API_KEY_HEADER);
    if (apiKey == null || apiKey.trim().isEmpty()) {
      // No API key - check if this route allows anonymous access
      String method = request.getMethod().name();
      if (routeScopeConfiguration.isAnonymousAccessAllowed(method, path)) {
        log.debug("Generating anonymous JWT for request to: {}", path);
        return generateAnonymousJwt(exchange, chain, method, path);
      }
      
      log.debug("No API key found for request to: {}", path);
      // No API key and not anonymous - let Spring Security decide if this endpoint requires authentication
      return chain.filter(exchange);
    }

    log.debug("Exchanging API key for JWT for request to: {}", path);

    // Exchange API key for JWT using OAuth2 Client Credentials flow
    String method = request.getMethod().name();
    return authenticationService.exchangeApiKeyForJwt(apiKey, method, path)
        .flatMap(tokenResponse -> {
          // Replace the API key with the JWT in the Authorization header
          ServerHttpRequest modifiedRequest = request.mutate()
              .header("Authorization", "Bearer " + tokenResponse.getAccessToken())
              .build();

          // Extract scopes from token response for Spring Security
          String[] scopes = tokenResponse.getScope() != null ? 
              tokenResponse.getScope().split(" ") : new String[0];
          
          var authorities = Arrays.stream(scopes)
              .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
              .collect(Collectors.toList());
          
          // Create authentication for the service principal (API key client)
          var authentication = new UsernamePasswordAuthenticationToken(
              "api-key-client", // principal name for API key clients
              null, 
              authorities
          );

          log.debug("API key exchanged for JWT successfully: {} scopes accessing: {}", 
              tokenResponse.getScope(), path);

          return chain.filter(exchange.mutate().request(modifiedRequest).build())
              .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        })
        .onErrorResume(GatewayAuthenticationService.AuthenticationException.class, ex -> {
          log.warn("API key authentication failed for request to {}: {}", path, ex.getMessage());
          exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
          exchange.getResponse().getHeaders().add("WWW-Authenticate", 
              String.format("ApiKey realm=\"%s\"", authConfiguration.getRealm()));
          return exchange.getResponse().setComplete();
        })
        .onErrorResume(Exception.class, ex -> {
          log.error("Unexpected error during API key authentication for request to {}: {}", path, ex.getMessage());
          exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
          return exchange.getResponse().setComplete();
        });
  }

  /**
   * Generate an anonymous JWT token for public endpoints that allow anonymous access.
   */
  private Mono<Void> generateAnonymousJwt(ServerWebExchange exchange, WebFilterChain chain, String method, String path) {
    ServerHttpRequest request = exchange.getRequest();
    
    // Generate anonymous JWT token using OAuth2 client credentials flow
    return authenticationService.generateAnonymousJwt(method, path)
        .flatMap(tokenResponse -> {
          // Add the JWT to the Authorization header for backend services
          ServerHttpRequest modifiedRequest = request.mutate()
              .header("Authorization", "Bearer " + tokenResponse.getAccessToken())
              .build();

          // Extract scopes from token response for Spring Security
          String[] scopes = tokenResponse.getScope() != null ? 
              tokenResponse.getScope().split(" ") : new String[0];
          
          var authorities = Arrays.stream(scopes)
              .map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
              .collect(Collectors.toList());
          
          // Create authentication for anonymous access
          var authentication = new UsernamePasswordAuthenticationToken(
              "anonymous", // principal name for anonymous access
              null, 
              authorities
          );

          log.debug("Anonymous JWT generated successfully: {} scopes accessing: {}", 
              tokenResponse.getScope(), path);

          return chain.filter(exchange.mutate().request(modifiedRequest).build())
              .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        })
        .onErrorResume(GatewayAuthenticationService.AuthenticationException.class, ex -> {
          log.warn("Anonymous JWT generation failed for request to {}: {}", path, ex.getMessage());
          // For anonymous access failures, we should still allow the request to continue
          // but without authentication context (let the backend decide)
          log.debug("Continuing without authentication for potentially public endpoint: {}", path);
          return chain.filter(exchange);
        })
        .onErrorResume(Exception.class, ex -> {
          log.error("Unexpected error during anonymous JWT generation for request to {}: {}", path, ex.getMessage());
          // For anonymous access, don't fail the request - let it continue without auth
          return chain.filter(exchange);
        });
  }
}
