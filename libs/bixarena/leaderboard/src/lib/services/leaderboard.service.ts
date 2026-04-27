import { computed, inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  LeaderboardEntry,
  LeaderboardListInner,
  LeaderboardSearchQuery,
  LeaderboardService as LeaderboardApiService,
} from '@sagebionetworks/bixarena/api-client';
import {
  DEFAULT_CATEGORY_SLUG,
  FETCH_ALL_PAGE_SIZE,
  LOOKBACK_DAYS,
} from '../leaderboard.constants';

@Injectable()
export class LeaderboardFacadeService {
  private readonly api = inject(LeaderboardApiService);

  readonly leaderboards = signal<LeaderboardListInner[]>([]);
  readonly entries = signal<LeaderboardEntry[]>([]);
  readonly totalElements = signal(0);
  readonly snapshotUpdatedAt = signal<string | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly totalVotes = computed(() =>
    this.entries().reduce((sum, e) => sum + (e.voteCount ?? 0), 0),
  );

  async loadLeaderboards(): Promise<void> {
    try {
      const list = await firstValueFrom(this.api.listLeaderboards());
      this.leaderboards.set(list ?? []);
      console.debug('✅ Fetched leaderboard categories', { count: list?.length ?? 0 });
    } catch (err) {
      console.error('❌ Failed to fetch leaderboard categories', err);
      this.leaderboards.set([]);
    }
  }

  async load(
    leaderboardId: string = DEFAULT_CATEGORY_SLUG,
    query: LeaderboardSearchQuery = { pageSize: FETCH_ALL_PAGE_SIZE },
  ): Promise<void> {
    this.loading.set(true);
    this.error.set(null);
    const finalQuery: LeaderboardSearchQuery = { lookback: LOOKBACK_DAYS, ...query };
    try {
      const page = await firstValueFrom(this.api.getLeaderboard(leaderboardId, finalQuery));
      this.entries.set(page.entries);
      this.totalElements.set(page.totalElements);
      this.snapshotUpdatedAt.set(page.updatedAt);
      console.debug('✅ Fetched leaderboard data', {
        leaderboardId,
        entries: page.entries.length,
        totalElements: page.totalElements,
        priorSnapshotId: page.priorSnapshotId ?? null,
      });
    } catch (err) {
      console.error('❌ Failed to fetch leaderboard data', {
        leaderboardId,
        query: finalQuery,
        err,
      });
      this.error.set('Could not load leaderboard');
      this.entries.set([]);
      this.totalElements.set(0);
      this.snapshotUpdatedAt.set(null);
    } finally {
      this.loading.set(false);
    }
  }
}
