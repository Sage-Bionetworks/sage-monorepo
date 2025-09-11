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
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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
      Optional<User> userOptional = resolveUserFromSubject(subject);

      if (userOptional.isEmpty()) {
        log.warn("No user found for JWT subject '{}' on request: {}", subject, requestUri);
        filterChain.doFilter(request, response);
        return;
      }

      User user = userOptional.get();

      // Check if user account is enabled
      if (!user.getEnabled()) {
        log.warn("User account is disabled: {} for request: {}", user.getUsername(), requestUri);
        filterChain.doFilter(request, response);
        return;
      }

      // Set up Spring Security authentication
      Authentication authentication = createAuthentication(user, jwt);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      log.debug(
        "Successfully authenticated user: {} with role: {} for request: {}",
        user.getUsername(),
        user.getRole(),
        requestUri
      );
    } catch (JwtException e) {
      log.debug("JWT token validation failed for request {}: {}", requestUri, e.getMessage());
      // Don't expose JWT validation details to client - continue without authentication
    } catch (DataAccessException e) {
      log.error(
        "Database error during authentication for request {}: {}",
        requestUri,
        e.getMessage()
      );
      // Continue without authentication - let downstream handle authorization
    } catch (IllegalArgumentException e) {
      log.warn("Invalid authentication data for request {}: {}", requestUri, e.getMessage());
      // Continue without authentication - let downstream handle authorization
    } catch (Exception e) {
      log.error(
        "Unexpected error during OAuth2 web authentication for request {}: {}",
        requestUri,
        e.getMessage(),
        e
      );
      // Continue without authentication - let downstream handle authorization
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Extract OAuth2 JWT token from secure cookies using configured cookie name.
   * Includes validation for cookie content.
   */
  private String extractJwtFromCookies(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies == null) {
      log.debug("No cookies found in request");
      return null;
    }

    String cookieName = authServiceProperties.getWeb().getAccessTokenCookieName();
    if (cookieName == null || cookieName.trim().isEmpty()) {
      log.error("Access token cookie name is not configured");
      return null;
    }

    for (Cookie cookie : cookies) {
      if (cookieName.equals(cookie.getName())) {
        String tokenValue = cookie.getValue();
        if (StringUtils.hasText(tokenValue)) {
          log.debug("Found JWT token cookie: {}", cookieName);
          return tokenValue;
        } else {
          log.debug("JWT token cookie '{}' is empty", cookieName);
        }
      }
    }

    log.debug("JWT token cookie '{}' not found in {} cookies", cookieName, cookies.length);
    return null;
  }

  /**
   * Resolves a user from the JWT subject claim.
   * Handles both UUID (regular users) and client_id (service accounts).
   */
  private Optional<User> resolveUserFromSubject(String subject) {
    if (subject == null || subject.trim().isEmpty()) {
      log.warn("Subject claim is null or empty");
      return Optional.empty();
    }

    try {
      // Try parsing as UUID (standard user ID)
      UUID userId = UUID.fromString(subject);
      Optional<User> userOpt = userRepository.findById(userId);
      if (userOpt.isPresent()) {
        log.debug("Found user by UUID: {}", userId);
        return userOpt;
      } else {
        log.debug("No user found for UUID: {}", userId);
      }
    } catch (IllegalArgumentException e) {
      // Not a UUID, might be a client_id (service account) or username
      log.debug("Subject '{}' is not a valid UUID, trying as username/client_id", subject);

      try {
        // Try to find by username as fallback
        Optional<User> userOpt = userRepository.findByUsernameIgnoreCase(subject);
        if (userOpt.isPresent()) {
          log.debug("Found user by username: {}", subject);
          return userOpt;
        } else {
          log.debug("No user found for username: {}", subject);
        }
      } catch (DataAccessException ex) {
        log.error(
          "Database error while looking up user by username '{}': {}",
          subject,
          ex.getMessage()
        );
      }
    } catch (DataAccessException e) {
      log.error("Database error while looking up user by UUID '{}': {}", subject, e.getMessage());
    }

    return Optional.empty();
  }

  /**
   * Creates a Spring Security authentication token for the given user and JWT.
   * Uses JwtAuthenticationToken which is appropriate for JWT-based authentication.
   * Includes validation of user properties.
   */
  private Authentication createAuthentication(User user, Jwt jwt) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }
    if (jwt == null) {
      throw new IllegalArgumentException("JWT cannot be null");
    }

    if (user.getRole() == null) {
      log.warn("User '{}' has null role, defaulting to USER", user.getUsername());
      // Create a minimal authority if role is null
      return new JwtAuthenticationToken(
        jwt,
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
      );
    }

    String roleName = "ROLE_" + user.getRole().name().toUpperCase();
    JwtAuthenticationToken jwtAuthToken = new JwtAuthenticationToken(
      jwt,
      Collections.singletonList(new SimpleGrantedAuthority(roleName))
    );

    // Set the user as details so it can be accessed later
    jwtAuthToken.setDetails(user);

    return jwtAuthToken;
  }
}
