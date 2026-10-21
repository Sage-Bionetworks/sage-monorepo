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
  APP_UPDATE_CONFIRMATION_RETRY_LIMIT,
  APP_UPDATE_CONFIRM_DELAY_MS,
  APP_UPDATE_MIN_CHECK_INTERVAL_MS,
  APP_UPDATE_POLL_INTERVAL_MS,
  APP_VERSION_CONFIRMED_MISMATCHES_METRIC,
  APP_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
  APP_VERSION_PATH,
  APP_VERSION_UNCONFIRMED_MISMATCHES_METRIC,
  CHUNK_LOAD_ERROR_MESSAGE,
  CHUNK_LOAD_FAILURES_METRIC,
  DATA_UPDATE_CONFIRM_DELAY_MS,
  DATA_VERSION_CONFIRMED_MISMATCHES_METRIC,
  DATA_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
  DATA_VERSION_UNCONFIRMED_MISMATCHES_METRIC,
  HTTP_RESPONSE_STATUS_CODE_ATTRIBUTE,
  STALE_CHUNK_PAGE_LOADED_AT_KEY,
  STALE_CHUNK_PAGE_LOADS_METRIC,
  STALE_CHUNK_PAGE_LOAD_GUARD_MS,
  STALE_CHUNK_PAGE_LOAD_PENDING_KEY,
  VERSION_CHECK_FAILURES_METRIC,
  VERSION_CHECK_TIMEOUT_MS,
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
  tap,
  timeout,
  TimeoutError,
  timer,
} from 'rxjs';
import { ErrorOverlayService } from './error-overlay.service';
import { LoggerService } from './logger.service';
import { PlatformService } from './platform.service';
import { DataVersion, isKnownDataVersion, VersionService } from './version.service';

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

