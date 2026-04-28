import {
  afterNextRender,
  ChangeDetectionStrategy,
  Component,
  computed,
  ElementRef,
  inject,
  Injector,
  input,
  output,
  signal,
  viewChild,
} from '@angular/core';
import { License } from '@sagebionetworks/bixarena/api-client';
import { KebabToTitlePipe } from '@sagebionetworks/bixarena/services';
import { Popover, PopoverModule } from 'primeng/popover';
import { DEFAULT_LEADERBOARD_FILTERS, LeaderboardFilters } from '../leaderboard.filters';

export interface LeaderboardCategoryOption {
  readonly id: string;
  readonly name: string;
}

export type LeaderboardView = 'table' | 'chart';

@Component({
  selector: 'bixarena-leaderboard-toolbar',
  imports: [PopoverModule, KebabToTitlePipe],
  templateUrl: './leaderboard-toolbar.component.html',
  styleUrl: './leaderboard-toolbar.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LeaderboardToolbarComponent {
  readonly categories = input.required<LeaderboardCategoryOption[]>();
  readonly activeCategoryId = input.required<string>();
  readonly searchTerm = input<string>('');
  readonly filters = input<LeaderboardFilters>(DEFAULT_LEADERBOARD_FILTERS);
  readonly view = input<LeaderboardView>('table');

  readonly categoryChange = output<string>();
  readonly searchChange = output<string>();
  readonly filtersChange = output<LeaderboardFilters>();
  readonly viewChange = output<LeaderboardView>();

  private readonly categoryPicker = viewChild.required<Popover>('categoryPicker');
  private readonly filterPopover = viewChild.required<Popover>('filterPopover');
  private readonly searchInput = viewChild<ElementRef<HTMLInputElement>>('searchInput');

  readonly pickerSearch = signal('');
  readonly searchExpanded = signal(false);

  readonly licenseOptions: License[] = [License.OpenSource, License.Proprietary];

  readonly activeCategoryName = computed(() => {
    const id = this.activeCategoryId();
    return this.categories().find((c) => c.id === id)?.name ?? id;
  });

  readonly filteredCategories = computed(() => {
    const term = this.pickerSearch().trim().toLowerCase();
    const all = this.categories();
    if (!term) return all;
    return all.filter((c) => c.name.toLowerCase().includes(term));
  });

  readonly activeFilterCount = computed(() => {
    let n = 0;
    if (this.filters().license !== null) n += 1;
    return n;
  });

  readonly hasActiveFilters = computed(() => this.activeFilterCount() > 0);

  readonly searchVisible = computed(() => this.searchExpanded() || this.searchTerm().length > 0);

  private readonly injector = inject(Injector);

  expandSearch(): void {
    this.searchExpanded.set(true);
    afterNextRender(() => this.searchInput()?.nativeElement.focus(), {
      injector: this.injector,
    });
  }

  onSearchInput(event: Event): void {
    this.searchChange.emit((event.target as HTMLInputElement).value);
  }

  onSearchBlur(): void {
    if (!this.searchTerm()) {
      this.searchExpanded.set(false);
    }
  }

  toggleCategoryPicker(event: Event): void {
    this.categoryPicker().toggle(event);
  }

  onPickerSearch(event: Event): void {
    this.pickerSearch.set((event.target as HTMLInputElement).value);
  }

  onCategorySelect(id: string): void {
    this.categoryChange.emit(id);
    this.pickerSearch.set('');
    this.categoryPicker().hide();
  }

  toggleFilterPopover(event: Event): void {
    this.filterPopover().toggle(event);
  }

  updateFilter<K extends keyof LeaderboardFilters>(key: K, value: LeaderboardFilters[K]): void {
    this.filtersChange.emit({ ...this.filters(), [key]: value });
  }

  resetFilter<K extends keyof LeaderboardFilters>(key: K): void {
    if (this.filters()[key] === DEFAULT_LEADERBOARD_FILTERS[key]) return;
    this.updateFilter(key, DEFAULT_LEADERBOARD_FILTERS[key]);
  }

  toggleLicense(license: License): void {
    const current = this.filters().license;
    this.updateFilter('license', current === license ? null : license);
  }

  clearAllFilters(): void {
    this.filtersChange.emit(DEFAULT_LEADERBOARD_FILTERS);
  }

  setView(next: LeaderboardView): void {
    if (this.view() !== next) this.viewChange.emit(next);
  }
}
