import { inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from './auth.service';
import { AnalyticsService, BattleEntryPoint, LoginEntryPoint } from './analytics.service';

const PENDING_PROMPT_KEY = 'bixarena.pendingPrompt';
const PENDING_EXAMPLE_PROMPT_ID_KEY = 'bixarena.pendingExamplePromptId';
const PENDING_PROMPT_ENTRY_POINT_KEY = 'bixarena.pendingPromptEntryPoint';
const PENDING_PROMPT_TS_KEY = 'bixarena.pendingPromptTs';
const PENDING_PROMPT_OWNER_KEY = 'bixarena.pendingPromptOwner';
const PENDING_PROMPT_TTL_MS = 15 * 60 * 1000;
const PENDING_PROMPT_MAX_LENGTH = 5000;

export interface PendingPrompt {
  prompt: string;
  examplePromptId: string | null;
  entryPoint: BattleEntryPoint | null;
}

// Module-level helper so AuthService.logout can clear pending without
// importing the service class (would create a circular dep).
export function clearPendingPromptStorage(): void {
  if (typeof sessionStorage === 'undefined') return;
  try {
    sessionStorage.removeItem(PENDING_PROMPT_KEY);
    sessionStorage.removeItem(PENDING_EXAMPLE_PROMPT_ID_KEY);
    sessionStorage.removeItem(PENDING_PROMPT_ENTRY_POINT_KEY);
    sessionStorage.removeItem(PENDING_PROMPT_TS_KEY);
    sessionStorage.removeItem(PENDING_PROMPT_OWNER_KEY);
  } catch {
    /* ignore */
  }
}

@Injectable({ providedIn: 'root' })
export class BattleGateService {
  readonly authService = inject(AuthService);
  private readonly analytics = inject(AnalyticsService);
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  readonly showLoginModal = signal(false);
  private readonly loginEntryPoint = signal<LoginEntryPoint | null>(null);

  setLoginEntryPoint(entryPoint: LoginEntryPoint): void {
    this.loginEntryPoint.set(entryPoint);
  }

  onLoginComplete(): void {
    this.showLoginModal.set(false);
    const entryPoint = this.loginEntryPoint();
    this.loginEntryPoint.set(null);
    if (entryPoint) {
      this.analytics.trackLoginInitiated(entryPoint);
    }
    this.authService.login('/battle');
  }

  onLoginCancel(): void {
    this.showLoginModal.set(false);
    this.loginEntryPoint.set(null);
    this.consumePendingPrompt();
  }

  // Stamp owner from cachedUser when known so a different user logging in
  // on the same browser can't inherit this prompt. cachedUser is null for
  // first-time visitors — accepted, the TTL is the only defense in that case.
  savePendingPrompt(
    text: string,
    examplePromptId?: string | null,
    entryPoint?: BattleEntryPoint,
  ): void {
    if (!this.isBrowser) return;
    const trimmed = text.trim();
    if (!trimmed) return;
    try {
      sessionStorage.setItem(PENDING_PROMPT_KEY, trimmed);
      sessionStorage.setItem(PENDING_PROMPT_TS_KEY, String(Date.now()));
      const owner = this.authService.cachedUser()?.username ?? '';
      if (owner) {
        sessionStorage.setItem(PENDING_PROMPT_OWNER_KEY, owner);
      } else {
        sessionStorage.removeItem(PENDING_PROMPT_OWNER_KEY);
      }
      if (examplePromptId) {
        sessionStorage.setItem(PENDING_EXAMPLE_PROMPT_ID_KEY, examplePromptId);
      } else {
        sessionStorage.removeItem(PENDING_EXAMPLE_PROMPT_ID_KEY);
      }
      if (entryPoint) {
        sessionStorage.setItem(PENDING_PROMPT_ENTRY_POINT_KEY, entryPoint);
      } else {
        sessionStorage.removeItem(PENDING_PROMPT_ENTRY_POINT_KEY);
      }
    } catch {
      /* private mode / quota — fail silent */
    }
  }

  hasPendingPrompt(): boolean {
    return this.peekValidPrompt() !== null;
  }

  consumePendingPrompt(): PendingPrompt | null {
    if (!this.isBrowser) return null;
    const valid = this.peekValidPrompt();
    if (!valid) {
      this.clearPending();
      return null;
    }
    const examplePromptId = sessionStorage.getItem(PENDING_EXAMPLE_PROMPT_ID_KEY);
    const entryPoint = sessionStorage.getItem(
      PENDING_PROMPT_ENTRY_POINT_KEY,
    ) as BattleEntryPoint | null;
    this.clearPending();
    return { prompt: valid, examplePromptId: examplePromptId || null, entryPoint };
  }

  // Public so guards / logout can drop stale state explicitly.
  clearPending(): void {
    if (!this.isBrowser) return;
    clearPendingPromptStorage();
  }

  // Returns the trimmed prompt if storage holds a valid, fresh, in-shape,
  // owner-matched entry — otherwise null. Self-clears expired/invalid entries.
  private peekValidPrompt(): string | null {
    if (!this.isBrowser) return null;
    if (this.isExpired() || this.ownerMismatches()) {
      this.clearPending();
      return null;
    }
    try {
      const raw = sessionStorage.getItem(PENDING_PROMPT_KEY);
      const trimmed = raw?.trim();
      if (!trimmed) return null;
      if (trimmed.length > PENDING_PROMPT_MAX_LENGTH) {
        this.clearPending();
        return null;
      }
      return trimmed;
    } catch {
      return null;
    }
  }

  // Treat missing/non-numeric timestamp as expired.
  private isExpired(): boolean {
    try {
      const tsRaw = sessionStorage.getItem(PENDING_PROMPT_TS_KEY);
      if (!tsRaw) return true;
      const ts = parseInt(tsRaw, 10);
      if (Number.isNaN(ts)) return true;
      return Date.now() - ts > PENDING_PROMPT_TTL_MS;
    } catch {
      return true;
    }
  }

  // True if entry was stamped for a different user than the one currently
  // signed in. Anonymous-saved (no stamp) is accepted by any user.
  private ownerMismatches(): boolean {
    try {
      const owner = sessionStorage.getItem(PENDING_PROMPT_OWNER_KEY);
      if (!owner) return false;
      const current =
        this.authService.user()?.preferred_username ??
        this.authService.cachedUser()?.username ??
        null;
      if (!current) return false;
      return owner !== current;
    } catch {
      return false;
    }
  }
}
