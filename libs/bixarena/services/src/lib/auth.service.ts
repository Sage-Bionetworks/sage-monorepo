import { computed, inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { ConfigService } from '@sagebionetworks/bixarena/config';

export interface UserInfo {
  sub: string;
  preferred_username: string;
  email: string;
  email_verified: boolean;
  roles: string[];
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
  private readonly configService = inject(ConfigService);

  readonly user = signal<UserInfo | null>(null);
  readonly isAuthenticated = computed(() => this.user() !== null);

  private get authUrl(): string {
    return this.configService.config.auth.csrBaseUrl;
  }

  async init(): Promise<void> {
    if (!this.isBrowser) return;
    try {
      const res = await fetch(`${this.authUrl}/userinfo`);
      if (res.ok) {
        this.user.set(await res.json());
      }
    } catch {
      // Not authenticated or auth service unavailable
    }
  }

  login(): void {
    if (!this.isBrowser) return;
    window.location.href = `${this.authUrl}/auth/login`;
  }

  async logout(): Promise<void> {
    if (!this.isBrowser) return;
    try {
      await fetch(`${this.authUrl}/auth/logout`, { method: 'POST' });
    } finally {
      this.user.set(null);
      window.location.href = '/';
    }
  }
}
