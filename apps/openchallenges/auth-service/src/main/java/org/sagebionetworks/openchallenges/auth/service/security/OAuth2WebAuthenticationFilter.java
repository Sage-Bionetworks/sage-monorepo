package org.sagebionetworks.openchallenges.auth.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.configuration.AuthServiceProperties;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * OAuth2 Web Authentication Filter that validates OAuth2 JWT tokens from cookies.
 *
 * This filter:
 * 1. Extracts OAuth2 JWT tokens from secure cookies
 * 2. Validates tokens using OAuth2 JwtDecoder
 * 3. Sets up Spring Security authentication context
 * 4. Skips authentication for public endpoints
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2WebAuthenticationFilter extends OncePerRequestFilter {

  private final JwtDecoder jwtDecoder;
  private final UserRepository userRepository;
  private final AuthServiceProperties authServiceProperties;

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    String requestUri = request.getRequestURI();
    log.debug("Processing request: {}", requestUri);

    // Extract JWT token from cookies
    String jwtToken = extractJwtFromCookies(request);

    if (jwtToken == null) {
      log.debug("No JWT token found in cookies for: {}", requestUri);
      filterChain.doFilter(request, response);
      return;
    }

    try {
      // Validate and decode OAuth2 JWT token
      Jwt jwt = jwtDecoder.decode(jwtToken);
      log.debug("Successfully decoded OAuth2 JWT token");

      // Extract subject from JWT claims (should be user UUID or client_id)
      String subject = jwt.getSubject();
      if (subject == null) {
        log.debug("No subject claim found in JWT token");
        filterChain.doFilter(request, response);
        return;
      }

      // Try to find user by UUID first
      Optional<User> userOptional = Optional.empty();
      try {
        UUID userId = UUID.fromString(subject);
        userOptional = userRepository.findById(userId);
        log.debug("Found user by UUID: {}", userId);
      } catch (IllegalArgumentException e) {
        // Not a UUID, could be a client_id for service account
        // For now, we don't support service account authentication in web filter
        log.debug("Subject is not a UUID, skipping authentication for: {}", subject);
        filterChain.doFilter(request, response);
        return;
      }

      if (userOptional.isEmpty()) {
        log.warn("User not found for UUID: {}", subject);
        filterChain.doFilter(request, response);
        return;
      }

      User user = userOptional.get();

      // Check if user account is enabled
      if (!user.getEnabled()) {
        log.warn("User account is disabled: {}", user.getUsername());
        filterChain.doFilter(request, response);
        return;
      }

      // Set up Spring Security authentication
      Authentication authentication = new UsernamePasswordAuthenticationToken(
        user,
        null,
        Collections.singletonList(
          new SimpleGrantedAuthority("ROLE_" + user.getRole().name().toUpperCase())
        )
      );

      SecurityContextHolder.getContext().setAuthentication(authentication);
      log.debug(
        "Successfully authenticated user: {} with role: {}",
        user.getUsername(),
        user.getRole()
      );
    } catch (JwtException e) {
      log.debug("JWT token validation failed: {}", e.getMessage());
    } catch (Exception e) {
      log.error("Unexpected error during OAuth2 web authentication", e);
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Extract OAuth2 JWT token from secure cookies using configured cookie name
   */
  private String extractJwtFromCookies(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      return null;
    }

    String cookieName = authServiceProperties.getWeb().getAccessTokenCookieName();
    for (Cookie cookie : cookies) {
      if (cookieName.equals(cookie.getName())) {
        String tokenValue = cookie.getValue();
        if (StringUtils.hasText(tokenValue)) {
          return tokenValue;
        }
      }
    }

    return null;
  }
}
