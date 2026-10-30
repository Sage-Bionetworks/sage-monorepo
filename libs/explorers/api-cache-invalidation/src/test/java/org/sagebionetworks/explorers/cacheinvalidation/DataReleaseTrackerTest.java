package org.sagebionetworks.explorers.cacheinvalidation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DataReleaseTrackerTest {

  private static final DataRelease INITIAL_RELEASE = new DataRelease("syn13363290", "117");
  private static final DataRelease NEXT_VERSION = new DataRelease("syn13363290", "118");
  private static final Instant REFRESHED_AT = Instant.parse("2026-10-30T17:00:00Z");
  private static final RuntimeException READ_ERROR = new IllegalStateException(
    "MongoDB connection lost"
  );

  @Mock
  private DataVersionProvider dataVersionProvider;

  @Mock
  private BiConsumer<DataRelease, DataRelease> changeListener;

  private MockedStatic<Sentry> sentry;
  private DataReleaseTracker tracker;

  @BeforeEach
  void setUp() {
    sentry = mockStatic(Sentry.class);
    tracker = new DataReleaseTracker(dataVersionProvider, Clock.fixed(REFRESHED_AT, ZoneOffset.UTC));
    tracker.addChangeListener(changeListener);
  }

  @AfterEach
  void tearDown() {
    sentry.close();
  }

  private void refreshTimes(int count) {
    for (int i = 0; i < count; i++) {
      tracker.refresh();
    }
  }

  private SentryEvent reportedEvent() {
    ArgumentCaptor<SentryEvent> event = ArgumentCaptor.forClass(SentryEvent.class);
    sentry.verify(() -> Sentry.captureEvent(event.capture()));
    return event.getValue();
  }

  @Test
  @DisplayName("should read the release while starting without reporting a change")
  void shouldReadTheReleaseWhileStartingWithoutReportingAChange() {
    when(dataVersionProvider.getCurrentDataRelease()).thenReturn(Optional.of(INITIAL_RELEASE));

    tracker.afterPropertiesSet();

    assertThat(tracker.latestRelease()).contains(INITIAL_RELEASE);
    assertThat(tracker.getState()).isEqualTo(
      new DataReleaseState("syn13363290-v117", REFRESHED_AT.toString(), 0)
    );
    verify(changeListener, never()).accept(any(), any());
  }

  @Test
  @DisplayName("should report a change to listeners when the release changes")
  void shouldReportAChangeToListenersWhenTheReleaseChanges() {
    when(dataVersionProvider.getCurrentDataRelease()).thenReturn(
      Optional.of(INITIAL_RELEASE),
      Optional.of(NEXT_VERSION)
    );

    refreshTimes(2);

    verify(changeListener).accept(INITIAL_RELEASE, NEXT_VERSION);
    assertThat(tracker.latestRelease()).contains(NEXT_VERSION);
  }

  @Test
  @DisplayName("should not report a change when the release is unchanged")
  void shouldNotReportAChangeWhenTheReleaseIsUnchanged() {
    when(dataVersionProvider.getCurrentDataRelease()).thenReturn(Optional.of(INITIAL_RELEASE));

    refreshTimes(3);

    verify(changeListener, never()).accept(any(), any());
  }

  @Test
  @DisplayName("should keep the last release and count failures when refreshes fail")
  void shouldKeepTheLastReleaseAndCountFailuresWhenRefreshesFail() {
    when(dataVersionProvider.getCurrentDataRelease())
      .thenReturn(Optional.of(INITIAL_RELEASE))
      .thenThrow(READ_ERROR)
      .thenReturn(Optional.empty());

    refreshTimes(3);

    assertThat(tracker.latestRelease()).contains(INITIAL_RELEASE);
    assertThat(tracker.getState()).isEqualTo(
      new DataReleaseState("syn13363290-v117", REFRESHED_AT.toString(), 2)
    );
  }

  @Test
  @DisplayName("should warn once when refreshes keep failing for the warning delay")
  void shouldWarnOnceWhenRefreshesKeepFailingForTheWarningDelay() {
    when(dataVersionProvider.getCurrentDataRelease())
      .thenReturn(Optional.of(INITIAL_RELEASE))
      .thenThrow(READ_ERROR);

    refreshTimes(1 + DataReleaseTracker.FAILURE_WARNING_THRESHOLD - 1);
    sentry.verify(() -> Sentry.captureEvent(any(SentryEvent.class)), never());

    refreshTimes(1 + DataReleaseTracker.FAILURE_WARNING_THRESHOLD);

    sentry.verify(() -> Sentry.captureEvent(any(SentryEvent.class)), times(1));
    SentryEvent event = reportedEvent();
    assertThat(event.getLevel()).isEqualTo(SentryLevel.WARNING);
    assertThat(event.getFingerprints()).isEqualTo(
      List.of(DataReleaseTracker.SENTRY_SOURCE, DataReleaseTracker.REFRESH_FAILING_MESSAGE)
    );
    assertThat(event.getTag("source")).isEqualTo(DataReleaseTracker.SENTRY_SOURCE);
    assertThat(event.getExtra("consecutiveFailures")).isEqualTo(
      DataReleaseTracker.FAILURE_WARNING_THRESHOLD
    );
    assertThat(event.getExtra("releaseInUse")).isEqualTo("syn13363290-v117");
    assertThat(event.getExtra("reason")).isEqualTo(
      DataReleaseTracker.DATA_RELEASE_READ_FAILED_REASON
    );
    assertThat(event.getThrowable()).hasCause(READ_ERROR);
  }

  @Test
  @DisplayName("should reset the failure count when a refresh succeeds again")
  void shouldResetTheFailureCountWhenARefreshSucceedsAgain() {
    when(dataVersionProvider.getCurrentDataRelease())
      .thenReturn(Optional.of(INITIAL_RELEASE))
      .thenThrow(READ_ERROR)
      .thenReturn(Optional.of(INITIAL_RELEASE));

    refreshTimes(3);

    assertThat(tracker.getState().consecutiveFailures()).isZero();
  }

  @Test
  @DisplayName("should report an unknown release once as an error without a refresh warning")
  void shouldReportAnUnknownReleaseOnceAsAnErrorWithoutARefreshWarning() {
    when(dataVersionProvider.getCurrentDataRelease()).thenReturn(Optional.empty());

    refreshTimes(DataReleaseTracker.FAILURE_WARNING_THRESHOLD * 2);

    assertThat(tracker.latestRelease()).isEmpty();
    SentryEvent event = reportedEvent();
    assertThat(event.getLevel()).isEqualTo(SentryLevel.ERROR);
    assertThat(event.getFingerprints()).isEqualTo(
      List.of(DataReleaseTracker.SENTRY_SOURCE, DataReleaseTracker.DATA_RELEASE_UNKNOWN_MESSAGE)
    );
    assertThat(event.getMessage().getMessage()).isEqualTo(
      DataReleaseTracker.SENTRY_SOURCE + ": " + DataReleaseTracker.DATA_RELEASE_UNKNOWN_MESSAGE
    );
    assertThat(event.getExtra("reason")).isEqualTo(
      DataReleaseTracker.DATAVERSION_DOCUMENT_INCOMPLETE_REASON
    );
  }

  @Test
  @DisplayName("should start using the release without reporting a change when one can be read")
  void shouldStartUsingTheReleaseWithoutReportingAChangeWhenOneCanBeRead() {
    when(dataVersionProvider.getCurrentDataRelease()).thenReturn(
      Optional.empty(),
      Optional.of(INITIAL_RELEASE)
    );

    refreshTimes(2);

    assertThat(tracker.latestRelease()).contains(INITIAL_RELEASE);
    verify(changeListener, never()).accept(any(), any());
  }

  @Test
  @DisplayName("should still call the other listeners and report the failure when one fails")
  void shouldStillCallTheOtherListenersAndReportTheFailureWhenOneFails() {
    RuntimeException listenerError = new IllegalStateException("cache clear failed");
    BiConsumer<DataRelease, DataRelease> failingListener = (previous, current) -> {
      throw listenerError;
    };
    DataReleaseTracker trackerWithFailingListener = new DataReleaseTracker(dataVersionProvider);
    trackerWithFailingListener.addChangeListener(failingListener);
    trackerWithFailingListener.addChangeListener(changeListener);
    when(dataVersionProvider.getCurrentDataRelease()).thenReturn(
      Optional.of(INITIAL_RELEASE),
      Optional.of(NEXT_VERSION)
    );

    trackerWithFailingListener.refresh();
    trackerWithFailingListener.refresh();

    verify(changeListener).accept(INITIAL_RELEASE, NEXT_VERSION);
    SentryEvent event = reportedEvent();
    assertThat(event.getFingerprints()).isEqualTo(
      List.of(DataReleaseTracker.SENTRY_SOURCE, DataReleaseTracker.CHANGE_LISTENER_FAILED_MESSAGE)
    );
    assertThat(event.getExtra("previous")).isEqualTo("syn13363290-v117");
    assertThat(event.getExtra("current")).isEqualTo("syn13363290-v118");
    assertThat(event.getThrowable()).hasCause(listenerError);
  }
}
