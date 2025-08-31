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

import java.util.List;

/**
 * WebFilter that handles API key authentication.
 * Runs BEFORE Spring Security authorization filters.
 */
@Component
public class ApiKeyAuthenticationGatewayFilter implements WebFilter {

  private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthenticationGatewayFilter.class);
  
  private static final String API_KEY_HEADER = "X-API-Key";
  
  // Standard trusted headers for downstream services
  private static final String X_AUTHENTICATED_USER_ID_HEADER = "X-Authenticated-User-Id";
  private static final String X_AUTHENTICATED_USER_HEADER = "X-Authenticated-User";
  private static final String X_AUTHENTICATED_ROLES_HEADER = "X-Authenticated-Roles";
  private static final String X_SCOPES_HEADER = "X-Scopes";

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

    // Skip if already authenticated by JWT (check for standard auth headers)
    if (request.getHeaders().containsKey(X_AUTHENTICATED_USER_ID_HEADER)) {
      logger.debug("Request already authenticated by JWT, skipping API key validation for: {}", path);
      return chain.filter(exchange);
    }

    // Extract API key from X-API-Key header
    String apiKey = request.getHeaders().getFirst(API_KEY_HEADER);
    if (apiKey == null || apiKey.trim().isEmpty()) {
      logger.debug("No API key found for request to: {}", path);
      // No API key - let Spring Security decide if this endpoint requires authentication
      return chain.filter(exchange);
    }

    logger.debug("Validating API key for request to: {}", path);

    // Validate API key with Auth Service
    return authenticationService.validateApiKey(apiKey)
        .flatMap(validationResponse -> {
          if (!validationResponse.isValid()) {
            logger.warn("Invalid API key for request to: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().add("WWW-Authenticate", 
                String.format("ApiKey realm=\"%s\"", authConfiguration.getRealm()));
            return exchange.getResponse().setComplete();
          }

          // Add standard user context headers for downstream services
          ServerHttpRequest.Builder requestBuilder = request.mutate()
              .header(X_AUTHENTICATED_USER_ID_HEADER, validationResponse.getUserId())
              .header(X_AUTHENTICATED_USER_HEADER, validationResponse.getUsername())
              .header(X_AUTHENTICATED_ROLES_HEADER, validationResponse.getRole());

          // Add scopes if available
          if (validationResponse.getScopes() != null && validationResponse.getScopes().length > 0) {
            requestBuilder.header(X_SCOPES_HEADER, String.join(",", validationResponse.getScopes()));
          }

          ServerHttpRequest modifiedRequest = requestBuilder.build();

          // Create Spring Security Authentication for authorization
          var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + validationResponse.getRole()));
          var authentication = new UsernamePasswordAuthenticationToken(
              validationResponse.getUsername(), 
              null, 
              authorities
          );

          logger.debug("API key authentication successful for {}: {} accessing: {}", 
              validationResponse.getType(), validationResponse.getUsername(), path);

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
