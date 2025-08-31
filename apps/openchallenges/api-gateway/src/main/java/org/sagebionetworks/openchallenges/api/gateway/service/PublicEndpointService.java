package org.sagebionetworks.openchallenges.api.gateway.service;

import org.springframework.stereotype.Service;

/**
 * Centralized service for determining which endpoints are public and don't require authentication.
 * This ensures consistent behavior across all authentication filters.
 */
@Service
public class PublicEndpointService {

  /**
   * Determines if an endpoint is public and doesn't require authentication.
   * 
   * @param path The request path to check
   * @return true if the endpoint is public, false if it requires authentication
   */
  public boolean isPublicEndpoint(String path) {
    return path.startsWith("/actuator/health") ||
           path.startsWith("/api/v1/auth/login") ||
           path.startsWith("/api/v1/auth/oauth2") ||
           path.startsWith("/api/v1/auth/jwt/validate") ||
           path.startsWith("/api/v1/auth/validate") ||
           path.equals("/api/v1/users/register") ||
           path.startsWith("/api/v1/challenge-analytics") ||
           path.startsWith("/api/v1/challenge-platforms") ||
           path.startsWith("/api/v1/edam-concepts");
  }
}
