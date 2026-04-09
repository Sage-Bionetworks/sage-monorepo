import { Injectable, inject } from '@angular/core';
import { LocalStorageService } from '@sagebionetworks/web-shared/angular/storage';

import { HIDE_VISUALIZATION_OVERVIEW_KEY, PAGE_SIZE_KEY } from './app-storage.constants';

@Injectable({
  providedIn: 'root',
})
export class AppStorageService {
  private readonly localStorageService = inject(LocalStorageService);

  private readonly VALID_PAGE_SIZES: number[] = [10, 25, 50];
  readonly pageSizeOptions: number[] = this.VALID_PAGE_SIZES;
  private readonly DEFAULT_PAGE_SIZE = this.VALID_PAGE_SIZES[0];

  isVisualizationOverviewHidden(): boolean {
    const value = this.localStorageService.getItem(HIDE_VISUALIZATION_OVERVIEW_KEY);
    if (value === 'true') {
      return true;
    }
    if (value !== null) {
      this.localStorageService.removeItem(HIDE_VISUALIZATION_OVERVIEW_KEY);
    }
    return false;
  }

  setVisualizationOverviewHidden(hidden: boolean): void {
    if (hidden) {
      this.localStorageService.setItem(HIDE_VISUALIZATION_OVERVIEW_KEY, 'true');
    } else {
      this.localStorageService.removeItem(HIDE_VISUALIZATION_OVERVIEW_KEY);
    }
  }

  getPageSize(): number {
    const stored = this.localStorageService.getItem(PAGE_SIZE_KEY);
    if (stored === null) {
      return this.DEFAULT_PAGE_SIZE;
    }
    const parsed = Number(stored);
    if (this.VALID_PAGE_SIZES.includes(parsed)) {
      return parsed;
    }
    this.localStorageService.removeItem(PAGE_SIZE_KEY);
    return this.DEFAULT_PAGE_SIZE;
  }

  setPageSize(size: number): void {
    if (!this.VALID_PAGE_SIZES.includes(size)) {
      return;
    }
    if (size === this.DEFAULT_PAGE_SIZE) {
      this.localStorageService.removeItem(PAGE_SIZE_KEY);
    } else {
      this.localStorageService.setItem(PAGE_SIZE_KEY, String(size));
    }
  }
}
