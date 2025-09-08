package org.sagebionetworks.openchallenges.auth.service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to capture OAuth2 token request parameters, particularly the 'resource' parameter
 * for RFC 8707 audience binding.
 */
@Slf4j
@Order(1)
public class TokenEndpointFilter extends OncePerRequestFilter {

  private static final ThreadLocal<String> RESOURCE_PARAMETER = new ThreadLocal<>();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
    
    log.info("🎯 TOKEN FILTER: Processing request {} {}", request.getMethod(), request.getRequestURI());
    
    // Only process OAuth2 token endpoint requests
    if ("/oauth2/token".equals(request.getRequestURI()) && "POST".equals(request.getMethod())) {
      String resourceParam = request.getParameter("resource");
      log.info("🎯 TOKEN FILTER: Captured resource parameter = {}", resourceParam);
      log.info("🎯 TOKEN FILTER: All parameters = {}", request.getParameterMap().keySet());
      
      if (resourceParam != null) {
        RESOURCE_PARAMETER.set(resourceParam);
        log.info("🎯 TOKEN FILTER: Stored resource parameter in ThreadLocal");
      }
      
      try {
        filterChain.doFilter(request, response);
      } finally {
        // Always clean up ThreadLocal to prevent memory leaks
        RESOURCE_PARAMETER.remove();
        log.debug("🎯 TOKEN FILTER: Cleaned up ThreadLocal");
      }
    } else {
      filterChain.doFilter(request, response);
    }
  }

  /**
   * Get the current resource parameter from ThreadLocal storage.
   * This is called by the JWT token customizer.
   */
  public static String getCurrentResourceParameter() {
    return RESOURCE_PARAMETER.get();
  }
}
