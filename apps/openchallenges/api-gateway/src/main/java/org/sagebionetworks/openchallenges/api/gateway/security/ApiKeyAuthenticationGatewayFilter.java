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
 * Gateway filter that validates API keys by calling the Auth Service
 * and adds user context headers for downstream services.
 * This filter runs after JWT authentication filter.
 */
@Component
public class ApiKeyAuthenticationGatewayFilter implements GlobalFilter, Ordered {

  private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthenticationGatewayFilter.class);
  
  private static final String API_KEY_HEADER = "X-API-Key";
  private static final String X_USER_ID_HEADER = "X-User-Id";
  private static final String X_USER_ROLE_HEADER = "X-User-Role";
  private static final String X_USER_TYPE_HEADER = "X-User-Type";
  private static final String X_USERNAME_HEADER = "X-Username";
  private static final String X_SERVICE_NAME_HEADER = "X-Service-Name";

  private final GatewayAuthenticationService authenticationService;
  private final PublicEndpointService publicEndpointService;

  public ApiKeyAuthenticationGatewayFilter(GatewayAuthenticationService authenticationService,
                                           PublicEndpointService publicEndpointService) {
    this.authenticationService = authenticationService;
    this.publicEndpointService = publicEndpointService;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getPath().toString();

    // Skip if already authenticated by JWT (has user headers)
    if (request.getHeaders().containsKey(X_USER_ID_HEADER)) {
      logger.debug("Request already authenticated by JWT, skipping API key validation for: {}", path);
      return chain.filter(exchange);
    }

    // Skip authentication for public endpoints
    if (publicEndpointService.isPublicEndpoint(path)) {
      logger.debug("Skipping API key authentication for public endpoint: {}", path);
      return chain.filter(exchange);
    }

    // Extract API key from X-API-Key header
    String apiKey = request.getHeaders().getFirst(API_KEY_HEADER);
    if (apiKey == null || apiKey.trim().isEmpty()) {
      logger.warn("No authentication token found for protected endpoint: {}", path);
      // Protected endpoint requires authentication - return 401 Unauthorized
      return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required"));
    }

    logger.debug("Validating API key for request to: {}", path);

    // Validate API key with Auth Service
    return authenticationService.validateApiKey(apiKey)
        .flatMap(validationResponse -> {
          if (!validationResponse.isValid()) {
            logger.warn("Invalid API key for request to: {}", path);
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API key"));
          }

          // Add user context headers for downstream services
          ServerHttpRequest.Builder requestBuilder = request.mutate()
              .header(X_USER_ID_HEADER, validationResponse.getUserId())
              .header(X_USERNAME_HEADER, validationResponse.getUsername())
              .header(X_USER_ROLE_HEADER, validationResponse.getRole())
              .header(X_USER_TYPE_HEADER, validationResponse.getType() != null ? validationResponse.getType() : "user");

          // Add service name header for service accounts
          if ("service".equals(validationResponse.getType()) && validationResponse.getServiceName() != null) {
            requestBuilder.header(X_SERVICE_NAME_HEADER, validationResponse.getServiceName());
          }

          ServerHttpRequest modifiedRequest = requestBuilder.build();

          logger.debug("API key authentication successful for {}: {} accessing: {}", 
              validationResponse.getType(), validationResponse.getUsername(), path);

          return chain.filter(exchange.mutate().request(modifiedRequest).build());
        })
        .onErrorResume(GatewayAuthenticationService.AuthenticationException.class, ex -> {
          logger.warn("API key authentication failed for request to {}: {}", path, ex.getMessage());
          return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication failed"));
        })
        .onErrorResume(Exception.class, ex -> {
          logger.error("Unexpected error during API key authentication for request to {}: {}", path, ex.getMessage());
          return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Authentication service error"));
        });
  }

  @Override
  public int getOrder() {
    return -90; // Lower priority than JWT filter, runs after JWT authentication
  }
}
