package org.sagebionetworks.bixarena.auth.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Populates the Spring SecurityContext from session attributes set after OIDC login.
 *
 * <p>The OIDC callback sets AUTH_SUBJECT (BixArena User ID UUID) and AUTH_ROLES in the HttpSession.
 * Because we are not delegating to a Spring Security Authentication mechanism, subsequent requests
 * would otherwise appear unauthenticated. This filter bridges the gap by turning those session
 * attributes into an Authentication object for the lifetime of the request.</p>
 *
 * <p>The principal (subject) is the BixArena User ID (UUID), which is a stable immutable identifier
 * per OIDC spec requirements.</p>
 */
@Component
@Slf4j
public class SessionAuthenticationFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    // If already authenticated, skip
    Authentication existing = SecurityContextHolder.getContext().getAuthentication();
    if (existing == null || !existing.isAuthenticated()) {
      HttpSession session = request.getSession(false);
      if (session != null) {
        String subject = (String) session.getAttribute("AUTH_SUBJECT");
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) session.getAttribute("AUTH_ROLES");
        if (subject != null && roles != null) {
          if (log.isDebugEnabled()) {
            log.debug(
              "SessionAuthenticationFilter: populating context sessionId={} subject={} roles={}",
              session.getId(),
              subject,
              roles
            );
          }
          List<GrantedAuthority> authorities = roles
            .stream()
            .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
          AbstractAuthenticationToken auth = new AbstractAuthenticationToken(authorities) {
            @Override
            public Object getCredentials() {
              return ""; // Session based
            }

            @Override
            public Object getPrincipal() {
              return subject;
            }
          };
          auth.setAuthenticated(true);
          SecurityContextHolder.getContext().setAuthentication(auth);
        } else if (log.isDebugEnabled()) {
          log.debug(
            "SessionAuthenticationFilter: sessionId={} present but missing auth attributes (subject={}, roles={})",
            session.getId(),
            subject,
            roles
          );
        }
      }
    }
    filterChain.doFilter(request, response);
  }
}
