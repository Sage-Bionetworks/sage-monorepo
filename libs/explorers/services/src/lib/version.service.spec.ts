import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { GitHubService } from './github.service';
import { PlatformService } from './platform.service';
import { DataVersion, VersionConfig, VersionService } from './version.service';

// Mock Octokit to prevent import issues
jest.mock('@octokit/rest', () => ({
  Octokit: jest.fn().mockImplementation(() => ({
    rest: {
      repos: {
        listTags: jest.fn(),
      },
    },
    paginate: {
      iterator: jest.fn(),
    },
  })),
}));

describe('VersionService', () => {
  let service: VersionService;
  let mockGitHubService: Partial<GitHubService>;
  let mockPlatformService: Partial<PlatformService>;

  const mockVersionConfig: VersionConfig = {
    appVersion: '1.2.3-rc1',
    tagName: 'agora/v1.2.3',
  };

  const mockDataVersion: DataVersion = {
    data_file: 'syn12345',
    data_version: '42',
    team_images_id: 'syn67890',
  };

  beforeEach(() => {
    mockGitHubService = {
      getCommitSHA: jest.fn(),
    };

    mockPlatformService = {
      isBrowser: true,
    };

    TestBed.configureTestingModule({
      providers: [
        VersionService,
        { provide: GitHubService, useValue: mockGitHubService },
        { provide: PlatformService, useValue: mockPlatformService },
      ],
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

  describe('formatSiteVersion', () => {
    it('should format site version with commit SHA', () => {
      const config = { appVersion: '1.2.3-rc1', tagName: 'v1.2.3' };
      const result = service.formatSiteVersion('abc1234', config);
      expect(result).toBe('1.2.3-abc1234');
    });

    it('should format site version without commit SHA', () => {
      const config = { appVersion: '1.2.3-rc1', tagName: 'v1.2.3' };
      const result = service.formatSiteVersion('', config);
      expect(result).toBe('1.2.3');
    });

    it('should format site version with empty appVersion', () => {
      const config = {
        appVersion: '',
        tagName: 'v1.0.0',
      };
      const result = service.formatSiteVersion('abc1234', config);
      expect(result).toBe('abc1234');
    });
  });

  describe('getSiteVersion', () => {
    it('should get site version successfully with commit SHA', () => {
      (mockGitHubService.getCommitSHA as jest.Mock).mockReturnValue(of('abc1234'));

      let result = '';
      service.getSiteVersion(mockVersionConfig, (siteVersion) => {
        result = siteVersion;
      });

      expect(result).toBe('1.2.3-abc1234');
      expect(mockGitHubService.getCommitSHA).toHaveBeenCalledWith('agora/v1.2.3');
    });

    it('should fallback to app version when GitHub service returns empty SHA', () => {
      (mockGitHubService.getCommitSHA as jest.Mock).mockReturnValue(of(''));

      let result = '';
      service.getSiteVersion(mockVersionConfig, (siteVersion) => {
        result = siteVersion;
      });

      expect(result).toBe('1.2.3');
    });

    it('should call onSuccess with unknown when SHA is empty and appVersion is empty', () => {
      const config: VersionConfig = {
        appVersion: '',
        tagName: 'test/tag',
      };
      (mockGitHubService.getCommitSHA as jest.Mock).mockReturnValue(of(''));

      let result = '';
      service.getSiteVersion(config, (siteVersion) => {
        result = siteVersion;
      });

      expect(result).toBe('unknown');
    });
  });
});
