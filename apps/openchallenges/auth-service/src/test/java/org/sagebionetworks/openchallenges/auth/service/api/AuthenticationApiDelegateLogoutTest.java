package org.sagebionetworks.openchallenges.auth.service.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LogoutRequestDto;
import org.sagebionetworks.openchallenges.auth.service.model.dto.LogoutResponseDto;
import org.sagebionetworks.openchallenges.auth.service.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Authentication API Delegate - Logout Tests")
class AuthenticationApiDelegateLogoutTest {

  @Mock
  private AuthenticationService authenticationService;

  @InjectMocks
  private AuthenticationApiDelegateImpl authenticationApiDelegate;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("should successfully logout and revoke single token")
  void shouldSuccessfullyLogoutAndRevokeSingleToken() {
    // Arrange
    String refreshToken = "test-refresh-token";
    LogoutRequestDto request = new LogoutRequestDto()
      .refreshToken(refreshToken)
      .revokeAllTokens(false);
    
    when(authenticationService.logout(refreshToken, false)).thenReturn(1);

    // Act
    ResponseEntity<LogoutResponseDto> response = authenticationApiDelegate.logout(request);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Successfully logged out", response.getBody().getMessage());
    assertEquals(1, response.getBody().getRevokedTokens());
    
    verify(authenticationService).logout(refreshToken, false);
  }

  @Test
  @DisplayName("should successfully logout and revoke all tokens")
  void shouldSuccessfullyLogoutAndRevokeAllTokens() {
    // Arrange
    String refreshToken = "test-refresh-token";
    LogoutRequestDto request = new LogoutRequestDto()
      .refreshToken(refreshToken)
      .revokeAllTokens(true);
    
    when(authenticationService.logout(refreshToken, true)).thenReturn(3);

    // Act
    ResponseEntity<LogoutResponseDto> response = authenticationApiDelegate.logout(request);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("Successfully logged out", response.getBody().getMessage());
    assertEquals(3, response.getBody().getRevokedTokens());
    
    verify(authenticationService).logout(refreshToken, true);
  }

  @Test
  @DisplayName("should handle logout error gracefully")
  void shouldHandleLogoutErrorGracefully() {
    // Arrange
    String refreshToken = "invalid-refresh-token";
    LogoutRequestDto request = new LogoutRequestDto()
      .refreshToken(refreshToken)
      .revokeAllTokens(false);
    
    when(authenticationService.logout(refreshToken, false))
      .thenThrow(new RuntimeException("Invalid refresh token"));

    // Act
    ResponseEntity<LogoutResponseDto> response = authenticationApiDelegate.logout(request);

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    
    verify(authenticationService).logout(refreshToken, false);
  }

  @Test
  @DisplayName("should handle null revokeAllTokens parameter gracefully")
  void shouldHandleNullRevokeAllTokensParameterGracefully() {
    // Arrange
    String refreshToken = "test-refresh-token";
    LogoutRequestDto request = new LogoutRequestDto()
      .refreshToken(refreshToken)
      .revokeAllTokens(null); // null should default to false
    
    when(authenticationService.logout(refreshToken, false)).thenReturn(1);

    // Act
    ResponseEntity<LogoutResponseDto> response = authenticationApiDelegate.logout(request);

    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().getRevokedTokens());
    
    verify(authenticationService).logout(refreshToken, false);
  }
}
