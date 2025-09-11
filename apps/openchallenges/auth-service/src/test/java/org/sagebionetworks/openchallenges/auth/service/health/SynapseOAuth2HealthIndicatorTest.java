package org.sagebionetworks.openchallenges.auth.service.health;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Test for SynapseOAuth2HealthIndicator to verify OAuth2 discovery endpoint health checking.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SynapseOAuth2HealthIndicator Tests")
class SynapseOAuth2HealthIndicatorTest {

  @Mock
  private RestTemplate restTemplate;

  private SynapseOAuth2HealthIndicator healthIndicator;

  @BeforeEach
  void setUp() {
    healthIndicator = new SynapseOAuth2HealthIndicator();
    // Inject mock RestTemplate using reflection
    try {
      var field = SynapseOAuth2HealthIndicator.class.getDeclaredField("restTemplate");
      field.setAccessible(true);
      field.set(healthIndicator, restTemplate);
    } catch (Exception e) {
      fail("Failed to inject mock RestTemplate: " + e.getMessage());
    }
  }

  @Test
  @DisplayName("should return UP status when Synapse discovery endpoint is healthy")
  void shouldReturnUpStatusWhenSynapseEndpointIsHealthy() {
    // Arrange
    Map<String, Object> discoveryResponse = Map.of(
      "issuer",
      "https://repo-prod.prod.sagebase.org/auth/v1",
      "authorization_endpoint",
      "https://repo-prod.prod.sagebase.org/auth/v1/oauth2/authorize",
      "token_endpoint",
      "https://repo-prod.prod.sagebase.org/auth/v1/oauth2/token",
      "userinfo_endpoint",
      "https://repo-prod.prod.sagebase.org/auth/v1/oauth2/userinfo"
    );

    when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(
      new ResponseEntity<>(discoveryResponse, HttpStatus.OK)
    );

    // Act
    Health health = healthIndicator.health();

    // Assert
    assertEquals(Status.UP, health.getStatus());
    assertEquals("Synapse OAuth2", health.getDetails().get("provider"));
    assertEquals(
      "https://repo-prod.prod.sagebase.org/auth/v1/.well-known/openid-configuration",
      health.getDetails().get("discoveryUrl")
    );
    assertEquals("https://repo-prod.prod.sagebase.org/auth/v1", health.getDetails().get("issuer"));
    assertTrue(health.getDetails().containsKey("responseTimeMs"));
    assertTrue(health.getDetails().containsKey("checkTime"));
  }

  @Test
  @DisplayName("should return DOWN status when discovery response is missing required fields")
  void shouldReturnDownStatusWhenDiscoveryResponseMissingFields() {
    // Arrange - Response missing required OAuth2 fields
    Map<String, Object> incompleteResponse = Map.of(
      "issuer",
      "https://repo-prod.prod.sagebase.org/auth/v1"
      // Missing authorization_endpoint and token_endpoint
    );

    when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(
      new ResponseEntity<>(incompleteResponse, HttpStatus.OK)
    );

    // Act
    Health health = healthIndicator.health();

    // Assert
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("Synapse OAuth2", health.getDetails().get("provider"));
    assertEquals(
      "Invalid discovery document - missing required OAuth2 fields",
      health.getDetails().get("error")
    );
    assertTrue(health.getDetails().containsKey("responseTimeMs"));
  }

  @Test
  @DisplayName("should return DOWN status when discovery endpoint returns non-success HTTP status")
  void shouldReturnDownStatusWhenNonSuccessHttpStatus() {
    // Arrange
    when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(
      new ResponseEntity<>(null, HttpStatus.SERVICE_UNAVAILABLE)
    );

    // Act
    Health health = healthIndicator.health();

    // Assert
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("Synapse OAuth2", health.getDetails().get("provider"));
    assertEquals("Non-success HTTP status", health.getDetails().get("error"));
    assertEquals(503, health.getDetails().get("httpStatus"));
  }

