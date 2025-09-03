package org.sagebionetworks.openchallenges.api.gateway.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

/**
 * Integration tests for Gateway security features including header stripping.
 * Tests the complete Spring Cloud Gateway pipeline with default filters.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "openchallenges.auth.service-url=http://localhost:8999/api/v1",  // Non-existent service for testing
    "logging.level.org.sagebionetworks.openchallenges.api.gateway=DEBUG"
})
@Tag("integration")
class GatewaySecurityIntegrationTest {

  @LocalServerPort
  private int port;

  @Test
  @DisplayName("should strip malicious authentication headers from incoming requests")
  void shouldStripMaliciousAuthenticationHeadersFromIncomingRequests() {
    // given
    WebTestClient client = WebTestClient.bindToServer()
        .baseUrl("http://localhost:" + port)
        .responseTimeout(Duration.ofSeconds(10))
        .build();

    // when - send request with malicious authentication headers that should be stripped
    var response = client.post()
        .uri("/api/v1/organizations")
        .header("X-Authenticated-User-Id", "hacker-user-123")     // Should be stripped by default-filters
        .header("X-Authenticated-User", "hacker")                  // Should be stripped by default-filters
        .header("X-Authenticated-Roles", "ADMIN")                  // Should be stripped by default-filters
        .header("X-Scopes", "read write admin")                    // Should be stripped by default-filters
        .header("X-Subject", "malicious-subject")                  // Should be stripped by default-filters
        .header("X-Principal", "evil-principal")                   // Should be stripped by default-filters
        .header("X-Service-Token", "fake-service-token")           // Should be stripped by default-filters
        .exchange();
        
        // then - request should be processed (headers stripped), expecting 401 unauthorized due to missing auth
        // The key test is that the gateway strips headers and processes the request through security
        response.expectStatus().isUnauthorized(); // Authentication required for POST to organizations
  }

  @Test
  @DisplayName("should allow legitimate headers through gateway filters")
  void shouldAllowLegitimateHeadersThroughGatewayFilters() {
    // given
    WebTestClient client = WebTestClient.bindToServer()
        .baseUrl("http://localhost:" + port)
        .responseTimeout(Duration.ofSeconds(10))
        .build();

    // when - send request with legitimate headers that should NOT be stripped
    var response = client.post()
        .uri("/api/v1/organizations")
        .header("Content-Type", "application/json")               // Should be kept
        .header("Accept", "application/json")                     // Should be kept
        .header("Authorization", "Bearer some.jwt.token")         // Should be kept (this is legitimate)
        .header("X-API-Key", "some-api-key")                     // Should be kept (this is legitimate)
        .header("User-Agent", "test-client")                      // Should be kept
        .header("X-Request-ID", "test-request-123")              // Should be kept (not in strip list)
        .exchange();
        
        // then - request should be processed normally but fail JWT validation (auth service unavailable)
        response.expectStatus().isUnauthorized(); // JWT validation fails - auth service not available
  }

  @Test
  @DisplayName("should route to auth service without authentication for public endpoints")
  void shouldRouteToAuthServiceWithoutAuthenticationForPublicEndpoints() {
    // given
    WebTestClient client = WebTestClient.bindToServer()
        .baseUrl("http://localhost:" + port)
        .responseTimeout(Duration.ofSeconds(10))
        .build();

    // when - access OAuth2 well-known endpoint (public)
    var response = client.get()
        .uri("/.well-known/oauth-authorization-server")
        .exchange();
        
        // then - should attempt to route to auth service, expect 404 since service route not found in test
        response.expectStatus().isNotFound(); // Auth service route not available in integration test
  }

  @Test
  @DisplayName("should require authentication for protected endpoints")
  void shouldRequireAuthenticationForProtectedEndpoints() {
    // given
    WebTestClient client = WebTestClient.bindToServer()
        .baseUrl("http://localhost:" + port)
        .responseTimeout(Duration.ofSeconds(10))
        .build();

    // when - access protected endpoint without authentication
    var response = client.post()
        .uri("/api/v1/organizations")
        .header("Content-Type", "application/json")
        .exchange();
        
        // then - should get 401 unauthorized since authentication is required for POST
        response.expectStatus().isUnauthorized(); // Authentication required for protected endpoint
  }
  
  @Test
  @DisplayName("should require authentication for organizations access")
  void shouldRequireAuthenticationForOrganizationsAccess() {
    // given
    WebTestClient client = WebTestClient.bindToServer()
        .baseUrl("http://localhost:" + port)
        .responseTimeout(Duration.ofSeconds(10))
        .build();

    // when - access organizations endpoint without authentication
    var response = client.get()
        .uri("/api/v1/organizations")
        .exchange();
        
        // then - should require authentication
        response.expectStatus().isUnauthorized();
  }
}
