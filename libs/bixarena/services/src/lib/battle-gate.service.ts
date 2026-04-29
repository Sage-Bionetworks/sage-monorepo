import { inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from './auth.service';
import { OnboardingService } from './onboarding.service';

const PENDING_PROMPT_KEY = 'bixarena.pendingPrompt';
const PENDING_EXAMPLE_PROMPT_ID_KEY = 'bixarena.pendingExamplePromptId';

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

  // TODO: after the auth-service supports a post-login redirect target,
  // route directly to /battle when a pending prompt exists so the user
  // doesn't land back on home with an unconsumed pending submission.
  onLoginComplete(): void {
    this.showLoginModal.set(false);
    this.authService.login();
  }

  onLoginCancel(): void {
    this.showLoginModal.set(false);
  }

  // sessionStorage survives the OIDC redirect round-trips. Both keys are
  // written as a unit — omitting examplePromptId removes it, so a free-form
  // composer submit can't inherit a stale id from a prior curated click.
  savePendingPrompt(text: string, examplePromptId?: string | null): void {
    if (!this.isBrowser) return;
    const trimmed = text.trim();
    if (!trimmed) return;
    try {
      sessionStorage.setItem(PENDING_PROMPT_KEY, trimmed);
      if (examplePromptId) {
        sessionStorage.setItem(PENDING_EXAMPLE_PROMPT_ID_KEY, examplePromptId);
      } else {
        sessionStorage.removeItem(PENDING_EXAMPLE_PROMPT_ID_KEY);
      }
    } catch {
      // sessionStorage can throw in private mode or when over quota — fail silent.
    }
  }

  // Whitespace-only prompts return null (defends against external seeds
  // that bypass save's trim). Both keys clear regardless.
  consumePendingPrompt(): { prompt: string; examplePromptId: string | null } | null {
    if (!this.isBrowser) return null;
    try {
      const prompt = sessionStorage.getItem(PENDING_PROMPT_KEY);
      const examplePromptId = sessionStorage.getItem(PENDING_EXAMPLE_PROMPT_ID_KEY);
      if (prompt !== null) sessionStorage.removeItem(PENDING_PROMPT_KEY);
      if (examplePromptId !== null) sessionStorage.removeItem(PENDING_EXAMPLE_PROMPT_ID_KEY);
      const trimmed = prompt?.trim();
      if (!trimmed) return null;
      return { prompt: trimmed, examplePromptId: examplePromptId || null };
    } catch {
      return null;
    }
  }
}
