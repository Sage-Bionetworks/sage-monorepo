package org.sagebionetworks.openchallenges.challenge.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sagebionetworks.openchallenges.challenge.service.configuration.AppProperties;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceApplicationTest {

  @Mock
  private AppProperties appProperties;

  @InjectMocks
  private ChallengeServiceApplication application;

  @Test
  @DisplayName("should log welcome message when application runs")
  void shouldLogWelcomeMessageWhenApplicationRuns() throws Exception {
    // given
    String expectedMessage = "Welcome to the challenge service.";
    when(appProperties.welcomeMessage()).thenReturn(expectedMessage);

    // when
    application.run();

    // then
    verify(appProperties).welcomeMessage();
  }
}
