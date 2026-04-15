import { Component, input, model, output, signal } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { ModalDialogComponent } from '../modal-dialog/modal-dialog.component';

interface OnboardingStep {
  title: string;
  description: string;
}

@Component({
  selector: 'bixarena-onboarding-modal',
  imports: [ModalDialogComponent, ButtonModule],
  templateUrl: './onboarding-modal.component.html',
  styleUrl: './onboarding-modal.component.scss',
})
export class OnboardingModalComponent {
  readonly visible = model(false);
  readonly termsUrl = input('');
  readonly agreed = output<boolean>();
  readonly dismissed = output<void>();

  readonly steps: OnboardingStep[] = [
    {
      title: 'Start a Battle',
      description:
        'Pick a curated biomedical question or submit your own prompt. Two AI models are randomly chosen to face off, and their identities stay anonymous so you can focus purely on the response quality.',
    },
    {
      title: 'Select the Better',
      description:
        'Review the two AI-generated answers side by side and decide which model demonstrates clearer reasoning or insight. Your choice directly shapes model performance metrics.',
    },
    {
      title: 'Reveal & Impact',
      description:
        "Once you've made your choice, the models are revealed. Only biomedical battles count toward the daily leaderboard. Ready for another round? Jump into your next battle.",
    },
  ];

  readonly showAgreement = signal(false);
  readonly dontShowChecked = signal(false);

  next(): void {
    this.showAgreement.set(true);
  }

  back(): void {
    this.showAgreement.set(false);
  }

  agree(): void {
    this.visible.set(false);
    this.showAgreement.set(false);
    this.agreed.emit(this.dontShowChecked());
  }

  onDismiss(): void {
    this.visible.set(false);
    this.showAgreement.set(false);
    this.dismissed.emit();
  }
}
