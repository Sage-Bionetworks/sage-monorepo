import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { firstValueFrom } from 'rxjs';
import { GitHubService } from './github.service';

// Mock Octokit
const mockOctokit = {
  rest: {
    repos: {
      listTags: jest.fn(),
    },
  },
  paginate: {
    iterator: jest.fn(),
  },
};

jest.mock('@octokit/rest', () => ({
  Octokit: jest.fn().mockImplementation(() => mockOctokit),
}));

describe('GitHubService', () => {
  let service: GitHubService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GitHubService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(GitHubService);

    jest.clearAllMocks();
  });

  afterAll(() => {
    jest.restoreAllMocks();
  });

  it('should create', () => {
    expect(service).toBeDefined();
  });

  it('should get sha for tag on second page', async () => {
    const tag = 'agora/v4.1.0-rc2';
    const expectedSHA = 'd29a0a5';
    const fullSHA = 'd29a0a522f143e21188fc3fe8371bec6c14402b9';

    // Mock the paginate iterator to return the tag on the second page
    const mockIterator = {
      async *[Symbol.asyncIterator]() {
        yield {
          data: [
            {
              name: 'some-other-tag',
              commit: { sha: 'abc123def456' },
            },
          ],
        };
        yield {
          data: [
            {
              name: 'agora/v4.1.0-rc2',
              commit: { sha: fullSHA },
            },
          ],
        };
      },
    };

    mockOctokit.paginate.iterator.mockReturnValue(mockIterator);

    const result = await firstValueFrom(service.getCommitSHA(tag));
    expect(result).toBe(expectedSHA);
  });

  it('should handle non-existant tag', async () => {
    const tag = 'does-not-exist';

    const mockIterator = {
      async *[Symbol.asyncIterator]() {
        yield {
          data: [
            {
              name: 'some-other-tag',
              commit: { sha: 'abc123def456' },
            },
          ],
        };
        // No more pages
      },
    };

    mockOctokit.paginate.iterator.mockReturnValue(mockIterator);

    const result = await firstValueFrom(service.getCommitSHA(tag));
    expect(result).toBe('');
  });

  it('should return empty string when fetching tags fails', async () => {
    const tag = 'any-tag';

    mockOctokit.paginate.iterator.mockImplementation(() => {
      throw new Error('Network error');
    });

    const result = await firstValueFrom(service.getCommitSHA(tag));
    expect(result).toBe('');
  });

  describe('getShortSHA', () => {
    it('should return first 7 characters of a valid SHA', () => {
      const fullSHA = 'b95bc34609ca7c9e6f64f0c5c0d3ca0df6880f9e';
      const result = service.getShortSHA(fullSHA);
      expect(result).toBe('b95bc34');
    });

    it('should return empty string for invalid SHA', () => {
      expect(service.getShortSHA('')).toBe('');
      expect(service.getShortSHA('short')).toBe('');
      expect(service.getShortSHA('too-long-sha-that-is-not-40-characters')).toBe('');
    });
  });
});
