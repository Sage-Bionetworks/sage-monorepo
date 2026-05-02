import { Component, computed, DestroyRef, effect, inject, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import {
  LeaderboardSearchQuery,
  LeaderboardSort,
  SortDirection,
} from '@sagebionetworks/bixarena/api-client';
import { HeroComponent } from '@sagebionetworks/bixarena/ui';
import { LeaderboardFacadeService } from './services/leaderboard.service';
import {
  LeaderboardSortChange,
  LeaderboardSortField,
  LeaderboardTableComponent,
} from './leaderboard-table/leaderboard-table.component';
import {
  LeaderboardToolbarComponent,
  LeaderboardView,
} from './leaderboard-toolbar/leaderboard-toolbar.component';
import { LeaderboardBarChartComponent } from './leaderboard-bar-chart/leaderboard-bar-chart.component';
import { DEFAULT_LEADERBOARD_FILTERS, LeaderboardFilters } from './leaderboard.filters';
import {
  DEFAULT_PAGE_SIZE,
  DEFAULT_SORT_FIELD,
  DEFAULT_SORT_ORDER,
  DEFAULT_CATEGORY_SLUG,
  PAGE_SIZE_OPTIONS,
  SEARCH_DEBOUNCE_MS,
} from './leaderboard.constants';

const SORT_FIELD_MAP: Record<LeaderboardSortField, LeaderboardSort> = {
  rank: 'rank',
  modelId: 'model_slug',
  btScore: 'bt_score',
  voteCount: 'vote_count',
};

@Component({
  selector: 'bixarena-leaderboard',
  imports: [
    LeaderboardTableComponent,
    LeaderboardToolbarComponent,
    LeaderboardBarChartComponent,
    HeroComponent,
    PaginatorModule,
    DatePipe,
    DecimalPipe,
  ],
  providers: [LeaderboardFacadeService],
  templateUrl: './leaderboard.component.html',
  styleUrl: './leaderboard.component.scss',
})
export class LeaderboardComponent {
  readonly facade = inject(LeaderboardFacadeService);

  readonly activeCategoryId = signal<string>(DEFAULT_CATEGORY_SLUG);
  readonly searchTerm = signal('');
  readonly filters = signal<LeaderboardFilters>(DEFAULT_LEADERBOARD_FILTERS);

  readonly pageFirst = signal(0);
  readonly pageRows = signal(DEFAULT_PAGE_SIZE);
  readonly pageSizeOptions = PAGE_SIZE_OPTIONS;

  readonly sortField = signal<LeaderboardSortField>(DEFAULT_SORT_FIELD);
  readonly sortOrder = signal<1 | -1>(DEFAULT_SORT_ORDER);

  readonly view = signal<LeaderboardView>('table');

  private readonly debouncedSearch = signal('');
  private searchTimer?: ReturnType<typeof setTimeout>;

  readonly categoryOptions = computed(() => {
    const list = this.facade.leaderboards();
    if (list.length === 0) {
      return [{ id: DEFAULT_CATEGORY_SLUG, name: 'Overall' }];
    }
    return list
      .filter((lb) => (lb.latestSnapshot?.entryCount ?? 0) > 0 || lb.id === DEFAULT_CATEGORY_SLUG)
      .map((lb) => ({ id: lb.id, name: lb.name }));
  });

  readonly query = computed<LeaderboardSearchQuery>(() => {
    const search = this.debouncedSearch().trim();
    const license = this.filters().license;
    const rows = this.pageRows();
    const pageNumber = rows > 0 ? Math.floor(this.pageFirst() / rows) : 0;
    return {
      pageNumber,
      pageSize: rows,
      sort: SORT_FIELD_MAP[this.sortField()],
      direction: this.sortOrder() === -1 ? SortDirection.Desc : SortDirection.Asc,
      ...(search ? { search } : {}),
      ...(license !== null ? { license } : {}),
    };
  });

  constructor() {
    void this.facade.loadLeaderboards();

    effect(() => {
      void this.facade.load(this.activeCategoryId(), this.query());
    });

    inject(DestroyRef).onDestroy(() => {
      if (this.searchTimer) clearTimeout(this.searchTimer);
    });
  }

  onSearchInput(value: string): void {
    this.searchTerm.set(value);
    if (this.searchTimer) clearTimeout(this.searchTimer);
    this.searchTimer = setTimeout(() => {
      this.debouncedSearch.set(value);
      this.pageFirst.set(0);
    }, SEARCH_DEBOUNCE_MS);
  }

  onFiltersChange(filters: LeaderboardFilters): void {
    this.filters.set(filters);
    this.pageFirst.set(0);
  }

  onCategoryChange(id: string): void {
    this.activeCategoryId.set(id);
    this.pageFirst.set(0);
  }

  onPageChange(event: PaginatorState): void {
    this.pageFirst.set(event.first ?? 0);
    this.pageRows.set(event.rows ?? DEFAULT_PAGE_SIZE);
  }

  onSortChange(change: LeaderboardSortChange): void {
    this.sortField.set(change.field);
    this.sortOrder.set(change.order);
    this.pageFirst.set(0);
  }

  retry(): void {
    void this.facade.load(this.activeCategoryId(), this.query());
  }

  onViewChange(next: LeaderboardView): void {
    this.view.set(next);
  }
}
