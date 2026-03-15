import { TestBed } from '@angular/core/testing';
import { PlatformService } from './platform.service';
import { DataVersion, VersionConfig, VersionService } from './version.service';

describe('VersionService', () => {
  let service: VersionService;
  let mockPlatformService: Partial<PlatformService>;
  const mockVersionConfig: VersionConfig = {
    appVersion: '1.2.3-rc1',
    commitSha: 'abc1234',
  };

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
