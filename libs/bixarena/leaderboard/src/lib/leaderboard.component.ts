import { Component, computed, DestroyRef, effect, inject, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { LeaderboardEntry } from '@sagebionetworks/bixarena/api-client';
import { LeaderboardFacadeService } from './services/leaderboard.service';
import {
  LeaderboardSortChange,
  LeaderboardSortField,
  LeaderboardTableComponent,
} from './leaderboard-table/leaderboard-table.component';
import { LeaderboardToolbarComponent } from './leaderboard-toolbar/leaderboard-toolbar.component';
import { DEFAULT_LEADERBOARD_FILTERS, LeaderboardFilters } from './leaderboard.filters';
import {
  DEFAULT_PAGE_SIZE,
  DEFAULT_SORT_FIELD,
  DEFAULT_SORT_ORDER,
  DEFAULT_CATEGORY_SLUG,
  FETCH_ALL_PAGE_SIZE,
  PAGE_SIZE_OPTIONS,
  SEARCH_DEBOUNCE_MS,
} from './leaderboard.constants';

@Component({
  selector: 'bixarena-leaderboard',
  imports: [
    LeaderboardTableComponent,
    LeaderboardToolbarComponent,
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

  readonly displayedEntries = computed<LeaderboardEntry[]>(() => {
    const { license } = this.filters();
    const entries = this.facade.entries();
    if (license === null) return entries;
    return entries.filter((e) => e.license === license);
  });

  readonly sortedEntries = computed<LeaderboardEntry[]>(() => {
    const entries = [...this.displayedEntries()];
    const field = this.sortField();
    const order = this.sortOrder();
    entries.sort((a, b) => {
      const av = a[field];
      const bv = b[field];
      if (av === bv) return 0;
      if (av == null) return 1;
      if (bv == null) return -1;
      return (av < bv ? -1 : 1) * order;
    });
    return entries;
  });

  readonly paginatedEntries = computed<LeaderboardEntry[]>(() => {
    const all = this.sortedEntries();
    const first = this.pageFirst();
    const rows = this.pageRows();
    return all.slice(first, first + rows);
  });

  constructor() {
    void this.facade.loadLeaderboards();

    effect(() => {
      const search = this.debouncedSearch().trim();
      const slug = this.activeCategoryId();
      void this.facade.load(slug, {
        pageSize: FETCH_ALL_PAGE_SIZE,
        ...(search ? { search } : {}),
      });
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
    void this.facade.load(this.activeCategoryId(), { pageSize: FETCH_ALL_PAGE_SIZE });
  }
}
