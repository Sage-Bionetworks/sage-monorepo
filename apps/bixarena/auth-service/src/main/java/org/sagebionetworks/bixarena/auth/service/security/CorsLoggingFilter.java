package org.sagebionetworks.bixarena.auth.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to log CORS-related request information for debugging.
 *
 * <p>This filter runs BEFORE Spring's CORS filter to log the Origin header and other CORS-related
 * information, helping debug CORS rejection issues.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsLoggingFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String origin = request.getHeader("Origin");
    String method = request.getMethod();
    String path = request.getRequestURI();

    if (origin != null) {
      log.debug("CORS request: method={} path={} origin={}", method, path, origin);

      // Log preflight requests separately
      if ("OPTIONS".equalsIgnoreCase(method)) {
        String accessControlRequestMethod = request.getHeader("Access-Control-Request-Method");
        String accessControlRequestHeaders = request.getHeader("Access-Control-Request-Headers");
        log.debug(
            "CORS preflight: origin={} requestMethod={} requestHeaders={}",
            origin,
            accessControlRequestMethod,
            accessControlRequestHeaders);
      }
    }

    // Continue the filter chain
    filterChain.doFilter(request, response);

    // Log CORS response headers
    if (origin != null) {
      String accessControlAllowOrigin = response.getHeader("Access-Control-Allow-Origin");
      String accessControlAllowCredentials =
          response.getHeader("Access-Control-Allow-Credentials");

      if (accessControlAllowOrigin != null) {
        log.debug(
            "CORS response: path={} allowOrigin={} allowCredentials={}",
            path,
            accessControlAllowOrigin,
            accessControlAllowCredentials);
      } else {
        log.warn(
            "CORS rejected: method={} path={} origin={} (no Allow-Origin header in response)",
            method,
            path,
            origin);
      }
    }
  }
}
