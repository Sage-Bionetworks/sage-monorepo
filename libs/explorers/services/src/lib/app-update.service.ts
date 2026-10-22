import { DOCUMENT } from '@angular/common';
import { HttpBackend, HttpClient, HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { DestroyRef, inject, Injectable, InjectionToken, isDevMode, NgZone } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NavigationError, Router } from '@angular/router';
import { AppVersion } from '@sagebionetworks/explorers/config';
import {
  APP_UPDATE_CONFIRMATION_RETRY_LIMIT,
  APP_UPDATE_CONFIRM_DELAY_MS,
  APP_UPDATE_MIN_CHECK_INTERVAL_MS,
  APP_UPDATE_POLL_INTERVAL_MS,
  APP_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
  APP_VERSION_PATH,
  CHUNK_LOAD_ERROR_MESSAGE,
  CHUNK_LOAD_FAILURES_METRIC,
  DATA_UPDATE_CONFIRM_DELAY_MS,
  DATA_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
  HTTP_RESPONSE_STATUS_CODE_ATTRIBUTE,
  STALE_CHUNK_PAGE_LOADED_AT_KEY,
  STALE_CHUNK_PAGE_LOADS_METRIC,
  STALE_CHUNK_PAGE_LOAD_GUARD_MS,
  STALE_CHUNK_PAGE_LOAD_PENDING_KEY,
  VERSION_CHECK_FAILURES_METRIC,
  VERSION_CHECK_TIMEOUT_MS,
  VERSION_CONFIRMED_MISMATCHES_METRIC,
  VERSION_UNCONFIRMED_MISMATCHES_METRIC,
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
  HttpError: 'http-error',
  Poll: 'poll',
  Reconnect: 'reconnect',
  VersionMismatchConfirmation: 'version-mismatch-confirmation',
  Visibility: 'visibility',
} as const;
export type AppUpdateCheckTrigger =
  (typeof AppUpdateCheckTriggerEnum)[keyof typeof AppUpdateCheckTriggerEnum];

/**
 * Which versions differ from the ones the tab loaded with. Reported as a Sentry metric attribute,
 * so tabs that missed both a deploy and a data release are counted as such rather than as either
 * one alone.
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
  misconfiguredWarning: string;
  checkFailedMessage: string;
}

const TRACKED_VERSION_CONFIGS: Record<TrackedVersion, TrackedVersionConfig> = {
  app: {
    endpoint: VersionCheckEndpointEnum.AppVersion,
    confirmDelayMs: APP_UPDATE_CONFIRM_DELAY_MS,
    misconfiguredWarning: APP_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
    checkFailedMessage: 'App version check failed',
  },
  data: {
    endpoint: VersionCheckEndpointEnum.DataVersion,
    confirmDelayMs: DATA_UPDATE_CONFIRM_DELAY_MS,
    misconfiguredWarning: DATA_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
    checkFailedMessage: 'Data version check failed',
  },
};

/** A server version that differs from the one this tab loaded with. */
interface ServerVersionMismatch {
  serverVersionKey: string;
  serverVersionLabel: string;
}

/**
 * The server versions that differ from the ones this tab loaded with: the app version, the data
 * version, or both. A version that matches has no entry.
 */
type VersionMismatches = Partial<Record<TrackedVersion, ServerVersionMismatch>>;

