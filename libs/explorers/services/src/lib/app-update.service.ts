import { DOCUMENT } from '@angular/common';
import { HttpBackend, HttpClient, HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { DestroyRef, inject, Injectable, NgZone, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NavigationError, Router } from '@angular/router';
import { AppVersion } from '@sagebionetworks/explorers/config';
import {
  APP_UPDATE_CONFIRM_DELAY_MS,
  APP_UPDATE_MIN_CHECK_INTERVAL_MS,
  APP_UPDATE_POLL_INTERVAL_MS,
  APP_VERSION_PATH,
  STALE_CLIENT_METRIC,
  TRANSIENT_VERSION_MISMATCH_METRIC,
  VERSION_CHECK_FAILED_METRIC,
  VERSION_ENDPOINT_MISCONFIGURED_WARNING,
} from '@sagebionetworks/explorers/constants';
import { isChunkLoadError } from '@sagebionetworks/explorers/sentry';
import * as Sentry from '@sentry/angular';
import {
  catchError,
  exhaustMap,
  filter,
  finalize,
  firstValueFrom,
  fromEvent,
  interval,
  map,
  merge,
  Observable,
  of,
  share,
  Subject,
  Subscription,
  timer,
} from 'rxjs';
import { ErrorOverlayService } from './error-overlay.service';
import { LoggerService } from './logger.service';
import { PlatformService } from './platform.service';
import { VersionService } from './version.service';

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

/** Why a version check failed. These values are reported as a Sentry metric attribute. */
export const VersionCheckFailureReasonEnum = {
  /** A 2xx whose body is not an app version, i.e. requests are misrouted */
  InvalidResponse: 'invalid-response',
  /** No response at all, usually the user's network (captive portal, VPN) */
  Network: 'network',
  /** The server runs a build without the version endpoint, or requests are misrouted */
  NotFound: 'not-found',
  /** A 5xx, i.e. no healthy app task, which is brief during a deploy */
  Server: 'server',
  /** Any other non-2xx response */
  UnexpectedStatus: 'unexpected-status',
} as const;
export type VersionCheckFailureReason =
  (typeof VersionCheckFailureReasonEnum)[keyof typeof VersionCheckFailureReasonEnum];

/** The status Angular reports when a request gets no response at all. */
export const NO_RESPONSE_STATUS = 0;

const MISCONFIGURATION_REASONS: ReadonlySet<VersionCheckFailureReason> = new Set([
  VersionCheckFailureReasonEnum.InvalidResponse,
  VersionCheckFailureReasonEnum.NotFound,
]);

/** A version mismatch that has not yet outlasted the confirmation delay. */
interface PendingVersionMismatch {
  serverVersion: AppVersion;
  trigger: AppUpdateCheckTrigger;
  detectedAt: number;
}

/**
 * Detects that this tab is running code from an earlier deploy by comparing the version it was
 * loaded with against the version the app server currently reports, then blocks the page with an
 * overlay whose only action is to reload.
 *
 * A mismatch must persist for `APP_UPDATE_CONFIRM_DELAY_MS` before the overlay is shown. During a
 * rolling deploy, old and new app tasks both answer the version endpoint, so a single mismatch can
 * come from a task that is about to be replaced. Staleness is judged by any difference rather than
 * by which version is newer, because a deploy rollback moves the server back to an earlier version
 * and tabs loaded from the rolled-back deploy must reload too.
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
  private readonly logger = inject(LoggerService);

  private readonly stale = signal(false);
  readonly isStale = this.stale.asReadonly();

  private clientVersion: AppVersion | null = null;
  private siteName = '';
  private lastCheckedAt = 0;
  private hasReportedMisconfiguration = false;
  private backgroundVersionCheckSubscription: Subscription | null = null;
  private pendingVersionMismatch: PendingVersionMismatch | null = null;
  private versionMismatchConfirmationSubscription: Subscription | null = null;
  private inFlightCheck$: Observable<boolean> | null = null;
  private readonly requestedChecks = new Subject<AppUpdateCheckTrigger>();

  /**
   * Starts checking for a newer deploy whenever the tab becomes visible again, whenever the
   * browser reconnects, periodically while the tab stays visible, and right away when a route's
   * lazy-loaded chunk fails to load. Does nothing during SSR or if already started.
   */
  start(clientVersion: AppVersion, siteName: string) {
    const browserWindow = this.document.defaultView;
    if (this.platformService.isServer || !browserWindow || this.clientVersion) {
      return;
    }
    this.clientVersion = clientVersion;
    this.siteName = siteName;

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
    // becoming stable, and hydration waits for stability before it finishes. The stale signal
    // still schedules change detection when it is set outside the zone.
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
      .subscribe(() => this.checkNow(AppUpdateCheckTriggerEnum.ChunkLoadError));
  }

  /**
   * Asks for a check when something hints the tab may be stale, such as a failed API request.
   * Subject to the same minimum interval as the background checks, so a burst of hints sends at
   * most one request.
   */
  requestCheck(trigger: AppUpdateCheckTrigger) {
    this.requestedChecks.next(trigger);
  }

  /**
   * Checks right away, ignoring the minimum interval, and resolves whether the server reports a
   * different version than this tab loaded with. Each build gives its lazy-loaded chunk files new
   * names, so while the server runs a different version, a chunk this tab requests may not exist
   * there. A true result means a failed chunk load is expected rather than a bug. Resolves false
   * before `start` and while offline, since the version is then unknown.
   */
  checkNow(trigger: AppUpdateCheckTrigger): Promise<boolean> {
    if (this.stale()) {
      return Promise.resolve(true);
    }
    if (!this.clientVersion || !this.isOnline()) {
      return Promise.resolve(false);
    }
    return firstValueFrom(this.check$(trigger));
  }

  /** Reuses a check already in progress, so concurrent triggers send one request. */
  private check$(trigger: AppUpdateCheckTrigger): Observable<boolean> {
    if (!this.inFlightCheck$) {
      this.inFlightCheck$ = this.fetchAndCompareServerVersion$(trigger).pipe(
        finalize(() => {
          this.inFlightCheck$ = null;
        }),
        share(),
      );
    }
    return this.inFlightCheck$;
  }

  /**
   * Fetches the version the server reports, compares it with the version this tab loaded with, and
   * hands the result to the mismatch, match, or failure handling. Emits whether the server reports
   * a different version; a failed check emits false, since the server's version is then unknown.
   */
  private fetchAndCompareServerVersion$(trigger: AppUpdateCheckTrigger): Observable<boolean> {
    this.lastCheckedAt = Date.now();
    return this.http.get<unknown>(APP_VERSION_PATH).pipe(
      map((body) => {
        if (!isAppVersion(body)) {
          this.recordCheckFailure(
            VersionCheckFailureReasonEnum.InvalidResponse,
            HttpStatusCode.Ok,
            trigger,
          );
          return false;
        }
        const isVersionMismatch = this.isVersionMismatch(body);
        if (isVersionMismatch) {
          this.handleVersionMismatch(body, trigger);
        } else {
          this.handleVersionMatch();
        }
        return isVersionMismatch;
      }),
      catchError((error: HttpErrorResponse) => {
        this.recordCheckFailure(getFailureReason(error), error.status, trigger);
        return of(false);
      }),
    );
  }

  /**
   * Every failure is counted, so the failure rate can be watched over time. Only failures that
   * point to a misconfiguration, and so will not resolve on their own, raise a Sentry event, once
   * per tab.
   */
  private recordCheckFailure(
    reason: VersionCheckFailureReason,
    status: number,
    trigger: AppUpdateCheckTrigger,
  ) {
    const failureDetails = { reason, status, trigger };
    Sentry.metrics.count(VERSION_CHECK_FAILED_METRIC, 1, { attributes: failureDetails });
    if (MISCONFIGURATION_REASONS.has(reason) && !this.hasReportedMisconfiguration) {
      this.hasReportedMisconfiguration = true;
      this.logger.warn(VERSION_ENDPOINT_MISCONFIGURED_WARNING, failureDetails);
    } else {
      this.logger.log('App version check failed', failureDetails);
    }
  }

  /**
   * A version that is empty on either side is unknown (e.g. local development), so it never
   * counts as a mismatch. Commit SHA identifies the code exactly; app version is the fallback.
   */
  private isVersionMismatch(serverVersion: AppVersion): boolean {
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
   * Shows the overlay once the servers stop serving this tab's version, whether a deploy replaced
   * it or a rollback removed it. While either is in progress, tasks of both versions answer for a
   * few minutes, so a single mismatch proves nothing. The first mismatch starts the confirmation
   * delay, and only a mismatch after the delay shows the overlay. A match before then clears it,
   * since a task still serves this tab's version. If that version really is going away, a later
   * mismatch starts the delay again.
   */
  private handleVersionMismatch(serverVersion: AppVersion, trigger: AppUpdateCheckTrigger) {
    if (!this.pendingVersionMismatch) {
      this.pendingVersionMismatch = { serverVersion, trigger, detectedAt: Date.now() };
      this.scheduleVersionMismatchConfirmation();
      return;
    }
    if (Date.now() - this.pendingVersionMismatch.detectedAt >= APP_UPDATE_CONFIRM_DELAY_MS) {
      this.handleStaleClient(serverVersion, this.pendingVersionMismatch.trigger);
    }
  }

  private scheduleVersionMismatchConfirmation() {
    // Subscribed outside the Angular zone for the same reason as the background checks.
    this.versionMismatchConfirmationSubscription = this.ngZone.runOutsideAngular(() =>
      timer(APP_UPDATE_CONFIRM_DELAY_MS)
        .pipe(
          exhaustMap(() => this.check$(AppUpdateCheckTriggerEnum.VersionMismatchConfirmation)),
          takeUntilDestroyed(this.destroyRef),
        )
        .subscribe(),
    );
  }

  private handleVersionMatch() {
    if (!this.pendingVersionMismatch || !this.clientVersion) {
      return;
    }
    Sentry.metrics.count(TRANSIENT_VERSION_MISMATCH_METRIC, 1, {
      attributes: {
        clientVersion: this.versionService.getSiteVersion(this.clientVersion),
        serverVersion: this.versionService.getSiteVersion(
          this.pendingVersionMismatch.serverVersion,
        ),
        trigger: this.pendingVersionMismatch.trigger,
      },
    });
    this.pendingVersionMismatch = null;
    this.versionMismatchConfirmationSubscription?.unsubscribe();
    this.versionMismatchConfirmationSubscription = null;
  }

  private handleStaleClient(serverVersion: AppVersion, trigger: AppUpdateCheckTrigger) {
    if (this.stale() || !this.clientVersion) {
      return;
    }
    this.stale.set(true);
    this.backgroundVersionCheckSubscription?.unsubscribe();
    this.versionMismatchConfirmationSubscription?.unsubscribe();
    this.errorOverlayService.showUpdateAvailable(this.siteName);
    Sentry.metrics.count(STALE_CLIENT_METRIC, 1, {
      attributes: {
        clientVersion: this.versionService.getSiteVersion(this.clientVersion),
        serverVersion: this.versionService.getSiteVersion(serverVersion),
        trigger,
      },
    });
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