  @Test
  @DisplayName("should return DOWN status when discovery endpoint is unreachable")
  void shouldReturnDownStatusWhenEndpointUnreachable() {
    // Arrange
    when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenThrow(
      new RestClientException("Connection refused")
    );

    // Act
    Health health = healthIndicator.health();

    // Assert
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("Synapse OAuth2", health.getDetails().get("provider"));
    assertEquals("Discovery endpoint unreachable", health.getDetails().get("error"));
    assertEquals("RestClientException", health.getDetails().get("exception"));
    assertEquals("Connection refused", health.getDetails().get("message"));
  }

  @Test
  @DisplayName("should cache successful health check results")
  void shouldCacheSuccessfulHealthCheckResults() {
    // Arrange
    Map<String, Object> discoveryResponse = Map.of(
      "issuer",
      "https://repo-prod.prod.sagebase.org/auth/v1",
      "authorization_endpoint",
      "https://repo-prod.prod.sagebase.org/auth/v1/oauth2/authorize",
      "token_endpoint",
      "https://repo-prod.prod.sagebase.org/auth/v1/oauth2/token"
    );

    when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(
      new ResponseEntity<>(discoveryResponse, HttpStatus.OK)
    );

    // Act - Call health check twice
    Health health1 = healthIndicator.health();
    Health health2 = healthIndicator.health();

    // Assert
    assertEquals(Status.UP, health1.getStatus());
    assertEquals(Status.UP, health2.getStatus());

    // Verify RestTemplate was only called once due to caching
    verify(restTemplate, times(1)).getForEntity(anyString(), eq(Map.class));
  }

  @Test
  @DisplayName("should cache failed health check results")
  void shouldCacheFailedHealthCheckResults() {
    // Arrange
    when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenThrow(
      new RestClientException("Network error")
    );

    // Act - Call health check twice
    Health health1 = healthIndicator.health();
    Health health2 = healthIndicator.health();

    // Assert
    assertEquals(Status.DOWN, health1.getStatus());
    assertEquals(Status.DOWN, health2.getStatus());

    // Verify RestTemplate was only called once due to caching
    verify(restTemplate, times(1)).getForEntity(anyString(), eq(Map.class));
  }

  @Test
  @DisplayName("should handle null response body gracefully")
  void shouldHandleNullResponseBodyGracefully() {
    // Arrange
    when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(
      new ResponseEntity<>(null, HttpStatus.OK)
    );

    // Act
    Health health = healthIndicator.health();

    // Assert
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("Non-success HTTP status", health.getDetails().get("error"));
  }

  @Test
  @DisplayName("should handle unexpected exceptions during health check")
  void shouldHandleUnexpectedExceptionsDuringHealthCheck() {
    // Arrange
    when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenThrow(
      new RuntimeException("Unexpected error")
    );

    // Act
    Health health = healthIndicator.health();

    // Assert
    assertEquals(Status.DOWN, health.getStatus());
    assertEquals("Unexpected health check failure", health.getDetails().get("error"));
    assertEquals("RuntimeException", health.getDetails().get("exception"));
    assertEquals("Unexpected error", health.getDetails().get("message"));
  }

  @Test
  @DisplayName("should use correct Synapse discovery URL")
  void shouldUseCorrectSynapseDiscoveryUrl() {
    // Arrange
    Map<String, Object> discoveryResponse = Map.of(
      "issuer",
      "https://repo-prod.prod.sagebase.org/auth/v1",
      "authorization_endpoint",
      "https://repo-prod.prod.sagebase.org/auth/v1/oauth2/authorize",
      "token_endpoint",
      "https://repo-prod.prod.sagebase.org/auth/v1/oauth2/token"
    );

    when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(
      new ResponseEntity<>(discoveryResponse, HttpStatus.OK)
    );

    // Act
    healthIndicator.health();

    // Assert
    verify(restTemplate).getForEntity(
      "https://repo-prod.prod.sagebase.org/auth/v1/.well-known/openid-configuration",
      Map.class
    );
  }
}
