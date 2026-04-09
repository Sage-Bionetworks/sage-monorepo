import { computed, inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { UserInfo } from '@sagebionetworks/bixarena/api-client';

export interface CachedUser {
  username: string;
  avatarUrl?: string;
}

const CACHE_KEY = 'ba-user';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
  private readonly configService = inject(ConfigService);

  readonly user = signal<UserInfo | null>(null);
  readonly isAuthenticated = computed(() => this.user() !== null);
  readonly cachedUser = signal<CachedUser | null>(this.loadCache());

  private get authUrl(): string {
    return this.configService.config.auth.baseUrls.csr;
  }

  async init(): Promise<void> {
    if (!this.isBrowser) return;
    try {
      const res = await fetch('/userinfo');
      if (res.ok) {
        const user: UserInfo = await res.json();
        this.user.set(user);
        this.saveCache({ username: user.preferred_username ?? '', avatarUrl: user.avatar_url });
      } else {
        this.clearCache();
      }
    } catch {
      this.clearCache();
    }
  }

  login(): void {
    if (!this.isBrowser) return;
    window.location.href = `${this.authUrl}/auth/login`;
  }

  // Only clears local state after server confirms session invalidation.
  // On failure, user stays logged in and can retry.
  async logout(): Promise<void> {
    if (!this.isBrowser) return;
    try {
      const res = await fetch('/auth/logout', { method: 'POST' });
      if (!res.ok) return;
    } catch {
      return;
    }
    this.user.set(null);
    this.clearCache();
    window.location.href = '/';
  }

  private loadCache(): CachedUser | null {
    if (!this.isBrowser) return null;
    try {
      const raw = localStorage.getItem(CACHE_KEY);
      if (!raw) return null;
      return JSON.parse(raw) as CachedUser;
    } catch {
      return null;
    }
  }

  private saveCache(cached: CachedUser): void {
    try {
      localStorage.setItem(CACHE_KEY, JSON.stringify(cached));
    } catch {
      // localStorage unavailable
    }
  }

  private clearCache(): void {
    this.cachedUser.set(null);
    try {
      localStorage.removeItem(CACHE_KEY);
    } catch {
      // localStorage unavailable
    }
  }
}
