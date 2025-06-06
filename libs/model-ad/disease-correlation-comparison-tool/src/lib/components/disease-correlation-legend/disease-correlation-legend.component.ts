import { Component } from '@angular/core';
import { LegendPanelComponent } from '@sagebionetworks/explorers/comparison-tools';

@Component({
  selector: 'model-ad-disease-correlation-legend',
  imports: [LegendPanelComponent],
  templateUrl: './disease-correlation-legend.component.html',
  styleUrls: ['./disease-correlation-legend.component.scss'],
})
export class DiseaseCorrelationLegendComponent {
  colorChartText = `Circle color indicates the log2 fold change value. Red shades indicate reduced expression levels in AD patients compared  to controls, while blue shades indicate increased expression levels in AD patients relative to controls.`;
  sizeChartText = `Circle diameter indicates P-value. Larger circles indicate higher statistical significance, while smaller circles indicate lower statistical significance.`;
}
