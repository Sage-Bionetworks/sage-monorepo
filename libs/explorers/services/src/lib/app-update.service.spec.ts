import {
  HttpInterceptorFn,
  HttpStatusCode,
  provideHttpClient,
  withInterceptors,
} from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter, Router, Routes } from '@angular/router';
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
  getAppUpdateMessage,
  getDataUpdateMessage,
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
import * as Sentry from '@sentry/angular';
import {
  APP_UPDATE_CHECKS_ENABLED,
  AppUpdateCheckTriggerEnum,
  AppUpdateService,
  AppVersionCheckOutcome,
  AppVersionCheckOutcomeEnum,
  MismatchedVersionsEnum,
  PAGE_LOADER,
  VersionCheckEndpointEnum,
  VersionCheckFailureReason,
  VersionCheckFailureReasonEnum,
} from './app-update.service';
import { ErrorOverlayService } from './error-overlay.service';
import { LoggerService } from './logger.service';
import { PlatformService } from './platform.service';
import { DataVersion, VersionService } from './version.service';
import { of } from 'rxjs';

jest.mock('@sentry/angular', () => ({
  metrics: { count: jest.fn() },
}));

const siteName = 'Model AD';
const loadedAppVersion: AppVersion = { appVersion: '4.0.0-rc3', commitSha: 'abc1234' };
const newerAppVersion: AppVersion = { appVersion: '4.0.0-rc4', commitSha: 'def5678' };
const unknownAppVersion: AppVersion = { appVersion: '', commitSha: '' };

const dataVersionUrl = 'https://modeladexplorer.org/api/v1/data-version';
const loadedDataVersion: DataVersion = { data_file: 'syn73774820', data_version: '191' };
const newerDataVersion: DataVersion = { data_file: 'syn73774820', data_version: '192' };
const laterDataVersion: DataVersion = { data_file: 'syn73774820', data_version: '193' };
const differentDataFile: DataVersion = { data_file: 'syn74000000', data_version: '191' };
const unknownDataVersion: DataVersion = { data_file: '', data_version: '' };

const missingChunkPath = 'genes';
const brokenRoutePath = 'models';
const routes: Routes = [
  {
    path: missingChunkPath,
    loadChildren: () =>
      Promise.reject(
        new TypeError(
          'Failed to fetch dynamically imported module: https://modeladexplorer.org/chunk-ABCD1234.js',
        ),
      ),
  },
  {
    path: brokenRoutePath,
    loadChildren: () => Promise.reject(new TypeError('Cannot read properties of undefined')),
  },
];

