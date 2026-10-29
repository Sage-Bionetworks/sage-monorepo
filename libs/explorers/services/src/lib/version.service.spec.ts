import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import {
  DATA_VERSION_LOADING,
  DATA_VERSION_UNKNOWN,
  VERSION_CHECK_TIMEOUT_MS,
} from '@sagebionetworks/explorers/constants';
import { firstValueFrom, lastValueFrom, NEVER, Observable, of, throwError, toArray } from 'rxjs';
import {
  formatHttpErrorReportMessage,
  HttpRequestError,
  NO_RESPONSE_STATUS,
} from './http-request-error';
import { SourceLogger } from './logger.service';
import { PlatformService } from './platform.service';
import {
  DATA_VERSION_ERROR_MESSAGE,
  DataVersion,
  DataVersionService,
  VersionService,
} from './version.service';

describe('VersionService', () => {
  let service: VersionService;
  let mockPlatformService: Partial<PlatformService>;

  const failedRequest = new HttpRequestError(
    new HttpErrorResponse({
      status: NO_RESPONSE_STATUS,
      url: 'https://modeladexplorer.org/api/v1/data-version',
    }),
    'GET',
  );

  const mockDataVersion: DataVersion = {
    data_file: 'syn12345',
    data_version: '42',
    team_images_id: 'syn67890',
  };

  beforeEach(() => {
    mockPlatformService = {
      isBrowser: true,
      isServer: false,
    };

    TestBed.configureTestingModule({
      providers: [VersionService, { provide: PlatformService, useValue: mockPlatformService }],
    });

    service = TestBed.inject(VersionService);
  });

  afterEach(() => {
    jest.useRealTimers();
    jest.restoreAllMocks();
  });

  function mockDataVersionService(
    getDataVersion: DataVersionService['getDataVersion'],
  ): DataVersionService {
    return { getDataVersion };
  }

  it('should create', () => {
    expect(service).toBeDefined();
  });

  describe('getDataVersion$', () => {
    function collectEmissions(dataVersion$: Observable<string>): Promise<string[]> {
      return firstValueFrom(dataVersion$.pipe(toArray()));
    }

    function createServiceOnServer(): VersionService {
      TestBed.resetTestingModule();
      TestBed.configureTestingModule({
        providers: [
          VersionService,
          { provide: PlatformService, useValue: { isBrowser: false, isServer: true } },
        ],
      });
      return TestBed.inject(VersionService);
    }

    it('should emit only loading while rendering on the server', async () => {
      const serverService = createServiceOnServer();
      const dataVersionService = mockDataVersionService(jest.fn());

      const emissions = await collectEmissions(serverService.getDataVersion$(dataVersionService));

      expect(emissions).toEqual([DATA_VERSION_LOADING]);
      expect(dataVersionService.getDataVersion).not.toHaveBeenCalled();
    });

    it('should emit loading, then the formatted data version on success', async () => {
      const dataVersionService = mockDataVersionService(() => of(mockDataVersion));

      const emissions = await collectEmissions(service.getDataVersion$(dataVersionService));

      expect(emissions).toEqual([DATA_VERSION_LOADING, service.formatDataVersion(mockDataVersion)]);
    });

    it('should emit loading, then unknown and report a Sentry warning when the request errors', async () => {
      const warnSpy = jest.spyOn(SourceLogger.prototype, 'warn').mockImplementation();
      const errorSpy = jest.spyOn(SourceLogger.prototype, 'error').mockImplementation();
      const dataVersionService = mockDataVersionService(() => throwError(() => failedRequest));

      const emissions = await collectEmissions(service.getDataVersion$(dataVersionService));

      expect(emissions).toEqual([DATA_VERSION_LOADING, DATA_VERSION_UNKNOWN]);
      expect(warnSpy).toHaveBeenCalledTimes(1);
      expect(warnSpy).toHaveBeenCalledWith(
        formatHttpErrorReportMessage(DATA_VERSION_ERROR_MESSAGE, failedRequest),
        expect.objectContaining({ error: failedRequest }),
      );
      expect(errorSpy).not.toHaveBeenCalled();
    });
  });

  describe('when the data version request does not answer in time', () => {
    it('should emit unknown and leave a breadcrumb rather than a Sentry warning', async () => {
      jest.useFakeTimers();
      const logSpy = jest.spyOn(SourceLogger.prototype, 'log').mockImplementation();
      const warnSpy = jest.spyOn(SourceLogger.prototype, 'warn').mockImplementation();

      const emissions = firstValueFrom(
        service.getDataVersion$(mockDataVersionService(() => NEVER)).pipe(toArray()),
      );
      jest.advanceTimersByTime(VERSION_CHECK_TIMEOUT_MS);

      await expect(emissions).resolves.toEqual([DATA_VERSION_LOADING, DATA_VERSION_UNKNOWN]);
      expect(logSpy).toHaveBeenCalledWith('Data version request timed out', {
        timeoutMs: VERSION_CHECK_TIMEOUT_MS,
      });
      expect(warnSpy).not.toHaveBeenCalled();
      expect(service.getLoadedDataVersion()).toBeNull();
    });
  });

  describe('getLoadedDataVersion', () => {
    const nextDataVersion: DataVersion = { ...mockDataVersion, data_version: '43' };

    it('should have no loaded data version before the request succeeds', () => {
      expect(service.getLoadedDataVersion()).toBeNull();
    });

    it('should keep the data version the first successful request loaded', async () => {
      await lastValueFrom(
        service.getDataVersion$(mockDataVersionService(() => of(mockDataVersion))),
      );
      await lastValueFrom(
        service.getDataVersion$(mockDataVersionService(() => of(nextDataVersion))),
      );

      expect(service.getLoadedDataVersion()).toEqual(mockDataVersion);
    });

    it('should not keep an unknown data version, so a later request can still record one', async () => {
      const unknownDataVersion: DataVersion = { data_file: '', data_version: '' };

      await lastValueFrom(
        service.getDataVersion$(mockDataVersionService(() => of(unknownDataVersion))),
      );
      await lastValueFrom(
        service.getDataVersion$(mockDataVersionService(() => of(mockDataVersion))),
      );

      expect(service.getLoadedDataVersion()).toEqual(mockDataVersion);
    });

    it('should not keep a data version when the request errors', async () => {
      jest.spyOn(SourceLogger.prototype, 'warn').mockImplementation();
      const dataVersionService = mockDataVersionService(() => throwError(() => failedRequest));

      await lastValueFrom(service.getDataVersion$(dataVersionService));

      expect(service.getLoadedDataVersion()).toBeNull();
    });
  });

  describe('formatDataVersion', () => {
    it('should format data version correctly', () => {
      const result = service.formatDataVersion(mockDataVersion);
      expect(result).toBe('syn12345-v42');
    });
  });

  describe('formatAppVersion', () => {
    it('should remove -rc suffix', () => {
      expect(service.formatAppVersion('1.2.3-rc1')).toBe('1.2.3');
      expect(service.formatAppVersion('2.0.0-rc10')).toBe('2.0.0');
    });

    it('should keep version without -rc suffix unchanged', () => {
      expect(service.formatAppVersion('1.2.3')).toBe('1.2.3');
      expect(service.formatAppVersion('2.0.0-beta')).toBe('2.0.0-beta');
    });

    it('should return empty string for empty version', () => {
      expect(service.formatAppVersion('')).toBe('');
    });
  });

  describe('getSiteVersion', () => {
    it('should format site version with commit SHA', () => {
      const config = { appVersion: '1.2.3-rc1', commitSha: 'abc1234' };
      const result = service.getSiteVersion(config);
      expect(result).toBe('1.2.3-abc1234');
    });

    it('should format site version without commit SHA', () => {
      const config = { appVersion: '1.2.3-rc1', commitSha: '' };
      const result = service.getSiteVersion(config);
      expect(result).toBe('1.2.3');
    });

    it('should format site version with empty appVersion', () => {
      const config = {
        appVersion: '',
        commitSha: 'abc1234',
      };
      const result = service.getSiteVersion(config);
      expect(result).toBe('abc1234');
    });
  });
});
