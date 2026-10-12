import { DOCUMENT } from '@angular/common';
import { HttpBackend, HttpClient, HttpErrorResponse, HttpStatusCode } from '@angular/common/http';
import { DestroyRef, inject, Injectable, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NavigationError, Router } from '@angular/router';
import { APP_VERSION_PATH, AppVersion } from '@sagebionetworks/explorers/config';
import {
  APP_UPDATE_MIN_CHECK_INTERVAL_MS,
  APP_UPDATE_POLL_INTERVAL_MS,
  STALE_CLIENT_METRIC,
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

/**
 * Detects that this tab is running code from an earlier deploy by comparing the version it was
 * loaded with against the version the app server currently reports, then blocks the page with an
 * overlay whose only action is to reload.
 */
@Injectable({ providedIn: 'root' })
export class AppUpdateService {
  // Bypasses the app's HTTP interceptors, which would replace a failed response with a plain Error
  // and lose the status this service classifies failures by.
  private readonly http = new HttpClient(inject(HttpBackend));
  private readonly document = inject(DOCUMENT);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);
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
  private checkSubscription: Subscription | null = null;
  private inFlightCheck$: Observable<boolean> | null = null;
  private readonly requestedChecks = new Subject<AppUpdateCheckTrigger>();

  /**
   * Starts checking for a newer deploy whenever the tab becomes visible again, whenever the
   * browser reconnects, periodically while the tab stays visible, and right away when a route's
   * lazy-loaded chunk fails to load. Does nothing during SSR or if already started.
   */
  start(clientVersion: AppVersion, siteName: string) {
    const window = this.document.defaultView;
    if (this.platformService.isServer || !window || this.clientVersion) {
      return;
    }
    this.clientVersion = clientVersion;
    this.siteName = siteName;

    const visibility$ = fromEvent(this.document, 'visibilitychange').pipe(
      map(() => AppUpdateCheckTriggerEnum.Visibility),
    );
    const reconnect$ = fromEvent(window, 'online').pipe(
      map(() => AppUpdateCheckTriggerEnum.Reconnect),
    );
    const poll$ = interval(APP_UPDATE_POLL_INTERVAL_MS).pipe(
      map(() => AppUpdateCheckTriggerEnum.Poll),
    );

    this.checkSubscription = merge(visibility$, reconnect$, poll$, this.requestedChecks)
      .pipe(
        filter(() => this.isTabVisible() && this.isOnline()),
        filter(() => Date.now() - this.lastCheckedAt >= APP_UPDATE_MIN_CHECK_INTERVAL_MS),
        exhaustMap((trigger) => this.check$(trigger)),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe();

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
   * Checks right away, ignoring the minimum interval, and resolves whether the tab is stale. For
   * errors that a stale tab explains, such as a chunk that failed to load. Resolves false before
   * `start` and while offline, since staleness is then unknown.
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
      this.inFlightCheck$ = this.checkForUpdate$(trigger).pipe(
        finalize(() => {
          this.inFlightCheck$ = null;
        }),
        share(),
      );
    }
    return this.inFlightCheck$;
  }

  private checkForUpdate$(trigger: AppUpdateCheckTrigger): Observable<boolean> {
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
        const isStale = this.isFromEarlierDeploy(body);
        if (isStale) {
          this.handleStaleClient(body, trigger);
        }
        return isStale;
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
    const details = { reason, status, trigger };
    Sentry.metrics.count(VERSION_CHECK_FAILED_METRIC, 1, { attributes: details });
    if (MISCONFIGURATION_REASONS.has(reason) && !this.hasReportedMisconfiguration) {
      this.hasReportedMisconfiguration = true;
      this.logger.warn(VERSION_ENDPOINT_MISCONFIGURED_WARNING, details);
    } else {
      this.logger.log('App version check failed', details);
    }
  }

  /**
   * A version that is empty on either side is unknown (e.g. local development), so it never
   * counts as a mismatch. Commit SHA identifies the code exactly; app version is the fallback.
   */
  private isFromEarlierDeploy(serverVersion: AppVersion): boolean {
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

  private handleStaleClient(serverVersion: AppVersion, trigger: AppUpdateCheckTrigger) {
    if (this.stale() || !this.clientVersion) {
      return;
    }
    this.stale.set(true);
    this.checkSubscription?.unsubscribe();
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
