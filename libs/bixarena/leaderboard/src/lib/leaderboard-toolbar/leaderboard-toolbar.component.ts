import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';

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

  onSearchInput(event: Event): void {
    this.searchChange.emit((event.target as HTMLInputElement).value);
  }

  onLicenseChange(license: LicenseFilter): void {
    this.filtersChange.emit({ ...this.filters(), license });
  }
}
