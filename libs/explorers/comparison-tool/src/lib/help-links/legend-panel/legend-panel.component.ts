import { Component, ViewEncapsulation, inject } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { DialogModule } from 'primeng/dialog';
import { LegendComponent } from './legend/legend.component';

@Component({
  selector: 'explorers-legend-panel',
  imports: [DialogModule, LegendComponent],
  templateUrl: './legend-panel.component.html',
  styleUrls: ['./legend-panel.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class LegendPanelComponent {
  comparisonToolService = inject(ComparisonToolService);

  viewConfig = this.comparisonToolService.viewConfig;

  openVisualizationOverviewDialog() {
    // close the legend panel and open the visualization overview
    this.comparisonToolService.setLegendVisibility(false);
    this.comparisonToolService.setVisualizationOverviewVisibility(true);
  }
}
