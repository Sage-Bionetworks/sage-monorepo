package org.sagebionetworks.explorers.cacheinvalidation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

@ExtendWith(MockitoExtension.class)
class DataReleaseHealthIndicatorTest {

  @Mock
  private DataReleaseTracker tracker;

  @InjectMocks
  private DataReleaseHealthIndicator healthIndicator;

  @Test
  @DisplayName("should report the release state as details when the release is known")
  void shouldReportTheReleaseStateAsDetailsWhenTheReleaseIsKnown() {
    when(tracker.getState()).thenReturn(
      new DataReleaseState("syn13363290-v117", "2026-10-30T17:00:00Z", 0)
    );

    Health health = healthIndicator.health();

    assertThat(health.getDetails()).isEqualTo(
      Map.of(
        "release",
        "syn13363290-v117",
        "lastRefreshedAt",
        "2026-10-30T17:00:00Z",
        "consecutiveFailures",
        0
      )
    );
  }

  @Test
  @DisplayName("should stay up when refreshes are failing and the release is unknown")
  void shouldStayUpWhenRefreshesAreFailingAndTheReleaseIsUnknown() {
    when(tracker.getState()).thenReturn(new DataReleaseState(null, null, 8));

    Health health = healthIndicator.health();

    Map<String, Object> expectedDetails = new HashMap<>();
    expectedDetails.put("release", null);
    expectedDetails.put("lastRefreshedAt", null);
    expectedDetails.put("consecutiveFailures", 8);
    assertThat(health.getStatus()).isEqualTo(Status.UP);
    assertThat(health.getDetails()).isEqualTo(expectedDetails);
  }
}
