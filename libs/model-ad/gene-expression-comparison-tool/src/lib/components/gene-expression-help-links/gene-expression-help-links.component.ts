import { Component, inject } from '@angular/core';
import { GeneExpressionLegendComponent } from '../gene-expression-legend/gene-expression-legend.component';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { HelpLinksComponent } from '@sagebionetworks/explorers/comparison-tools';

@Component({
  selector: 'model-ad-gene-expression-help-links',
  imports: [GeneExpressionLegendComponent, HelpLinksComponent],
  templateUrl: './gene-expression-help-links.component.html',
  styleUrls: ['./gene-expression-help-links.component.scss'],
})
export class GeneExpressionHelpLinksComponent {
  private legendService = inject(ComparisonToolService);

  toggleLegend() {
    this.legendService.toggleLegend();
  }

  toggleVisualizationOverview() {
    // TODO implement
  }
}
