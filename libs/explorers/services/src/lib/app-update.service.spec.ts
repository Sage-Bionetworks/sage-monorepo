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
  APP_UPDATE_CONFIRM_DELAY_MS,
  APP_UPDATE_CONFIRMATION_RETRY_LIMIT,
  APP_UPDATE_MIN_CHECK_INTERVAL_MS,
  APP_UPDATE_POLL_INTERVAL_MS,
  APP_VERSION_PATH,
  CHUNK_LOAD_ERROR_MESSAGE,
  CHUNK_LOAD_FAILED_METRIC,
  DATA_UPDATE_CONFIRM_DELAY_MS,
  DATA_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
  getAppUpdateMessage,
  getDataUpdateMessage,
  STALE_APP_METRIC,
  STALE_DATA_METRIC,
  TRANSIENT_DATA_VERSION_MISMATCH_METRIC,
  TRANSIENT_APP_VERSION_MISMATCH_METRIC,
  VERSION_CHECK_FAILED_METRIC,
  VERSION_CHECK_TIMEOUT_MS,
  APP_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
} from '@sagebionetworks/explorers/constants';
import * as Sentry from '@sentry/angular';
import {
  APP_UPDATE_CHECKS_ENABLED,
  AppUpdateCheckTriggerEnum,
  AppUpdateService,
  AppVersionCheckOutcome,
  AppVersionCheckOutcomeEnum,
  MismatchedVersionsEnum,
  NO_RESPONSE_STATUS,
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
const loadedVersion: AppVersion = { appVersion: '4.0.0-rc3', commitSha: 'abc1234' };
const newerVersion: AppVersion = { appVersion: '4.0.0-rc4', commitSha: 'def5678' };
const unknownVersion: AppVersion = { appVersion: '', commitSha: '' };

const dataVersionUrl = 'https://modeladexplorer.org/api/v1/data-version';
const loadedDataVersion: DataVersion = { data_file: 'syn73774820', data_version: '191' };
const newerDataVersion: DataVersion = { data_file: 'syn73774820', data_version: '192' };
const newerDataFile: DataVersion = { data_file: 'syn74000000', data_version: '191' };
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

  function setup({ isServer = false, checksEnabled = true } = {}): AppUpdateService {
    logger = { log: jest.fn(), warn: jest.fn(), error: jest.fn() };
    appInterceptor = jest.fn<ReturnType<HttpInterceptorFn>, Parameters<HttpInterceptorFn>>(
      (req, next) => next(req),
    );
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([appInterceptor])),
        provideHttpClientTesting(),
        provideRouter(routes),
        { provide: PlatformService, useValue: { isBrowser: !isServer, isServer } },
        { provide: LoggerService, useValue: { forSource: () => logger } },
        { provide: APP_UPDATE_CHECKS_ENABLED, useValue: checksEnabled },
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
    appUpdateService.start(loadedVersion, siteName, dataVersionUrl);
  }

  function respondWithServerDataVersion(serverDataVersion: DataVersion) {
    httpMock.expectOne(dataVersionUrl).flush(serverDataVersion);
  }

  function respondToCheck(serverVersion: AppVersion, serverDataVersion: DataVersion) {
    respondWithServerVersion(serverVersion);
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

  function respondWithServerVersion(serverVersion: AppVersion) {
    httpMock.expectOne(APP_VERSION_PATH).flush(serverVersion);
  }

  function respondWithNetworkFailure() {
    httpMock.expectOne(APP_VERSION_PATH).error(new ProgressEvent('error'));
  }

  /** Answers the check scheduled for when the confirmation delay ends with the newer version. */
  function confirmNewerVersion() {
    jest.advanceTimersByTime(APP_UPDATE_CONFIRM_DELAY_MS);
    respondWithServerVersion(newerVersion);
  }

  beforeEach(() => {
    jest.useFakeTimers();
    visibilityState = 'visible';
    isBrowserOnline = true;
    jest.spyOn(document, 'visibilityState', 'get').mockImplementation(() => visibilityState);
    jest.spyOn(window.navigator, 'onLine', 'get').mockImplementation(() => isBrowserOnline);
  });

  afterEach(() => {
    // A request that timed out is cancelled rather than answered.
    httpMock.verify({ ignoreCancelled: true });
    TestBed.resetTestingModule();
    jest.useRealTimers();
    jest.clearAllMocks();
    jest.restoreAllMocks();
  });

  describe('when it checks', () => {
    it('does not check right after the page loads', () => {
      setup().start(loadedVersion, siteName);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
    });

    it('does not check in a development build, where the dev server has no version endpoint', () => {
      setup({ checksEnabled: false }).start(loadedVersion, siteName);

      returnToTab();
      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
    });

    it('does not check while rendering on the server', () => {
      setup({ isServer: true }).start(loadedVersion, siteName);

      returnToTab();
      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
    });

    it('bypasses the app HTTP interceptors', () => {
      setup().start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(loadedVersion);

      expect(appInterceptor).not.toHaveBeenCalled();
    });

    it('polls while the tab stays visible', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);
      respondWithServerVersion(newerVersion);
      confirmNewerVersion();

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('does not poll while the tab is hidden', () => {
      setup().start(loadedVersion, siteName);

      setTabVisibility('hidden');
      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
    });

    it('checks at most once per minimum interval when the tab is revisited repeatedly', () => {
      setup().start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(loadedVersion);
      returnToTab();
      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();
      respondWithServerVersion(loadedVersion);
    });

    it('skips checks while offline and checks once the browser reconnects', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      isBrowserOnline = false;
      returnToTab();
      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);
      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);

      reconnect();
      respondWithServerVersion(newerVersion);
      confirmNewerVersion();

      expect(isShowingAppUpdateNotice()).toBe(true);
    });
  });

  describe('when the server responds with a version', () => {
    it('stays current when the server reports the version the tab loaded with', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(loadedVersion);

      expect(isShowingAppUpdateNotice()).toBe(false);
      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it('shows the app update overlay once a newer deploy outlasts the confirmation delay', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(newerVersion);
      confirmNewerVersion();

      expect(isShowingAppUpdateNotice()).toBe(true);
      expect(errorOverlayService.activeMessage()).toBe(getAppUpdateMessage(siteName));
    });

    it('counts the stale tab in Sentry metrics with both versions and the first trigger', () => {
      setup().start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(newerVersion);
      confirmNewerVersion();

      const versionService = TestBed.inject(VersionService);
      expect(Sentry.metrics.count).toHaveBeenCalledTimes(1);
      expect(Sentry.metrics.count).toHaveBeenCalledWith(STALE_APP_METRIC, 1, {
        attributes: {
          clientVersion: versionService.getSiteVersion(loadedVersion),
          serverVersion: versionService.getSiteVersion(newerVersion),
          trigger: AppUpdateCheckTriggerEnum.Visibility,
          mismatchedVersions: MismatchedVersionsEnum.App,
        },
      });
    });

    it('compares app versions when either side has no commit SHA', () => {
      const appUpdateService = setup();
      appUpdateService.start({ ...loadedVersion, commitSha: '' }, siteName);

      returnToTab();
      respondWithServerVersion(newerVersion);
      confirmNewerVersion();

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('stays current when the server version is unknown', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(unknownVersion);

      expect(isShowingAppUpdateNotice()).toBe(false);
    });

    it('stops checking once the tab is known to be stale', () => {
      setup().start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(newerVersion);
      confirmNewerVersion();

      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);
      returnToTab();

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      expect(Sentry.metrics.count).toHaveBeenCalledTimes(1);
    });
  });

  describe('while a version mismatch is unconfirmed', () => {
    it('restarts the confirmation delay when a different deploy appears before it ends', () => {
      const laterVersion: AppVersion = { appVersion: '4.0.0-rc5', commitSha: '9a8b7c6' };
      setup().start(loadedVersion, siteName);
      returnToTab();
      respondWithServerVersion(newerVersion);
      jest.advanceTimersByTime(APP_UPDATE_CONFIRM_DELAY_MS - APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();
      respondWithServerVersion(laterVersion);

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      expect(isShowingAppUpdateNotice()).toBe(false);
    });

    it('restarts the confirmation delay when a release candidate without a commit SHA replaces another', () => {
      const loadedVersionWithoutSha: AppVersion = { ...loadedVersion, commitSha: '' };
      const newerVersionWithoutSha: AppVersion = { ...newerVersion, commitSha: '' };
      const laterVersionWithoutSha: AppVersion = { appVersion: '4.0.0-rc5', commitSha: '' };
      setup().start(loadedVersionWithoutSha, siteName);
      returnToTab();
      respondWithServerVersion(newerVersionWithoutSha);
      jest.advanceTimersByTime(APP_UPDATE_CONFIRM_DELAY_MS - APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();
      respondWithServerVersion(laterVersionWithoutSha);

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      expect(isShowingAppUpdateNotice()).toBe(false);
    });

    it('retries a confirmation that joined a failed check started by another trigger', () => {
      const timeBeforeConfirmationMs = 1;
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);
      returnToTab();
      respondWithServerVersion(newerVersion);
      jest.advanceTimersByTime(APP_UPDATE_CONFIRM_DELAY_MS - timeBeforeConfirmationMs);
      appUpdateService.requestCheck(AppUpdateCheckTriggerEnum.HttpError);
      jest.advanceTimersByTime(timeBeforeConfirmationMs);
      respondWithNetworkFailure();

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      respondWithServerVersion(newerVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('sends no confirmation request while the tab is hidden, and confirms once it returns', () => {
      setup().start(loadedVersion, siteName);
      returnToTab();
      respondWithServerVersion(newerVersion);
      setTabVisibility('hidden');

      jest.advanceTimersByTime(APP_UPDATE_CONFIRM_DELAY_MS);
      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);

      setTabVisibility('visible');
      respondWithServerVersion(newerVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('stops retrying a failed confirmation once the retry limit is reached', () => {
      setup().start(loadedVersion, siteName);
      returnToTab();
      respondWithServerVersion(newerVersion);
      jest.advanceTimersByTime(APP_UPDATE_CONFIRM_DELAY_MS);

      for (let attempt = 0; attempt < APP_UPDATE_CONFIRMATION_RETRY_LIMIT; attempt++) {
        respondWithNetworkFailure();
        jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      }

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      expect(isShowingAppUpdateNotice()).toBe(false);
    });

    it('does not show the overlay on the first mismatch', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(newerVersion);

      expect(isShowingAppUpdateNotice()).toBe(false);
      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it('does not show the overlay when the mismatch recurs before the confirmation delay ends', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(newerVersion);
      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();
      respondWithServerVersion(newerVersion);

      expect(isShowingAppUpdateNotice()).toBe(false);
    });

    it('stays current and counts a transient mismatch when the confirmation matches', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(newerVersion);
      jest.advanceTimersByTime(APP_UPDATE_CONFIRM_DELAY_MS);
      respondWithServerVersion(loadedVersion);

      const versionService = TestBed.inject(VersionService);
      expect(isShowingAppUpdateNotice()).toBe(false);
      expect(Sentry.metrics.count).toHaveBeenCalledTimes(1);
      expect(Sentry.metrics.count).toHaveBeenCalledWith(TRANSIENT_APP_VERSION_MISMATCH_METRIC, 1, {
        attributes: {
          clientVersion: versionService.getSiteVersion(loadedVersion),
          serverVersion: versionService.getSiteVersion(newerVersion),
          trigger: AppUpdateCheckTriggerEnum.Visibility,
        },
      });
    });

    it('starts the confirmation delay over after a transient mismatch', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(newerVersion);
      jest.advanceTimersByTime(APP_UPDATE_CONFIRM_DELAY_MS);
      respondWithServerVersion(loadedVersion);
      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();
      respondWithServerVersion(newerVersion);

      expect(isShowingAppUpdateNotice()).toBe(false);
    });

    it('retries a failed confirmation check after the minimum interval', () => {
      setup().start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(newerVersion);
      jest.advanceTimersByTime(APP_UPDATE_CONFIRM_DELAY_MS);
      respondWithNetworkFailure();
      expect(isShowingAppUpdateNotice()).toBe(false);

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      respondWithServerVersion(newerVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('resolves a first mismatch as a mismatch without showing the overlay', async () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      const isVersionMismatch = appUpdateService.isAppStale(AppUpdateCheckTriggerEnum.Visibility);
      respondWithServerVersion(newerVersion);

      await expect(isVersionMismatch).resolves.toBe(true);
      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });
  });

  describe.each([
    {
      endpoint: VersionCheckEndpointEnum.AppVersion,
      url: APP_VERSION_PATH,
      misconfiguredWarning: APP_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
      startTab: () => setup().start(loadedVersion, siteName),
      respondToOtherEndpoint: () => undefined,
    },
    {
      endpoint: VersionCheckEndpointEnum.DataVersion,
      url: dataVersionUrl,
      misconfiguredWarning: DATA_VERSION_ENDPOINT_MISCONFIGURED_WARNING,
      startTab: () => startWithDataChecks(setup()),
      respondToOtherEndpoint: () => respondWithServerVersion(loadedVersion),
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
        status: number;
        isMisconfiguration: boolean;
      }[] = [
        {
          scenario: 'no response reaches the browser',
          respond: respondWithNoResponse,
          reason: VersionCheckFailureReasonEnum.Network,
          status: NO_RESPONSE_STATUS,
          isMisconfiguration: false,
        },
        {
          scenario: 'the server does not answer in time',
          respond: () => jest.advanceTimersByTime(VERSION_CHECK_TIMEOUT_MS),
          reason: VersionCheckFailureReasonEnum.Timeout,
          status: NO_RESPONSE_STATUS,
          isMisconfiguration: false,
        },
        {
          scenario: 'no healthy task answers',
          respond: respondWithStatus(HttpStatusCode.ServiceUnavailable, 'Service Unavailable'),
          reason: VersionCheckFailureReasonEnum.Server,
          status: HttpStatusCode.ServiceUnavailable,
          isMisconfiguration: false,
        },
        {
          scenario: 'access is refused',
          respond: respondWithStatus(HttpStatusCode.Forbidden, 'Forbidden'),
          reason: VersionCheckFailureReasonEnum.UnexpectedStatus,
          status: HttpStatusCode.Forbidden,
          isMisconfiguration: false,
        },
        {
          scenario: 'the server has no version endpoint',
          respond: respondWithStatus(HttpStatusCode.NotFound, 'Not Found'),
          reason: VersionCheckFailureReasonEnum.NotFound,
          status: HttpStatusCode.NotFound,
          isMisconfiguration: true,
        },
        {
          scenario: 'a misrouted request returns a page instead of a version',
          respond: respondWithBody('<!doctype html><html></html>'),
          reason: VersionCheckFailureReasonEnum.InvalidResponse,
          status: HttpStatusCode.Ok,
          isMisconfiguration: true,
        },
        {
          scenario: 'the browser cannot parse the response as JSON',
          respond: respondWithUnparsableBody,
          reason: VersionCheckFailureReasonEnum.InvalidResponse,
          status: HttpStatusCode.Ok,
          isMisconfiguration: true,
        },
        {
          scenario: 'a misrouted request returns another endpoint’s JSON',
          respond: respondWithBody({ status: 'UP' }),
          reason: VersionCheckFailureReasonEnum.InvalidResponse,
          status: HttpStatusCode.Ok,
          isMisconfiguration: true,
        },
      ];

      it.each(failures)(
        'counts the failure and stays current when $scenario',
        ({ respond, reason, status }) => {
          startTab();

          returnToTab();
          respondToOtherEndpoint();
          respond();

          expect(Sentry.metrics.count).toHaveBeenCalledWith(VERSION_CHECK_FAILED_METRIC, 1, {
            attributes: { endpoint, reason, status, trigger: AppUpdateCheckTriggerEnum.Visibility },
          });
          expect(errorOverlayService.isOverlayVisible()).toBe(false);
        },
      );

      it.each(failures.filter(({ isMisconfiguration }) => isMisconfiguration))(
        'raises a Sentry warning when $scenario',
        ({ respond, reason, status }) => {
          startTab();

          returnToTab();
          respondToOtherEndpoint();
          respond();

          expect(logger.warn).toHaveBeenCalledWith(misconfiguredWarning, {
            endpoint,
            reason,
            status,
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
      respondWithServerVersion(newerVersion);
      jest.advanceTimersByTime(VERSION_CHECK_TIMEOUT_MS);

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('checks again once the stalled request has timed out', () => {
      setup().start(loadedVersion, siteName);

      returnToTab();
      jest.advanceTimersByTime(VERSION_CHECK_TIMEOUT_MS);
      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();

      const openRequests = httpMock.match(APP_VERSION_PATH).filter(({ cancelled }) => !cancelled);
      expect(openRequests).toHaveLength(1);
    });

    it('resolves an immediate check once the stalled request has timed out', async () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      const isVersionMismatch = appUpdateService.isAppStale(
        AppUpdateCheckTriggerEnum.ChunkLoadError,
      );
      jest.advanceTimersByTime(VERSION_CHECK_TIMEOUT_MS);

      await expect(isVersionMismatch).resolves.toBe(false);
    });
  });

  describe('after a failed check', () => {
    it('keeps checking', () => {
      setup().start(loadedVersion, siteName);

      returnToTab();
      respondWithNetworkFailure();
      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);
      respondWithServerVersion(newerVersion);
      confirmNewerVersion();

      expect(isShowingAppUpdateNotice()).toBe(true);
    });
  });

  describe('when an error hints the tab may be stale', () => {
    it('shows the overlay right away when a route chunk fails to load on a mismatched version', async () => {
      setup().start(loadedVersion, siteName);
      returnToTab();
      respondWithServerVersion(loadedVersion);

      await navigateTo(missingChunkPath);
      respondWithServerVersion(newerVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
      expect(Sentry.metrics.count).toHaveBeenCalledWith(
        STALE_APP_METRIC,
        1,
        expect.objectContaining({
          attributes: expect.objectContaining({
            trigger: AppUpdateCheckTriggerEnum.ChunkLoadError,
          }),
        }),
      );
    });

    it('asks to reload when a route chunk fails to load but the version matches', async () => {
      setup().start(loadedVersion, siteName);

      await navigateTo(missingChunkPath);
      respondWithServerVersion(loadedVersion);
      await jest.advanceTimersByTimeAsync(0);

      expect(errorOverlayService.activeMessage()).toBe(CHUNK_LOAD_ERROR_MESSAGE);
    });

    it('asks to reload when a route chunk fails to load while offline', async () => {
      setup().start(loadedVersion, siteName);
      isBrowserOnline = false;

      await navigateTo(missingChunkPath);
      await jest.advanceTimersByTimeAsync(0);

      expect(errorOverlayService.activeMessage()).toBe(CHUNK_LOAD_ERROR_MESSAGE);
    });

    it('asks to reload when a route chunk fails to load and the version check fails', async () => {
      setup().start(loadedVersion, siteName);

      await navigateTo(missingChunkPath);
      respondWithNetworkFailure();
      await jest.advanceTimersByTimeAsync(0);

      expect(errorOverlayService.activeMessage()).toBe(CHUNK_LOAD_ERROR_MESSAGE);
    });

    it('shows the overlay right away when a route chunk fails during a check started by returning to the tab', async () => {
      setup().start(loadedVersion, siteName);

      returnToTab();
      await navigateTo(missingChunkPath);
      respondWithServerVersion(newerVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('waits out the confirmation delay on the next check after a route chunk failure', async () => {
      setup().start(loadedVersion, siteName);
      returnToTab();
      await navigateTo(missingChunkPath);
      respondWithServerVersion(loadedVersion);

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();
      respondWithServerVersion(newerVersion);

      expect(isShowingAppUpdateNotice()).toBe(false);
    });

    it('does not ask to reload when a route fails for another reason', async () => {
      setup().start(loadedVersion, siteName);

      await navigateTo(brokenRoutePath);
      await jest.advanceTimersByTimeAsync(0);

      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it('does not check when a route fails for another reason', async () => {
      setup().start(loadedVersion, siteName);

      await navigateTo(brokenRoutePath);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
    });

    it('checks when asked after an HTTP error', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      appUpdateService.requestCheck(AppUpdateCheckTriggerEnum.HttpError);
      respondWithServerVersion(newerVersion);
      confirmNewerVersion();

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('checks at most once per minimum interval when asked repeatedly', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      appUpdateService.requestCheck(AppUpdateCheckTriggerEnum.HttpError);
      respondWithServerVersion(loadedVersion);
      appUpdateService.requestCheck(AppUpdateCheckTriggerEnum.HttpError);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
    });

    it('sends one request when checks are asked for while one is in progress', async () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      const firstCheck = appUpdateService.isAppStale(AppUpdateCheckTriggerEnum.ChunkLoadError);
      const secondCheck = appUpdateService.isAppStale(AppUpdateCheckTriggerEnum.ChunkLoadError);
      respondWithServerVersion(newerVersion);

      await expect(Promise.all([firstCheck, secondCheck])).resolves.toEqual([true, true]);
    });

    it('resolves that the tab is stale without another request once known', async () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);
      returnToTab();
      respondWithServerVersion(newerVersion);
      confirmNewerVersion();

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
      appUpdateService.start(loadedVersion, siteName);
      isBrowserOnline = false;

      const isStale = appUpdateService.isAppStale(AppUpdateCheckTriggerEnum.ChunkLoadError);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      await expect(isStale).resolves.toBe(false);
    });
  });

  describe('when counting failed chunk loads', () => {
    function chunkLoadFailureCount(outcome: AppVersionCheckOutcome) {
      return [CHUNK_LOAD_FAILED_METRIC, 1, { attributes: { outcome } }] as const;
    }

    it('counts a failure on a mismatched version as stale', async () => {
      setup().start(loadedVersion, siteName);

      await navigateTo(missingChunkPath);
      respondWithServerVersion(newerVersion);
      await jest.advanceTimersByTimeAsync(0);

      expect(Sentry.metrics.count).toHaveBeenCalledWith(
        ...chunkLoadFailureCount(AppVersionCheckOutcomeEnum.Stale),
      );
    });

    it('counts a failure on a matching version as current', async () => {
      setup().start(loadedVersion, siteName);

      await navigateTo(missingChunkPath);
      respondWithServerVersion(loadedVersion);
      await jest.advanceTimersByTimeAsync(0);

      expect(Sentry.metrics.count).toHaveBeenCalledWith(
        ...chunkLoadFailureCount(AppVersionCheckOutcomeEnum.Current),
      );
    });

    it('counts a failure while offline as offline', async () => {
      setup().start(loadedVersion, siteName);
      isBrowserOnline = false;

      await navigateTo(missingChunkPath);
      await jest.advanceTimersByTimeAsync(0);

      expect(Sentry.metrics.count).toHaveBeenCalledWith(
        ...chunkLoadFailureCount(AppVersionCheckOutcomeEnum.Offline),
      );
    });

    it('counts a failure whose version check errors as check failed', async () => {
      setup().start(loadedVersion, siteName);

      await navigateTo(missingChunkPath);
      respondWithNetworkFailure();
      await jest.advanceTimersByTimeAsync(0);

      expect(Sentry.metrics.count).toHaveBeenCalledWith(
        ...chunkLoadFailureCount(AppVersionCheckOutcomeEnum.CheckFailed),
      );
    });

    it('counts a failure whose version check times out as check failed', async () => {
      setup().start(loadedVersion, siteName);

      await navigateTo(missingChunkPath);
      await jest.advanceTimersByTimeAsync(VERSION_CHECK_TIMEOUT_MS);

      expect(Sentry.metrics.count).toHaveBeenCalledWith(
        ...chunkLoadFailureCount(AppVersionCheckOutcomeEnum.CheckFailed),
      );
    });

    it('does not count a route that fails for another reason', async () => {
      setup().start(loadedVersion, siteName);

      await navigateTo(brokenRoutePath);
      await jest.advanceTimersByTimeAsync(0);

      expect(Sentry.metrics.count).not.toHaveBeenCalledWith(
        CHUNK_LOAD_FAILED_METRIC,
        expect.anything(),
        expect.anything(),
      );
    });
  });

  describe('when the data version is checked', () => {
    it('restarts the confirmation delay when a different data release appears before it ends', () => {
      startWithDataChecks(setup());
      returnToTab();
      respondToCheck(loadedVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS - APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();
      respondToCheck(loadedVersion, newerDataFile);

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);

      expect(httpMock.match(dataVersionUrl)).toHaveLength(0);
      expect(isShowingDataUpdateNotice()).toBe(false);
    });

    it('restarts the confirmation delay when the confirmation check finds a different data release', () => {
      startWithDataChecks(setup());
      returnToTab();
      respondToCheck(loadedVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedVersion, newerDataFile);

      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS - 1);

      expect(httpMock.match(dataVersionUrl)).toHaveLength(0);
      expect(isShowingDataUpdateNotice()).toBe(false);
    });

    it('shows the overlay once a different data release found by the confirmation check outlasts its own delay', () => {
      startWithDataChecks(setup());
      returnToTab();
      respondToCheck(loadedVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedVersion, newerDataFile);

      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedVersion, newerDataFile);

      expect(isShowingDataUpdateNotice()).toBe(true);
    });

    it('shows the overlay once a different data release outlasts its own confirmation delay', () => {
      startWithDataChecks(setup());
      returnToTab();
      respondToCheck(loadedVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS - APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();
      respondToCheck(loadedVersion, newerDataFile);

      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedVersion, newerDataFile);

      expect(isShowingDataUpdateNotice()).toBe(true);
    });

    it('does not check the data version without a data version URL', () => {
      const appUpdateService = setup();
      loadFooterDataVersion();
      appUpdateService.start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(loadedVersion);

      expect(httpMock.match(dataVersionUrl)).toHaveLength(0);
    });

    it('detects a later data release when the footer did not load a data version', () => {
      setup().start(loadedVersion, siteName, dataVersionUrl);

      returnToTab();
      respondToCheck(loadedVersion, loadedDataVersion);
      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();
      respondToCheck(loadedVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedVersion, newerDataVersion);

      expect(isShowingDataUpdateNotice()).toBe(true);
    });

    it('does not keep an unknown data version as the one the tab loaded with', () => {
      setup().start(loadedVersion, siteName, dataVersionUrl);

      returnToTab();
      respondToCheck(loadedVersion, unknownDataVersion);

      expect(TestBed.inject(VersionService).getLoadedDataVersion()).toBeNull();
    });

    it('checks the data version alongside the app version, bypassing the app HTTP interceptors', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedVersion, loadedDataVersion);

      expect(appInterceptor).not.toHaveBeenCalled();
      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it('does not show the data update overlay on the first mismatch', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedVersion, newerDataVersion);

      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it('shows the data update overlay once a new data release outlasts the confirmation delay', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedVersion, newerDataVersion);

      expect(isShowingDataUpdateNotice()).toBe(true);
    });

    it('counts a new data file as a new data release', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedVersion, newerDataFile);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedVersion, newerDataFile);

      expect(isShowingDataUpdateNotice()).toBe(true);
    });

    it('counts the stale data in Sentry metrics with both data versions and the first trigger', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedVersion, newerDataVersion);

      const versionService = TestBed.inject(VersionService);
      expect(Sentry.metrics.count).toHaveBeenCalledWith(STALE_DATA_METRIC, 1, {
        attributes: {
          clientVersion: versionService.formatDataVersion(loadedDataVersion),
          serverVersion: versionService.formatDataVersion(newerDataVersion),
          trigger: AppUpdateCheckTriggerEnum.Visibility,
          mismatchedVersions: MismatchedVersionsEnum.Data,
        },
      });
    });

    it('stays current when the server data version is unknown', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedVersion, unknownDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);

      expect(httpMock.match(dataVersionUrl)).toHaveLength(0);
      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it('stays current and counts a transient data mismatch when the confirmation matches', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedVersion, loadedDataVersion);

      expect(errorOverlayService.isOverlayVisible()).toBe(false);
      expect(Sentry.metrics.count).toHaveBeenCalledWith(
        TRANSIENT_DATA_VERSION_MISMATCH_METRIC,
        1,
        expect.anything(),
      );
    });

    it('retries a failed data confirmation check after the minimum interval', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondWithServerVersion(loadedVersion);
      httpMock.expectOne(dataVersionUrl).error(new ProgressEvent('error'));
      expect(errorOverlayService.isOverlayVisible()).toBe(false);

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      respondToCheck(loadedVersion, newerDataVersion);

      expect(isShowingDataUpdateNotice()).toBe(true);
    });

    it('does not treat a new data release as explaining a failed chunk load', async () => {
      const appUpdateService = setup();
      startWithDataChecks(appUpdateService);

      const isVersionMismatch = appUpdateService.isAppStale(
        AppUpdateCheckTriggerEnum.ChunkLoadError,
      );
      respondToCheck(loadedVersion, newerDataVersion);

      await expect(isVersionMismatch).resolves.toBe(false);
      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it('shows the app update notice when both a deploy and a data release are confirmed', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(newerVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(newerVersion, newerDataVersion);
      expect(isShowingDataUpdateNotice()).toBe(true);

      jest.advanceTimersByTime(APP_UPDATE_CONFIRM_DELAY_MS - DATA_UPDATE_CONFIRM_DELAY_MS);
      respondWithServerVersion(newerVersion);

      expect(isShowingAppUpdateNotice()).toBe(true);
    });

    it('stops checking once the data is known to be stale', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedVersion, newerDataVersion);

      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);
      returnToTab();

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      expect(httpMock.match(dataVersionUrl)).toHaveLength(0);
    });

    it('checks the data version when asked after an HTTP error', () => {
      const appUpdateService = setup();
      startWithDataChecks(appUpdateService);

      appUpdateService.requestCheck(AppUpdateCheckTriggerEnum.HttpError);
      respondToCheck(loadedVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedVersion, newerDataVersion);

      expect(isShowingDataUpdateNotice()).toBe(true);
    });
  });

  describe('when reporting which versions differed', () => {
    function staleMetricMismatchedVersions(staleMetric: string) {
      const staleMetricCall = (Sentry.metrics.count as jest.Mock).mock.calls.find(
        ([metricName]) => metricName === staleMetric,
      );
      return staleMetricCall?.[2].attributes.mismatchedVersions;
    }

    it('reports only the app version when the data version still matches', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(newerVersion, loadedDataVersion);
      jest.advanceTimersByTime(APP_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(newerVersion, loadedDataVersion);

      expect(staleMetricMismatchedVersions(STALE_APP_METRIC)).toBe(MismatchedVersionsEnum.App);
    });

    it('reports only the data version when the app version still matches', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(loadedVersion, newerDataVersion);

      expect(staleMetricMismatchedVersions(STALE_DATA_METRIC)).toBe(MismatchedVersionsEnum.Data);
    });

    it('reports both versions on each stale metric when a deploy and a data release are both confirmed', () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(newerVersion, newerDataVersion);
      jest.advanceTimersByTime(DATA_UPDATE_CONFIRM_DELAY_MS);
      respondToCheck(newerVersion, newerDataVersion);
      jest.advanceTimersByTime(APP_UPDATE_CONFIRM_DELAY_MS - DATA_UPDATE_CONFIRM_DELAY_MS);
      respondWithServerVersion(newerVersion);

      expect(staleMetricMismatchedVersions(STALE_DATA_METRIC)).toBe(
        MismatchedVersionsEnum.AppAndData,
      );
      expect(staleMetricMismatchedVersions(STALE_APP_METRIC)).toBe(
        MismatchedVersionsEnum.AppAndData,
      );
    });

    it('reports both versions when a failed chunk load finds both changed in one check', async () => {
      startWithDataChecks(setup());

      await navigateTo(missingChunkPath);
      respondToCheck(newerVersion, newerDataVersion);

      expect(staleMetricMismatchedVersions(STALE_APP_METRIC)).toBe(
        MismatchedVersionsEnum.AppAndData,
      );
    });

    it('reports both versions when a data mismatch is still awaiting confirmation', async () => {
      startWithDataChecks(setup());

      returnToTab();
      respondToCheck(loadedVersion, newerDataVersion);
      await navigateTo(missingChunkPath);
      respondWithServerVersion(newerVersion);
      httpMock.expectOne(dataVersionUrl).error(new ProgressEvent('error'));

      expect(staleMetricMismatchedVersions(STALE_APP_METRIC)).toBe(
        MismatchedVersionsEnum.AppAndData,
      );
    });
  });
});
