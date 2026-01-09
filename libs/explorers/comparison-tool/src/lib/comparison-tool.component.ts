import { Component, effect, inject, input, model, untracked, viewChild } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';
import { ComparisonToolControlsComponent } from './comparison-tool-controls/comparison-tool-controls.component';
import { ComparisonToolFilterListComponent } from './comparison-tool-filter-list/comparison-tool-filter-list.component';
import { ComparisonToolFilterPanelComponent } from './comparison-tool-filter-panel/comparison-tool-filter-panel.component';
import { ComparisonToolHeaderComponent } from './comparison-tool-header/comparison-tool-header.component';
import { ComparisonToolTableComponent } from './comparison-tool-table/comparison-tool-table.component';
import { HeatmapDetailsPanelComponent } from './comparison-tool-table/heatmap-details-panel/heatmap-details-panel.component';
import { HelpLinksComponent } from './help-links/help-links.component';

@Component({
  selector: 'explorers-comparison-tool',
  imports: [
    LoadingContainerComponent,
    ComparisonToolHeaderComponent,
    ComparisonToolFilterPanelComponent,
    ComparisonToolControlsComponent,
    ComparisonToolFilterListComponent,
    ComparisonToolTableComponent,
    HelpLinksComponent,
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

  isFilterPanelOpen = model(false);

  toggleFilterPanel() {
    this.isFilterPanelOpen.update((isOpen) => !isOpen);
  }

  heatmapDetailsPanel = viewChild(HeatmapDetailsPanelComponent);

  constructor() {
    effect(() => {
      const panelData = this.comparisonToolService.heatmapDetailsPanelData();
      // Use untracked to avoid infinite loop with HeatmapDetailsPanelComponent
      untracked(() => {
        if (panelData) {
          this.heatmapDetailsPanel()?.show(panelData.event, panelData.data);
        } else {
          this.heatmapDetailsPanel()?.hide();
        }
      });
    });
  }
}
