import { inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { LocalStorageService } from '@sagebionetworks/web-shared/angular/storage';

const STORAGE_KEY = 'ba-t';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
  private readonly storage = inject(LocalStorageService);
  readonly isDark = signal(false);

  init(): void {
    if (!this.isBrowser) return;
    const stored = this.storage.getItem(STORAGE_KEY);
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    const dark = stored !== null ? stored === 'dark' : prefersDark;
    this.apply(dark);
  }

  toggle(): void {
    this.apply(!this.isDark());
  }

  private apply(dark: boolean): void {
    this.isDark.set(dark);
    if (!this.isBrowser) return;
    document.documentElement.classList.toggle('dark', dark);
    this.storage.setItem(STORAGE_KEY, dark ? 'dark' : 'light');
  }
}
