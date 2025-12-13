import { computed, inject, signal } from '@angular/core';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';
import { ComparisonToolService } from './comparison-tool.service';

export class ComparisonToolFilterService {
  private readonly comparisonToolService = inject(ComparisonToolService);
  private readonly DEFAULT_SIGNIFICANCE_THRESHOLD = 0.05;

  private readonly significanceThresholdSignal = signal(this.DEFAULT_SIGNIFICANCE_THRESHOLD);
  private readonly significanceThresholdActiveSignal = signal(false);

  readonly significanceThreshold = this.significanceThresholdSignal.asReadonly();
  readonly significanceThresholdActive = this.significanceThresholdActiveSignal.asReadonly();

  readonly searchTerm = this.comparisonToolService.searchTerm;
  readonly filters = this.comparisonToolService.filters;

  readonly hasSelectedFilters = computed(() => {
    return this.filters().some((filter) => filter.options.some((option) => option.selected));
  });

  setFilters(filters: ComparisonToolFilter[]) {
    // Use structuredClone to ensure a new reference is created
    const clonedFilters = structuredClone(filters);
    this.comparisonToolService.updateQuery({
      filters: clonedFilters,
      pageNumber: this.comparisonToolService.FIRST_PAGE_NUMBER,
    });
  }

  updateSearchTerm(term: string) {
    this.comparisonToolService.updateQuery({
      searchTerm: term,
      pageNumber: this.comparisonToolService.FIRST_PAGE_NUMBER,
    });
  }

  setSignificanceThresholdActive(value: boolean) {
    this.significanceThresholdActiveSignal.set(value);
  }

  setSignificanceThreshold(value: number) {
    this.significanceThresholdSignal.set(value);
  }

  getSelectedFilters() {
    //TODO implement
  }
}
