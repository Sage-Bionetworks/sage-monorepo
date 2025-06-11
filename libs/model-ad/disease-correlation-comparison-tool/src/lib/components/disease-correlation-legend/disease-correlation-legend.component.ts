import { Component } from '@angular/core';
import { LegendPanelComponent } from '@sagebionetworks/explorers/comparison-tools';

@Component({
  selector: 'model-ad-disease-correlation-legend',
  imports: [LegendPanelComponent],
  templateUrl: './disease-correlation-legend.component.html',
  styleUrls: ['./disease-correlation-legend.component.scss'],
})
export class DiseaseCorrelationLegendComponent {
  colorChartLowerLabel = 'Negative Correlation';
  colorChartUpperLabel = 'Positive Correlation';
  colorChartText = `Circle color indicates the correlation between changes in gene expression in the model versus in humans with AD. Red shades indicate a negative correlation, while blue shades indicate a positive correlation.`;
  sizeChartLowerLabel = 'Significant';
  sizeChartUpperLabel = 'Insignificant';
  sizeChartText = `Circle diameter indicates P-value. Larger circles indicate higher statistical significance, while smaller circles indicate lower statistical significance.`;
}
