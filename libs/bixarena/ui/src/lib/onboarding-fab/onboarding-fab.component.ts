import { Component, inject, output } from '@angular/core';
import { AnalyticsService } from '@sagebionetworks/bixarena/services';

@Component({
  selector: 'bixarena-onboarding-fab',
  templateUrl: './onboarding-fab.component.html',
  styleUrl: './onboarding-fab.component.scss',
})
export class OnboardingFabComponent {
  readonly openRequested = output<void>();
  private readonly analytics = inject(AnalyticsService);

  open(): void {
    this.analytics.trackOnboardingReopened();
    this.openRequested.emit();
  }
}
