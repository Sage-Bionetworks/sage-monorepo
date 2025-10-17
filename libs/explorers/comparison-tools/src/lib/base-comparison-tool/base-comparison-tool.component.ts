import { Component, inject, input, model } from '@angular/core';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';
import { ComparisonToolColumnsComponent } from '../comparison-tool-columns/comparison-tool-columns.component';
import { ComparisonToolControlsComponent } from '../comparison-tool-controls/comparison-tool-controls.component';
import { ComparisonToolFilterPanelComponent } from '../comparison-tool-filter-panel/comparison-tool-filter-panel.component';
import { ComparisonToolHeaderComponent } from '../comparison-tool-header/comparison-tool-header.component';

@Component({
  selector: 'explorers-base-comparison-tool',
  imports: [
    LoadingContainerComponent,
    ComparisonToolHeaderComponent,
    ComparisonToolFilterPanelComponent,
    ComparisonToolControlsComponent,
    ComparisonToolColumnsComponent,
  ],
  templateUrl: './base-comparison-tool.component.html',
  styleUrls: ['./base-comparison-tool.component.scss'],
})
export class BaseComparisonToolComponent {
  private readonly comparisonToolService = inject(ComparisonToolService);
  private readonly comparisonToolFilterService = inject(ComparisonToolFilterService);

  isLoading = input(true);
  showSignificanceControls = input(true);
  headerTitle = input.required<string>();
  filterResultsButtonTooltip = input.required<string>();

  currentConfig = this.comparisonToolService.currentConfig;
  resultsCount = this.comparisonToolService.totalResultsCount;

  isFilterPanelOpen = model(false);

  toggleFilterPanel() {
    this.isFilterPanelOpen.update((isOpen) => !isOpen);
  }

  onFiltersChanged(filters: ComparisonToolFilter[]) {
    this.comparisonToolFilterService.setFilters(filters);
  }
}
