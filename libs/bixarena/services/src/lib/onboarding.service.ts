import { inject, Injectable } from '@angular/core';
import { LocalStorageService } from '@sagebionetworks/web-shared/angular/storage';

const ONBOARDING_KEY = 'ba-onboarding-done';

@Injectable({ providedIn: 'root' })
export class OnboardingService {
  private readonly storage = inject(LocalStorageService);

  hasSeen(): boolean {
    return this.storage.getItem(ONBOARDING_KEY) === 'true';
  }

  markSeen(): void {
    this.storage.setItem(ONBOARDING_KEY, 'true');
  }
}
