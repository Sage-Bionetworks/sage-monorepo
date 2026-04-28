import { inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from './auth.service';
import { OnboardingService } from './onboarding.service';

const PENDING_PROMPT_KEY = 'bixarena.pendingPrompt';

@Injectable({ providedIn: 'root' })
export class BattleGateService {
  readonly authService = inject(AuthService);
  private readonly onboardingService = inject(OnboardingService);
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  readonly showLoginModal = signal(false);
  readonly showOnboardingModal = signal(false);

  private pendingResolver: ((passed: boolean) => void) | null = null;

  // TODO: if concurrent calls become an issue, cache the pending Promise so all callers share it
  async checkOnboarding(): Promise<boolean> {
    if (!this.onboardingService.hasCompleted()) {
      this.showOnboardingModal.set(true);
      return new Promise((resolve) => {
        this.pendingResolver = resolve;
      });
    }
    return true;
  }

  onOnboardingComplete(dontShowAgain: boolean): void {
    this.onboardingService.markComplete(dontShowAgain);
    this.showOnboardingModal.set(false);
    this.pendingResolver?.(true);
    this.pendingResolver = null;
  }

  onOnboardingDismiss(): void {
    this.showOnboardingModal.set(false);
    this.pendingResolver?.(false);
    this.pendingResolver = null;
  }

  onLoginComplete(): void {
    this.showLoginModal.set(false);
    this.authService.login();
  }

  onLoginCancel(): void {
    this.showLoginModal.set(false);
  }

  // Persist a prompt for the unauthenticated submit → login → /battle flow.
  // Stored in sessionStorage so it survives the OIDC redirect round-trips
  savePendingPrompt(text: string): void {
    if (!this.isBrowser) return;
    const trimmed = text.trim();
    if (!trimmed) return;
    try {
      sessionStorage.setItem(PENDING_PROMPT_KEY, trimmed);
    } catch {
      // sessionStorage can throw in private mode or when over quota — fail silent.
    }
  }

  // One-shot read: returns the saved prompt (trimmed) and clears it.
  // Whitespace-only values are treated as empty so external/manual seeds
  // can't bypass the trim that savePendingPrompt enforces.
  consumePendingPrompt(): string | null {
    if (!this.isBrowser) return null;
    try {
      const value = sessionStorage.getItem(PENDING_PROMPT_KEY);
      if (value !== null) sessionStorage.removeItem(PENDING_PROMPT_KEY);
      const trimmed = value?.trim();
      return trimmed ? trimmed : null;
    } catch {
      return null;
    }
  }
}
