import { isPlatformBrowser } from '@angular/common';
import { Injectable, PLATFORM_ID, inject } from '@angular/core';

import { LOGGER } from './logger';

@Injectable({ providedIn: 'root' })
export class LocalStorageService {
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
  private readonly logger = inject(LOGGER);

  getItem(key: string): string | null {
    if (!this.isBrowser) {
      return null;
    }
    try {
      return localStorage.getItem(key);
    } catch (e) {
      this.logger.error('Failed to get item from localStorage', e);
      return null;
    }
  }

  setItem(key: string, value: string): void {
    if (!this.isBrowser) {
      return;
    }
    try {
      localStorage.setItem(key, value);
    } catch (e) {
      this.logger.error('Failed to set item in localStorage', e);
    }
  }

  removeItem(key: string): void {
    if (!this.isBrowser) {
      return;
    }
    try {
      localStorage.removeItem(key);
    } catch (e) {
      this.logger.error('Failed to remove item from localStorage', e);
    }
  }
}