/** Loads a URL as a new page, replacing the running app. */
export const PAGE_LOADER = new InjectionToken<(url: string) => void>('PAGE_LOADER', {
  providedIn: 'root',
  factory: () => {
    const document = inject(DOCUMENT);
    return (url) => document.location.assign(url);
  },
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
  confirmedMismatchMetric: string;
  unconfirmedMismatchMetric: string;
  misconfiguredWarning: string;
  checkFailedMessage: string;
}

const TRACKED_VERSION_CONFIGS: Record<TrackedVersion, TrackedVersionConfig> = {
  app: {
    endpoint: VersionCheckEndpointEnum.AppVersion,
    confirmDelayMs: APP_UPDATE_CONFIRM_DELAY_MS,
    confirmationTrigger: AppUpdateCheckTriggerEnum.AppVersionMismatchConfirmation,
    confirmedMismatchMetric: APP_VERSION_CONFIRMED_MISMATCHES_METRIC,
    unconfirmedMismatchMetric: APP_VERSION_UNCONFIRMED_MISMATCHES_METRIC,
    misconfiguredWarning: APP_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
    checkFailedMessage: 'App version check failed',
  },
  data: {
    endpoint: VersionCheckEndpointEnum.DataVersion,
    confirmDelayMs: DATA_UPDATE_CONFIRM_DELAY_MS,
    confirmationTrigger: AppUpdateCheckTriggerEnum.DataVersionMismatchConfirmation,
    confirmedMismatchMetric: DATA_VERSION_CONFIRMED_MISMATCHES_METRIC,
    unconfirmedMismatchMetric: DATA_VERSION_UNCONFIRMED_MISMATCHES_METRIC,
    misconfiguredWarning: DATA_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
    checkFailedMessage: 'Data version check failed',
  },
};

/** A version mismatch that has not yet outlasted its confirmation delay. */
interface PendingVersionMismatch {
  serverVersionKey: string;
  serverVersionLabel: string;
  trigger: AppUpdateCheckTrigger;
  detectedAt: number;
}

/**
 * The result of comparing one version with the one the tab loaded with, or null when the version
 * is unknown because the check was skipped or failed. `serverVersionKey` identifies the server's
 * exact version and is what tells two versions apart. `serverVersionLabel` is the same version as
 * the footer and Sentry metrics show it, which drops details such as an app version's `-rcN`
 * suffix, so it is for display only.
 */
type VersionComparison = {
  isMismatch: boolean;
  serverVersionKey: string;
  serverVersionLabel: string;
} | null;

type VersionComparisons = Record<TrackedVersion, VersionComparison>;

/**
 * Detects that this tab is out of date, then blocks the page with an overlay whose only action is
 * to reload. A tab can fall behind in two independent ways, and every check compares both.
 *
 * App version mismatch: the code the tab runs does not match what the app server's
 * `/app-version` reports. During a rolling deploy, the load balancer sends requests to old and new
 * app tasks for a few minutes, so a single check reports whichever task answered. Prompting then
 * would do more harm than good: a tab could reload onto an old task and be prompted again, and a
 * tab already running the new code could be told to reload. So a mismatch must still be there
 * after `APP_UPDATE_CONFIRM_DELAY_MS`, which outlasts the cutover; by then every task serves the
 * same version, and a reload reliably gets it. A match in between means a task still serves this
 * tab's version, so the mismatch is dropped. The exception is a lazy-loaded chunk that fails to
 * load on a mismatched version: the tab can no longer navigate, so the overlay shows right away.
 *
 * Data version mismatch: the data release the tab shows does not match what the API's data
 * version endpoint reports. A data release switches over in one step, but each API task only
 * notices on its own once-a-minute check, then clears its caches, and clears them again a minute
 * later because a slow uncached query that read the previous release just before the switch could
 * start caching after the first cache clear operation. Until both clears have run, a reload could
 * still be served the previous release's cached data. So a mismatch must still be there after
 * `DATA_UPDATE_CONFIRM_DELAY_MS`, which outlasts both clears, and a reload then gets the new data.
 * A match in between means the data was rolled back, so the mismatch is dropped.
 *
 * For mismatches on both: any difference counts, not only a newer version. If a deployment fails,
 * the rollback moves the server back to an earlier version and tabs loaded from the rolled-back
 * version must reload too. When both versions differ, the app update notice is shown, since the
 * reload fixes both.
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
  private readonly loadPage = inject(PAGE_LOADER);

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
  private inFlightCheck$: Observable<VersionComparisons> | null = null;
  private readonly requestedChecks = new Subject<AppUpdateCheckTrigger>();

  /**
   * Starts checking for a newer deploy whenever the tab becomes visible again, whenever the
   * browser reconnects, periodically while the tab stays visible, and right away when a route's
   * lazy-loaded chunk fails to load, which shows either the update notice or a reload prompt.
   * With a `dataVersionUrl`, each check also compares the data version against the one the tab
   * loaded with (see `VersionService.getLoadedDataVersion`). Does nothing during SSR, when checks
   * are disabled, or if already started.
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
    this.recordStaleChunkPageLoad(browserWindow);

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
        filter(
          (event): event is NavigationError =>
            event instanceof NavigationError && isChunkLoadError(event.error),
        ),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((event) => this.handleChunkLoadError(event.url));
  }

  /**
   * Handles a route whose lazy-loaded chunk failed to download, which leaves the page without that
   * route's content until the tab reloads. `targetUrl` is the page the user was navigating to.
   *
   * The app version is checked first. When the server runs a different version, a deploy most
   * likely removed the chunk, so the tab loads `targetUrl` as a new page, which gets the current
   * build and lands the user where they meant to go. Reloading would land them back on the page
   * they were leaving, since the router only updates the URL once a navigation succeeds. If the
   * tab already did this within `STALE_CHUNK_PAGE_LOAD_GUARD_MS`, it keeps the update notice the
   * check showed instead.
   *
   * In every other case (the version matches, the browser is offline, or the check failed), the
   * download most likely failed on the network, so the error overlay asks the user to reload.
   *
   * Each failure is counted once in a Sentry metric, labeled with the check's outcome.
   */
  private async handleChunkLoadError(targetUrl: string) {
    const outcome = await this.getAppVersionCheckOutcome(AppUpdateCheckTriggerEnum.ChunkLoadError);
    Sentry.metrics.count(CHUNK_LOAD_FAILURES_METRIC, 1, { attributes: { outcome } });
    if (outcome === AppVersionCheckOutcomeEnum.Stale) {
      this.loadPageForStaleChunk(targetUrl);
    } else {
      this.errorOverlayService.showError(CHUNK_LOAD_ERROR_MESSAGE);
    }
  }

  /**
   * Loads `targetUrl` as a new page, at most once per `STALE_CHUNK_PAGE_LOAD_GUARD_MS`. Without
   * `sessionStorage` (e.g. blocked by privacy settings), the guard cannot be kept, so no page is
   * loaded and the update notice stays.
   */
  private loadPageForStaleChunk(targetUrl: string) {
    try {
      const sessionStorage = this.document.defaultView?.sessionStorage;
      if (!sessionStorage) {
        return;
      }
      const pageLoadedAt = Number(sessionStorage.getItem(STALE_CHUNK_PAGE_LOADED_AT_KEY));
      if (Date.now() - pageLoadedAt <= STALE_CHUNK_PAGE_LOAD_GUARD_MS) {
        return;
      }
      sessionStorage.setItem(STALE_CHUNK_PAGE_LOADED_AT_KEY, String(Date.now()));
      // Counted by the next page, since a metric recorded now could be lost when this page unloads
      sessionStorage.setItem(STALE_CHUNK_PAGE_LOAD_PENDING_KEY, 'true');
    } catch {
      return;
    }
    this.loadPage(targetUrl);
  }

  /**
   * Counts a page load `loadPageForStaleChunk` made before this page loaded. The pending flag is
   * cleared here, so each page load is counted once.
   */
  private recordStaleChunkPageLoad(browserWindow: Window) {
    try {
      if (browserWindow.sessionStorage.getItem(STALE_CHUNK_PAGE_LOAD_PENDING_KEY) === null) {
        return;
      }
      browserWindow.sessionStorage.removeItem(STALE_CHUNK_PAGE_LOAD_PENDING_KEY);
    } catch {
      return;
    }
    Sentry.metrics.count(STALE_CHUNK_PAGE_LOADS_METRIC, 1);
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
   * without waiting out the confirmation delay (see `getAppVersionCheckOutcome`). Resolves false
   * before `start` and while offline, since the version is then unknown.
   */
  isAppStale(trigger: AppUpdateCheckTrigger): Promise<boolean> {
    if (!this.clientVersion) {
      return Promise.resolve(false);
    }
    return this.getAppVersionCheckOutcome(trigger).then(
      (outcome) => outcome === AppVersionCheckOutcomeEnum.Stale,
    );
  }

  /**
   * Checks the app version right away. For a chunk-load check, this method also shows the overlay
   * on a mismatch without waiting out the confirmation delay: the tab cannot navigate until it
   * reloads, so waiting would only leave it broken. It does so on its own subscription to the
   * check, as the result arrives, whether this call started the check or joined one another
   * trigger had already started.
   */
  private async getAppVersionCheckOutcome(
    trigger: AppUpdateCheckTrigger,
  ): Promise<AppVersionCheckOutcome> {
    if (this.stale.app()) {
      return AppVersionCheckOutcomeEnum.Stale;
    }
    if (!this.isOnline()) {
      return AppVersionCheckOutcomeEnum.Offline;
    }
    const { app: appComparison } = await firstValueFrom(
      this.check$(trigger).pipe(
        tap((comparisons) => {
          if (trigger === AppUpdateCheckTriggerEnum.ChunkLoadError && comparisons.app?.isMismatch) {
            this.handleConfirmedStaleVersion(
              'app',
              comparisons.app.serverVersionLabel,
              trigger,
              comparisons,
            );
          }
        }),
      ),
    );
    if (!appComparison) {
      return AppVersionCheckOutcomeEnum.CheckFailed;
    }
    return appComparison.isMismatch
      ? AppVersionCheckOutcomeEnum.Stale
      : AppVersionCheckOutcomeEnum.Current;
  }

  /**
   * Reuses a check already in progress, so concurrent triggers send one request per endpoint. Both
   * comparisons are handled together once both responses are in, so a tab confirmed stale is
   * reported with every version that differed. Emits both comparisons; each is null when its
   * version could not be compared. A caller that joins a check another trigger started gets the
   * same result, so anything specific to its own trigger is handled after the check resolves.
   */
  private check$(trigger: AppUpdateCheckTrigger): Observable<VersionComparisons> {
    if (!this.inFlightCheck$) {
      this.lastCheckedAt = Date.now();
      this.inFlightCheck$ = forkJoin({
        app: this.checkAppVersion$(trigger),
        data: this.checkDataVersion$(trigger),
      }).pipe(
        map((comparisons) => {
          this.handleComparisons(comparisons, trigger);
          return comparisons;
        }),
        finalize(() => {
          this.inFlightCheck$ = null;
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
  private handleComparisons(comparisons: VersionComparisons, trigger: AppUpdateCheckTrigger) {
    for (const trackedVersion of ['data', 'app'] as const) {
      const comparison = comparisons[trackedVersion];
      if (!comparison) {
        continue;
      }
      if (comparison.isMismatch) {
        this.handleVersionMismatch(
          trackedVersion,
          comparison.serverVersionKey,
          comparison.serverVersionLabel,
          trigger,
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
      key: ({ appVersion, commitSha }) => JSON.stringify([appVersion, commitSha]),
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
      key: ({ data_file: dataFile, data_version: dataVersion }) =>
        JSON.stringify([dataFile, dataVersion]),
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
      key: (version: Version) => string;
      label: (version: Version) => string;
    },
  ): Observable<VersionComparison> {
    return this.http.get<unknown>(url).pipe(
      timeout(VERSION_CHECK_TIMEOUT_MS),
      map((body) => {
        if (!comparison.isVersion(body)) {
          this.recordCheckFailure(
            trackedVersion,
            VersionCheckFailureReasonEnum.InvalidResponse,
            HttpStatusCode.Ok,
            trigger,
          );
          return null;
        }
        return {
          isMismatch: comparison.isMismatch(body),
          serverVersionKey: comparison.key(body),
          serverVersionLabel: comparison.label(body),
        };
      }),
      catchError((error: HttpErrorResponse | TimeoutError) => {
        if (error instanceof TimeoutError) {
          this.recordCheckFailure(
            trackedVersion,
            VersionCheckFailureReasonEnum.Timeout,
            NO_RESPONSE_STATUS,
            trigger,
          );
        } else {
          this.recordCheckFailure(trackedVersion, getFailureReason(error), error.status, trigger);
        }
        return of(null);
      }),
    );
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
    // A request that got no response has no HTTP status to report; `reason` says why it got none
    const httpStatusAttribute =
      status === NO_RESPONSE_STATUS ? {} : { [HTTP_RESPONSE_STATUS_CODE_ATTRIBUTE]: status };
    const failureDetails = { endpoint, reason, ...httpStatusAttribute, trigger };
    Sentry.metrics.count(VERSION_CHECK_FAILURES_METRIC, 1, { attributes: failureDetails });
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
   * release a check sees becomes it and counts as a match. Both the data file and version number
   * are compared. An empty value on either side leaves the release unknown, so it never counts as a
   * mismatch, and is never kept as the loaded release.
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
   * Called when a check finds the server's version differs from the one this tab loaded with.
   * This method decides whether the difference has lasted long enough to show the overlay:
   * - A newly seen version is recorded as pending, and a re-check is scheduled for when its
   *   confirmation delay ends. App and data versions each have their own delay and pending state.
   * - The same version still there after the full delay means the tab is out of date, so the
   *   overlay is shown.
   * - A different version seen while one is pending (two releases or deploys back to back)
   *   restarts the delay, so the newer version always gets its full delay.
   *
   * A chunk that failed to load skips the delay, but that is handled by `getAppVersionCheckOutcome`
   * once the check resolves, not here.
   */
  private handleVersionMismatch(
    trackedVersion: TrackedVersion,
    serverVersionKey: string,
    serverVersionLabel: string,
    trigger: AppUpdateCheckTrigger,
    comparisons: VersionComparisons,
  ) {
    const pendingMismatch = this.pendingMismatches[trackedVersion];
    // Compared by key, not label, so that versions the label displays the same way, such as
    // `4.0.0-rc4` and `4.0.0-rc5` without a commit SHA, still count as different versions.
    if (!pendingMismatch || pendingMismatch.serverVersionKey !== serverVersionKey) {
      // Only one timer is kept per tracked version. This stops the earlier version's timer before
      // `scheduleConfirmation` starts the new one. Otherwise the earlier timer would keep running
      // with nothing left to stop it, and send an extra check later.
      this.cancelConfirmation(trackedVersion);
      this.pendingMismatches[trackedVersion] = {
        serverVersionKey,
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
      // Reports the trigger that first found the mismatch, not the confirmation check that ended
      // the delay, so the metric shows what made the tab notice the new version.
      this.handleConfirmedStaleVersion(
        trackedVersion,
        serverVersionLabel,
        pendingMismatch.trigger,
        comparisons,
      );
    }
  }

  /**
   * Re-checks the server once a pending mismatch's delay ends. This method decides whether to retry
   * from the check's result rather than from how it was started: a confirmation that joins a check
   * another trigger already started still retries if that shared check could not compare this
   * version (the request failed or timed out). Retries wait the minimum interval and stop after
   * `APP_UPDATE_CONFIRMATION_RETRY_LIMIT` attempts, after which the background checks take over.
   *
   * While the tab is hidden or offline, no request is sent. Returning to the tab or reconnecting
   * runs a check of its own, and since the delay has already passed, that check confirms the
   * mismatch.
   */
  private scheduleConfirmation(
    trackedVersion: TrackedVersion,
    delayMs: number,
    failedAttempts = 0,
  ) {
    // Subscribed outside the Angular zone for the same reason as the background checks.
    this.confirmationSubscriptions[trackedVersion] = this.ngZone.runOutsideAngular(() =>
      timer(delayMs)
        .pipe(
          filter(() => this.isTabVisible() && this.isOnline()),
          switchMap(() => this.check$(TRACKED_VERSION_CONFIGS[trackedVersion].confirmationTrigger)),
          takeUntilDestroyed(this.destroyRef),
        )
        .subscribe((comparisons) => {
          const couldNotCompare = comparisons[trackedVersion] === null;
          const nextAttempt = failedAttempts + 1;
          if (
            couldNotCompare &&
            this.pendingMismatches[trackedVersion] &&
            nextAttempt < APP_UPDATE_CONFIRMATION_RETRY_LIMIT
          ) {
            this.scheduleConfirmation(
              trackedVersion,
              APP_UPDATE_MIN_CHECK_INTERVAL_MS,
              nextAttempt,
            );
          }
        }),
    );
  }

  private handleVersionMatch(trackedVersion: TrackedVersion) {
    const pendingMismatch = this.pendingMismatches[trackedVersion];
    const clientVersionLabel = this.getClientVersionLabel(trackedVersion);
    if (!pendingMismatch || !clientVersionLabel) {
      return;
    }
    Sentry.metrics.count(TRACKED_VERSION_CONFIGS[trackedVersion].unconfirmedMismatchMetric, 1, {
      attributes: {
        client_version: clientVersionLabel,
        server_version: pendingMismatch.serverVersionLabel,
        trigger: pendingMismatch.trigger,
      },
    });
    this.cancelConfirmation(trackedVersion);
  }

  /**
   * Called once a mismatch has lasted its full delay and the tab is out of date. This method
   * shows the app or data update overlay, records the confirmed mismatch metric, and stops further
   * background checks, since the reload the overlay asks for fixes both versions. Runs at most once
   * per version.
   *
   * When the data version is the one out of date, a pending app mismatch keeps its timer. With
   * background checks stopped, that timer is the only way a deploy already under way can still be
   * confirmed. Confirming it switches the overlay to the app update message, which takes
   * precedence, and records the app version's confirmed mismatch metric, so the tab is counted as
   * missing both the deploy and the data release. The reload fixes both either way; the timer only
   * keeps the message and metrics accurate.
   */
  private handleConfirmedStaleVersion(
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
    Sentry.metrics.count(TRACKED_VERSION_CONFIGS[trackedVersion].confirmedMismatchMetric, 1, {
      attributes: {
        client_version: clientVersionLabel,
        server_version: serverVersionLabel,
        trigger,
        mismatched_versions: mismatchedVersions,
      },
    });
  }

  /**
   * Works out what the confirmed mismatch metric reports (`app`, `data`, or `app-and-data`) when
   * one of the two versions has just been confirmed stale. That version differs by definition. The
   * question is whether the other one differs too, so that a tab that missed both a deploy and a
   * data release is reported as such.
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
