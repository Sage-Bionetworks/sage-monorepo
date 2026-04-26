import {
  ChangeDetectionStrategy,
  Component,
  computed,
  input,
  output,
  signal,
  viewChild,
} from '@angular/core';
import { Popover, PopoverModule } from 'primeng/popover';

export type LicenseFilter = 'all' | 'open-source' | 'commercial';

export interface LeaderboardFilters {
  readonly license: LicenseFilter;
}

export const DEFAULT_LEADERBOARD_FILTERS: LeaderboardFilters = {
  license: 'all',
};

export interface LeaderboardCategoryOption {
  readonly id: string;
  readonly name: string;
}

@Component({
  selector: 'bixarena-leaderboard-toolbar',
  imports: [PopoverModule],
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
  readonly pickerSearch = signal('');

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

  onSearchInput(event: Event): void {
    this.searchChange.emit((event.target as HTMLInputElement).value);
  }

  onLicenseChange(license: LicenseFilter): void {
    this.filtersChange.emit({ ...this.filters(), license });
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
}
