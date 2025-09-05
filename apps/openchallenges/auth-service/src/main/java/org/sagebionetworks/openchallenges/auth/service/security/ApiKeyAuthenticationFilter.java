package org.sagebionetworks.openchallenges.auth.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.sagebionetworks.openchallenges.auth.service.model.entity.ApiKey;
import org.sagebionetworks.openchallenges.auth.service.service.ApiKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to authenticate requests using API keys in the X-API-Key header
 */
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);
  private static final String API_KEY_HEADER = "X-API-Key";

  private final ApiKeyService apiKeyService;

  public ApiKeyAuthenticationFilter(ApiKeyService apiKeyService) {
    this.apiKeyService = apiKeyService;
  }

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    String requestURI = request.getRequestURI();
    logger.debug("Processing request: {} {}", request.getMethod(), requestURI);

    // Skip authentication for public endpoints
    if (isPublicEndpoint(requestURI)) {
      logger.debug("Skipping authentication for public endpoint: {}", requestURI);
      filterChain.doFilter(request, response);
      return;
    }

    // Extract API key from X-API-Key header
    String apiKeyValue = request.getHeader(API_KEY_HEADER);
    if (apiKeyValue == null || apiKeyValue.trim().isEmpty()) {
      logger.debug("No valid X-API-Key header found for: {}", requestURI);
      filterChain.doFilter(request, response);
      return;
    }

    try {
      // Validate API key
      Optional<ApiKey> apiKeyOptional = apiKeyService.validateApiKey(apiKeyValue);

      if (apiKeyOptional.isPresent()) {
        ApiKey apiKey = apiKeyOptional.get();

        // Create authentication token with User entity as principal
        List<SimpleGrantedAuthority> authorities = List.of(
          new SimpleGrantedAuthority("ROLE_" + apiKey.getUser().getRole().name().toUpperCase())
        );

        UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(apiKey.getUser(), null, authorities);
        
        // Store the API key as authentication details for scope resolution
        authentication.setDetails(apiKey);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.debug(
          "Successfully authenticated user: {} for request: {}",
          apiKey.getUser().getUsername(),
          requestURI
        );
      } else {
        logger.warn("Invalid API key provided for request: {}", requestURI);
        // Send 401 Unauthorized for invalid API key
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Invalid API key\"}");
        return;
      }
    } catch (Exception e) {
      logger.error("Error during API key authentication for request: {}", requestURI, e);
    }

    filterChain.doFilter(request, response);
  }

  private boolean isPublicEndpoint(String requestURI) {
    if (requestURI == null) {
      return false;
    }
    return (
      requestURI.equals("/v1/auth/login") ||
      requestURI.equals("/v1/auth/validate") ||
      requestURI.equals("/v1/auth/jwt/validate") ||
      requestURI.equals("/v1/auth/oauth2/authorize") ||
      requestURI.equals("/v1/auth/oauth2/callback") ||
      requestURI.equals("/login") ||
      requestURI.equals("/auth/oauth2/google") ||
      requestURI.equals("/auth/callback") ||
      requestURI.startsWith("/actuator/") ||
      requestURI.startsWith("/v3/api-docs") ||
      requestURI.startsWith("/swagger-ui")
    );
  }
}
