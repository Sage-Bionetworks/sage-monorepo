import { inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import {
  LeaderboardEntry,
  LeaderboardListInner,
  LeaderboardSearchQuery,
  LeaderboardService as LeaderboardApiService,
} from '@sagebionetworks/bixarena/api-client';
import { DEFAULT_CATEGORY_SLUG, LOOKBACK_DAYS } from '../leaderboard.constants';

@Injectable()
export class LeaderboardFacadeService {
  private readonly api = inject(LeaderboardApiService);

  readonly leaderboards = signal<LeaderboardListInner[]>([]);
  readonly entries = signal<LeaderboardEntry[]>([]);
  readonly totalElements = signal(0);
  readonly entryCount = signal(0);
  readonly voteCount = signal(0);
  readonly snapshotUpdatedAt = signal<string | null>(null);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  async loadLeaderboards(): Promise<void> {
    try {
      const list = await firstValueFrom(this.api.listLeaderboards());
      this.leaderboards.set(list ?? []);
      const total = list?.length ?? 0;
      const withEntries = (list ?? []).filter(
        (l) => (l.latestSnapshot?.entryCount ?? 0) > 0,
      ).length;
      console.debug('✅ Fetched leaderboard categories', { total, withEntries });
    } catch (err) {
      console.error('❌ Failed to fetch leaderboard categories', err);
      this.leaderboards.set([]);
    }
  }

  async load(
    leaderboardId: string = DEFAULT_CATEGORY_SLUG,
    query: LeaderboardSearchQuery = {},
  ): Promise<void> {
    this.loading.set(true);
    this.error.set(null);
    const finalQuery: LeaderboardSearchQuery = { lookback: LOOKBACK_DAYS, ...query };
    console.debug('🔎 Fetching leaderboard data', { leaderboardId, query: finalQuery });
    try {
      const page = await firstValueFrom(this.api.getLeaderboard(leaderboardId, finalQuery));
      this.entries.set(page.entries);
      this.totalElements.set(page.totalElements);
      this.entryCount.set(page.entryCount);
      this.voteCount.set(page.voteCount);
      this.snapshotUpdatedAt.set(page.updatedAt);
      console.debug('✅ Fetched leaderboard data', {
        leaderboardId,
        entries: page.entries.length,
        totalElements: page.totalElements,
        entryCount: page.entryCount,
        voteCount: page.voteCount,
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
      this.entryCount.set(0);
      this.voteCount.set(0);
      this.snapshotUpdatedAt.set(null);
    } finally {
      this.loading.set(false);
    }
  }
}
