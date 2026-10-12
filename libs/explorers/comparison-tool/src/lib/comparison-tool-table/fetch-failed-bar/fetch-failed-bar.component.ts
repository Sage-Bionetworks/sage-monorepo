import { Component, input, output } from '@angular/core';
import { ButtonModule } from 'primeng/button';

@Component({
  selector: 'explorers-comparison-tool-fetch-failed-bar',
  imports: [ButtonModule],
  templateUrl: './fetch-failed-bar.component.html',
  styleUrls: ['./fetch-failed-bar.component.scss'],
})
export class FetchFailedBarComponent {
  message = input.required<string>();
  buttonLabel = input.required<string>();
  loading = input<boolean>(false);

  retry = output<void>();
}
