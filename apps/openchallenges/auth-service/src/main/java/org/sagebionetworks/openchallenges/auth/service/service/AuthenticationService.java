package org.sagebionetworks.openchallenges.auth.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Authentication service for OpenChallenges.
 * 
 * Note: OAuth2 authentication flows and token management are now handled by Spring Authorization Server.
 * This includes:
 * - User authentication via /oauth2/authorize endpoint
 * - Token issuance and refresh via /oauth2/token endpoint  
 * - Token revocation via /oauth2/revoke endpoint
 * - Token introspection via /oauth2/introspect endpoint
 * 
 * This service is kept for any additional authentication-related utilities that may be needed.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

  /**
   * Placeholder for any future authentication-related utilities.
   * Current authentication flows are handled by Spring OAuth2 Authorization Server.
   */
  public void placeholder() {
    // This service is simplified since OAuth2 flows are now handled by Spring Authorization Server
    // Any future authentication utilities can be added here
  }
}
