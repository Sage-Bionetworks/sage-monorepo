package org.sagebionetworks.model.ad.api.next.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor for logging all API HTTP responses with their status codes.
 * This runs after request completion and captures the actual response status.
 * Uses different log levels based on HTTP status code:
 * - INFO for 2xx/3xx (success)
 * - WARN for 4xx (client errors)
 * - ERROR for 5xx (server errors)
 */
@Component
@Slf4j
public class ApiLoggingInterceptor implements HandlerInterceptor {

  @Override
  public void afterCompletion(
    HttpServletRequest request,
    HttpServletResponse response,
    Object handler,
    Exception ex
  ) {
    int status = response.getStatus();
    String method = request.getMethod();
    String uri = request.getRequestURI();

    if (status >= 500) {
      log.error("{} {} status={}", method, uri, status);
    } else if (status >= 400) {
      log.warn("{} {} status={}", method, uri, status);
    } else {
      log.info("{} {} status={}", method, uri, status);
    }
  }
}
