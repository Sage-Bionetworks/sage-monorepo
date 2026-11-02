package org.sagebionetworks.explorers.cacheinvalidation;

import io.sentry.Sentry;
import io.sentry.SentryEvent;
import io.sentry.SentryLevel;
import io.sentry.protocol.Message;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Keeps this API task's view of the current data release, re-reading it every refresh interval so a
 * release switch reaches the task within that interval. A failed refresh keeps the last release
 * read, so a database outage doesn't change which release the task serves. The first read happens
 * while the application starts, before the server takes traffic.
 */
@Slf4j
public class DataReleaseTracker implements InitializingBean {

  /** Must match `DATA_RELEASE_REFRESH_INTERVAL_MS` in `libs/explorers/constants/src/lib/data-release.ts`. */
  public static final long REFRESH_INTERVAL_MS = 15_000L;

  static final long REFRESH_FAILURE_WARNING_DELAY_MS = 2 * 60 * 1000L;
  static final int FAILURE_WARNING_THRESHOLD = (int) Math.ceil(
    (double) REFRESH_FAILURE_WARNING_DELAY_MS / REFRESH_INTERVAL_MS
  );

  /** The source the tracker's Sentry events are titled, grouped, and tagged with. */
  static final String SENTRY_SOURCE = "DataReleaseTracker";
  static final String DATA_RELEASE_UNKNOWN_MESSAGE = "Data release unknown";
  static final String REFRESH_FAILING_MESSAGE = "Data release refresh keeps failing";
  static final String CHANGE_LISTENER_FAILED_MESSAGE = "A data release change listener failed";
  static final String DATAVERSION_DOCUMENT_INCOMPLETE_REASON =
    "The dataversion document is missing or incomplete";
  static final String DATA_RELEASE_READ_FAILED_REASON = "The data release could not be read";

  private final DataVersionProvider dataVersionProvider;
  private final Clock clock;
  private final List<BiConsumer<DataRelease, DataRelease>> changeListeners =
    new CopyOnWriteArrayList<>();

  private volatile DataRelease release;
  private volatile Instant lastRefreshedAt;
  private volatile int consecutiveFailures;
  private boolean hasReportedUnknownRelease;
  private boolean hasWarnedOfFailingRefresh;

  public DataReleaseTracker(DataVersionProvider dataVersionProvider) {
    this(dataVersionProvider, Clock.systemUTC());
  }

  DataReleaseTracker(DataVersionProvider dataVersionProvider, Clock clock) {
    this.dataVersionProvider = dataVersionProvider;
    this.clock = clock;
  }

  @Override
  public void afterPropertiesSet() {
    refresh();
  }

  /** The last release read, or empty if none has been read since the application started. */
  public Optional<DataRelease> latestRelease() {
    return Optional.ofNullable(release);
  }

  /**
   * Calls the listener with the previous and the new release each time a refresh reads a release
   * different from the last one. Reading the first release is not a change.
   */
  public void addChangeListener(BiConsumer<DataRelease, DataRelease> listener) {
    changeListeners.add(listener);
  }

  public DataReleaseState getState() {
    return new DataReleaseState(
      release == null ? null : release.toString(),
      lastRefreshedAt == null ? null : lastRefreshedAt.toString(),
      consecutiveFailures
    );
  }

  @Scheduled(initialDelay = REFRESH_INTERVAL_MS, fixedDelay = REFRESH_INTERVAL_MS)
  public synchronized void refresh() {
    Optional<DataRelease> currentRelease;
    try {
      currentRelease = dataVersionProvider.getCurrentDataRelease();
    } catch (RuntimeException e) {
      recordFailure(DATA_RELEASE_READ_FAILED_REASON, e);
      return;
    }
    currentRelease.ifPresentOrElse(this::recordRelease, () ->
      recordFailure(DATAVERSION_DOCUMENT_INCOMPLETE_REASON, null)
    );
  }

  private void recordRelease(DataRelease currentRelease) {
    if (hasWarnedOfFailingRefresh) {
      log.info("Data release refresh recovered after {} failed attempts", consecutiveFailures);
    }
    DataRelease previous = release;
    release = currentRelease;
    lastRefreshedAt = clock.instant();
    consecutiveFailures = 0;
    hasWarnedOfFailingRefresh = false;

    if (previous == null) {
      log.info("Initial data release observed: {}", currentRelease);
      return;
    }
    if (!previous.equals(currentRelease)) {
      log.info("Data release changed {} -> {}", previous, currentRelease);
      notifyChange(previous, currentRelease);
    }
  }

  private void notifyChange(DataRelease previous, DataRelease current) {
    for (BiConsumer<DataRelease, DataRelease> listener : changeListeners) {
      try {
        listener.accept(previous, current);
      } catch (RuntimeException e) {
        log.error(CHANGE_LISTENER_FAILED_MESSAGE, e);
        report(
          SentryLevel.ERROR,
          CHANGE_LISTENER_FAILED_MESSAGE,
          Map.of("previous", previous.toString(), "current", current.toString()),
          e
        );
      }
    }
  }

  // An unknown release and a failing refresh are reported separately: the first means no release
  // has been read since the application started, the second that refreshes have kept failing after
  // one was.
  private void recordFailure(String reason, Throwable error) {
    consecutiveFailures++;

    if (release == null) {
      if (!hasReportedUnknownRelease) {
        hasReportedUnknownRelease = true;
        log.error("{}: {}", DATA_RELEASE_UNKNOWN_MESSAGE, reason, error);
        report(SentryLevel.ERROR, DATA_RELEASE_UNKNOWN_MESSAGE, Map.of("reason", reason), error);
      }
      return;
    }

    if (consecutiveFailures >= FAILURE_WARNING_THRESHOLD && !hasWarnedOfFailingRefresh) {
      hasWarnedOfFailingRefresh = true;
      String releaseInUse = release.toString();
      log.warn(
        "{}: {} failures in a row, still using {}: {}",
        REFRESH_FAILING_MESSAGE,
        consecutiveFailures,
        releaseInUse,
        reason,
        error
      );
      report(
        SentryLevel.WARNING,
        REFRESH_FAILING_MESSAGE,
        Map.of(
          "consecutiveFailures",
          consecutiveFailures,
          "releaseInUse",
          releaseInUse,
          "reason",
          reason
        ),
        error
      );
    }
  }

  /**
   * Sends a tracker event to Sentry the way the frontend's `SourceLogger` and the agora Node API's
   * tracker do: titled `<source>: <message>` and grouped by source and message, which stay fixed,
   * with the values that vary per occurrence as extra data. A caught error is attached as the
   * event's exception.
   */
  private void report(
    SentryLevel level,
    String message,
    Map<String, Object> extras,
    Throwable error
  ) {
    String title = SENTRY_SOURCE + ": " + message;
    SentryEvent event = error == null
      ? new SentryEvent()
      : new SentryEvent(new IllegalStateException(title, error));
    Message eventMessage = new Message();
    eventMessage.setMessage(title);
    event.setMessage(eventMessage);
    event.setLevel(level);
    event.setFingerprints(List.of(SENTRY_SOURCE, message));
    event.setTag("source", SENTRY_SOURCE);
    extras.forEach(event::setExtra);
    Sentry.captureEvent(event);
  }
}
