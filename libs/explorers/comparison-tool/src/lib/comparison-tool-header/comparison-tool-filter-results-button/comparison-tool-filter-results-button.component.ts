import { Component, input, output, signal } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';

@Component({
  selector: 'explorers-comparison-tool-filter-results-button',
  imports: [ButtonModule, TooltipModule],
  templateUrl: './comparison-tool-filter-results-button.component.html',
  styleUrls: ['./comparison-tool-filter-results-button.component.scss'],
})
export class ComparisonToolFilterResultsButtonComponent {
  tooltip = input('');
  filterToggle = output<boolean>();
  private filterState = signal(false);

  toggle() {
    this.filterState.update((state) => !state);
    this.filterToggle.emit(this.filterState());
  }
}
