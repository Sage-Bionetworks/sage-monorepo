package org.sagebionetworks.openchallenges.organization.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter to authenticate requests using trusted headers set by the API Gateway.
 * 
 * The API Gateway validates JWT tokens or API keys and forwards authenticated
 * user information to downstream services via trusted headers:
 * - X-Authenticated-User-Id: The authenticated user's ID
 * - X-Authenticated-User: The authenticated user's username
 * - X-Authenticated-Roles: The authenticated user's roles
 * - X-Scopes: OAuth2 scopes (if applicable)
 * 
 * This filter trusts these headers and creates Spring Security authentication context
 * without performing additional validation calls to the auth service.
 */
public class TrustedHeaderAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(TrustedHeaderAuthenticationFilter.class);

  private static final String X_AUTHENTICATED_USER_ID_HEADER = "X-Authenticated-User-Id";
  private static final String X_AUTHENTICATED_USER_HEADER = "X-Authenticated-User";
  private static final String X_AUTHENTICATED_ROLES_HEADER = "X-Authenticated-Roles";
  private static final String X_SCOPES_HEADER = "X-Scopes";

  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {
    
    String userIdHeader = request.getHeader(X_AUTHENTICATED_USER_ID_HEADER);
    String usernameHeader = request.getHeader(X_AUTHENTICATED_USER_HEADER);
    String rolesHeader = request.getHeader(X_AUTHENTICATED_ROLES_HEADER);
    String scopesHeader = request.getHeader(X_SCOPES_HEADER);

    if (StringUtils.hasText(userIdHeader) && StringUtils.hasText(usernameHeader)) {
      try {
        // Parse user ID
        UUID userId = UUID.fromString(userIdHeader);
        
        // Parse roles (assuming single role for now, could be comma-separated)
        String role = StringUtils.hasText(rolesHeader) ? rolesHeader.trim() : "USER";
        
        // Parse scopes (comma-separated if multiple)
        List<String> scopes = List.of();
        if (StringUtils.hasText(scopesHeader)) {
          scopes = List.of(scopesHeader.split(","))
              .stream()
              .map(String::trim)
              .filter(StringUtils::hasText)
              .toList();
        }

        // Create authenticated user
        AuthenticatedUser user = new AuthenticatedUser(
          userId,
          usernameHeader,
          role,
          scopes
        );

        // Set authentication in security context
        UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.debug(
          "Successfully authenticated user from trusted headers: {} (ID: {}) with role: {} and scopes: {}",
          user.getUsername(),
          user.getUserId(),
          user.getRole(),
          user.getScopes()
        );
        
      } catch (IllegalArgumentException e) {
        logger.warn("Invalid user ID format in trusted header: {}", userIdHeader);
        // Continue without authentication - let authorization decide if this is allowed
      } catch (Exception e) {
        logger.error("Unexpected error processing trusted headers", e);
        // Continue without authentication - let authorization decide if this is allowed
      }
    } else {
      logger.debug("No trusted authentication headers found, continuing without authentication");
    }

    filterChain.doFilter(request, response);
  }
}
