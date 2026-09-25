import { TestBed } from '@angular/core/testing';
import { DATA_VERSION_LOADING, DATA_VERSION_UNKNOWN } from '@sagebionetworks/explorers/constants';
import { firstValueFrom, lastValueFrom, Observable, of, throwError, toArray } from 'rxjs';
import { SKIP_ERROR_REPORTING, SUPPRESS_ERROR_OVERLAY } from './http-context-tokens';
import { LoggerService } from './logger.service';
import { PlatformService } from './platform.service';
import { DataVersion, DataVersionService, VersionService } from './version.service';

describe('VersionService', () => {
  let service: VersionService;
  let mockPlatformService: Partial<PlatformService>;

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
    jest.restoreAllMocks();
  });

  it('should create', () => {
    expect(service).toBeDefined();
  });

  describe('getDataVersion$', () => {
    function mockDataVersionService(
      getDataVersion: DataVersionService['getDataVersion'],
    ): DataVersionService {
      return { getDataVersion };
    }

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

    it('should request without the error overlay and with caller-owned error reporting', async () => {
      const getDataVersion: jest.MockedFunction<DataVersionService['getDataVersion']> = jest
        .fn()
        .mockReturnValue(of(mockDataVersion));

      await lastValueFrom(service.getDataVersion$(mockDataVersionService(getDataVersion)));

      const context = getDataVersion.mock.calls[0][2]?.context;
      expect(context?.get(SUPPRESS_ERROR_OVERLAY)).toBe(true);
      expect(context?.get(SKIP_ERROR_REPORTING)).toBe(true);
    });

    it('should emit loading, then unknown and report a Sentry warning when the request errors', async () => {
      const error = new Error('Unable to connect to the server. Please check your connection.');
      const warnSpy = jest.spyOn(TestBed.inject(LoggerService), 'warn').mockImplementation();
      const dataVersionService = mockDataVersionService(() => throwError(() => error));

      const emissions = await collectEmissions(service.getDataVersion$(dataVersionService));

      expect(emissions).toEqual([DATA_VERSION_LOADING, DATA_VERSION_UNKNOWN]);
      expect(warnSpy).toHaveBeenCalledWith('Failed to fetch data version', { error });
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
