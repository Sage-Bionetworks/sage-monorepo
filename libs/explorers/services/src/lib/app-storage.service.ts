import { Injectable, inject } from '@angular/core';
import { LocalStorageService } from '@sagebionetworks/web-shared/angular/storage';

import {
  DEFAULT_PAGE_SIZE,
  HIDE_OVERWRITE_PINS_CONFIRMATION_KEY,
  HIDE_TUTORIAL_KEY,
  PAGE_SIZE_KEY,
  VALID_PAGE_SIZES,
} from './app-storage.constants';

@Injectable({
  providedIn: 'root',
})
export class AppStorageService {
  private readonly localStorageService = inject(LocalStorageService);

  isTutorialHidden(): boolean {
    return this.getFlag(HIDE_TUTORIAL_KEY);
  }

  setTutorialHidden(hidden: boolean): void {
    this.setFlag(HIDE_TUTORIAL_KEY, hidden);
  }

  isOverwritePinsConfirmationHidden(): boolean {
    return this.getFlag(HIDE_OVERWRITE_PINS_CONFIRMATION_KEY);
  }

  setOverwritePinsConfirmationHidden(hidden: boolean): void {
    this.setFlag(HIDE_OVERWRITE_PINS_CONFIRMATION_KEY, hidden);
  }

  getPageSize(): number {
    const stored = this.localStorageService.getItem(PAGE_SIZE_KEY);
    if (stored === null) {
      return DEFAULT_PAGE_SIZE;
    }
    const parsed = Number(stored);
    if (VALID_PAGE_SIZES.includes(parsed)) {
      return parsed;
    }
    this.localStorageService.removeItem(PAGE_SIZE_KEY);
    return DEFAULT_PAGE_SIZE;
  }

  setPageSize(size: number): void {
    if (!VALID_PAGE_SIZES.includes(size)) {
      return;
    }
    if (size === DEFAULT_PAGE_SIZE) {
      this.localStorageService.removeItem(PAGE_SIZE_KEY);
    } else {
      this.localStorageService.setItem(PAGE_SIZE_KEY, String(size));
    }
  }

  // A flag is stored only while set, so any other stored value is invalid and removed
  private getFlag(key: string): boolean {
    const value = this.localStorageService.getItem(key);
    if (value === 'true') {
      return true;
    }
    if (value !== null) {
      this.localStorageService.removeItem(key);
    }
    return false;
  }

  private setFlag(key: string, value: boolean): void {
    if (value) {
      this.localStorageService.setItem(key, 'true');
    } else {
      this.localStorageService.removeItem(key);
    }
  }
}
