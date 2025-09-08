package org.sagebionetworks.openchallenges.auth.service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to capture and store the 'resource' parameter from OAuth2 token requests.
 * This allows us to access the resource parameter in the JWT token customizer
 * for audience-scoped tokens (RFC 8707 style).
 */
@Component
@Order(-100) // Execute early in the filter chain
@Slf4j
public class OAuth2ResourceParameterFilter extends OncePerRequestFilter {

  private static final ThreadLocal<String> RESOURCE_PARAMETER = new ThreadLocal<>();

  /**
   * Get the resource parameter from the current request context.
   */
  public static String getCurrentResourceParameter() {
    return RESOURCE_PARAMETER.get();
  }

  /**
   * Clear the resource parameter from the current thread.
   */
  public static void clearResourceParameter() {
    RESOURCE_PARAMETER.remove();
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                FilterChain filterChain) throws ServletException, IOException {
    
    // Only process OAuth2 token endpoint requests
    if (!"/oauth2/token".equals(request.getRequestURI())) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      // Extract resource parameter if present
      String resourceParameter = request.getParameter("resource");
      if (resourceParameter != null && !resourceParameter.trim().isEmpty()) {
        log.info("🎯 CAPTURED RESOURCE PARAMETER: {}", resourceParameter);
        RESOURCE_PARAMETER.set(resourceParameter);
      } else {
        log.info("🎯 NO RESOURCE PARAMETER FOUND IN REQUEST");
      }

      // Continue with the request
      filterChain.doFilter(request, response);
      
    } finally {
      // Always clean up the ThreadLocal
      clearResourceParameter();
    }
  }
}
