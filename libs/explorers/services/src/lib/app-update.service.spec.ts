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
  APP_UPDATE_MIN_CHECK_INTERVAL_MS,
  APP_UPDATE_POLL_INTERVAL_MS,
  APP_VERSION_PATH,
  getAppUpdateMessage,
  STALE_CLIENT_METRIC,
  TRANSIENT_VERSION_MISMATCH_METRIC,
  VERSION_CHECK_FAILED_METRIC,
  VERSION_ENDPOINT_MISCONFIGURED_WARNING,
} from '@sagebionetworks/explorers/constants';
import * as Sentry from '@sentry/angular';
import {
  AppUpdateCheckTriggerEnum,
  AppUpdateService,
  NO_RESPONSE_STATUS,
  VersionCheckFailureReason,
  VersionCheckFailureReasonEnum,
} from './app-update.service';
import { ErrorOverlayService } from './error-overlay.service';
import { LoggerService } from './logger.service';
import { PlatformService } from './platform.service';
import { VersionService } from './version.service';

jest.mock('@sentry/angular', () => ({
  metrics: { count: jest.fn() },
}));

const siteName = 'Model AD';
const loadedVersion: AppVersion = { appVersion: '4.0.0-rc3', commitSha: 'abc1234' };
const newerVersion: AppVersion = { appVersion: '4.0.0-rc4', commitSha: 'def5678' };
const unknownVersion: AppVersion = { appVersion: '', commitSha: '' };

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

  function setup({ isServer = false } = {}): AppUpdateService {
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
        { provide: LoggerService, useValue: logger },
      ],
    });
    httpMock = TestBed.inject(HttpTestingController);
    errorOverlayService = TestBed.inject(ErrorOverlayService);
    return TestBed.inject(AppUpdateService);
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
    httpMock.verify();
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

      expect(appUpdateService.isStale()).toBe(true);
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

      expect(appUpdateService.isStale()).toBe(true);
    });
  });

  describe('when the server responds with a version', () => {
    it('stays current when the server reports the version the tab loaded with', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(loadedVersion);

      expect(appUpdateService.isStale()).toBe(false);
      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });

    it('shows the app update overlay once a newer deploy outlasts the confirmation delay', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(newerVersion);
      confirmNewerVersion();

      expect(appUpdateService.isStale()).toBe(true);
      expect(errorOverlayService.activeMessage()).toBe(getAppUpdateMessage(siteName));
    });

    it('counts the stale tab in Sentry metrics with both versions and the first trigger', () => {
      setup().start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(newerVersion);
      confirmNewerVersion();

      const versionService = TestBed.inject(VersionService);
      expect(Sentry.metrics.count).toHaveBeenCalledTimes(1);
      expect(Sentry.metrics.count).toHaveBeenCalledWith(STALE_CLIENT_METRIC, 1, {
        attributes: {
          clientVersion: versionService.getSiteVersion(loadedVersion),
          serverVersion: versionService.getSiteVersion(newerVersion),
          trigger: AppUpdateCheckTriggerEnum.Visibility,
        },
      });
    });

    it('compares app versions when either side has no commit SHA', () => {
      const appUpdateService = setup();
      appUpdateService.start({ ...loadedVersion, commitSha: '' }, siteName);

      returnToTab();
      respondWithServerVersion(newerVersion);
      confirmNewerVersion();

      expect(appUpdateService.isStale()).toBe(true);
    });

    it('stays current when the server version is unknown', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(unknownVersion);

      expect(appUpdateService.isStale()).toBe(false);
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
    it('does not show the overlay on the first mismatch', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(newerVersion);

      expect(appUpdateService.isStale()).toBe(false);
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

      expect(appUpdateService.isStale()).toBe(false);
    });

    it('stays current and counts a transient mismatch when the confirmation matches', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(newerVersion);
      jest.advanceTimersByTime(APP_UPDATE_CONFIRM_DELAY_MS);
      respondWithServerVersion(loadedVersion);

      const versionService = TestBed.inject(VersionService);
      expect(appUpdateService.isStale()).toBe(false);
      expect(Sentry.metrics.count).toHaveBeenCalledTimes(1);
      expect(Sentry.metrics.count).toHaveBeenCalledWith(TRANSIENT_VERSION_MISMATCH_METRIC, 1, {
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

      expect(appUpdateService.isStale()).toBe(false);
    });

    it('confirms on the next check when the confirmation check fails', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      returnToTab();
      respondWithServerVersion(newerVersion);
      jest.advanceTimersByTime(APP_UPDATE_CONFIRM_DELAY_MS);
      respondWithNetworkFailure();
      expect(appUpdateService.isStale()).toBe(false);

      jest.advanceTimersByTime(APP_UPDATE_MIN_CHECK_INTERVAL_MS);
      returnToTab();
      respondWithServerVersion(newerVersion);

      expect(appUpdateService.isStale()).toBe(true);
    });

    it('resolves a first mismatch as stale without showing the overlay', async () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      const isStale = appUpdateService.checkNow(AppUpdateCheckTriggerEnum.ChunkLoadError);
      respondWithServerVersion(newerVersion);

      await expect(isStale).resolves.toBe(true);
      expect(errorOverlayService.isOverlayVisible()).toBe(false);
    });
  });

  describe('when the check fails', () => {
    const respondWithStatus = (status: number, statusText: string) => () =>
      httpMock.expectOne(APP_VERSION_PATH).flush(null, { status, statusText });
    const respondWithUnparsableBody = () =>
      httpMock
        .expectOne(APP_VERSION_PATH)
        .error(new ProgressEvent('error'), { status: HttpStatusCode.Ok, statusText: 'OK' });
    const respondWithBody = (body: string | object) => () =>
      httpMock.expectOne(APP_VERSION_PATH).flush(body);

    const failures: {
      scenario: string;
      respond: () => void;
      reason: VersionCheckFailureReason;
      status: number;
      isMisconfiguration: boolean;
    }[] = [
      {
        scenario: 'no response reaches the browser',
        respond: respondWithNetworkFailure,
        reason: VersionCheckFailureReasonEnum.Network,
        status: NO_RESPONSE_STATUS,
        isMisconfiguration: false,
      },
      {
        scenario: 'no healthy app task answers',
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
        const appUpdateService = setup();
        appUpdateService.start(loadedVersion, siteName);

        returnToTab();
        respond();

        expect(Sentry.metrics.count).toHaveBeenCalledWith(VERSION_CHECK_FAILED_METRIC, 1, {
          attributes: { reason, status, trigger: AppUpdateCheckTriggerEnum.Visibility },
        });
        expect(appUpdateService.isStale()).toBe(false);
        expect(errorOverlayService.isOverlayVisible()).toBe(false);
      },
    );

    it.each(failures.filter(({ isMisconfiguration }) => isMisconfiguration))(
      'raises a Sentry warning when $scenario',
      ({ respond, reason, status }) => {
        setup().start(loadedVersion, siteName);

        returnToTab();
        respond();

        expect(logger.warn).toHaveBeenCalledWith(VERSION_ENDPOINT_MISCONFIGURED_WARNING, {
          reason,
          status,
          trigger: AppUpdateCheckTriggerEnum.Visibility,
        });
      },
    );

    it.each(failures.filter(({ isMisconfiguration }) => !isMisconfiguration))(
      'raises no Sentry warning when $scenario',
      ({ respond }) => {
        setup().start(loadedVersion, siteName);

        returnToTab();
        respond();

        expect(logger.warn).not.toHaveBeenCalled();
      },
    );

    it('raises the misconfiguration warning once per tab', () => {
      setup().start(loadedVersion, siteName);
      const respondNotFound = respondWithStatus(HttpStatusCode.NotFound, 'Not Found');

      returnToTab();
      respondNotFound();
      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);
      respondNotFound();

      expect(Sentry.metrics.count).toHaveBeenCalledTimes(2);
      expect(logger.warn).toHaveBeenCalledTimes(1);
    });

    it('keeps checking after a failed check', () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);

      returnToTab();
      respondWithNetworkFailure();
      jest.advanceTimersByTime(APP_UPDATE_POLL_INTERVAL_MS);
      respondWithServerVersion(newerVersion);
      confirmNewerVersion();

      expect(appUpdateService.isStale()).toBe(true);
    });
  });

  describe('when an error hints the tab may be stale', () => {
    it('checks right away when a route chunk fails to load, even just after another check', async () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);
      returnToTab();
      respondWithServerVersion(loadedVersion);

      await navigateTo(missingChunkPath);
      respondWithServerVersion(newerVersion);
      confirmNewerVersion();

      expect(appUpdateService.isStale()).toBe(true);
      expect(Sentry.metrics.count).toHaveBeenCalledWith(
        STALE_CLIENT_METRIC,
        1,
        expect.objectContaining({
          attributes: expect.objectContaining({
            trigger: AppUpdateCheckTriggerEnum.ChunkLoadError,
          }),
        }),
      );
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

      expect(appUpdateService.isStale()).toBe(true);
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

      const firstCheck = appUpdateService.checkNow(AppUpdateCheckTriggerEnum.ChunkLoadError);
      const secondCheck = appUpdateService.checkNow(AppUpdateCheckTriggerEnum.ChunkLoadError);
      respondWithServerVersion(newerVersion);

      await expect(Promise.all([firstCheck, secondCheck])).resolves.toEqual([true, true]);
    });

    it('resolves that the tab is stale without another request once known', async () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);
      returnToTab();
      respondWithServerVersion(newerVersion);
      confirmNewerVersion();

      const isStale = appUpdateService.checkNow(AppUpdateCheckTriggerEnum.ChunkLoadError);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      await expect(isStale).resolves.toBe(true);
    });

    it('resolves that the tab is current without a request before it starts', async () => {
      const isStale = setup().checkNow(AppUpdateCheckTriggerEnum.ChunkLoadError);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      await expect(isStale).resolves.toBe(false);
    });

    it('resolves that the tab is current without a request while offline', async () => {
      const appUpdateService = setup();
      appUpdateService.start(loadedVersion, siteName);
      isBrowserOnline = false;

      const isStale = appUpdateService.checkNow(AppUpdateCheckTriggerEnum.ChunkLoadError);

      expect(httpMock.match(APP_VERSION_PATH)).toHaveLength(0);
      await expect(isStale).resolves.toBe(false);
    });
  });
});
