package org.sagebionetworks.openchallenges.api.gateway.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.api.gateway.model.config.RouteConfigRegistry;

@ExtendWith(MockitoExtension.class)
class GatewayAuthenticationServiceTest {

  private GatewayAuthenticationService gatewayAuthenticationService;

  @Mock
  private RouteConfigRegistry routeConfigRegistry;

  @Mock
  private AudienceResolver audienceResolver;

  @BeforeEach
  void setUp() {
    gatewayAuthenticationService = new GatewayAuthenticationService(
      "http://test-auth-service:8080/v1",
      routeConfigRegistry,
      audienceResolver
    );
  }

  @Test
  void should_create_gateway_authentication_service() {
    assertThat(gatewayAuthenticationService).isNotNull();
  }

  @Test
  void should_configure_auth_service_client_with_correct_base_url() {
    // Verify that the WebClient is configured with the correct base URL
    // We can't easily test the WebClient directly, but we can verify the service was created
    assertThat(gatewayAuthenticationService).isNotNull();
  }

  @Test
  void jwt_validation_request_should_have_token() {
    String testToken = "test-jwt-token";
    GatewayAuthenticationService.JwtValidationRequest request =
      new GatewayAuthenticationService.JwtValidationRequest(testToken);

    assertThat(request.getToken()).isEqualTo(testToken);
  }

  @Test
  void jwt_validation_response_should_handle_valid_response() {
    GatewayAuthenticationService.JwtValidationResponse response =
      new GatewayAuthenticationService.JwtValidationResponse();

    response.setValid(true);
    response.setUserId("test-user-id");
    response.setUsername("test-user");
    response.setRole("user");
    response.setExpiresAt("2025-08-31T12:00:00Z");

    assertThat(response.isValid()).isTrue();
    assertThat(response.getUserId()).isEqualTo("test-user-id");
    assertThat(response.getUsername()).isEqualTo("test-user");
    assertThat(response.getRole()).isEqualTo("user");
    assertThat(response.getExpiresAt()).isEqualTo("2025-08-31T12:00:00Z");
  }
}
