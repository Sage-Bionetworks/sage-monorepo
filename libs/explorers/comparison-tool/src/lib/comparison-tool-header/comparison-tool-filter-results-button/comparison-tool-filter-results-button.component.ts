import { Component, inject, input } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';

@Component({
  selector: 'explorers-comparison-tool-filter-results-button',
  imports: [ButtonModule, TooltipModule],
  templateUrl: './comparison-tool-filter-results-button.component.html',
  styleUrls: ['./comparison-tool-filter-results-button.component.scss'],
})
export class ComparisonToolFilterResultsButtonComponent {
  private readonly comparisonToolService = inject(ComparisonToolService);

  tooltip = input('');

  toggle() {
    this.comparisonToolService.toggleFilterPanel();
  }
}
