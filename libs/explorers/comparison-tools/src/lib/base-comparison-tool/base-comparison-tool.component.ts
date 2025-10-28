import { Component, inject, input, model } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';
import { ComparisonToolColumnsComponent } from '../comparison-tool-columns/comparison-tool-columns.component';
import { ComparisonToolControlsComponent } from '../comparison-tool-controls/comparison-tool-controls.component';
import { ComparisonToolFilterListComponent } from '../comparison-tool-filter-list/comparison-tool-filter-list.component';
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
    ComparisonToolFilterListComponent,
  ],
  templateUrl: './base-comparison-tool.component.html',
  styleUrls: ['./base-comparison-tool.component.scss'],
})
export class BaseComparisonToolComponent {
  private readonly comparisonToolService = inject(ComparisonToolService);

  isLoading = input(true);
  showSignificanceControls = input(true);

  currentConfig = this.comparisonToolService.currentConfig;
  resultsCount = this.comparisonToolService.totalResultsCount;

  isFilterPanelOpen = model(false);

  toggleFilterPanel() {
    this.isFilterPanelOpen.update((isOpen) => !isOpen);
  }
}
