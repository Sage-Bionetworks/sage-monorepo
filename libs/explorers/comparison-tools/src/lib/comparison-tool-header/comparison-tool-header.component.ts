import { Component, input, viewChild } from '@angular/core';
import {
  ComparisonToolFilter,
  ComparisonToolFilterConfig,
} from '@sagebionetworks/explorers/models';
import { ComparisonToolFilterPanelComponent } from './comparison-tool-filter-panel/comparison-tool-filter-panel.component';
import { ComparisonToolFilterResultsButtonComponent } from './comparison-tool-filter-results-button/comparison-tool-filter-results-button.component';
import { ComparisonToolShareURLButtonComponent } from './comparison-tool-share-url-button/comparison-tool-share-url-button.component';

@Component({
  selector: 'explorers-comparison-tool-header',
  imports: [
    ComparisonToolFilterResultsButtonComponent,
    ComparisonToolShareURLButtonComponent,
    ComparisonToolFilterPanelComponent,
  ],
  templateUrl: './comparison-tool-header.component.html',
  styleUrls: ['./comparison-tool-header.component.scss'],
})
export class ComparisonToolHeaderComponent {
  filterResultsButtonTooltip = input.required<string>();
  headerTitle = input.required<string>();
  filterConfigs = input.required<ComparisonToolFilterConfig[]>();
  onFiltersChange = input.required<(filters: ComparisonToolFilter[]) => void>();

  filterPanel = viewChild<ComparisonToolFilterPanelComponent>('filterPanel');

  toggleFilterPanel() {
    this.filterPanel()?.toggle();
  }
}
