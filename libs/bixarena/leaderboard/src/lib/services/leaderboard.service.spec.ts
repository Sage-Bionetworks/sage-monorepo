import { TestBed } from '@angular/core/testing';
import { noop, of, throwError } from 'rxjs';
import {
  LeaderboardEntryPage,
  LeaderboardService as LeaderboardApiService,
} from '@sagebionetworks/bixarena/api-client';
import { LeaderboardFacadeService } from './leaderboard.service';

const samplePage: LeaderboardEntryPage = {
  number: 0,
  size: 2,
  totalElements: 2,
  totalPages: 1,
  hasNext: false,
  hasPrevious: false,
  updatedAt: '2026-04-25T10:00:00Z',
  snapshotId: 'snap-1',
  entries: [
    {
      id: 'entry-1',
      modelId: 'model-1',
      modelName: 'GPT-5',
      modelOrganization: 'OpenAI',
      modelUrl: 'https://example.com/gpt-5',
      license: 'proprietary',
      btScore: 1280,
      voteCount: 1500,
      rank: 1,
      bootstrapQ025: 1265,
      bootstrapQ975: 1295,
      createdAt: '2026-04-25T10:00:00Z',
    },
    {
      id: 'entry-2',
      modelId: 'model-2',
      modelName: 'Llama 4-Med',
      modelOrganization: 'Meta',
      modelUrl: 'https://example.com/llama-4-med',
      license: 'open-source',
      btScore: 1241,
      voteCount: 1100,
      rank: 2,
      bootstrapQ025: 1228,
      bootstrapQ975: 1260,
      createdAt: '2026-04-25T10:00:00Z',
    },
  ],
};

describe('LeaderboardFacadeService', () => {
  let service: LeaderboardFacadeService;
  let api: { getLeaderboard: jest.Mock; listLeaderboards: jest.Mock };

  beforeEach(() => {
    api = {
      getLeaderboard: jest.fn().mockReturnValue(of(samplePage)),
      listLeaderboards: jest.fn().mockReturnValue(of([])),
    };
    TestBed.configureTestingModule({
      providers: [LeaderboardFacadeService, { provide: LeaderboardApiService, useValue: api }],
    });
    service = TestBed.inject(LeaderboardFacadeService);
  });

  it('starts empty', () => {
    expect(service.entries()).toEqual([]);
    expect(service.leaderboards()).toEqual([]);
    expect(service.totalElements()).toBe(0);
    expect(service.totalVotes()).toBe(0);
    expect(service.snapshotUpdatedAt()).toBeNull();
    expect(service.loading()).toBe(false);
    expect(service.error()).toBeNull();
  });

  it('loads available leaderboards', async () => {
    api.listLeaderboards.mockReturnValueOnce(
      of([
        {
          id: 'overall',
          name: 'Overall',
          description: 'All models',
          updatedAt: '2026-04-25T10:00:00Z',
        },
      ]),
    );
    await service.loadLeaderboards();
    expect(api.listLeaderboards).toHaveBeenCalled();
    expect(service.leaderboards().length).toBe(1);
    expect(service.leaderboards()[0].id).toBe('overall');
  });

  it('loads entries and exposes snapshot metadata', async () => {
    await service.load();

    expect(api.getLeaderboard).toHaveBeenCalledWith('overall', {
      lookback: 7,
      pageSize: 1000,
    });
    expect(service.entries().length).toBe(2);
    expect(service.totalElements()).toBe(2);
    expect(service.snapshotUpdatedAt()).toBe('2026-04-25T10:00:00Z');
    expect(service.error()).toBeNull();
    expect(service.loading()).toBe(false);
  });

  it('computes totalVotes from loaded entries', async () => {
    await service.load();
    expect(service.totalVotes()).toBe(2600);
  });

  it('passes through search query', async () => {
    await service.load('overall', { pageSize: 50, search: 'gpt' });
    expect(api.getLeaderboard).toHaveBeenCalledWith('overall', {
      lookback: 7,
      pageSize: 50,
      search: 'gpt',
    });
  });

  it('exposes an error message and resets state on failure', async () => {
    api.getLeaderboard.mockReturnValueOnce(throwError(() => new Error('boom')));
    const errorSpy = jest.spyOn(console, 'error').mockImplementation(noop);

    await service.load();

    expect(service.error()).toBe('Could not load leaderboard');
    expect(service.entries()).toEqual([]);
    expect(service.totalElements()).toBe(0);
    expect(service.snapshotUpdatedAt()).toBeNull();
    expect(service.loading()).toBe(false);
    errorSpy.mockRestore();
  });
});
