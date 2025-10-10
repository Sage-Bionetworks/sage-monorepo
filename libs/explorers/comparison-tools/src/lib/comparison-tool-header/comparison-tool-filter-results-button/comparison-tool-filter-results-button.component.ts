import { Component, inject, input } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { TooltipModule } from 'primeng/tooltip';
import { FilterPanelService } from '../../filter-panel/filter-panel.service';

@Component({
  selector: 'explorers-comparison-tool-filter-results-button',
  imports: [ButtonModule, TooltipModule],
  templateUrl: './comparison-tool-filter-results-button.component.html',
  styleUrls: ['./comparison-tool-filter-results-button.component.scss'],
})
export class ComparisonToolFilterResultsButtonComponent {
  tooltip = input('');
  category = input('');
  filterPanelService = inject(FilterPanelService);

  toggle() {
    this.filterPanelService.toggle();
  }
}
