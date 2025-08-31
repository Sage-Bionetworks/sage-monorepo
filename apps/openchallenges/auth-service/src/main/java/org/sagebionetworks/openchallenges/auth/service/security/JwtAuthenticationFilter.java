package org.sagebionetworks.openchallenges.auth.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.sagebionetworks.openchallenges.auth.service.model.entity.User;
import org.sagebionetworks.openchallenges.auth.service.repository.UserRepository;
import org.sagebionetworks.openchallenges.auth.service.service.JwtService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * JWT Authentication Filter that validates JWT tokens on incoming requests.
 * 
 * This filter:
 * 1. Extracts JWT tokens from Authorization header
 * 2. Validates token using JwtService
 * 3. Sets up Spring Security authentication context
 * 4. Skips authentication for public endpoints
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    /**
     * Public endpoints that don't require JWT authentication
     */
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
        "/api/v1/auth/login",
        "/api/v1/auth/oauth2/authorize",
        "/api/v1/auth/oauth2/callback",
        "/api/v1/auth/refresh",
        "/api/v1/auth/validate",
        "/api/v1/auth/jwt/validate",
        "/actuator",
        "/v3/api-docs",
        "/swagger-ui",
        "/swagger-resources"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            // Skip JWT authentication for public endpoints
            if (isPublicEndpoint(request.getRequestURI())) {
                log.debug("Skipping JWT authentication for public endpoint: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            // Extract JWT token from Authorization header
            String jwt = extractJwtFromRequest(request);
            if (jwt == null) {
                log.debug("No JWT token found in request to: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            // Validate JWT token and extract username
            String username = jwtService.extractUsername(jwt);
            if (username == null || !jwtService.isTokenValid(jwt, username)) {
                log.debug("Invalid JWT token for request to: {}", request.getRequestURI());
                filterChain.doFilter(request, response);
                return;
            }

            // Set up authentication context if not already authenticated
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                setupAuthenticationContext(request, username, jwt);
            }

        } catch (Exception e) {
            log.warn("JWT authentication failed for request to {}: {}", request.getRequestURI(), e.getMessage());
            // Continue without authentication - let endpoint handle unauthorized access
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Check if the request URI matches a public endpoint
     */
    private boolean isPublicEndpoint(String requestUri) {
        if (requestUri == null) {
            return false;
        }
        
        return PUBLIC_ENDPOINTS.stream()
                .anyMatch(endpoint -> requestUri.startsWith(endpoint));
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        return null;
    }

    /**
     * Set up Spring Security authentication context with user details
     */
    private void setupAuthenticationContext(HttpServletRequest request, String username, String jwt) {
        try {
            // Load user from database
            Optional<User> userOpt = userRepository.findByUsernameIgnoreCase(username);
            if (userOpt.isEmpty()) {
                log.debug("User not found in database: {}", username);
                return;
            }

            User user = userOpt.get();
            if (!user.isActive()) {
                log.debug("User account is disabled: {}", username);
                return;
            }

            // Create authorities based on user role
            List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().name().toUpperCase())
            );

            // Create authentication token
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                user, // Principal (User entity)
                jwt,  // Credentials (JWT token)
                authorities
            );

            // Set additional details
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Set authentication in security context
            SecurityContextHolder.getContext().setAuthentication(authToken);
            
            log.debug("JWT authentication successful for user: {} with role: {}", username, user.getRole());

        } catch (Exception e) {
            log.warn("Failed to setup authentication context for user {}: {}", username, e.getMessage());
        }
    }
}
