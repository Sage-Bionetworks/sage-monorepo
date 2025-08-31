package org.sagebionetworks.openchallenges.api.gateway.security;

import org.sagebionetworks.openchallenges.api.gateway.service.GatewayAuthenticationService;
import org.sagebionetworks.openchallenges.api.gateway.service.PublicEndpointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Gateway filter that validates JWT tokens by calling the Auth Service
 * and adds user context headers for downstream services.
 */
@Component
public class JwtAuthenticationGatewayFilter implements GlobalFilter, Ordered {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationGatewayFilter.class);
  
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";
  private static final String X_USER_ID_HEADER = "X-User-Id";
  private static final String X_USER_ROLE_HEADER = "X-User-Role";
  private static final String X_USER_TYPE_HEADER = "X-User-Type";
  private static final String X_USERNAME_HEADER = "X-Username";

  private final GatewayAuthenticationService authenticationService;
  private final PublicEndpointService publicEndpointService;

  public JwtAuthenticationGatewayFilter(GatewayAuthenticationService authenticationService,
                                        PublicEndpointService publicEndpointService) {
    this.authenticationService = authenticationService;
    this.publicEndpointService = publicEndpointService;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getPath().toString();

    // Skip authentication for public endpoints
    if (publicEndpointService.isPublicEndpoint(path)) {
      logger.debug("Skipping JWT authentication for public endpoint: {}", path);
      return chain.filter(exchange);
    }

    // Extract JWT token from Authorization header
    String authHeader = request.getHeaders().getFirst(AUTHORIZATION_HEADER);
    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      logger.debug("No JWT token found in request to: {}", path);
      // For protected endpoints, require authentication
      // Let API key filter handle it, but if that also fails, should return 401
      return chain.filter(exchange);
    }

    String token = authHeader.substring(BEARER_PREFIX.length());
    logger.debug("Validating JWT token for request to: {}", path);

    // Validate JWT token with Auth Service
    return authenticationService.validateJwt(token)
        .flatMap(validationResponse -> {
          if (!validationResponse.isValid()) {
            logger.warn("Invalid JWT token for request to: {}", path);
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token"));
          }

          // Add user context headers for downstream services
          ServerHttpRequest modifiedRequest = request.mutate()
              .header(X_USER_ID_HEADER, validationResponse.getUserId())
              .header(X_USERNAME_HEADER, validationResponse.getUsername())
              .header(X_USER_ROLE_HEADER, validationResponse.getRole())
              .header(X_USER_TYPE_HEADER, "user")
              .build();

          logger.debug("JWT authentication successful for user: {} accessing: {}", 
              validationResponse.getUsername(), path);

          return chain.filter(exchange.mutate().request(modifiedRequest).build());
        })
        .onErrorResume(GatewayAuthenticationService.AuthenticationException.class, ex -> {
          logger.warn("JWT authentication failed for request to {}: {}", path, ex.getMessage());
          return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed"));
        })
        .onErrorResume(Exception.class, ex -> {
          logger.error("Unexpected error during JWT authentication for request to {}: {}", path, ex.getMessage());
          return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authentication service error"));
        });
  }

  @Override
  public int getOrder() {
    return -100; // High priority, execute before other filters
  }
}
