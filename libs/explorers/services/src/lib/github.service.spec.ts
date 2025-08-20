import { provideHttpClient } from '@angular/common/http';
import { GitHubService } from './github.service';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

describe('GitHubService', () => {
  let service: GitHubService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GitHubService, provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(GitHubService);
  });

  it('should create', () => {
    expect(service).toBeDefined();
  });

  it('should get sha', () => {
    const tag = 'agora/v0.0.2';
    const expectedSHA = 'Xb95bc34609ca7c9e6f64f0c5c0d3ca0df6880f9e';

    let result = '';
    service.getCommitSHA(tag).subscribe((response) => {
      result = response;
      expect(result).toBe(expectedSHA);
    });
  });
});
