import { inject, signal } from '@angular/core';
import {
  ComparisonToolFilter,
  ComparisonToolFilterOption,
} from '@sagebionetworks/explorers/models';
import { FilterService } from 'primeng/api';

export class ComparisonToolFilterService {
  private readonly filterService = inject(FilterService);

  private readonly filtersSignal = signal<ComparisonToolFilter[]>([]);
  private readonly searchTermSignal = signal<string>('');

  readonly filters = this.filtersSignal.asReadonly();
  readonly searchTerm = this.searchTermSignal.asReadonly();

  setFilters(filters: ComparisonToolFilter[]) {
    this.filtersSignal.set(filters);
  }

  updateSearchTerm(term: string) {
    this.searchTermSignal.set(term);
  }

  handleFilterChange(option: ComparisonToolFilterOption) {
    //TODO implement
  }

  getSelectedFilters() {
    //TODO implement
  }

  updateSearchFilters(filters: { [key: string]: any }) {
    //TODO implement
  }
}
