package org.sagebionetworks.openchallenges.api.gateway.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

class OAuth2AudienceTest {

  @Test
  void shouldHandleStringAudience() {
    // Given
    String singleAudience = "openchallenges-client";
    
    // When
    OAuth2JwtService.OAuth2Audience audience = new OAuth2JwtService.OAuth2Audience(singleAudience);
    
    // Then
    assertEquals("openchallenges-client", audience.getPrimary());
    assertEquals(List.of("openchallenges-client"), audience.getAll());
    assertTrue(audience.contains("openchallenges-client"));
    assertFalse(audience.contains("other-client"));
    assertEquals("openchallenges-client", audience.toJson());
  }

  @Test
  void shouldHandleListAudience() {
    // Given
    List<String> multipleAudiences = List.of("openchallenges-client", "another-client");
    
    // When
    OAuth2JwtService.OAuth2Audience audience = new OAuth2JwtService.OAuth2Audience(multipleAudiences);
    
    // Then
    assertEquals("openchallenges-client", audience.getPrimary());
    assertEquals(multipleAudiences, audience.getAll());
    assertTrue(audience.contains("openchallenges-client"));
    assertTrue(audience.contains("another-client"));
    assertFalse(audience.contains("third-client"));
    assertEquals(multipleAudiences, audience.toJson());
  }

  @Test
  void shouldHandleEmptyList() {
    // Given
    List<String> emptyList = List.of();
    
    // When
    OAuth2JwtService.OAuth2Audience audience = new OAuth2JwtService.OAuth2Audience(emptyList);
    
    // Then
    assertNull(audience.getPrimary());
    assertTrue(audience.getAll().isEmpty());
    assertFalse(audience.contains("any-client"));
    assertEquals(emptyList, audience.toJson());
  }

  @Test
  void shouldHandleNullOrInvalidInput() {
    // Given
    Object invalidInput = Integer.valueOf(123);
    
    // When
    OAuth2JwtService.OAuth2Audience audience = new OAuth2JwtService.OAuth2Audience(invalidInput);
    
    // Then
    assertNull(audience.getPrimary());
    assertTrue(audience.getAll().isEmpty());
    assertFalse(audience.contains("any-client"));
  }

  @Test
  void shouldReturnStringForSingleAudienceJsonSerialization() {
    // Given
    OAuth2JwtService.OAuth2Audience singleAudience = new OAuth2JwtService.OAuth2Audience("client1");
    OAuth2JwtService.OAuth2Audience multipleAudiences = new OAuth2JwtService.OAuth2Audience(List.of("client1", "client2"));
    
    // When & Then
    assertEquals("client1", singleAudience.toJson());
    assertEquals(List.of("client1", "client2"), multipleAudiences.toJson());
  }
}
