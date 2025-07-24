import { Component, inject } from '@angular/core';
import { DiseaseCorrelationLegendComponent } from '../disease-correlation-legend/disease-correlation-legend.component';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { HelpLinksComponent } from '@sagebionetworks/explorers/comparison-tools';

@Component({
  selector: 'model-ad-disease-correlation-help-links',
  imports: [DiseaseCorrelationLegendComponent, HelpLinksComponent],
  templateUrl: './disease-correlation-help-links.component.html',
  styleUrls: ['./disease-correlation-help-links.component.scss'],
})
export class DiseaseCorrelationHelpLinksComponent {
  private legendService = inject(ComparisonToolService);

  toggleLegend() {
    this.legendService.toggleLegend();
  }

  toggleVisualizationOverview() {
    // TODO implement
  }
}
