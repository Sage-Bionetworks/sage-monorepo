package org.sagebionetworks.bixarena.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Temporary diagnostics filter to understand logout persistence.
 * Logs cookie header, presence/value of JSESSIONID, session id, and AUTH_SUBJECT.
 * Remove once issue resolved.
 */
@Component
@Order(0)
@Slf4j
public class DiagnosticsFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    try {
      String path = request.getRequestURI();
      if (path.startsWith("/v1/auth") || path.equals("/v1/echo")) {
        String cookieHeader = request.getHeader("Cookie");
        String jsid = null;
        if (cookieHeader != null) {
          for (String part : cookieHeader.split(";")) {
            part = part.trim();
            if (part.startsWith("JSESSIONID=")) {
              jsid = part.substring("JSESSIONID=".length());
              break;
            }
          }
        }
        var session = request.getSession(false);
        Object authSubject = null;
        if (session != null) {
          authSubject = session.getAttribute("AUTH_SUBJECT");
        }
        if (log.isInfoEnabled()) {
          log.info(
            "[diag] path={} jsessionPresent={} sessionId={} authSubject={}",
            path,
            jsid != null,
            session != null ? session.getId() : null,
            authSubject
          );
        }
        if (log.isDebugEnabled() && request.getCookies() != null) {
          log.debug(
            "[diag] cookies={}",
            Arrays.stream(request.getCookies()).map(Cookie::getName).toList()
          );
        }
      }
    } catch (Exception e) {
      // swallow; diagnostics only
    }
    filterChain.doFilter(request, response);
  }
}
