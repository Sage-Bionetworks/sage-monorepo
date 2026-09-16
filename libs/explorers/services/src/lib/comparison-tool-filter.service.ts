import { computed, inject, signal } from '@angular/core';
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

  setFilterOptionSelected(filterQueryParamKey: string, optionLabel: string, selected: boolean) {
    const existingOption = this.filters()
      .find((filter) => filter.query_param_key === filterQueryParamKey)
      ?.options.find((option) => option.label === optionLabel);
    if (!existingOption || existingOption.selected === selected) {
      return;
    }

    const updatedFilters = this.filters().map((filter) =>
      filter.query_param_key === filterQueryParamKey
        ? {
            ...filter,
            options: filter.options.map((option) =>
              option.label === optionLabel ? { ...option, selected } : option,
            ),
          }
        : filter,
    );
    this.comparisonToolService.updateQuery({
      filters: updatedFilters,
      pageNumber: this.comparisonToolService.FIRST_PAGE_NUMBER,
    });
  }

  clearAllFilters() {
    if (!this.hasSelectedFilters()) {
      return;
    }

    const updatedFilters = this.filters().map((filter) => ({
      ...filter,
      options: filter.options.map((option) => ({ ...option, selected: false })),
    }));
    this.comparisonToolService.updateQuery({
      filters: updatedFilters,
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
}
