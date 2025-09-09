package org.sagebionetworks.openchallenges.auth.service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

    // Public endpoints that don't require authentication
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/login",
        "/auth/oauth2/",
        "/auth/callback",
        "/oauth2/",
        "/.well-known/",
        "/actuator/",
        "/favicon.ico",
        "/error"
    );

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        
        String requestUri = request.getRequestURI();
        log.debug("Processing request: {}", requestUri);

        // Skip authentication for public endpoints
        if (isPublicEndpoint(requestUri)) {
            log.debug("Skipping authentication for public endpoint: {}", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

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

            // Extract username from JWT claims
            String username = jwt.getClaimAsString("preferred_username");
            if (username == null) {
                log.debug("No preferred_username claim found in JWT token");
                filterChain.doFilter(request, response);
                return;
            }

            // Load user from database
            Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);
            if (userOptional.isEmpty()) {
                log.warn("User not found for username: {}", username);
                filterChain.doFilter(request, response);
                return;
            }

            User user = userOptional.get();
            
            // Check if user account is enabled
            if (!user.getEnabled()) {
                log.warn("User account is disabled: {}", username);
                filterChain.doFilter(request, response);
                return;
            }

            // Set up Spring Security authentication
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name().toUpperCase()))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Successfully authenticated user: {} with role: {}", username, user.getRole());

        } catch (JwtException e) {
            log.debug("JWT token validation failed: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during OAuth2 web authentication", e);
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
     * Extract OAuth2 JWT token from secure cookies
     */
    private String extractJwtFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if ("oc_access_token".equals(cookie.getName())) {
                String tokenValue = cookie.getValue();
                if (StringUtils.hasText(tokenValue)) {
                    return tokenValue;
                }
            }
        }
        
        return null;
    }
}
