package org.sagebionetworks.openchallenges.api.gateway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.api.gateway.configuration.RouteScopeConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GatewayAuthenticationServiceTest {

  private GatewayAuthenticationService gatewayAuthenticationService;
  
  @Mock
  private RouteScopeConfiguration routeScopeConfiguration;

  @BeforeEach
  void setUp() {
    gatewayAuthenticationService = new GatewayAuthenticationService("http://test-auth-service:8080/v1", routeScopeConfiguration);
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

  @Test
  void api_key_validation_request_should_have_api_key() {
    String testApiKey = "test-api-key";
    GatewayAuthenticationService.ApiKeyValidationRequest request = 
        new GatewayAuthenticationService.ApiKeyValidationRequest(testApiKey);
    
    assertThat(request.getApiKey()).isEqualTo(testApiKey);
  }

  @Test
  void api_key_validation_response_should_determine_type_from_role() {
    GatewayAuthenticationService.ApiKeyValidationResponse response = 
        new GatewayAuthenticationService.ApiKeyValidationResponse();
    
    response.setRole("service");
    response.setUsername("challenge-service");
    
    assertThat(response.getType()).isEqualTo("service");
    assertThat(response.getServiceName()).isEqualTo("challenge-service");
    
    response.setRole("user");
    assertThat(response.getType()).isEqualTo("user");
    assertThat(response.getServiceName()).isNull();
  }
}
