import { inject, Injectable, signal } from '@angular/core';
import { AuthService } from './auth.service';
import { OnboardingService } from './onboarding.service';

@Injectable({ providedIn: 'root' })
export class BattleGateService {
  readonly authService = inject(AuthService);
  private readonly onboardingService = inject(OnboardingService);

  readonly showLoginModal = signal(false);
  readonly showOnboardingModal = signal(false);

  private pendingResolver: ((passed: boolean) => void) | null = null;

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
}
