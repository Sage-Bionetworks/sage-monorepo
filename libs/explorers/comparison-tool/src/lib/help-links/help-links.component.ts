import { Component, computed, inject } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { LegendPanelComponent } from './legend-panel/legend-panel.component';
import { VisualizationOverviewPanelComponent } from './visualization-overview-panel/visualization-overview-panel.component';

@Component({
  selector: 'explorers-help-links',
  imports: [LegendPanelComponent, VisualizationOverviewPanelComponent],
  templateUrl: './help-links.component.html',
  styleUrls: ['./help-links.component.scss'],
})
export class HelpLinksComponent {
  comparisonToolService = inject(ComparisonToolService);

  viewConfig = this.comparisonToolService.viewConfig;

  hasData = computed(() => {
    const unpinnedData = this.comparisonToolService.unpinnedData();
    return unpinnedData.length > 0;
  });
}
