package org.sagebionetworks.openchallenges.api.gateway.security;

import org.sagebionetworks.openchallenges.api.gateway.configuration.AuthConfiguration;
import org.sagebionetworks.openchallenges.api.gateway.service.GatewayAuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * WebFilter that handles API key authentication.
 * Runs BEFORE Spring Security authorization filters.
 */
@Component
public class ApiKeyAuthenticationGatewayFilter implements WebFilter {

  private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthenticationGatewayFilter.class);
  
  private static final String API_KEY_HEADER = "X-API-Key";

  private final GatewayAuthenticationService authenticationService;
  private final AuthConfiguration authConfiguration;

  public ApiKeyAuthenticationGatewayFilter(
      GatewayAuthenticationService authenticationService,
      AuthConfiguration authConfiguration) {
    this.authenticationService = authenticationService;
    this.authConfiguration = authConfiguration;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getPath().toString();

    // Skip if already authenticated by JWT filter
    if (exchange.getRequest().getHeaders().containsKey("Authorization")) {
      logger.debug("Request already has Authorization header, skipping API key validation for: {}", path);
      return chain.filter(exchange);
    }

    // Extract API key from X-API-Key header
    String apiKey = request.getHeaders().getFirst(API_KEY_HEADER);
    if (apiKey == null || apiKey.trim().isEmpty()) {
      logger.debug("No API key found for request to: {}", path);
      // No API key - let Spring Security decide if this endpoint requires authentication
      return chain.filter(exchange);
    }

    logger.debug("Exchanging API key for JWT for request to: {}", path);

    // Exchange API key for JWT using OAuth2 Client Credentials flow
    return authenticationService.exchangeApiKeyForJwt(apiKey)
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

          logger.debug("API key exchanged for JWT successfully: {} scopes accessing: {}", 
              tokenResponse.getScope(), path);

          return chain.filter(exchange.mutate().request(modifiedRequest).build())
              .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        })
        .onErrorResume(GatewayAuthenticationService.AuthenticationException.class, ex -> {
          logger.warn("API key authentication failed for request to {}: {}", path, ex.getMessage());
          exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
          exchange.getResponse().getHeaders().add("WWW-Authenticate", 
              String.format("ApiKey realm=\"%s\"", authConfiguration.getRealm()));
          return exchange.getResponse().setComplete();
        })
        .onErrorResume(Exception.class, ex -> {
          logger.error("Unexpected error during API key authentication for request to {}: {}", path, ex.getMessage());
          exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
          return exchange.getResponse().setComplete();
        });
  }
}
