import { inject, Injectable, signal } from '@angular/core';
import {
  ComparisonToolFilter,
  ComparisonToolFilterOption,
} from '@sagebionetworks/explorers/models';
import { FilterService } from 'primeng/api';
// import { intersectFilterCallback, urlLinkCallback } from './filter-helpers';

@Injectable({
  providedIn: 'root',
})
export class FilterPanelService {
  private filterService = inject(FilterService);

  private isOpenSignal = signal(false);
  private filtersSignal = signal<ComparisonToolFilter[]>([]);
  private activePaneSignal = signal(-1);

  readonly isOpen = this.isOpenSignal.asReadonly();
  readonly filters = this.filtersSignal.asReadonly();
  readonly activePane = this.activePaneSignal.asReadonly();

  setFilters(filters: ComparisonToolFilter[]) {
    // this.filtersSignal.set([...filters]);
    this.filtersSignal.set(filters);
  }

  toggle() {
    this.isOpenSignal.update((current) => !current);

    // Reset active pane when closing
    if (!this.isOpen()) {
      this.activePaneSignal.set(-1);
    }
  }

  close() {
    this.isOpenSignal.set(false);
    this.activePaneSignal.set(-1);
  }

  open() {
    this.isOpenSignal.set(true);
  }

  setActivePane(index: number) {
    this.activePaneSignal.set(index);
  }

  handleFilterChange(option: ComparisonToolFilterOption) {
    // update filters to reflect changed option
    const currentFilters = this.filtersSignal();
    const updatedFilters = currentFilters.map((filter) => ({
      ...filter,
      options: filter.options.map((o) =>
        // o.value === option.value ? { ...o, selected: !o.selected } : o

        // set selected based on label match
        o.label === option.label ? { ...o, selected: option.selected } : o,
      ),
    }));
    this.filtersSignal.set(updatedFilters);
  }

  getSelectedFilters() {
    const values: { [key: string]: string | number | string[] | number[] } = {};

    for (const filter of this.filters()) {
      const selectedOptions = filter.options
        .filter((option) => option.selected)
        .map((option) => option.label);
      if (selectedOptions.length) {
        values[filter.name] = selectedOptions;
      }
    }

    return values;
  }

  updateSearchFilters(filters: { [key: string]: any }) {
    throw new Error('Method not implemented.');
  }
}
