import { inject, Injectable, PLATFORM_ID, signal } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from './auth.service';
import { OnboardingService } from './onboarding.service';

const PENDING_PROMPT_KEY = 'ba-pending-prompt';

@Injectable({ providedIn: 'root' })
export class BattleGateService {
  private readonly isBrowser = isPlatformBrowser(inject(PLATFORM_ID));
  readonly authService = inject(AuthService);
  private readonly onboardingService = inject(OnboardingService);

  readonly showLoginModal = signal(false);
  readonly showOnboardingModal = signal(false);

  private pendingPrompt: string | null = null;
  private pendingResolver: ((passed: boolean) => void) | null = null;

  async checkGates(prompt: string): Promise<boolean> {
    if (!this.authService.isAuthenticated()) {
      return this.waitForGate(prompt, this.showLoginModal);
    }
    if (!this.onboardingService.hasCompleted()) {
      return this.waitForGate(prompt, this.showOnboardingModal);
    }
    return true;
  }

  onLoginComplete(): void {
    this.saveSessionPrompt();
    this.showLoginModal.set(false);
    this.authService.login();
  }

  onLoginCancel(): void {
    this.resolveGate(false, this.showLoginModal);
  }

  onOnboardingComplete(dontShowAgain: boolean): void {
    this.onboardingService.markComplete(dontShowAgain);
    this.resolveGate(true, this.showOnboardingModal);
  }

  onOnboardingDismiss(): void {
    this.resolveGate(false, this.showOnboardingModal);
  }

  consumePendingPrompt(): string | null {
    if (!this.isBrowser) return null;
    try {
      const prompt = sessionStorage.getItem(PENDING_PROMPT_KEY);
      if (prompt) sessionStorage.removeItem(PENDING_PROMPT_KEY);
      return prompt;
    } catch {
      return null;
    }
  }

  private waitForGate(
    prompt: string,
    modalSignal: ReturnType<typeof signal<boolean>>,
  ): Promise<boolean> {
    this.pendingPrompt = prompt;
    modalSignal.set(true);
    return new Promise((resolve) => {
      this.pendingResolver = resolve;
    });
  }

  private resolveGate(passed: boolean, modalSignal: ReturnType<typeof signal<boolean>>): void {
    modalSignal.set(false);
    this.pendingResolver?.(passed);
    this.pendingResolver = null;
    this.pendingPrompt = null;
  }

  private saveSessionPrompt(): void {
    if (this.pendingPrompt && this.isBrowser) {
      try {
        sessionStorage.setItem(PENDING_PROMPT_KEY, this.pendingPrompt);
      } catch {
        // sessionStorage unavailable
      }
    }
  }
}
