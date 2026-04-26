import {
  ChangeDetectionStrategy,
  Component,
  computed,
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

  readonly categoryChange = output<string>();
  readonly searchChange = output<string>();
  readonly filtersChange = output<LeaderboardFilters>();

  private readonly categoryPicker = viewChild.required<Popover>('categoryPicker');
  private readonly filterPopover = viewChild.required<Popover>('filterPopover');
  readonly pickerSearch = signal('');

  readonly licenseOptions: License[] = [License.OpenSource, License.Commercial];

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

  onSearchInput(event: Event): void {
    this.searchChange.emit((event.target as HTMLInputElement).value);
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
}
