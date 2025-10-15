import { inject, Injectable, signal } from '@angular/core';
import {
  ComparisonToolFilter,
  ComparisonToolFilterOption,
} from '@sagebionetworks/explorers/models';
import { FilterService } from 'primeng/api';

@Injectable({
  providedIn: 'root',
})
export class ComparisonToolFilterService {
  private filterService = inject(FilterService);

  private filtersSignal = signal<ComparisonToolFilter[]>([]);
  private searchTermSignal = signal<string>('');

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
