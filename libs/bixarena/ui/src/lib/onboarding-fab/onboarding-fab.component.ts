import { Component, output } from '@angular/core';

@Component({
  selector: 'bixarena-onboarding-fab',
  templateUrl: './onboarding-fab.component.html',
  styleUrl: './onboarding-fab.component.scss',
})
export class OnboardingFabComponent {
  readonly openRequested = output<void>();

  open(): void {
    this.openRequested.emit();
  }
}
