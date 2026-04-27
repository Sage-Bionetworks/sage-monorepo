import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import {
  LeaderboardListInner,
  LeaderboardService as LeaderboardApiService,
} from '@sagebionetworks/bixarena/api-client';
import { of } from 'rxjs';
import { LeaderboardComponent } from './leaderboard.component';

describe('LeaderboardComponent', () => {
  let apiStub: { getLeaderboard: jest.Mock; listLeaderboards: jest.Mock };

  function createFixture(): ComponentFixture<LeaderboardComponent> {
    const fixture = TestBed.createComponent(LeaderboardComponent);
    fixture.detectChanges();
    return fixture;
  }

  function makeLeaderboard(
    id: string,
    overrides: Partial<LeaderboardListInner> = {},
  ): LeaderboardListInner {
    return {
      id,
      name: id,
      description: '',
      updatedAt: '2026-04-25T10:00:00Z',
      ...overrides,
    };
  }

  beforeEach(async () => {
    apiStub = {
      getLeaderboard: jest.fn().mockReturnValue(
        of({
          number: 0,
          size: 0,
          totalElements: 0,
          totalPages: 0,
          hasNext: false,
          hasPrevious: false,
          updatedAt: new Date().toISOString(),
          snapshotId: 'snap-1',
          entries: [],
        }),
      ),
      listLeaderboards: jest.fn().mockReturnValue(of([])),
    };

    await TestBed.configureTestingModule({
      imports: [LeaderboardComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: LeaderboardApiService, useValue: apiStub },
      ],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = createFixture();
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should render the hero title', () => {
    const fixture = createFixture();
    const title = (fixture.nativeElement as HTMLElement).querySelector('.title');
    expect(title?.textContent).toContain('Leader');
  });

  describe('categoryOptions filtering', () => {
    it('hides categories with no latestSnapshot', async () => {
      apiStub.listLeaderboards.mockReturnValueOnce(
        of([
          makeLeaderboard('overall', {
            latestSnapshot: {
              id: 'snap-overall',
              visibility: 'public',
              createdAt: '2026-04-25T10:00:00Z',
              updatedAt: '2026-04-25T10:00:00Z',
              entryCount: 5,
            },
          }),
          makeLeaderboard('biochemistry'),
        ]),
      );
      const fixture = createFixture();
      await fixture.whenStable();
      fixture.detectChanges();

      expect(fixture.componentInstance.categoryOptions().map((o) => o.id)).toEqual(['overall']);
    });

    it('hides categories whose latestSnapshot has zero entries', async () => {
      apiStub.listLeaderboards.mockReturnValueOnce(
        of([
          makeLeaderboard('overall', {
            latestSnapshot: {
              id: 'snap-overall',
              visibility: 'public',
              createdAt: '2026-04-25T10:00:00Z',
              updatedAt: '2026-04-25T10:00:00Z',
              entryCount: 5,
            },
          }),
          makeLeaderboard('neuroscience', {
            latestSnapshot: {
              id: 'snap-neuro',
              visibility: 'public',
              createdAt: '2026-04-25T10:00:00Z',
              updatedAt: '2026-04-25T10:00:00Z',
              entryCount: 0,
            },
          }),
        ]),
      );
      const fixture = createFixture();
      await fixture.whenStable();
      fixture.detectChanges();

      expect(fixture.componentInstance.categoryOptions().map((o) => o.id)).toEqual(['overall']);
    });

    it('keeps overall even when it has no entries', async () => {
      apiStub.listLeaderboards.mockReturnValueOnce(of([makeLeaderboard('overall')]));
      const fixture = createFixture();
      await fixture.whenStable();
      fixture.detectChanges();

      expect(fixture.componentInstance.categoryOptions().map((o) => o.id)).toEqual(['overall']);
    });

    it('keeps categories with entries', async () => {
      apiStub.listLeaderboards.mockReturnValueOnce(
        of([
          makeLeaderboard('overall', {
            latestSnapshot: {
              id: 'snap-overall',
              visibility: 'public',
              createdAt: '2026-04-25T10:00:00Z',
              updatedAt: '2026-04-25T10:00:00Z',
              entryCount: 10,
            },
          }),
          makeLeaderboard('cancer-biology', {
            latestSnapshot: {
              id: 'snap-cancer',
              visibility: 'public',
              createdAt: '2026-04-25T10:00:00Z',
              updatedAt: '2026-04-25T10:00:00Z',
              entryCount: 4,
            },
          }),
        ]),
      );
      const fixture = createFixture();
      await fixture.whenStable();
      fixture.detectChanges();

      expect(fixture.componentInstance.categoryOptions().map((o) => o.id)).toEqual([
        'overall',
        'cancer-biology',
      ]);
    });
  });
});
