package org.sagebionetworks.openchallenges.api.gateway.security;

import org.sagebionetworks.openchallenges.api.gateway.configuration.AuthConfiguration;
import org.sagebionetworks.openchallenges.api.gateway.service.OAuth2JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
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
 * WebFilter that handles JWT authentication using OAuth2 standards.
 * Runs BEFORE Spring Security authorization filters.
 */
@Component
public class JwtAuthenticationGatewayFilter implements WebFilter {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationGatewayFilter.class);

  private final OAuth2JwtService oAuth2JwtService;
  private final AuthConfiguration authConfiguration;

  public JwtAuthenticationGatewayFilter(
      OAuth2JwtService oAuth2JwtService,
      AuthConfiguration authConfiguration) {
    this.oAuth2JwtService = oAuth2JwtService;
    this.authConfiguration = authConfiguration;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getPath().toString();

    // Extract JWT token from Authorization header
    String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      logger.debug("No Bearer token found for request to: {}", path);
      // No JWT token - continue to API key authentication or Spring Security
      return chain.filter(exchange);
    }

    String jwtToken = authHeader.substring(7); // Remove "Bearer " prefix
    if (jwtToken.trim().isEmpty()) {
      logger.debug("Empty Bearer token found for request to: {}", path);
      // Empty JWT token - continue to API key authentication or Spring Security
      return chain.filter(exchange);
    }
    
    logger.debug("Validating JWT token for request to: {}", path);

    // Validate JWT token with OAuth2 JWT Service
    return oAuth2JwtService.validateJwt(jwtToken)
        .flatMap(validationResponse -> {
          if (!validationResponse.isValid()) {
            logger.warn("Invalid JWT token for request to: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            exchange.getResponse().getHeaders().add("WWW-Authenticate", 
                String.format("Bearer realm=\"%s\"", authConfiguration.getRealm()));
            return exchange.getResponse().setComplete();
          }

          // JWT Passthrough Mode: Pass the original JWT to resource servers
          // Resource servers will validate JWT directly and extract scopes
          // Note: Header stripping is handled by Spring Cloud Gateway default filters
          ServerHttpRequest.Builder requestBuilder = request.mutate()
              // Keep the original Authorization header with JWT
              .header(HttpHeaders.AUTHORIZATION, authHeader);

          ServerHttpRequest modifiedRequest = requestBuilder.build();

          // Create Spring Security Authentication for authorization
          var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + validationResponse.getRole()));
          var authentication = new UsernamePasswordAuthenticationToken(
              validationResponse.getUsername(), 
              null, 
              authorities
          );

          logger.debug("JWT authentication successful for user: {} accessing: {}", 
              validationResponse.getUsername(), path);

          return chain.filter(exchange.mutate().request(modifiedRequest).build())
              .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
        })
        .onErrorResume(Exception.class, ex -> {
          logger.warn("JWT authentication failed for request to {}: {}", path, ex.getMessage());
          exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
          exchange.getResponse().getHeaders().add("WWW-Authenticate", 
              String.format("Bearer realm=\"%s\"", authConfiguration.getRealm()));
          return exchange.getResponse().setComplete();
        });
  }
}
