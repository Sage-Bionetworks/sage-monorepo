import { Component } from '@angular/core';
import { LegendPanelComponent } from '@sagebionetworks/explorers/comparison-tools';

@Component({
  selector: 'model-ad-gene-expression-legend',
  imports: [LegendPanelComponent],
  templateUrl: './gene-expression-legend.component.html',
  styleUrls: ['./gene-expression-legend.component.scss'],
})
export class GeneExpressionLegendComponent {
  colorChartText = `Circle color indicates the log2 fold change value. Red shades indicate reduced expression levels in AD patients compared  to controls, while blue shades indicate increased expression levels in AD patients relative to controls.`;
  sizeChartText = `Circle diameter indicates P-value. Larger circles indicate higher statistical significance, while smaller circles indicate lower statistical significance.`;
}