/** Version mismatches that have not yet outlasted their confirmation delay. */
interface PendingVersionMismatches {
  mismatches: VersionMismatches;
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
 * Both versions are confirmed together. A check records which versions differ, and that set has
 * to stay the same for the longest delay among them: data alone waits its own delay, but app and
 * data together wait the app's. A different set (another deploy or release, or a version that
 * matches again) starts the wait over. Confirming shows one overlay and stops every check, since
 * the reload it asks for fixes both versions. When both differ, the app update notice is shown.
 *
 * Any difference counts, not only a newer version. If a deployment fails, the rollback moves the
 * server back to an earlier version and tabs loaded from the rolled-back version must reload too.
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

  private clientVersion: AppVersion | null = null;
  private dataVersionUrl: string | null = null;
  private siteName = '';
  private lastCheckedAt = 0;
  private readonly reportedMisconfigurations = new Set<VersionCheckEndpoint>();
  private backgroundVersionCheckSubscription: Subscription | null = null;
  private pendingMismatches: PendingVersionMismatches | null = null;
  private confirmationSubscription: Subscription | null = null;
  /** The mismatches that were confirmed, once the tab is known to be out of date. */
  private confirmedMismatches: VersionMismatches | null = null;
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
    if (this.confirmedMismatches?.app) {
      return AppVersionCheckOutcomeEnum.Stale;
    }
    if (!this.isOnline()) {
      return AppVersionCheckOutcomeEnum.Offline;
    }
    const { app: appComparison } = await firstValueFrom(
      this.check$(trigger).pipe(
        tap((comparisons) => {
          const isChunkLoadError = trigger === AppUpdateCheckTriggerEnum.ChunkLoadError;
          // The check just recorded what differs, so whatever is pending includes the app version
          if (isChunkLoadError && comparisons.app?.isMismatch && this.pendingMismatches) {
            const { compared, unverified } = splitByCompared(
              this.pendingMismatches.mismatches,
              comparisons,
            );
            this.handleConfirmedMismatches(compared, trigger, unverified);
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
   * comparisons are handled together once both responses are in. Emits both comparisons; each is
   * null when its version could not be compared. A caller that joins a check another trigger
   * started gets the same result, so anything specific to its own trigger is handled after the
   * check resolves.
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
   * Called with every check's result, and decides whether the tab is out of date:
   * - Nothing differs: any pending mismatch is dropped and counted as unconfirmed.
   * - What differs is new, or changed since the last check: the wait starts over for it.
   * - The same versions still differ after the wait: the tab is out of date.
   *
   * A version the check could not compare keeps its earlier state, since the check learned nothing
   * about it. Once the wait has passed, the versions this check compared are confirmed, and a
   * pending one it could not compare is reported as unverified. A version whose endpoint keeps
   * failing therefore never holds back the other one.
   */
  private handleComparisons(comparisons: VersionComparisons, trigger: AppUpdateCheckTrigger) {
    if (this.confirmedMismatches) {
      return;
    }
    const pendingMismatches = this.pendingMismatches;
    const mismatches = this.getMismatches(comparisons, pendingMismatches?.mismatches ?? {});
    if (!hasMismatches(mismatches)) {
      this.handleVersionsMatch();
      return;
    }
    // Compared by key, not label, so that versions the label displays the same way, such as
    // `4.0.0-rc4` and `4.0.0-rc5` without a commit SHA, still count as different versions.
    if (
      !pendingMismatches ||
      getMismatchesKey(mismatches) !== getMismatchesKey(pendingMismatches.mismatches)
    ) {
      this.startConfirmation(mismatches, trigger);
      return;
    }
    const hasWaitedLongEnough =
      Date.now() - pendingMismatches.detectedAt >= getConfirmDelayMs(mismatches);
    const { compared, unverified } = splitByCompared(mismatches, comparisons);
    if (hasWaitedLongEnough && hasMismatches(compared)) {
      // Reports the trigger that first found the mismatch, not the confirmation check that ended
      // the delay, so the metric shows what made the tab notice the new version.
      this.handleConfirmedMismatches(compared, pendingMismatches.trigger, unverified);
    }
  }

  /**
   * Works out which versions differ from this check's comparisons. A version the check could not
   * compare is taken from `pendingMismatches`, the previous result.
   */
  private getMismatches(
    comparisons: VersionComparisons,
    pendingMismatches: VersionMismatches,
  ): VersionMismatches {
    const mismatches: VersionMismatches = {};
    for (const trackedVersion of TRACKED_VERSIONS) {
      const comparison = comparisons[trackedVersion];
      if (!comparison) {
        if (pendingMismatches[trackedVersion]) {
          mismatches[trackedVersion] = pendingMismatches[trackedVersion];
        }
      } else if (comparison.isMismatch) {
        const { serverVersionKey, serverVersionLabel } = comparison;
        mismatches[trackedVersion] = { serverVersionKey, serverVersionLabel };
      }
    }
    return mismatches;
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
    if (!this.dataVersionUrl || this.confirmedMismatches) {
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
   * Records `mismatches` as pending, and schedules a check for when the longest of their delays
   * ends. Any earlier wait is cancelled, so only one timer is ever running.
   */
  private startConfirmation(mismatches: VersionMismatches, trigger: AppUpdateCheckTrigger) {
    this.cancelConfirmation();
    this.pendingMismatches = { mismatches, trigger, detectedAt: Date.now() };
    this.scheduleConfirmation(getConfirmDelayMs(mismatches));
  }

  /**
   * Re-checks the server once the pending mismatches' delay ends. This method decides whether to
   * retry from the check's result rather than from how it was started: a confirmation that joins a
   * check another trigger already started still retries if that shared check could not compare any
   * pending version (every request failed or timed out). Retries wait the minimum interval and stop
   * after `APP_UPDATE_CONFIRMATION_RETRY_LIMIT` attempts, after which the background checks take
   * over.
   *
   * While the tab is hidden or offline, no request is sent. Returning to the tab or reconnecting
   * runs a background check, and since the delay has already passed, that check confirms the
   * mismatch. The background checks keep running until a mismatch is confirmed, so that always
   * works.
   */
  private scheduleConfirmation(delayMs: number, failedAttempts = 0) {
    const scheduledFor = this.pendingMismatches;
    // Subscribed outside the Angular zone for the same reason as the background checks.
    this.confirmationSubscription = this.ngZone.runOutsideAngular(() =>
      timer(delayMs)
        .pipe(
          filter(() => this.isTabVisible() && this.isOnline()),
          switchMap(() => this.check$(AppUpdateCheckTriggerEnum.VersionMismatchConfirmation)),
          takeUntilDestroyed(this.destroyRef),
        )
        .subscribe((comparisons) => {
          // The check may have started a new wait with its own timer, or confirmed the mismatch
          if (this.pendingMismatches !== scheduledFor || !scheduledFor) {
            return;
          }
          const nextAttempt = failedAttempts + 1;
          const { compared } = splitByCompared(scheduledFor.mismatches, comparisons);
          if (!hasMismatches(compared) && nextAttempt < APP_UPDATE_CONFIRMATION_RETRY_LIMIT) {
            this.scheduleConfirmation(APP_UPDATE_MIN_CHECK_INTERVAL_MS, nextAttempt);
          }
        }),
    );
  }

  /** Drops a pending mismatch that went away before its delay ended, and counts it. */
  private handleVersionsMatch() {
    const pendingMismatches = this.pendingMismatches;
    if (!pendingMismatches) {
      return;
    }
    Sentry.metrics.count(VERSION_UNCONFIRMED_MISMATCHES_METRIC, 1, {
      attributes: this.getMismatchAttributes(
        pendingMismatches.mismatches,
        pendingMismatches.trigger,
      ),
    });
    this.cancelConfirmation();
  }

  /**
   * Called once mismatches have lasted their full delay, or right away for a chunk that failed to
   * load on a different app version. This method shows the update overlay (the app update notice
   * when the app version differs, since it takes precedence), records the confirmed mismatch
   * metric, and stops every further check, since the reload the overlay asks for fixes both
   * versions. Runs at most once.
   *
   * `mismatches` were compared again by the check that confirmed them. `unverifiedMismatches` were
   * pending, but that check could not compare them, so they are reported separately.
   */
  private handleConfirmedMismatches(
    mismatches: VersionMismatches,
    trigger: AppUpdateCheckTrigger,
    unverifiedMismatches: VersionMismatches = {},
  ) {
    if (this.confirmedMismatches) {
      return;
    }
    this.confirmedMismatches = mismatches;
    this.backgroundVersionCheckSubscription?.unsubscribe();
    this.cancelConfirmation();
    if (mismatches.app) {
      this.errorOverlayService.showAppUpdateAvailable(this.siteName);
    } else {
      this.errorOverlayService.showDataUpdateAvailable(this.siteName);
    }
    Sentry.metrics.count(VERSION_CONFIRMED_MISMATCHES_METRIC, 1, {
      attributes: this.getMismatchAttributes(mismatches, trigger, unverifiedMismatches),
    });
  }

  /**
   * The metric attributes for `mismatches`: which versions differ, and for each, the version the
   * tab loaded with and the one the server reports. `unverifiedMismatches` are listed in
   * `unverified_versions`, with their server version from the last check that compared them.
   */
  private getMismatchAttributes(
    mismatches: VersionMismatches,
    trigger: AppUpdateCheckTrigger,
    unverifiedMismatches: VersionMismatches = {},
  ): Record<string, string> {
    const attributes: Record<string, string> = {
      mismatched_versions: getMismatchedVersions(mismatches),
      trigger,
    };
    if (hasMismatches(unverifiedMismatches)) {
      attributes['unverified_versions'] = getMismatchedVersions(unverifiedMismatches);
    }
    const app = mismatches.app ?? unverifiedMismatches.app;
    if (app) {
      attributes['client_app_version'] = this.getClientVersionLabel('app');
      attributes['server_app_version'] = app.serverVersionLabel;
    }
    const data = mismatches.data ?? unverifiedMismatches.data;
    if (data) {
      attributes['client_data_version'] = this.getClientVersionLabel('data');
      attributes['server_data_version'] = data.serverVersionLabel;
    }
    return attributes;
  }

  private cancelConfirmation() {
    this.pendingMismatches = null;
    this.confirmationSubscription?.unsubscribe();
    this.confirmationSubscription = null;
  }

  /**
   * The version this tab loaded with, as the footer shows it. Empty only before `start`, or before
   * any data version loads, and a mismatch cannot be found before then.
   */
  private getClientVersionLabel(trackedVersion: TrackedVersion): string {
    if (trackedVersion === 'app') {
      return this.clientVersion ? this.versionService.getSiteVersion(this.clientVersion) : '';
    }
    const loadedDataVersion = this.versionService.getLoadedDataVersion();
    return loadedDataVersion ? this.versionService.formatDataVersion(loadedDataVersion) : '';
  }

  private isTabVisible(): boolean {
    return this.document.visibilityState === 'visible';
  }

  /** `navigator.onLine` is only reliable when false, so anything else is treated as online. */
  private isOnline(): boolean {
    return this.document.defaultView?.navigator.onLine !== false;
  }
}

const TRACKED_VERSIONS: readonly TrackedVersion[] = ['app', 'data'];

function hasMismatches(mismatches: VersionMismatches): boolean {
  return TRACKED_VERSIONS.some((trackedVersion) => mismatches[trackedVersion]);
}

/** Identifies the exact server versions in `mismatches`, so two results can be compared. */
function getMismatchesKey(mismatches: VersionMismatches): string {
  return JSON.stringify(
    TRACKED_VERSIONS.map((trackedVersion) => mismatches[trackedVersion]?.serverVersionKey ?? null),
  );
}

/** The longest confirmation delay among the versions in `mismatches`. */
function getConfirmDelayMs(mismatches: VersionMismatches): number {
  return Math.max(
    ...TRACKED_VERSIONS.filter((trackedVersion) => mismatches[trackedVersion]).map(
      (trackedVersion) => TRACKED_VERSION_CONFIGS[trackedVersion].confirmDelayMs,
    ),
  );
}

/**
 * Splits `mismatches` into the versions this check compared and the ones it could not, because
 * their request failed or timed out.
 */
function splitByCompared(
  mismatches: VersionMismatches,
  comparisons: VersionComparisons,
): { compared: VersionMismatches; unverified: VersionMismatches } {
  const compared: VersionMismatches = {};
  const unverified: VersionMismatches = {};
  for (const trackedVersion of TRACKED_VERSIONS) {
    const mismatch = mismatches[trackedVersion];
    if (!mismatch) {
      continue;
    }
    if (comparisons[trackedVersion]) {
      compared[trackedVersion] = mismatch;
    } else {
      unverified[trackedVersion] = mismatch;
    }
  }
  return { compared, unverified };
}

function getMismatchedVersions(mismatches: VersionMismatches): MismatchedVersions {
  if (mismatches.app && mismatches.data) {
    return MismatchedVersionsEnum.AppAndData;
  }
  return mismatches.app ? MismatchedVersionsEnum.App : MismatchedVersionsEnum.Data;
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
