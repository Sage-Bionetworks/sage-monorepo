import {
  ChangeDetectionStrategy,
  Component,
  computed,
  DestroyRef,
  inject,
  OnInit,
  PLATFORM_ID,
  signal,
} from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { catchError, forkJoin, map, of, tap } from 'rxjs';
import {
  LeaderboardEntry,
  LeaderboardEntryPage,
  LeaderboardListInner,
  LeaderboardService,
} from '@sagebionetworks/bixarena/api-client';
import { ModelOrgLogoService } from '@sagebionetworks/bixarena/services';
import { AvatarComponent } from '@sagebionetworks/bixarena/ui';
import { TooltipModule } from 'primeng/tooltip';
import { LEADERBOARD_COLUMN_COUNT, LOOKBACK_DAYS } from '../home.constants';

interface LeaderboardColumn {
  slug: string;
  name: string;
  voteCount: number;
  entries: RenderedEntry[];
}

interface RenderedEntry {
  id: string;
  rank: number;
  modelId: string;
  modelName: string;
  modelOrganization: string | null;
  orgLogoUrl: string | null;
  orgLogoMono: boolean;
  score: number;
  rankDelta: number | null;
  absDelta: number;
}

@Component({
  selector: 'bixarena-leaderboard-section',
  imports: [RouterLink, AvatarComponent, TooltipModule],
  templateUrl: './leaderboard-section.component.html',
  styleUrl: './leaderboard-section.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LeaderboardSectionComponent implements OnInit {
  private readonly leaderboardApi = inject(LeaderboardService);
  private readonly orgLogo = inject(ModelOrgLogoService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  private readonly columnsSignal = signal<LeaderboardColumn[]>([]);
  // Hidden until enough data — keeps SSR/pre-fetch from rendering an empty grid.
  private readonly hidden = signal(true);

  readonly visible = computed(() => !this.hidden());
  readonly columns = this.columnsSignal.asReadonly();

  ngOnInit(): void {
    if (!this.isBrowser) return;

    console.debug('🔎 Fetching leaderboard list for home columns');
    this.leaderboardApi
      .listLeaderboards()
      .pipe(
        tap((all) => console.debug('✅ Fetched leaderboard list', { count: all.length })),
        catchError((err) => {
          console.error('❌ Failed to fetch leaderboard list', err);
          return of<LeaderboardListInner[]>([]);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((all) => {
        const withSnapshot = all.filter((l) => (l.latestSnapshot?.entryCount ?? 0) > 0);
        if (withSnapshot.length < LEADERBOARD_COLUMN_COUNT) return;

        forkJoin(
          withSnapshot.map((l) => {
            const query = { pageSize: LEADERBOARD_COLUMN_COUNT, lookback: LOOKBACK_DAYS };
            console.debug('🔎 Fetching leaderboard column', { leaderboardId: l.id, query });
            return this.leaderboardApi.getLeaderboard(l.id, query).pipe(
              tap((page) =>
                console.debug('✅ Fetched leaderboard column', {
                  leaderboardId: l.id,
                  entries: page.entries.length,
                  voteCount: page.voteCount,
                }),
              ),
              map((page) => this.toColumn(l, page)),
              catchError((err) => {
                console.error('❌ Failed to fetch leaderboard column', err);
                return of<LeaderboardColumn | null>(null);
              }),
            );
          }),
        )
          .pipe(takeUntilDestroyed(this.destroyRef))
          .subscribe((results) => {
            const columns = results
              .filter((c): c is LeaderboardColumn => c !== null && c.entries.length > 0)
              .sort((a, b) => b.voteCount - a.voteCount)
              .slice(0, LEADERBOARD_COLUMN_COUNT);
            if (columns.length < LEADERBOARD_COLUMN_COUNT) return;
            this.columnsSignal.set(columns);
            this.hidden.set(false);
          });
      });
  }

  diffStateOf(entry: RenderedEntry): 'up' | 'down' | 'flat' {
    const delta = entry.rankDelta;
    if (delta != null && delta > 0) return 'up';
    if (delta != null && delta < 0) return 'down';
    return 'flat';
  }

  private toColumn(meta: LeaderboardListInner, page: LeaderboardEntryPage): LeaderboardColumn {
    return {
      slug: meta.id,
      name: meta.name,
      voteCount: page.voteCount,
      entries: page.entries.slice(0, LEADERBOARD_COLUMN_COUNT).map((e) => this.renderedEntry(e)),
    };
  }

  private renderedEntry(entry: LeaderboardEntry): RenderedEntry {
    return {
      id: entry.id,
      rank: entry.rank,
      modelId: entry.modelId,
      modelName: entry.modelName,
      modelOrganization: entry.modelOrganization ?? null,
      orgLogoUrl: this.orgLogo.getLogoUrl(entry.modelOrganization),
      orgLogoMono: this.orgLogo.isMonoLogo(entry.modelOrganization),
      score: Math.round(entry.btScore),
      rankDelta: entry.rankDelta ?? null,
      absDelta: entry.rankDelta != null ? Math.abs(entry.rankDelta) : 0,
    };
  }
}
