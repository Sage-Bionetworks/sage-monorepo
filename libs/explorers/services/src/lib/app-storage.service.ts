import { Injectable, inject } from '@angular/core';
import { PlatformService } from './platform.service';

@Injectable({
  providedIn: 'root',
})
export class AppStorageService {
  private readonly platformService = inject(PlatformService);

  private readonly HIDE_VISUALIZATION_OVERVIEW_KEY = 'hide_visualization_overview';
  private readonly PAGE_SIZE_KEY = 'comparison_tool_page_size';
  private readonly VALID_PAGE_SIZES: number[] = [10, 25, 50];
  readonly pageSizeOptions: number[] = this.VALID_PAGE_SIZES;
  private readonly DEFAULT_PAGE_SIZE = this.VALID_PAGE_SIZES[0];

  isVisualizationOverviewHidden(): boolean {
    if (this.platformService.isServer) {
      return false;
    }

    try {
      const value = localStorage.getItem(this.HIDE_VISUALIZATION_OVERVIEW_KEY);
      if (value === 'true') {
        return true;
      }
      if (value !== null) {
        localStorage.removeItem(this.HIDE_VISUALIZATION_OVERVIEW_KEY);
      }
      return false;
    } catch {
      return false;
    }
  }

  setVisualizationOverviewHidden(hidden: boolean): void {
    if (this.platformService.isServer) {
      return;
    }

    try {
      localStorage.setItem(this.HIDE_VISUALIZATION_OVERVIEW_KEY, String(hidden));
    } catch {
      // Silently fail if localStorage is unavailable
    }
  }

  getPageSize(): number {
    if (this.platformService.isServer) {
      return this.DEFAULT_PAGE_SIZE;
    }

    try {
      const stored = localStorage.getItem(this.PAGE_SIZE_KEY);
      if (stored === null) {
        return this.DEFAULT_PAGE_SIZE;
      }
      const parsed = Number(stored);
      if (this.VALID_PAGE_SIZES.includes(parsed)) {
        return parsed;
      }
      localStorage.removeItem(this.PAGE_SIZE_KEY);
      return this.DEFAULT_PAGE_SIZE;
    } catch {
      return this.DEFAULT_PAGE_SIZE;
    }
  }

  setPageSize(size: number): void {
    if (this.platformService.isServer) {
      return;
    }

    if (!this.VALID_PAGE_SIZES.includes(size)) {
      return;
    }

    try {
      localStorage.setItem(this.PAGE_SIZE_KEY, String(size));
    } catch {
      // Silently fail if localStorage is unavailable
    }
  }
}
