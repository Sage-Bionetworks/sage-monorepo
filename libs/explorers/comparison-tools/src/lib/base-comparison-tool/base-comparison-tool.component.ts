import { Component, input, model } from '@angular/core';
import {
  ComparisonToolConfigFilter,
  ComparisonToolFilter,
} from '@sagebionetworks/explorers/models';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';
import { ComparisonToolFilterPanelComponent } from '../comparison-tool-filter-panel/comparison-tool-filter-panel.component';
import { ComparisonToolHeaderComponent } from '../comparison-tool-header/comparison-tool-header.component';

@Component({
  selector: 'explorers-base-comparison-tool',
  imports: [
    LoadingContainerComponent,
    ComparisonToolHeaderComponent,
    ComparisonToolFilterPanelComponent,
  ],
  templateUrl: './base-comparison-tool.component.html',
  styleUrls: ['./base-comparison-tool.component.scss'],
})
export class BaseComparisonToolComponent {
  isLoading = input(true);
  resultsCount = input(0);

  headerTitle = input.required<string>();
  filterResultsButtonTooltip = input.required<string>();
  filterConfigs = input.required<ComparisonToolConfigFilter[]>();
  onFiltersChange = input.required<(filters: ComparisonToolFilter[]) => void>();
  isFilterPanelOpen = model(false);

  toggleFilterPanel() {
    this.isFilterPanelOpen.update((isOpen) => !isOpen);
  }
}
