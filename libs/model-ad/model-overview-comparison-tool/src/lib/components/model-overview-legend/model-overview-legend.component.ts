import { Component } from '@angular/core';
import { LegendPanelComponent } from '@sagebionetworks/explorers/comparison-tools';

@Component({
  selector: 'model-ad-model-overview-legend',
  imports: [LegendPanelComponent],
  templateUrl: './model-overview-legend.component.html',
  styleUrls: ['./model-overview-legend.component.scss'],
})
export class ModelOverviewLegendComponent {
  colorChartText = `Circle color indicates the correlation between changes in gene expression in the model versus in humans with AD. Red shades indicate a negative correlation, while blue shades indicate a positive correlation.`;
  sizeChartText = `Circle diameter indicates P-value. Larger circles indicate higher statistical significance, while smaller circles indicate lower statistical significance.`;
}
