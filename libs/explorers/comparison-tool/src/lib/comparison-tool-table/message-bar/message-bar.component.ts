import { Component, input, output } from '@angular/core';
import { ButtonModule } from 'primeng/button';

/**
 * A full-width message in place of table rows. Named after PrimeNG's severities. `warn` reports a
 * problem, such as a failed fetch, and is announced as an alert. `info` explains an expected empty
 * state.
 */
export type MessageBarSeverity = 'warn' | 'info';

@Component({
  selector: 'explorers-comparison-tool-message-bar',
  imports: [ButtonModule],
  templateUrl: './message-bar.component.html',
  styleUrls: ['./message-bar.component.scss'],
})
export class MessageBarComponent {
  message = input.required<string>();
  severity = input<MessageBarSeverity>('warn');
  // The button is shown only when a label is given
  buttonLabel = input<string | null>(null);
  loading = input<boolean>(false);

  buttonClick = output<void>();
}
