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

  private isOpenSignal = signal(false);
  private filtersSignal = signal<ComparisonToolFilter[]>([]);
  private activePaneSignal = signal(-1);

  readonly isOpen = this.isOpenSignal.asReadonly();
  readonly filters = this.filtersSignal.asReadonly();
  readonly activePane = this.activePaneSignal.asReadonly();

  setFilters(filters: ComparisonToolFilter[]) {
    this.filtersSignal.set(filters);
  }

  toggle() {
    this.isOpenSignal.update((current) => !current);

    // Reset activePane when closing
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
    //TODO implement
    throw new Error('Method not implemented.');
  }

  getSelectedFilters() {
    //TODO implement
    throw new Error('Method not implemented.');
  }

  updateSearchFilters(filters: { [key: string]: any }) {
    //TODO implement
    throw new Error('Method not implemented.');
  }
}
