import { inject, Injectable, signal } from '@angular/core';
import { LocalStorageService } from '@sagebionetworks/web-shared/angular/storage';

const ONBOARDING_KEY = 'ba-onboarding-done';

@Injectable({ providedIn: 'root' })
export class OnboardingService {
  private readonly storage = inject(LocalStorageService);

  readonly hasCompleted = signal<boolean>(this.storage.getItem(ONBOARDING_KEY) === 'true');

  markComplete(dontShowAgain: boolean): void {
    this.hasCompleted.set(true);
    if (dontShowAgain) {
      this.storage.setItem(ONBOARDING_KEY, 'true');
    }
  }
}
