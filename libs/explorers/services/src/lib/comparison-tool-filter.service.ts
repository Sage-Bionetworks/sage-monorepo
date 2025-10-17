import { inject, signal } from '@angular/core';
import {
  ComparisonToolFilter,
  ComparisonToolFilterOption,
} from '@sagebionetworks/explorers/models';
import { FilterService } from 'primeng/api';

export class ComparisonToolFilterService {
  private DEFAULT_SIGNIFICANCE_THRESHOLD = 0.05;

  private readonly filterService = inject(FilterService);
  private DEFAULT_SIGNIFICANCE_THRESHOLD = 0.05;

  private readonly filtersSignal = signal<ComparisonToolFilter[]>([]);
  private readonly searchTermSignal = signal<string>('');
  private readonly significanceThresholdSignal = signal(this.DEFAULT_SIGNIFICANCE_THRESHOLD);
  private readonly significanceThresholdActiveSignal = signal(false);

  readonly filters = this.filtersSignal.asReadonly();
  readonly searchTerm = this.searchTermSignal.asReadonly();
  readonly significanceThreshold = this.significanceThresholdSignal.asReadonly();
  readonly significanceThresholdActive = this.significanceThresholdActiveSignal.asReadonly();

  setFilters(filters: ComparisonToolFilter[]) {
    this.filtersSignal.set(filters);
  }

  updateSearchTerm(term: string) {
    this.searchTermSignal.set(term);
  }

  setSignificanceThresholdActive(value: boolean) {
    this.significanceThresholdActiveSignal.set(value);
  }

  setSignificanceThreshold(value: number) {
    this.significanceThresholdSignal.set(value);
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
