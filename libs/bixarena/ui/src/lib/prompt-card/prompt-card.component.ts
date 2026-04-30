import { Component, input, output } from '@angular/core';

@Component({
  selector: 'bixarena-prompt-card',
  templateUrl: './prompt-card.component.html',
  styleUrl: './prompt-card.component.scss',
})
export class PromptCardComponent {
  readonly question = input.required<string>();
  readonly category = input<string | undefined>(undefined);
  readonly battleCount = input<number | undefined>(undefined);
  readonly ctaText = input<string>('Battle this');
  readonly skeleton = input<boolean>(false);

  readonly cardClick = output<void>();

  onClick(): void {
    if (this.skeleton()) return;
    this.cardClick.emit();
  }
}
