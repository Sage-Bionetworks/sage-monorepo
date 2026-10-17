import { DOCUMENT } from '@angular/common';
import { HttpBackend, HttpClient, HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import {
  DestroyRef,
  inject,
  Injectable,
  InjectionToken,
  isDevMode,
  NgZone,
  signal,
  WritableSignal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NavigationError, Router } from '@angular/router';
import { AppVersion } from '@sagebionetworks/explorers/config';
import {
  APP_UPDATE_CONFIRM_DELAY_MS,
  APP_UPDATE_MIN_CHECK_INTERVAL_MS,
  APP_UPDATE_POLL_INTERVAL_MS,
  APP_VERSION_PATH,
  CHUNK_LOAD_ERROR_MESSAGE,
  CHUNK_LOAD_FAILED_METRIC,
  DATA_UPDATE_CONFIRM_DELAY_MS,
  DATA_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
  STALE_APP_METRIC,
  STALE_DATA_METRIC,
  TRANSIENT_DATA_VERSION_MISMATCH_METRIC,
  VERSION_CHECK_TIMEOUT_MS,
  TRANSIENT_APP_VERSION_MISMATCH_METRIC,
  VERSION_CHECK_FAILED_METRIC,
  APP_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
} from '@sagebionetworks/explorers/constants';
import { isChunkLoadError } from '@sagebionetworks/explorers/sentry';
import * as Sentry from '@sentry/angular';
import {
  catchError,
  exhaustMap,
  filter,
  finalize,
  firstValueFrom,
  forkJoin,
  fromEvent,
  interval,
  map,
  merge,
  Observable,
  of,
  share,
  Subject,
  Subscription,
  switchMap,
  timeout,
  TimeoutError,
  timer,
} from 'rxjs';
import { ErrorOverlayService } from './error-overlay.service';
import { LoggerService } from './logger.service';
import { PlatformService } from './platform.service';
import { DataVersion, VersionService } from './version.service';

export const AppUpdateCheckTriggerEnum = {
  ChunkLoadError: 'chunk-load-error',
  DataVersionMismatchConfirmation: 'data-version-mismatch-confirmation',
  HttpError: 'http-error',
  Poll: 'poll',
  Reconnect: 'reconnect',
  AppVersionMismatchConfirmation: 'app-version-mismatch-confirmation',
  Visibility: 'visibility',
} as const;
export type AppUpdateCheckTrigger =
  (typeof AppUpdateCheckTriggerEnum)[keyof typeof AppUpdateCheckTriggerEnum];

/**
 * Which versions differed from the ones the tab loaded with when it was confirmed stale. Reported
 * as a Sentry metric attribute, so tabs that missed both a deploy and a data release are counted as
 * such rather than as either one alone.
 */
export const MismatchedVersionsEnum = {
  App: 'app',
  AppAndData: 'app-and-data',
  Data: 'data',
} as const;
export type MismatchedVersions =
  (typeof MismatchedVersionsEnum)[keyof typeof MismatchedVersionsEnum];

/**
 * What an immediate app version check found. Reported as the outcome attribute of the chunk-load
 * failure metric, which tells deploys apart from network problems.
 */
export const AppVersionCheckOutcomeEnum = {
  /** The version request errored or timed out, so whether the tab is stale is unknown */
  CheckFailed: 'check-failed',
  /** The server runs the version this tab loaded with */
  Current: 'current',
  /** The browser reports no connection, so no request was sent */
  Offline: 'offline',
  /** The server runs a different version than this tab loaded with */
  Stale: 'stale',
} as const;
export type AppVersionCheckOutcome =
  (typeof AppVersionCheckOutcomeEnum)[keyof typeof AppVersionCheckOutcomeEnum];

/** Which version endpoint a check failure came from. Reported as a Sentry metric attribute. */
export const VersionCheckEndpointEnum = {
  AppVersion: 'app-version',
  DataVersion: 'data-version',
} as const;
export type VersionCheckEndpoint =
  (typeof VersionCheckEndpointEnum)[keyof typeof VersionCheckEndpointEnum];

/** Why a version check failed. These values are reported as a Sentry metric attribute. */
export const VersionCheckFailureReasonEnum = {
  /** A 2xx whose body is not a version, i.e. requests are misrouted */
  InvalidResponse: 'invalid-response',
  /** No response at all, usually the user's network (captive portal, VPN) */
  Network: 'network',
  /** The server runs a build without the version endpoint, or requests are misrouted */
  NotFound: 'not-found',
  /** A 5xx, i.e. no healthy task, which is brief during a deploy */
  Server: 'server',
  /** No response within `VERSION_CHECK_TIMEOUT_MS`, usually a stalled connection */
  Timeout: 'timeout',
  /** Any other non-2xx response */
  UnexpectedStatus: 'unexpected-status',
} as const;
export type VersionCheckFailureReason =
  (typeof VersionCheckFailureReasonEnum)[keyof typeof VersionCheckFailureReasonEnum];

/** The status Angular reports when a request gets no response at all, also used for a timeout. */
export const NO_RESPONSE_STATUS = 0;

const MISCONFIGURATION_REASONS: ReadonlySet<VersionCheckFailureReason> = new Set([
  VersionCheckFailureReasonEnum.InvalidResponse,
  VersionCheckFailureReasonEnum.NotFound,
]);

/**
 * Whether this app checks for newer deploys. Off in development builds, since the dev server does
 * not run the app server's routes and renders the version endpoint as a page instead.
 */
export const APP_UPDATE_CHECKS_ENABLED = new InjectionToken<boolean>('APP_UPDATE_CHECKS_ENABLED', {
  providedIn: 'root',
  factory: () => !isDevMode(),
});

/**
 * The two versions this service tracks: the app version, which reflects the code the tab runs,
 * and the data version, which reflects the data release it shows.
 */
type TrackedVersion = 'app' | 'data';

interface TrackedVersionConfig {
  endpoint: VersionCheckEndpoint;
  confirmDelayMs: number;
  confirmationTrigger: AppUpdateCheckTrigger;
  staleMetric: string;
  transientMismatchMetric: string;
  misconfiguredWarning: string;
  checkFailedMessage: string;
}

const TRACKED_VERSION_CONFIGS: Record<TrackedVersion, TrackedVersionConfig> = {
  app: {
    endpoint: VersionCheckEndpointEnum.AppVersion,
    confirmDelayMs: APP_UPDATE_CONFIRM_DELAY_MS,
    confirmationTrigger: AppUpdateCheckTriggerEnum.AppVersionMismatchConfirmation,
    staleMetric: STALE_APP_METRIC,
    transientMismatchMetric: TRANSIENT_APP_VERSION_MISMATCH_METRIC,
    misconfiguredWarning: APP_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
    checkFailedMessage: 'App version check failed',
  },
  data: {
    endpoint: VersionCheckEndpointEnum.DataVersion,
    confirmDelayMs: DATA_UPDATE_CONFIRM_DELAY_MS,
    confirmationTrigger: AppUpdateCheckTriggerEnum.DataVersionMismatchConfirmation,
    staleMetric: STALE_DATA_METRIC,
    transientMismatchMetric: TRANSIENT_DATA_VERSION_MISMATCH_METRIC,
    misconfiguredWarning: DATA_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
    checkFailedMessage: 'Data version check failed',
  },
};

/** A version mismatch that has not yet outlasted its confirmation delay. */
interface PendingVersionMismatch {
  serverVersionLabel: string;
  trigger: AppUpdateCheckTrigger;
  detectedAt: number;
}

/**
 * The result of comparing one version with the one the tab loaded with, or null when the version
 * is unknown because the check was skipped or failed.
 */
type VersionComparison = { isMismatch: boolean; serverVersionLabel: string } | null;

type VersionComparisons = Record<TrackedVersion, VersionComparison>;

interface VersionCheckResult {
  isAppVersionKnown: boolean;
  isAppVersionMismatch: boolean;
  isDataVersionMismatch: boolean;
}

/**
 * Detects that this tab is running code from an earlier deploy, or showing data from an earlier
 * data release, by comparing the versions it was loaded with against the versions the servers
 * currently report, then blocks the page with an overlay whose only action is to reload.
 *
 * A mismatch must persist for its confirmation delay before the overlay is shown. During a rolling
 * deploy, old and new app tasks both answer the version endpoint, so a single mismatch can come
 * from a task that is about to be replaced. A data release switches over in one step, but the APIs
 * clear their caches up to a couple of minutes later, so a reload before then could still show the
 * old data. A chunk that fails to load on a mismatched app version is the exception: the tab's
 * navigation is already broken, and reloading is the only fix whichever task answers.
 *
 * Staleness is judged by any difference rather than by which version is newer, because a rollback
 * moves the server back to an earlier version and tabs loaded from the rolled-back one must reload
 * too.
 */
@Injectable({ providedIn: 'root' })
export class AppUpdateService {
  // Bypasses the app's HTTP interceptors, which would replace a failed response with a plain Error
  // and lose the status this service classifies failures by.
  private readonly http = new HttpClient(inject(HttpBackend));
  private readonly document = inject(DOCUMENT);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
  private readonly ngZone = inject(NgZone);
  private readonly platformService = inject(PlatformService);
  private readonly errorOverlayService = inject(ErrorOverlayService);
  private readonly versionService = inject(VersionService);
  private readonly logger = inject(LoggerService).forSource('AppUpdateService');
  private readonly checksEnabled = inject(APP_UPDATE_CHECKS_ENABLED);

  private readonly stale: Record<TrackedVersion, WritableSignal<boolean>> = {
    app: signal(false),
    data: signal(false),
  };

  private clientVersion: AppVersion | null = null;
  private dataVersionUrl: string | null = null;
  private siteName = '';
  private lastCheckedAt = 0;
  private readonly reportedMisconfigurations = new Set<VersionCheckEndpoint>();
  private backgroundVersionCheckSubscription: Subscription | null = null;
  private readonly pendingMismatches: Record<TrackedVersion, PendingVersionMismatch | null> = {
    app: null,
    data: null,
  };
  private readonly confirmationSubscriptions: Record<TrackedVersion, Subscription | null> = {
    app: null,
    data: null,
  };
  private inFlightCheck$: Observable<VersionCheckResult> | null = null;
  private hasChunkLoadErrorJoinedInFlightCheck = false;
  private readonly requestedChecks = new Subject<AppUpdateCheckTrigger>();

  /**
   * Starts checking for a newer deploy whenever the tab becomes visible again, whenever the
   * browser reconnects, periodically while the tab stays visible, and right away when a route's
   * lazy-loaded chunk fails to load, which shows the overlay whatever the check finds. With a
   * `dataVersionUrl`, each check also compares the data version against the one the tab loaded
   * with (see `VersionService.getLoadedDataVersion`). Does nothing during SSR, when checks are
   * disabled, or if already started.
   */
  start(clientVersion: AppVersion, siteName: string, dataVersionUrl?: string) {
    const browserWindow = this.document.defaultView;
    if (
      this.platformService.isServer ||
      !this.checksEnabled ||
      !browserWindow ||
      this.clientVersion
    ) {
      return;
    }
    this.clientVersion = clientVersion;
    this.siteName = siteName;
    this.dataVersionUrl = dataVersionUrl ?? null;

    const visibility$ = fromEvent(this.document, 'visibilitychange').pipe(
      map(() => AppUpdateCheckTriggerEnum.Visibility),
    );
    const reconnect$ = fromEvent(browserWindow, 'online').pipe(
      map(() => AppUpdateCheckTriggerEnum.Reconnect),
    );
    const poll$ = interval(APP_UPDATE_POLL_INTERVAL_MS).pipe(
      map(() => AppUpdateCheckTriggerEnum.Poll),
    );

    // Subscribed outside the Angular zone because a pending interval keeps the app from ever
    // becoming stable, and hydration waits for stability before it finishes. The overlay's signals
    // still schedule change detection when they are set outside the zone.
    this.backgroundVersionCheckSubscription = this.ngZone.runOutsideAngular(() =>
      merge(visibility$, reconnect$, poll$, this.requestedChecks)
        .pipe(
          filter(() => this.isTabVisible() && this.isOnline()),
          filter(() => Date.now() - this.lastCheckedAt >= APP_UPDATE_MIN_CHECK_INTERVAL_MS),
          exhaustMap((trigger) => this.check$(trigger)),
          takeUntilDestroyed(this.destroyRef),
        )
        .subscribe(),
    );

    this.router.events
      .pipe(
        filter((event) => event instanceof NavigationError && isChunkLoadError(event.error)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(() => this.handleChunkLoadError());
  }

  /**
   * Handles a route whose lazy-loaded chunk failed to download, which leaves the page without that
   * route's content until the tab reloads.
   *
   * The app version is checked first. When the server runs a different version, a deploy most
   * likely removed the chunk, and the check itself shows the update notice. In every other case
   * (the version matches, the browser is offline, or the check failed), the download most likely
   * failed on the network, so the error overlay asks the user to reload.
   *
   * Each failure is counted once in a Sentry metric, labeled with the check's outcome.
   */
  private async handleChunkLoadError() {
    const outcome = await this.checkAppVersionNow(AppUpdateCheckTriggerEnum.ChunkLoadError);
    Sentry.metrics.count(CHUNK_LOAD_FAILED_METRIC, 1, { attributes: { outcome } });
    if (outcome !== AppVersionCheckOutcomeEnum.Stale) {
      this.errorOverlayService.showError(CHUNK_LOAD_ERROR_MESSAGE);
    }
  }

  /**
   * Asks for a check when something hints the tab may be stale, such as a failed API request.
   * Subject to the same minimum interval as the background checks, so a burst of hints sends at
   * most one request per endpoint.
   */
  requestCheck(trigger: AppUpdateCheckTrigger) {
    this.requestedChecks.next(trigger);
  }

  /**
   * Checks right away, ignoring the minimum interval, and resolves whether the server reports a
   * different app version than this tab loaded with. Each build gives its lazy-loaded chunk files
   * new names, so while the server runs a different version, a chunk this tab requests may not
   * exist there. A true result means a failed chunk load is expected rather than a bug. A data
   * version mismatch does not explain a missing chunk, so it never resolves true, though the check
   * still records it. With the `ChunkLoadError` trigger, an app version mismatch shows the overlay
   * without waiting out the confirmation delay. Resolves false before `start` and while offline,
   * since the version is then unknown.
   */
  checkNow(trigger: AppUpdateCheckTrigger): Promise<boolean> {
    if (!this.clientVersion) {
      return Promise.resolve(false);
    }
    return this.checkAppVersionNow(trigger).then(
      (outcome) => outcome === AppVersionCheckOutcomeEnum.Stale,
    );
  }

  private async checkAppVersionNow(
    trigger: AppUpdateCheckTrigger,
  ): Promise<AppVersionCheckOutcome> {
    if (this.stale.app()) {
      return AppVersionCheckOutcomeEnum.Stale;
    }
    if (!this.isOnline()) {
      return AppVersionCheckOutcomeEnum.Offline;
    }
    const { isAppVersionKnown, isAppVersionMismatch } = await firstValueFrom(this.check$(trigger));
    if (isAppVersionMismatch) {
      return AppVersionCheckOutcomeEnum.Stale;
    }
    return isAppVersionKnown
      ? AppVersionCheckOutcomeEnum.Current
      : AppVersionCheckOutcomeEnum.CheckFailed;
  }

  /**
   * Reuses a check already in progress, so concurrent triggers send one request per endpoint. Both
   * comparisons are handled together once both responses are in, so a tab confirmed stale is
   * reported with every version that differed. A chunk load that fails during a check started by
   * another trigger makes that check handle the app version as a chunk-load check, so a mismatch
   * shows the overlay without the confirmation delay. The data version keeps the trigger that
   * started the check, since a failed chunk load says nothing about the data release.
   */
  private check$(trigger: AppUpdateCheckTrigger): Observable<VersionCheckResult> {
    if (this.inFlightCheck$) {
      if (trigger === AppUpdateCheckTriggerEnum.ChunkLoadError) {
        this.hasChunkLoadErrorJoinedInFlightCheck = true;
      }
    } else {
      this.lastCheckedAt = Date.now();
      this.inFlightCheck$ = forkJoin({
        app: this.checkAppVersion$(trigger),
        data: this.checkDataVersion$(trigger),
      }).pipe(
        map((comparisons) => {
          const appTrigger = this.hasChunkLoadErrorJoinedInFlightCheck
            ? AppUpdateCheckTriggerEnum.ChunkLoadError
            : trigger;
          this.handleComparisons(comparisons, { app: appTrigger, data: trigger });
          return {
            isAppVersionKnown: comparisons.app !== null,
            isAppVersionMismatch: comparisons.app?.isMismatch ?? false,
            isDataVersionMismatch: comparisons.data?.isMismatch ?? false,
          };
        }),
        finalize(() => {
          this.inFlightCheck$ = null;
          this.hasChunkLoadErrorJoinedInFlightCheck = false;
        }),
        share(),
      );
    }
    return this.inFlightCheck$;
  }

  /**
   * The data comparison is handled first, so when the app version is confirmed stale in the same
   * check, handling it cancels any data confirmation the data comparison just scheduled.
   */
  private handleComparisons(
    comparisons: VersionComparisons,
    triggers: Record<TrackedVersion, AppUpdateCheckTrigger>,
  ) {
    for (const trackedVersion of ['data', 'app'] as const) {
      const comparison = comparisons[trackedVersion];
      if (!comparison) {
        continue;
      }
      if (comparison.isMismatch) {
        this.handleVersionMismatch(
          trackedVersion,
          comparison.serverVersionLabel,
          triggers[trackedVersion],
          comparisons,
        );
      } else {
        this.handleVersionMatch(trackedVersion);
      }
    }
  }

  private checkAppVersion$(trigger: AppUpdateCheckTrigger): Observable<VersionComparison> {
    return this.fetchAndCompareVersion$('app', APP_VERSION_PATH, trigger, {
      isVersion: isAppVersion,
      isMismatch: (serverVersion) => this.isAppVersionMismatch(serverVersion),
      label: (version) => this.versionService.getSiteVersion(version),
    });
  }

  /** Skipped when no data version URL was given or when the tab is already known to be stale. */
  private checkDataVersion$(trigger: AppUpdateCheckTrigger): Observable<VersionComparison> {
    if (!this.dataVersionUrl || this.stale.app() || this.stale.data()) {
      return of(null);
    }
    return this.fetchAndCompareVersion$('data', this.dataVersionUrl, trigger, {
      isVersion: isDataVersion,
      isMismatch: (serverVersion) => this.compareDataVersion(serverVersion),
      label: (version) => this.versionService.formatDataVersion(version),
    });
  }

  /**
   * Fetches a version and compares it with the one this tab loaded with. A failure is recorded
   * right away and emits null, since the server's version is then unknown.
   */
  private fetchAndCompareVersion$<Version>(
    trackedVersion: TrackedVersion,
    url: string,
    trigger: AppUpdateCheckTrigger,
    comparison: {
      isVersion: (body: unknown) => body is Version;
      isMismatch: (serverVersion: Version) => boolean;
      label: (version: Version) => string;
    },
  ): Observable<VersionComparison> {
    return this.http.get<unknown>(url).pipe(
      timeout(VERSION_CHECK_TIMEOUT_MS),
      map((body) => {
        if (!comparison.isVersion(body)) {
          this.handleCheckFailure(
            trackedVersion,
            VersionCheckFailureReasonEnum.InvalidResponse,
            HttpStatusCode.Ok,
            trigger,
          );
          return null;
        }
        return {
          isMismatch: comparison.isMismatch(body),
          serverVersionLabel: comparison.label(body),
        };
      }),
      catchError((error: HttpErrorResponse | TimeoutError) => {
        if (error instanceof TimeoutError) {
          this.handleCheckFailure(
            trackedVersion,
            VersionCheckFailureReasonEnum.Timeout,
            NO_RESPONSE_STATUS,
            trigger,
          );
        } else {
          this.handleCheckFailure(trackedVersion, getFailureReason(error), error.status, trigger);
        }
        return of(null);
      }),
    );
  }

  /**
   * A failed confirmation check leaves the mismatch unconfirmed, so it is retried after the minimum
   * interval rather than waiting for the next background check.
   */
  private handleCheckFailure(
    trackedVersion: TrackedVersion,
    reason: VersionCheckFailureReason,
    status: number,
    trigger: AppUpdateCheckTrigger,
  ) {
    this.recordCheckFailure(trackedVersion, reason, status, trigger);
    if (
      trigger === TRACKED_VERSION_CONFIGS[trackedVersion].confirmationTrigger &&
      this.pendingMismatches[trackedVersion]
    ) {
      this.scheduleConfirmation(trackedVersion, APP_UPDATE_MIN_CHECK_INTERVAL_MS);
    }
  }

  /**
   * Every failure is counted, so the failure rate can be watched over time. Only failures that
   * point to a misconfiguration, and so will not resolve on their own, raise a Sentry event, once
   * per endpoint per tab.
   */
  private recordCheckFailure(
    trackedVersion: TrackedVersion,
    reason: VersionCheckFailureReason,
    status: number,
    trigger: AppUpdateCheckTrigger,
  ) {
    const { endpoint, misconfiguredWarning, checkFailedMessage } =
      TRACKED_VERSION_CONFIGS[trackedVersion];
    const failureDetails = { endpoint, reason, status, trigger };
    Sentry.metrics.count(VERSION_CHECK_FAILED_METRIC, 1, { attributes: failureDetails });
    if (MISCONFIGURATION_REASONS.has(reason) && !this.reportedMisconfigurations.has(endpoint)) {
      this.reportedMisconfigurations.add(endpoint);
      this.logger.warn(misconfiguredWarning, failureDetails);
    } else {
      this.logger.log(checkFailedMessage, failureDetails);
    }
  }

  /**
   * A version that is empty on either side is unknown (e.g. local development), so it never
   * counts as a mismatch. Commit SHA identifies the code exactly; app version is the fallback.
   */
  private isAppVersionMismatch(serverVersion: AppVersion): boolean {
    const clientVersion = this.clientVersion;
    if (!clientVersion) {
      return false;
    }
    if (clientVersion.commitSha && serverVersion.commitSha) {
      return clientVersion.commitSha !== serverVersion.commitSha;
    }
    if (clientVersion.appVersion && serverVersion.appVersion) {
      return clientVersion.appVersion !== serverVersion.appVersion;
    }
    return false;
  }

  /**
   * Resolves whether the server's data release differs from the one this tab loaded with. When the
   * footer's request failed or timed out, the tab has no loaded release yet, so the first known
   * release a check sees becomes it and counts as a match. A release can change the data file, the
   * version number, or both. An empty value on either side leaves the release unknown, so it never
   * counts as a mismatch, and is never kept as the loaded release.
   */
  private compareDataVersion(serverVersion: DataVersion): boolean {
    if (!isKnownDataVersion(serverVersion)) {
      return false;
    }
    const loadedVersion = this.versionService.getLoadedDataVersion();
    if (!loadedVersion) {
      this.versionService.recordLoadedDataVersion(serverVersion);
      return false;
    }
    if (!isKnownDataVersion(loadedVersion)) {
      return false;
    }
    return (
      loadedVersion.data_file !== serverVersion.data_file ||
      loadedVersion.data_version !== serverVersion.data_version
    );
  }

  /**
   * Shows the overlay once the servers stop serving this tab's version, whether a deploy or data
   * release replaced it or a rollback removed it. The first mismatch starts the confirmation delay,
   * and only a mismatch after the delay shows the overlay. A match before then clears it, since the
   * servers still serve this tab's version. If that version really is going away, a later mismatch
   * starts the delay again. An app version mismatch found because a chunk failed to load skips the
   * delay, since the tab cannot navigate until it reloads.
   */
  private handleVersionMismatch(
    trackedVersion: TrackedVersion,
    serverVersionLabel: string,
    trigger: AppUpdateCheckTrigger,
    comparisons: VersionComparisons,
  ) {
    if (trackedVersion === 'app' && trigger === AppUpdateCheckTriggerEnum.ChunkLoadError) {
      this.handleStaleVersion(trackedVersion, serverVersionLabel, trigger, comparisons);
      return;
    }
    const pendingMismatch = this.pendingMismatches[trackedVersion];
    if (!pendingMismatch) {
      this.pendingMismatches[trackedVersion] = {
        serverVersionLabel,
        trigger,
        detectedAt: Date.now(),
      };
      this.scheduleConfirmation(
        trackedVersion,
        TRACKED_VERSION_CONFIGS[trackedVersion].confirmDelayMs,
      );
      return;
    }
    if (
      Date.now() - pendingMismatch.detectedAt >=
      TRACKED_VERSION_CONFIGS[trackedVersion].confirmDelayMs
    ) {
      this.handleStaleVersion(
        trackedVersion,
        serverVersionLabel,
        pendingMismatch.trigger,
        comparisons,
      );
    }
  }

  private scheduleConfirmation(trackedVersion: TrackedVersion, delayMs: number) {
    // Subscribed outside the Angular zone for the same reason as the background checks.
    this.confirmationSubscriptions[trackedVersion] = this.ngZone.runOutsideAngular(() =>
      timer(delayMs)
        .pipe(
          switchMap(() => this.check$(TRACKED_VERSION_CONFIGS[trackedVersion].confirmationTrigger)),
          takeUntilDestroyed(this.destroyRef),
        )
        .subscribe(),
    );
  }

  private handleVersionMatch(trackedVersion: TrackedVersion) {
    const pendingMismatch = this.pendingMismatches[trackedVersion];
    const clientVersionLabel = this.getClientVersionLabel(trackedVersion);
    if (!pendingMismatch || !clientVersionLabel) {
      return;
    }
    Sentry.metrics.count(TRACKED_VERSION_CONFIGS[trackedVersion].transientMismatchMetric, 1, {
      attributes: {
        clientVersion: clientVersionLabel,
        serverVersion: pendingMismatch.serverVersionLabel,
        trigger: pendingMismatch.trigger,
      },
    });
    this.cancelConfirmation(trackedVersion);
  }

  private handleStaleVersion(
    trackedVersion: TrackedVersion,
    serverVersionLabel: string,
    trigger: AppUpdateCheckTrigger,
    comparisons: VersionComparisons,
  ) {
    const clientVersionLabel = this.getClientVersionLabel(trackedVersion);
    if (this.stale[trackedVersion]() || !clientVersionLabel) {
      return;
    }
    // Read before the confirmations below are cancelled, since a pending mismatch counts.
    const mismatchedVersions = this.getMismatchedVersions(trackedVersion, comparisons);
    this.stale[trackedVersion].set(true);
    this.backgroundVersionCheckSubscription?.unsubscribe();
    this.cancelConfirmation('data');
    if (trackedVersion === 'app') {
      this.cancelConfirmation('app');
      this.errorOverlayService.showAppUpdateAvailable(this.siteName);
    } else {
      this.errorOverlayService.showDataUpdateAvailable(this.siteName);
    }
    Sentry.metrics.count(TRACKED_VERSION_CONFIGS[trackedVersion].staleMetric, 1, {
      attributes: {
        clientVersion: clientVersionLabel,
        serverVersion: serverVersionLabel,
        trigger,
        mismatchedVersions,
      },
    });
  }

  /**
   * Works out what the stale metric reports (`app`, `data`, or `app-and-data`) when one of the
   * two versions has just been confirmed stale. That version differs by definition. The question
   * is whether the other one differs too, so that a tab that missed both a deploy and a data
   * release is reported as such.
   */
  private getMismatchedVersions(
    confirmedStaleVersion: TrackedVersion,
    latestComparisons: VersionComparisons,
  ): MismatchedVersions {
    // The version just confirmed stale needs no lookup; only the other one does.
    const isAppVersionMismatched =
      confirmedStaleVersion === 'app' || this.differsFromLoadedVersion('app', latestComparisons);
    const isDataVersionMismatched =
      confirmedStaleVersion === 'data' || this.differsFromLoadedVersion('data', latestComparisons);
    if (isAppVersionMismatched && isDataVersionMismatched) {
      return MismatchedVersionsEnum.AppAndData;
    }
    return isAppVersionMismatched ? MismatchedVersionsEnum.App : MismatchedVersionsEnum.Data;
  }

  /**
   * Whether the server's app or data version differs from the one this tab loaded with.
   * The latest check's comparison is the best evidence, so it's used when the check got one.
   * When that request failed, timed out, or was skipped, earlier checks are the only evidence:
   * the version may already be confirmed stale, or a mismatch may be waiting out its
   * confirmation delay.
   */
  private differsFromLoadedVersion(
    trackedVersion: TrackedVersion,
    latestComparisons: VersionComparisons,
  ): boolean {
    const latestComparison = latestComparisons[trackedVersion];
    if (latestComparison) {
      return latestComparison.isMismatch;
    }
    const isAlreadyConfirmedStale = this.stale[trackedVersion]();
    const isAwaitingConfirmation = this.pendingMismatches[trackedVersion] !== null;
    return isAlreadyConfirmedStale || isAwaitingConfirmation;
  }

  private cancelConfirmation(trackedVersion: TrackedVersion) {
    this.pendingMismatches[trackedVersion] = null;
    this.confirmationSubscriptions[trackedVersion]?.unsubscribe();
    this.confirmationSubscriptions[trackedVersion] = null;
  }

  private getClientVersionLabel(trackedVersion: TrackedVersion): string | null {
    if (trackedVersion === 'app') {
      return this.clientVersion ? this.versionService.getSiteVersion(this.clientVersion) : null;
    }
    const loadedDataVersion = this.versionService.getLoadedDataVersion();
    return loadedDataVersion ? this.versionService.formatDataVersion(loadedDataVersion) : null;
  }

  private isTabVisible(): boolean {
    return this.document.visibilityState === 'visible';
  }

  /** `navigator.onLine` is only reliable when false, so anything else is treated as online. */
  private isOnline(): boolean {
    return this.document.defaultView?.navigator.onLine !== false;
  }
}

function isAppVersion(body: unknown): body is AppVersion {
  if (typeof body !== 'object' || body === null) {
    return false;
  }
  const { appVersion, commitSha } = body as Record<string, unknown>;
  return typeof appVersion === 'string' && typeof commitSha === 'string';
}

function isDataVersion(body: unknown): body is DataVersion {
  if (typeof body !== 'object' || body === null) {
    return false;
  }
  const { data_file: dataFile, data_version: dataVersion } = body as Record<string, unknown>;
  return typeof dataFile === 'string' && typeof dataVersion === 'string';
}

function isKnownDataVersion({ data_file: dataFile, data_version: dataVersion }: DataVersion) {
  return Boolean(dataFile) && Boolean(dataVersion);
}

function getFailureReason(error: HttpErrorResponse): VersionCheckFailureReason {
  if (error.status === NO_RESPONSE_STATUS) {
    return VersionCheckFailureReasonEnum.Network;
  }
  // A 2xx error means the body failed to parse as JSON, e.g. an HTML page from a misrouted request
  if (error.status >= HttpStatusCode.Ok && error.status < HttpStatusCode.MultipleChoices) {
    return VersionCheckFailureReasonEnum.InvalidResponse;
  }
  if (error.status === HttpStatusCode.NotFound) {
    return VersionCheckFailureReasonEnum.NotFound;
  }
  if (error.status >= HttpStatusCode.InternalServerError) {
    return VersionCheckFailureReasonEnum.Server;
  }
  return VersionCheckFailureReasonEnum.UnexpectedStatus;
}
