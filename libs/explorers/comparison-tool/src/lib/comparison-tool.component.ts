import { Component, inject, input } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';
import { ComparisonToolControlsComponent } from './comparison-tool-controls/comparison-tool-controls.component';
import { ComparisonToolFilterListComponent } from './comparison-tool-filter-list/comparison-tool-filter-list.component';
import { ComparisonToolFilterPanelComponent } from './comparison-tool-filter-panel/comparison-tool-filter-panel.component';
import { ComparisonToolFooterComponent } from './comparison-tool-footer/comparison-tool-footer.component';
import { ComparisonToolHeaderComponent } from './comparison-tool-header/comparison-tool-header.component';
import { ComparisonToolTableComponent } from './comparison-tool-table/comparison-tool-table.component';
import { HeatmapDetailsPanelComponent } from './comparison-tool-table/heatmap-details-panel/heatmap-details-panel.component';

@Component({
  selector: 'explorers-comparison-tool',
  imports: [
    LoadingContainerComponent,
    ComparisonToolHeaderComponent,
    ComparisonToolFilterPanelComponent,
    ComparisonToolControlsComponent,
    ComparisonToolFilterListComponent,
    ComparisonToolTableComponent,
    ComparisonToolFooterComponent,
    HeatmapDetailsPanelComponent,
  ],
  templateUrl: './comparison-tool.component.html',
  styleUrls: ['./comparison-tool.component.scss'],
})
export class ComparisonToolComponent {
  private readonly comparisonToolService = inject(ComparisonToolService);

  isLoading = input(true);

  currentConfig = this.comparisonToolService.currentConfig;
  loadingResultsCount = this.comparisonToolService.loadingResultsCount;
}