describe('AppUpdateService', () => {
  let visibilityState: DocumentVisibilityState;
  let isBrowserOnline: boolean;
  let httpMock: HttpTestingController;
  let errorOverlayService: ErrorOverlayService;
  let logger: { log: jest.Mock; warn: jest.Mock; error: jest.Mock };
  let appInterceptor: jest.Mock;
  let loadPage: jest.Mock;

  function setup({ isServer = false, checksEnabled = true } = {}): AppUpdateService {
    logger = { log: jest.fn(), warn: jest.fn(), error: jest.fn() };
    appInterceptor = jest.fn<ReturnType<HttpInterceptorFn>, Parameters<HttpInterceptorFn>>(
      (req, next) => next(req),
    );
    loadPage = jest.fn();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([appInterceptor])),
        provideHttpClientTesting(),
        provideRouter(routes),
        { provide: PlatformService, useValue: { isBrowser: !isServer, isServer } },
        { provide: LoggerService, useValue: { forSource: () => logger } },
        { provide: APP_UPDATE_CHECKS_ENABLED, useValue: checksEnabled },
        { provide: PAGE_LOADER, useValue: loadPage },
      ],
    });
    httpMock = TestBed.inject(HttpTestingController);
    errorOverlayService = TestBed.inject(ErrorOverlayService);
    return TestBed.inject(AppUpdateService);
  }

  function isShowingAppUpdateNotice(): boolean {
    return errorOverlayService.activeMessage() === getAppUpdateMessage(siteName);
  }

  function isShowingDataUpdateNotice(): boolean {
    return errorOverlayService.activeMessage() === getDataUpdateMessage(siteName);
  }

  /** Loads the footer's data version, which the tab's data version checks compare against. */
  function loadFooterDataVersion() {
    TestBed.inject(VersionService)
      .getDataVersion$({ getDataVersion: () => of(loadedDataVersion) })
      .subscribe();
  }

  function startWithDataChecks(appUpdateService: AppUpdateService) {
    loadFooterDataVersion();
    appUpdateService.start(loadedAppVersion, siteName, dataVersionUrl);
  }

  function respondWithServerDataVersion(serverDataVersion: DataVersion) {
    httpMock.expectOne(dataVersionUrl).flush(serverDataVersion);
  }

  function respondToCheck(serverAppVersion: AppVersion, serverDataVersion: DataVersion) {
    respondWithServerAppVersion(serverAppVersion);
    respondWithServerDataVersion(serverDataVersion);
  }

  function setTabVisibility(state: DocumentVisibilityState) {
    visibilityState = state;
    document.dispatchEvent(new Event('visibilitychange'));
  }

  function returnToTab() {
    setTabVisibility('hidden');
    setTabVisibility('visible');
  }

  function reconnect() {
    isBrowserOnline = true;
    window.dispatchEvent(new Event('online'));
  }

  async function navigateTo(path: string) {
    await TestBed.inject(Router)
      .navigateByUrl(`/${path}`)
      .catch(() => undefined);
  }

  function respondWithServerAppVersion(serverAppVersion: AppVersion) {
    httpMock.expectOne(APP_VERSION_PATH).flush(serverAppVersion);
  }

  function respondWithNetworkFailure() {
    httpMock.expectOne(APP_VERSION_PATH).error(new ProgressEvent('error'));
  }

  function respondWithDataVersionFailure() {
    httpMock.expectOne(dataVersionUrl).error(new ProgressEvent('error'));
  }

  /**
   * Advances time, answering the background polls along the way with the given versions. Steps are
   * shorter than the request timeout, so each poll is answered before it times out. The last step
   * is left unanswered, so the check due when the time is up stays for the caller.
   */
  function advanceAnsweringPolls(
    durationMs: number,
    serverAppVersion: AppVersion,
    serverDataVersion?: DataVersion,
  ) {
    const maxStepMs = VERSION_CHECK_TIMEOUT_MS / 2;
    for (let remainingMs = durationMs; remainingMs > 0; ) {
      const stepMs = Math.min(maxStepMs, remainingMs);
      jest.advanceTimersByTime(stepMs);
      remainingMs -= stepMs;
      if (remainingMs > 0) {
        httpMock.match(APP_VERSION_PATH).forEach((request) => request.flush(serverAppVersion));
        if (serverDataVersion) {
          httpMock.match(dataVersionUrl).forEach((request) => request.flush(serverDataVersion));
        }
      }
    }
  }

  /** Answers the check scheduled for when the confirmation delay ends with the newer version. */
  function confirmNewerAppVersion() {
    advanceAnsweringPolls(APP_UPDATE_CONFIRM_DELAY_MS, newerAppVersion);
    respondWithServerAppVersion(newerAppVersion);
  }

  beforeEach(() => {
    jest.useFakeTimers();
    visibilityState = 'visible';
    isBrowserOnline = true;
    jest.spyOn(document, 'visibilityState', 'get').mockImplementation(() => visibilityState);
    jest.spyOn(window.navigator, 'onLine', 'get').mockImplementation(() => isBrowserOnline);
  });

  afterEach(() => {
    window.sessionStorage.clear();
    // A request that timed out is cancelled rather than answered.
    httpMock.verify({ ignoreCancelled: true });
    TestBed.resetTestingModule();
    jest.useRealTimers();
    jest.clearAllMocks();
    jest.restoreAllMocks();
  });

  describe('when it checks', () => {
    it('does not check right after the page loads', () => {
      setup().start(loadedAppVersion, siteName);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
    });

    it('does not check in a development build, where the dev server has no version endpoint', () => {
      setup({ checksEnabled: false }).start(loadedAppVersion, siteName);

      returnToTab();
      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
    });

    it('does not check while rendering on the server', () => {
      setup({ isServer: true }).start(loadedAppVersion, siteName);

      returnToTab();
      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
    });

    it('bypasses the app HTTP interceptors', () => {
      setup().start(loadedAppVersion, siteName);

      returnToTab();
      respondWithServerAppVersion(loadedAppVersion);

      expect(appInterceptor).not.toHaveBeenCalled();
    });

    it('polls while the tab stays visible', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);

      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);
      respondWithServerAppVersion(newerAppVersion);
      confirmNewerAppVersion();

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('does not poll while the tab is hidden', () => {
      setup().start(loadedAppVersion, siteName);

      setTabVisibility('hidden');
      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
    });

    it('checks at most once per minimum interval when the tab is revisited repeatedly', () => {
      setup().start(loadedAppVersion, siteName);

      returnToTab();
      respondWithServerAppVersion(loadedAppVersion);
      returnToTab();
      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();
      respondWithServerAppVersion(loadedAppVersion);
    });

    it('skips checks while offline and checks once the browser reconnects', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);

      isBrowserOnline = false;
      returnToTab();
      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);
      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);

      reconnect();
      respondWithServerAppVersion(newerAppVersion);
      confirmNewerAppVersion();

      expect(isShowingAppUpdateNotice()).toBe(true);
    });
  });

  describe('after the tab loaded a new page for a stale chunk', () => {
    it('counts the reload once', () => {
      window.sessionStorage.setItem(STALE_CHUNK_PAGE_LOAD_PENDING_KEY, 'true');

      setup().start(loadedAppVersion, siteName);

      expect(Sentry.metrics.count).toHaveBeenCalledWith(STALE_CHUNK_PAGE_LOADS_METRIC, 1);
      expect(window.sessionStorage.getItem(STALE_CHUNK_PAGE_LOAD_PENDING_KEY)).toBeNull();
    });

    it('counts nothing when the tab did not load a new page for a stale chunk', () => {
      setup().start(loadedAppVersion, siteName);

      expect(Sentry.metrics.count).not.toHaveBeenCalledWith(STALE_CHUNK_PAGE_LOADS_METRIC, 1);
    });
  });

  describe('when the server responds with a version', () => {
    it('stays current when the server reports the version the tab loaded with', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);

      returnToTab();
      respondWithServerAppVersion(loadedAppVersion);

      expect(isShowingAppUpdateNotice()).toBe(false);
      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it('shows the app update overlay once a newer deploy outlasts the confirmation delay', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);

      returnToTab();
      respondWithServerAppVersion(newerAppVersion);
      confirmNewerAppVersion();

      expect(isShowingAppUpdateNotice()).toBe(true);
      expect(errorOverlayService.activeMessage()).toBe(getAppUpdateMessage(siteName));
    });

    it('counts the stale tab in Sentry metrics with both versions and the first trigger', () => {
      setup().start(loadedAppVersion, siteName);

      returnToTab();
      respondWithServerAppVersion(newerAppVersion);
      confirmNewerAppVersion();

      const versionService = TestBed.inject(VersionService);
      expect(Sentry.metrics.count).toHaveBeenCalledTimes(1);
      expect(Sentry.metrics.count).toHaveBeenCalledWith(VERSION_CONFIRMED_MISMATCHES_METRIC, 1, {
        attributes: {
          mismatched_versions: MismatchedVersionsEnum.App,
          trigger: AppUpdateCheckTriggerEnum.Visibility,
          client_app_version: versionService.getSiteVersion(loadedAppVersion),
          server_app_version: versionService.getSiteVersion(newerAppVersion),
        },
      });
    });

    it('compares app versions when either side has no commit SHA', () => {
      const appUpdateService = setup();
      appUpdateService.start({ ...loadedAppVersion, commitSha: '' }, siteName);

      returnToTab();
      respondWithServerAppVersion(newerAppVersion);
      confirmNewerAppVersion();

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('stays current when the server version is unknown', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);

      returnToTab();
      respondWithServerAppVersion(unknownAppVersion);

      expect(isShowingAppUpdateNotice()).toBe(false);
    });

    it('stops checking once the tab is known to be stale', () => {
      setup().start(loadedAppVersion, siteName);

      returnToTab();
      respondWithServerAppVersion(newerAppVersion);
      confirmNewerAppVersion();

      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);
      returnToTab();

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      expect(Sentry.metrics.count).toHaveBeenCalledTimes(1);
    });
  });

  describe('while a version mismatch is unconfirmed', () => {
    it('restarts the confirmation delay when a different deploy appears before it ends', () => {
      const laterAppVersion: AppVersion = { appVersion: '4.0.0-rc5', commitSha: '9a8b7c6' };
      setup().start(loadedAppVersion, siteName);
      returnToTab();
      respondWithServerAppVersion(newerAppVersion);
      advanceAnsweringPolls(
        APP_UPDATE_CONFIRM_DELAY_MS - APP_UPDATE_MIN_CHECK_INTERVAL_MS,
        newerAppVersion,
      );
      returnToTab();
      respondWithServerAppVersion(laterAppVersion);

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      expect(isShowingAppUpdateNotice()).toBe(false);
    });

    it('restarts the confirmation delay when a release candidate without a commit SHA replaces another', () => {
      const loadedAppVersionWithoutSha: AppVersion = { ...loadedAppVersion, commitSha: '' };
      const newerAppVersionWithoutSha: AppVersion = { ...newerAppVersion, commitSha: '' };
      const laterAppVersionWithoutSha: AppVersion = { appVersion: '4.0.0-rc5', commitSha: '' };
      setup().start(loadedAppVersionWithoutSha, siteName);
      returnToTab();
      respondWithServerAppVersion(newerAppVersionWithoutSha);
      advanceAnsweringPolls(
        APP_UPDATE_CONFIRM_DELAY_MS - APP_UPDATE_MIN_CHECK_INTERVAL_MS,
        newerAppVersionWithoutSha,
      );
      returnToTab();
      respondWithServerAppVersion(laterAppVersionWithoutSha);

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      expect(isShowingAppUpdateNotice()).toBe(false);
    });

    it('retries a confirmation that joined a failed check started by another trigger', () => {
      const timeBeforeConfirmationMs = 1;
      // Finds the mismatch between poll ticks, so the last poll before the confirmation is more
      // than the minimum interval earlier and does not hold back the HTTP error's check
      const timeBeforeMismatchMs = APP_UPDATE_MIN_CHECK_INTERVAL_MS / 2;
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);
      jest.advanceTimersByTime(timeBeforeMismatchMs);
      returnToTab();
      respondWithServerAppVersion(newerAppVersion);
      advanceAnsweringPolls(
        APP_UPDATE_CONFIRM_DELAY_MS - timeBeforeConfirmationMs,
        newerAppVersion,
      );
      appUpdateService.requestCheck(AppUpdateCheckTriggerEnum.HttpError);
      jest.advanceTimersByTime(timeBeforeConfirmationMs);
      respondWithNetworkFailure();

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      respondWithServerAppVersion(newerAppVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('sends no confirmation request while the tab is hidden, and confirms once it returns', () => {
      setup().start(loadedAppVersion, siteName);
      returnToTab();
      respondWithServerAppVersion(newerAppVersion);
      setTabVisibility('hidden');

      jest.advanceTimersByTime(APP_UPDATE_CONFIRM_DELAY_MS);
      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);

      setTabVisibility('visible');
      respondWithServerAppVersion(newerAppVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('stops retrying a failed confirmation once the retry limit is reached', () => {
      setup().start(loadedAppVersion, siteName);
      returnToTab();
      respondWithServerAppVersion(newerAppVersion);
      advanceAnsweringPolls(APP_UPDATE_CONFIRM_DELAY_MS, newerAppVersion);

      for (let attempt = 0; attempt < APP_UPDATE_CONFIRMATION_RETRY_LIMIT; attempt++) {
        respondWithNetworkFailure();
        jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      }
      // The background poll that takes over, due as the next retry would have been
      respondWithNetworkFailure();
      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      expect(isShowingAppUpdateNotice()).toBe(false);
    });

    it('does not show the overlay on the first mismatch', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);

      returnToTab();
      respondWithServerAppVersion(newerAppVersion);

      expect(isShowingAppUpdateNotice()).toBe(false);
      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it('does not show the overlay when the mismatch recurs before the confirmation delay ends', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);

      returnToTab();
      respondWithServerAppVersion(newerAppVersion);
      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();
      respondWithServerAppVersion(newerAppVersion);

      expect(isShowingAppUpdateNotice()).toBe(false);
    });

    it('stays current and counts an unconfirmed mismatch when the confirmation matches', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);

      returnToTab();
      respondWithServerAppVersion(newerAppVersion);
      advanceAnsweringPolls(APP_UPDATE_CONFIRM_DELAY_MS, newerAppVersion);
      respondWithServerAppVersion(loadedAppVersion);

      const versionService = TestBed.inject(VersionService);
      expect(isShowingAppUpdateNotice()).toBe(false);
      expect(Sentry.metrics.count).toHaveBeenCalledTimes(1);
      expect(Sentry.metrics.count).toHaveBeenCalledWith(VERSION_UNCONFIRMED_MISMATCHES_METRIC, 1, {
        attributes: {
          mismatched_versions: MismatchedVersionsEnum.App,
          trigger: AppUpdateCheckTriggerEnum.Visibility,
          client_app_version: versionService.getSiteVersion(loadedAppVersion),
          server_app_version: versionService.getSiteVersion(newerAppVersion),
        },
      });
    });

    it('starts the confirmation delay over after an unconfirmed mismatch', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);

      returnToTab();
      respondWithServerAppVersion(newerAppVersion);
      advanceAnsweringPolls(APP_UPDATE_CONFIRM_DELAY_MS, newerAppVersion);
      respondWithServerAppVersion(loadedAppVersion);
      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();
      respondWithServerAppVersion(newerAppVersion);

      expect(isShowingAppUpdateNotice()).toBe(false);
    });

    it('retries a failed confirmation check after the minimum interval', () => {
      setup().start(loadedAppVersion, siteName);

      returnToTab();
      respondWithServerAppVersion(newerAppVersion);
      advanceAnsweringPolls(APP_UPDATE_CONFIRM_DELAY_MS, newerAppVersion);
      respondWithNetworkFailure();
      expect(isShowingAppUpdateNotice()).toBe(false);

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      respondWithServerAppVersion(newerAppVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('resolves a first mismatch as a mismatch without showing the overlay', async () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);

      const isVersionMismatch = appUpdateService.isAppStale(AppUpdateCheckTriggerEnum.Visibility);
      respondWithServerAppVersion(newerAppVersion);

      await expect(isVersionMismatch).resolves.toBe(true);
      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });
  });

  describe.each([
    {
      endpoint: VersionCheckEndpointEnum.AppVersion,
      url: APP_VERSION_PATH,
      misconfiguredWarning: APP_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
      startTab: () => setup().start(loadedAppVersion, siteName),
      respondToOtherEndpoint: () => undefined,
    },
    {
      endpoint: VersionCheckEndpointEnum.DataVersion,
      url: dataVersionUrl,
      misconfiguredWarning: DATA_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
      startTab: () => startWithDataChecks(setup()),
      respondToOtherEndpoint: () => respondWithServerAppVersion(loadedAppVersion),
    },
  ])(
    'when the $endpoint check fails',
    ({ endpoint, url, misconfiguredWarning, startTab, respondToOtherEndpoint }) => {
      const respondWithStatus = (status: number, statusText: string) => () =>
        httpMock.expectOne(url).flush(null, { status, statusText });
      const respondWithNoResponse = () => httpMock.expectOne(url).error(new ProgressEvent('error'));
      const respondWithUnparsableBody = () =>
        httpMock
          .expectOne(url)
          .error(new ProgressEvent('error'), { status: HttpStatusCode.Ok, statusText: 'OK' });
      const respondWithBody = (body: string | object) => () => httpMock.expectOne(url).flush(body);

      const failures: {
        scenario: string;
        respond: () => void;
        reason: VersionCheckFailureReason;
        httpStatusAttribute: Record<string, number>;
        isMisconfiguration: boolean;
      }[] = [
        {
          scenario: 'no response reaches the browser',
          respond: respondWithNoResponse,
          reason: VersionCheckFailureReasonEnum.Network,
          httpStatusAttribute: {},
          isMisconfiguration: false,
        },
        {
          scenario: 'the server does not answer in time',
          respond: () => jest.advanceTimersByTime(VERSION_CHECK_TIMEOUT_MS),
          reason: VersionCheckFailureReasonEnum.Timeout,
          httpStatusAttribute: {},
          isMisconfiguration: false,
        },
        {
          scenario: 'no healthy task answers',
          respond: respondWithStatus(HttpStatusCode.ServiceUnavailable, 'Service Unavailable'),
          reason: VersionCheckFailureReasonEnum.Server,
          httpStatusAttribute: {
            [HTTP_RESPONSE_STATUS_CODE_ATTRIBUTE]: HttpStatusCode.ServiceUnavailable,
          },
          isMisconfiguration: false,
        },
        {
          scenario: 'access is refused',
          respond: respondWithStatus(HttpStatusCode.Forbidden, 'Forbidden'),
          reason: VersionCheckFailureReasonEnum.UnexpectedStatus,
          httpStatusAttribute: { [HTTP_RESPONSE_STATUS_CODE_ATTRIBUTE]: HttpStatusCode.Forbidden },
          isMisconfiguration: false,
        },
        {
          scenario: 'the server has no version endpoint',
          respond: respondWithStatus(HttpStatusCode.NotFound, 'Not Found'),
          reason: VersionCheckFailureReasonEnum.NotFound,
          httpStatusAttribute: { [HTTP_RESPONSE_STATUS_CODE_ATTRIBUTE]: HttpStatusCode.NotFound },
          isMisconfiguration: true,
        },
        {
          scenario: 'a misrouted request returns a page instead of a version',
          respond: respondWithBody('<!doctype html><html></html>'),
          reason: VersionCheckFailureReasonEnum.InvalidResponse,
          httpStatusAttribute: { [HTTP_RESPONSE_STATUS_CODE_ATTRIBUTE]: HttpStatusCode.Ok },
          isMisconfiguration: true,
        },
        {
          scenario: 'the browser cannot parse the response as JSON',
          respond: respondWithUnparsableBody,
          reason: VersionCheckFailureReasonEnum.InvalidResponse,
          httpStatusAttribute: { [HTTP_RESPONSE_STATUS_CODE_ATTRIBUTE]: HttpStatusCode.Ok },
          isMisconfiguration: true,
        },
        {
          scenario: 'a misrouted request returns another endpoint’s JSON',
          respond: respondWithBody({ status: 'UP' }),
          reason: VersionCheckFailureReasonEnum.InvalidResponse,
          httpStatusAttribute: { [HTTP_RESPONSE_STATUS_CODE_ATTRIBUTE]: HttpStatusCode.Ok },
          isMisconfiguration: true,
        },
      ];

      it.each(failures)(
        'counts the failure and stays current when $scenario',
        ({ respond, reason, httpStatusAttribute }) => {
          startTab();

          returnToTab();
          respondToOtherEndpoint();
          respond();

          expect(Sentry.metrics.count).toHaveBeenCalledWith(VERSION_CHECK_FAILURES_METRIC, 1, {
            attributes: {
              endpoint,
              reason,
              ...httpStatusAttribute,
              trigger: AppUpdateCheckTriggerEnum.Visibility,
            },
          });
          expect(errorOverlayService.isOverlayVisible()).toBe(false);
        },
      );

      it.each(failures.filter(({ isMisconfiguration }) => isMisconfiguration))(
        'raises a Sentry warning when $scenario',
        ({ respond, reason, httpStatusAttribute }) => {
          startTab();

          returnToTab();
          respondToOtherEndpoint();
          respond();

          expect(logger.warn).toHaveBeenCalledWith(misconfiguredWarning, {
            endpoint,
            reason,
            ...httpStatusAttribute,
            trigger: AppUpdateCheckTriggerEnum.Visibility,
          });
        },
      );

      it.each(failures.filter(({ isMisconfiguration }) => !isMisconfiguration))(
        'raises no Sentry warning when $scenario',
        ({ respond }) => {
          startTab();

          returnToTab();
          respondToOtherEndpoint();
          respond();

          expect(logger.warn).not.toHaveBeenCalled();
        },
      );

      it('raises the misconfiguration warning once per tab', () => {
        startTab();
        const respondNotFound = respondWithStatus(HttpStatusCode.NotFound, 'Not Found');

        returnToTab();
        respondToOtherEndpoint();
        respondNotFound();
        jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);
        respondToOtherEndpoint();
        respondNotFound();

        expect(Sentry.metrics.count).toHaveBeenCalledTimes(2);
        expect(logger.warn).toHaveBeenCalledTimes(1);
      });
    },
  );

  describe('when a version request does not answer in time', () => {
    it('still handles the other endpoint, so a stalled data request does not hold back a deploy', async () => {
      startWithDataChecks(setup());

      await navigateTo(missingChunkPath);
      respondWithServerAppVersion(newerAppVersion);
      jest.advanceTimersByTime(VERSION_CHECK_TIMEOUT_MS);

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('checks again once the stalled request has timed out', () => {
      setup().start(loadedAppVersion, siteName);

      returnToTab();
      jest.advanceTimersByTime(VERSION_CHECK_TIMEOUT_MS);
      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();

      const openRequests = httpMock.match(APP_VERSION_PATH).filter(({ cancelled }) => !cancelled);
      expect(openRequests).toHaveLength(1);
    });

    it('resolves an immediate check once the stalled request has timed out', async () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);

      const isVersionMismatch = appUpdateService.isAppStale(
        AppUpdateCheckTriggerEnum.ChunkLoadError,
      );
      jest.advanceTimersByTime(VERSION_CHECK_TIMEOUT_MS);

      await expect(isVersionMismatch).resolves.toBe(false);
    });
  });

  describe('after a failed check', () => {
    it('keeps checking', () => {
      setup().start(loadedAppVersion, siteName);

      returnToTab();
      respondWithNetworkFailure();
      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);
      respondWithServerAppVersion(newerAppVersion);
      confirmNewerAppVersion();

      expect(isShowingAppUpdateNotice()).toBe(true);
    });
  });

  describe('when an error hints the tab may be stale', () => {
    it('shows the overlay right away when a route chunk fails to load on a mismatched version', async () => {
      setup().start(loadedAppVersion, siteName);
      returnToTab();
      respondWithServerAppVersion(loadedAppVersion);

      await navigateTo(missingChunkPath);
      respondWithServerAppVersion(newerAppVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
      expect(Sentry.metrics.count).toHaveBeenCalledWith(
        VERSION_CONFIRMED_MISMATCHES_METRIC,
        1,
        expect.objectContaining({
          attributes: expect.objectContaining({
            trigger: AppUpdateCheckTriggerEnum.ChunkLoadError,
          }),
        }),
      );
    });

    describe('when a route chunk fails to load on a mismatched version', () => {
      const targetUrl = `/${missingChunkPath}`;

      async function failRouteChunkOnNewerDeploy() {
        await navigateTo(missingChunkPath);
        respondWithServerAppVersion(newerAppVersion);
        await jest.advanceTimersByTimeAsync(0);
      }

      it('loads the page the user was navigating to, and marks it to be counted', async () => {
        setup().start(loadedAppVersion, siteName);

        await failRouteChunkOnNewerDeploy();

        expect(loadPage).toHaveBeenCalledWith(targetUrl);
        expect(window.sessionStorage.getItem(STALE_CHUNK_PAGE_LOAD_PENDING_KEY)).toBe('true');
      });

      it('keeps the update notice instead of loading another page within the guard window', async () => {
        const pageLoadedAt = Date.now() - STALE_CHUNK_PAGE_LOAD_GUARD_MS;
        window.sessionStorage.setItem(STALE_CHUNK_PAGE_LOADED_AT_KEY, String(pageLoadedAt));
        setup().start(loadedAppVersion, siteName);

        await failRouteChunkOnNewerDeploy();

        expect(loadPage).not.toHaveBeenCalled();
        expect(isShowingAppUpdateNotice()).toBe(true);
      });

      it('loads the page again once the guard window has passed', async () => {
        const pageLoadedAt = Date.now() - STALE_CHUNK_PAGE_LOAD_GUARD_MS - 1;
        window.sessionStorage.setItem(STALE_CHUNK_PAGE_LOADED_AT_KEY, String(pageLoadedAt));
        setup().start(loadedAppVersion, siteName);

        await failRouteChunkOnNewerDeploy();

        expect(loadPage).toHaveBeenCalledWith(targetUrl);
      });

      it('keeps the update notice when session storage is unavailable', async () => {
        setup().start(loadedAppVersion, siteName);
        jest.spyOn(Storage.prototype, 'getItem').mockImplementation(() => {
          throw new DOMException('Access is denied', 'SecurityError');
        });

        await failRouteChunkOnNewerDeploy();

        expect(loadPage).not.toHaveBeenCalled();
        expect(isShowingAppUpdateNotice()).toBe(true);
      });
    });

    it('asks to reload when a route chunk fails to load but the version matches', async () => {
      setup().start(loadedAppVersion, siteName);

      await navigateTo(missingChunkPath);
      respondWithServerAppVersion(loadedAppVersion);
      await jest.advanceTimersByTimeAsync(0);

      expect(errorOverlayService.activeMessage()).toBe(CHUNK_LOAD_ERROR_MESSAGE);
      expect(loadPage).not.toHaveBeenCalled();
    });

    it('asks to reload when a route chunk fails to load while offline', async () => {
      setup().start(loadedAppVersion, siteName);
      isBrowserOnline = false;

      await navigateTo(missingChunkPath);
      await jest.advanceTimersByTimeAsync(0);

      expect(errorOverlayService.activeMessage()).toBe(CHUNK_LOAD_ERROR_MESSAGE);
    });

    it('asks to reload when a route chunk fails to load and the version check fails', async () => {
      setup().start(loadedAppVersion, siteName);

      await navigateTo(missingChunkPath);
      respondWithNetworkFailure();
      await jest.advanceTimersByTimeAsync(0);

      expect(errorOverlayService.activeMessage()).toBe(CHUNK_LOAD_ERROR_MESSAGE);
    });

    it('shows the overlay right away when a route chunk fails during a check started by returning to the tab', async () => {
      setup().start(loadedAppVersion, siteName);

      returnToTab();
      await navigateTo(missingChunkPath);
      respondWithServerAppVersion(newerAppVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('waits out the confirmation delay on the next check after a route chunk failure', async () => {
      setup().start(loadedAppVersion, siteName);
      returnToTab();
      await navigateTo(missingChunkPath);
      respondWithServerAppVersion(loadedAppVersion);

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();
      respondWithServerAppVersion(newerAppVersion);

      expect(isShowingAppUpdateNotice()).toBe(false);
    });

    it('does not ask to reload when a route fails for another reason', async () => {
      setup().start(loadedAppVersion, siteName);

      await navigateTo(brokenRoutePath);
      await jest.advanceTimersByTimeAsync(0);

      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it('does not check when a route fails for another reason', async () => {
      setup().start(loadedAppVersion, siteName);

      await navigateTo(brokenRoutePath);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
    });

    it('checks when asked after an HTTP error', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);

      appUpdateService.requestCheck(AppUpdateCheckTriggerEnum.HttpError);
      respondWithServerAppVersion(newerAppVersion);
      confirmNewerAppVersion();

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('checks at most once per minimum interval when asked repeatedly', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);

      appUpdateService.requestCheck(AppUpdateCheckTriggerEnum.HttpError);
      respondWithServerAppVersion(loadedAppVersion);
      appUpdateService.requestCheck(AppUpdateCheckTriggerEnum.HttpError);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
    });

    it('sends one request when checks are asked for while one is in progress', async () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);

      const firstCheck = appUpdateService.isAppStale(AppUpdateCheckTriggerEnum.ChunkLoadError);
      const secondCheck = appUpdateService.isAppStale(AppUpdateCheckTriggerEnum.ChunkLoadError);
      respondWithServerAppVersion(newerAppVersion);

      await expect(Promise.all([firstCheck, secondCheck])).resolves.toEqual([true, true]);
    });

    it('resolves that the tab is stale without another request once known', async () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);
      returnToTab();
      respondWithServerAppVersion(newerAppVersion);
      confirmNewerAppVersion();

      const isStale = appUpdateService.isAppStale(AppUpdateCheckTriggerEnum.ChunkLoadError);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      await expect(isStale).resolves.toBe(true);
    });

    it('resolves that the tab is current without a request before it starts', async () => {
      const isStale = setup().isAppStale(AppUpdateCheckTriggerEnum.ChunkLoadError);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      await expect(isStale).resolves.toBe(false);
    });

    it('resolves that the tab is current without a request while offline', async () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedAppVersion, siteName);
      isBrowserOnline = false;

      const isStale = appUpdateService.isAppStale(AppUpdateCheckTriggerEnum.ChunkLoadError);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      await expect(isStale).resolves.toBe(false);
    });
  });

  describe('when counting failed chunk loads', () => {
    function chunkLoadFailureCount(outcome: AppVersionCheckOutcome) {
      return [CHUNK_LOAD_FAILURES_METRIC, 1, { attributes: { outcome } }] as const;
    }

    it('counts a failure on a mismatched version as stale', async () => {
      setup().start(loadedAppVersion, siteName);

      await navigateTo(missingChunkPath);
      respondWithServerAppVersion(newerAppVersion);
      await jest.advanceTimersByTimeAsync(0);

      expect(Sentry.metrics.count).toHaveBeenCalledWith(
        ...chunkLoadFailureCount(AppVersionCheckOutcomeEnum.Stale),
      );
    });

    it('counts a failure on a matching version as current', async () => {
      setup().start(loadedAppVersion, siteName);

      await navigateTo(missingChunkPath);
      respondWithServerAppVersion(loadedAppVersion);
      await jest.advanceTimersByTimeAsync(0);

      expect(Sentry.metrics.count).toHaveBeenCalledWith(
        ...chunkLoadFailureCount(AppVersionCheckOutcomeEnum.Current),
      );
    });

    it('counts a failure while offline as offline', async () => {
      setup().start(loadedAppVersion, siteName);
      isBrowserOnline = false;

      await navigateTo(missingChunkPath);
      await jest.advanceTimersByTimeAsync(0);

      expect(Sentry.metrics.count).toHaveBeenCalledWith(
        ...chunkLoadFailureCount(AppVersionCheckOutcomeEnum.Offline),
      );
    });

    it('counts a failure whose version check errors as check failed', async () => {
      setup().start(loadedAppVersion, siteName);

      await navigateTo(missingChunkPath);
      respondWithNetworkFailure();
      await jest.advanceTimersByTimeAsync(0);

      expect(Sentry.metrics.count).toHaveBeenCalledWith(
        ...chunkLoadFailureCount(AppVersionCheckOutcomeEnum.CheckFailed),
      );
    });

    it('counts a failure whose version check times out as check failed', async () => {
      setup().start(loadedAppVersion, siteName);

      await navigateTo(missingChunkPath);
      await jest.advanceTimersByTimeAsync(VERSION_CHECK_TIMEOUT_MS);

      expect(Sentry.metrics.count).toHaveBeenCalledWith(
        ...chunkLoadFailureCount(AppVersionCheckOutcomeEnum.CheckFailed),
      );
    });

    it('does not count a route that fails for another reason', async () => {
      setup().start(loadedAppVersion, siteName);

      await navigateTo(brokenRoutePath);
      await jest.advanceTimersByTimeAsync(0);

      expect(Sentry.metrics.count).not.toHaveBeenCalledWith(
        CHUNK_LOAD_FAILURES_METRIC,
        expect.anything(),
        expect.anything(),
      );
    });
  });

  describe('when the data version is checked', () => {
    it('does not check again before the confirmation, since the delay is shorter than the minimum check interval', () => {
      startWithDataChecks(setup());
      returnToTab();
      respondToCheck(loadedAppVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS - 1);

      returnToTab();

      expect(httpMock.match(dataVersionUrl)).toHaveLength(0);
      expect(isShowingDataUpdateNotice()).toBe(false);
    });

    it('restarts the confirmation delay when the confirmation check finds a different data release', () => {
      startWithDataChecks(setup());
      returnToTab();
      respondToCheck(loadedAppVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedAppVersion, laterDataVersion);

      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS - 1);

      expect(httpMock.match(dataVersionUrl)).toHaveLength(0);
      expect(isShowingDataUpdateNotice()).toBe(false);
    });

    it('shows the overlay once a different data release found by the confirmation check outlasts its own delay', () => {
      startWithDataChecks(setup());
      returnToTab();
      respondToCheck(loadedAppVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedAppVersion, laterDataVersion);

      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedAppVersion, laterDataVersion);

      expect(isShowingDataUpdateNotice()).toBe(true);
    });

    it('does not check the data version without a data version URL', () => {
      const appUpdateService = setup();
      loadFooterDataVersion();
      appUpdateService.start(loadedAppVersion, siteName);

      returnToTab();
      respondWithServerAppVersion(loadedAppVersion);

      expect(httpMock.match(dataVersionUrl)).toHaveLength(0);
    });

    it('detects a later data release when the footer did not load a data version', () => {
      setup().start(loadedAppVersion, siteName, dataVersionUrl);

      returnToTab();
      respondToCheck(loadedAppVersion, loadedDataVersion);
      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();
      respondToCheck(loadedAppVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedAppVersion, newerDataVersion);

      expect(isShowingDataUpdateNotice()).toBe(true);
    });

    it('does not keep an unknown data version as the one the tab loaded with', () => {
      setup().start(loadedAppVersion, siteName, dataVersionUrl);

      returnToTab();
      respondToCheck(loadedAppVersion, unknownDataVersion);

      expect(TestBed.inject(VersionService).getLoadedDataVersion()).toBeNull();
    });

    it('checks the data version alongside the app version, bypassing the app HTTP interceptors', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedAppVersion, loadedDataVersion);

      expect(appInterceptor).not.toHaveBeenCalled();
      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it('does not show the data update overlay on the first mismatch', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedAppVersion, newerDataVersion);

      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it('shows the data update overlay once a new data release outlasts the confirmation delay', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedAppVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedAppVersion, newerDataVersion);

      expect(isShowingDataUpdateNotice()).toBe(true);
    });

    it('shows the data update overlay when only the data file changes', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedAppVersion, differentDataFile);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedAppVersion, differentDataFile);

      expect(isShowingDataUpdateNotice()).toBe(true);
    });

    it('counts the stale data in Sentry metrics with both data versions and the first trigger', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedAppVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedAppVersion, newerDataVersion);

      const versionService = TestBed.inject(VersionService);
      expect(Sentry.metrics.count).toHaveBeenCalledWith(VERSION_CONFIRMED_MISMATCHES_METRIC, 1, {
        attributes: {
          mismatched_versions: MismatchedVersionsEnum.Data,
          trigger: AppUpdateCheckTriggerEnum.Visibility,
          client_data_version: versionService.formatDataVersion(loadedDataVersion),
          server_data_version: versionService.formatDataVersion(newerDataVersion),
        },
      });
    });

    it('stays current when the server data version is unknown', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedAppVersion, unknownDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);

      expect(httpMock.match(dataVersionUrl)).toHaveLength(0);
      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it('stays current and counts an unconfirmed data mismatch when the confirmation matches', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedAppVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedAppVersion, loadedDataVersion);

      expect(errorOverlayService.isOverlayVisible()).toBe(false);
      expect(Sentry.metrics.count).toHaveBeenCalledWith(
        VERSION_UNCONFIRMED_MISMATCHES_METRIC,
        1,
        expect.objectContaining({
          attributes: expect.objectContaining({ mismatched_versions: MismatchedVersionsEnum.Data }),
        }),
      );
    });

    it('retries a failed data confirmation check after the minimum interval', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedAppVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondWithServerAppVersion(loadedAppVersion);
      httpMock.expectOne(dataVersionUrl).error(new ProgressEvent('error'));
      expect(errorOverlayService.isOverlayVisible()).toBe(false);

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      respondToCheck(loadedAppVersion, newerDataVersion);

      expect(isShowingDataUpdateNotice()).toBe(true);
    });

    it('does not treat a new data release as explaining a failed chunk load', async () => {
      const appUpdateService = setup();
      startWithDataChecks(appUpdateService);

      const isVersionMismatch = appUpdateService.isAppStale(
        AppUpdateCheckTriggerEnum.ChunkLoadError,
      );
      respondToCheck(loadedAppVersion, newerDataVersion);

      await expect(isVersionMismatch).resolves.toBe(false);
      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it('waits out the app delay, then shows the app update notice, when a deploy and a data release are both found', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(newerAppVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      expect(errorOverlayService.isOverlayVisible()).toBe(false);

      advanceAnsweringPolls(
        APP_UPDATE_CONFIRM_DELAY_MS - DATA_UPDATE_CONFIRM_DELAY_MS,
        newerAppVersion,
        newerDataVersion,
      );
      respondToCheck(newerAppVersion, newerDataVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('confirms a deploy and a data release once the tab returns, when it was hidden as the app delay ended', () => {
      startWithDataChecks(setup());
      returnToTab();
      respondToCheck(newerAppVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      setTabVisibility('hidden');

      jest.advanceTimersByTime(APP_UPDATE_CONFIRM_DELAY_MS - DATA_UPDATE_CONFIRM_DELAY_MS);
      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);

      setTabVisibility('visible');
      respondToCheck(newerAppVersion, newerDataVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('starts the wait over with the app delay when the data confirmation check also finds a deploy', () => {
      startWithDataChecks(setup());
      returnToTab();
      respondToCheck(loadedAppVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(newerAppVersion, newerDataVersion);

      advanceAnsweringPolls(APP_UPDATE_CONFIRM_DELAY_MS - 1, newerAppVersion, newerDataVersion);
      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      expect(errorOverlayService.isOverlayVisible()).toBe(false);

      jest.advanceTimersByTime(1);
      respondToCheck(newerAppVersion, newerDataVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('starts the wait over for the deploy alone when the data release goes away', () => {
      startWithDataChecks(setup());
      returnToTab();
      respondToCheck(newerAppVersion, newerDataVersion);
      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();
      respondToCheck(newerAppVersion, loadedDataVersion);

      advanceAnsweringPolls(APP_UPDATE_CONFIRM_DELAY_MS, newerAppVersion, loadedDataVersion);
      respondToCheck(newerAppVersion, loadedDataVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
      expect(Sentry.metrics.count).not.toHaveBeenCalledWith(
        VERSION_UNCONFIRMED_MISMATCHES_METRIC,
        expect.anything(),
        expect.anything(),
      );
    });

    it('confirms on a later background check once the confirmation retries run out', () => {
      startWithDataChecks(setup());
      returnToTab();
      respondToCheck(newerAppVersion, newerDataVersion);
      advanceAnsweringPolls(APP_UPDATE_CONFIRM_DELAY_MS, newerAppVersion, newerDataVersion);
      for (let attempt = 0; attempt < APP_UPDATE_CONFIRMATION_RETRY_LIMIT; attempt++) {
        respondWithNetworkFailure();
        respondWithDataVersionFailure();
        jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      }

      returnToTab();
      respondToCheck(newerAppVersion, newerDataVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('stops checking once the data is known to be stale', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedAppVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedAppVersion, newerDataVersion);

      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);
      returnToTab();

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      expect(httpMock.match(dataVersionUrl)).toHaveLength(0);
    });

    it('checks the data version when asked after an HTTP error', () => {
      const appUpdateService = setup();
      startWithDataChecks(appUpdateService);

      appUpdateService.requestCheck(AppUpdateCheckTriggerEnum.HttpError);
      respondToCheck(loadedAppVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedAppVersion, newerDataVersion);

      expect(isShowingDataUpdateNotice()).toBe(true);
    });
  });

  describe('when reporting which versions differed', () => {
    function confirmedMismatchMetricCalls() {
      return (Sentry.metrics.count as jest.Mock).mock.calls.filter(
        ([metricName]) => metricName === VERSION_CONFIRMED_MISMATCHES_METRIC,
      );
    }

    function mismatchedVersionsReported() {
      return confirmedMismatchMetricCalls()[0]?.[2].attributes.mismatched_versions;
    }

    function unverifiedVersionsReported() {
      return confirmedMismatchMetricCalls()[0]?.[2].attributes.unverified_versions;
    }

    it('reports only the app version when the data version still matches', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(newerAppVersion, loadedDataVersion);
      advanceAnsweringPolls(APP_UPDATE_CONFIRM_DELAY_MS, newerAppVersion, loadedDataVersion);
      respondToCheck(newerAppVersion, loadedDataVersion);

      expect(mismatchedVersionsReported()).toBe(MismatchedVersionsEnum.App);
    });

    it('reports only the data version when the app version still matches', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedAppVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedAppVersion, newerDataVersion);

      expect(mismatchedVersionsReported()).toBe(MismatchedVersionsEnum.Data);
    });

    it('reports a deploy and a data release once, with both versions, after the app delay', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(newerAppVersion, newerDataVersion);
      advanceAnsweringPolls(APP_UPDATE_CONFIRM_DELAY_MS, newerAppVersion, newerDataVersion);
      respondToCheck(newerAppVersion, newerDataVersion);

      expect(confirmedMismatchMetricCalls()).toHaveLength(1);
      expect(mismatchedVersionsReported()).toBe(MismatchedVersionsEnum.AppAndData);
    });

    it('reports both versions when a failed chunk load finds both changed in one check', async () => {
      startWithDataChecks(setup());

      await navigateTo(missingChunkPath);
      respondToCheck(newerAppVersion, newerDataVersion);

      expect(mismatchedVersionsReported()).toBe(MismatchedVersionsEnum.AppAndData);
    });

    it('reports a waiting data release as unverified when a failed chunk load cannot check it again', async () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedAppVersion, newerDataVersion);
      await navigateTo(missingChunkPath);
      respondWithServerAppVersion(newerAppVersion);
      respondWithDataVersionFailure();

      expect(mismatchedVersionsReported()).toBe(MismatchedVersionsEnum.App);
      expect(unverifiedVersionsReported()).toBe(MismatchedVersionsEnum.Data);
    });

    it('confirms the deploy and reports the data release as unverified when the data request keeps failing', () => {
      startWithDataChecks(setup());
      returnToTab();
      respondToCheck(newerAppVersion, newerDataVersion);

      advanceAnsweringPolls(APP_UPDATE_CONFIRM_DELAY_MS, newerAppVersion, newerDataVersion);
      respondWithServerAppVersion(newerAppVersion);
      respondWithDataVersionFailure();

      const versionService = TestBed.inject(VersionService);
      expect(isShowingAppUpdateNotice()).toBe(true);
      expect(Sentry.metrics.count).toHaveBeenCalledWith(VERSION_CONFIRMED_MISMATCHES_METRIC, 1, {
        attributes: {
          mismatched_versions: MismatchedVersionsEnum.App,
          unverified_versions: MismatchedVersionsEnum.Data,
          trigger: AppUpdateCheckTriggerEnum.Visibility,
          client_app_version: versionService.getSiteVersion(loadedAppVersion),
          server_app_version: versionService.getSiteVersion(newerAppVersion),
          client_data_version: versionService.formatDataVersion(loadedDataVersion),
          server_data_version: versionService.formatDataVersion(newerDataVersion),
        },
      });
    });

    it('confirms the data release and reports the deploy as unverified when the app request keeps failing', () => {
      startWithDataChecks(setup());
      returnToTab();
      respondToCheck(newerAppVersion, newerDataVersion);

      advanceAnsweringPolls(APP_UPDATE_CONFIRM_DELAY_MS, newerAppVersion, newerDataVersion);
      respondWithNetworkFailure();
      respondWithServerDataVersion(newerDataVersion);

      expect(isShowingDataUpdateNotice()).toBe(true);
      expect(mismatchedVersionsReported()).toBe(MismatchedVersionsEnum.Data);
      expect(unverifiedVersionsReported()).toBe(MismatchedVersionsEnum.App);
    });

    it('confirms nothing and tries again when neither request can be compared', () => {
      startWithDataChecks(setup());
      returnToTab();
      respondToCheck(newerAppVersion, newerDataVersion);

      advanceAnsweringPolls(APP_UPDATE_CONFIRM_DELAY_MS, newerAppVersion, newerDataVersion);
      respondWithNetworkFailure();
      respondWithDataVersionFailure();
      expect(errorOverlayService.isOverlayVisible()).toBe(false);

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      respondToCheck(newerAppVersion, newerDataVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
      expect(unverifiedVersionsReported()).toBeUndefined();
    });
  });
});
