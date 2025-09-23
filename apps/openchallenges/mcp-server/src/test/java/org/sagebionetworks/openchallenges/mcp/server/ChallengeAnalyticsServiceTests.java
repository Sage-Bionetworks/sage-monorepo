package org.sagebionetworks.openchallenges.mcp.server;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sagebionetworks.openchallenges.api.client.api.ChallengeAnalyticsApi;
import org.sagebionetworks.openchallenges.api.client.model.ChallengesPerYear;
import org.sagebionetworks.openchallenges.mcp.server.service.ChallengeAnalyticsService;

class ChallengeAnalyticsServiceTests {

  private ChallengeAnalyticsApi mockApi;
  private ChallengeAnalyticsService service;

  @BeforeEach
  void setUp() {
    mockApi = mock(ChallengeAnalyticsApi.class);
    service = new ChallengeAnalyticsService(mockApi);
  }

  @Test
  void testGetChallengesPerYear() {
    // Arrange
    ChallengesPerYear expected = new ChallengesPerYear();
    expected.addYearsItem("2020").addChallengeCountsItem(5);
    when(mockApi.getChallengesPerYear()).thenReturn(expected);

    // Act
    ChallengesPerYear actual = service.getChallengesPerYear();

    // Assert
    assertEquals(expected, actual);
    verify(mockApi, times(1)).getChallengesPerYear();
  }
}
