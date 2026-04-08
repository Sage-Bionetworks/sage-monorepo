import { computed, inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

export interface UserInfo {
  sub: string;
  preferred_username: string;
  email: string;
  email_verified: boolean;
  roles: string[];
  avatar_url?: string;
}

const CACHE_KEY = 'ba-user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
  readonly user = signal<UserInfo | null>(null);
  readonly isAuthenticated = computed(() => this.user() !== null);
  readonly cachedUsername = signal<string | null>(this.loadCache());

  async init(): Promise<void> {
    if (!this.isBrowser) return;
    try {
      const res = await fetch('/userinfo');
      if (res.ok) {
        const user: UserInfo = await res.json();
        this.user.set(user);
        this.saveCache(user.preferred_username);
      } else {
        this.clearCache();
      }
    } catch {
      this.clearCache();
    }
  }

  login(): void {
    if (!this.isBrowser) return;
    window.location.href = '/auth/login';
  }

  async logout(): Promise<void> {
    if (!this.isBrowser) return;
    try {
      await fetch('/auth/logout', { method: 'POST' });
    } finally {
      this.user.set(null);
      this.clearCache();
      window.location.href = '/';
    }
  }

  private loadCache(): string | null {
    if (!this.isBrowser) return null;
    try {
      return localStorage.getItem(CACHE_KEY);
    } catch {
      return null;
    }
  }

  private saveCache(username: string): void {
    try {
      localStorage.setItem(CACHE_KEY, username);
    } catch {
      // localStorage unavailable
    }
  }

  private clearCache(): void {
    this.cachedUsername.set(null);
    try {
      localStorage.removeItem(CACHE_KEY);
    } catch {
      // localStorage unavailable
    }
  }
}
