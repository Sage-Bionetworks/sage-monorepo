import { Component, inject } from '@angular/core';
import { ModelOverviewLegendComponent } from '../model-overview-legend/model-overview-legend.component';
import { ComparisonToolService } from '@sagebionetworks/model-ad/services';
import { HelpLinksComponent } from '@sagebionetworks/explorers/comparison-tools';

@Component({
  selector: 'model-ad-model-overview-help-links',
  imports: [ModelOverviewLegendComponent, HelpLinksComponent],
  templateUrl: './model-overview-help-links.component.html',
  styleUrls: ['./model-overview-help-links.component.scss'],
})
export class ModelOverviewHelpLinksComponent {
  private legendService = inject(ComparisonToolService);

  toggleLegend() {
    this.legendService.toggleLegend();
  }

  toggleVisualizationOverview() {
    // TODO implement
  }
}
