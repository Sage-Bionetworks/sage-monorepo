import { Component, inject } from '@angular/core';
import { HelpLinksComponent } from '@sagebionetworks/explorers/comparison-tools';
import { DiseaseCorrelationComparisonToolService } from '../../services/disease-correlation-comparison-tool.service';
import { DiseaseCorrelationLegendComponent } from '../disease-correlation-legend/disease-correlation-legend.component';

@Component({
  selector: 'model-ad-disease-correlation-help-links',
  imports: [DiseaseCorrelationLegendComponent, HelpLinksComponent],
  templateUrl: './disease-correlation-help-links.component.html',
  styleUrls: ['./disease-correlation-help-links.component.scss'],
})
export class DiseaseCorrelationHelpLinksComponent {
  private readonly legendService = inject(DiseaseCorrelationComparisonToolService);

  toggleLegend() {
    this.legendService.toggleLegend();
  }

  toggleVisualizationOverview() {
    // TODO implement
  }
}
