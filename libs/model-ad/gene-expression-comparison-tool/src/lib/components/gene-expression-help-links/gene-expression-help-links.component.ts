import { Component, inject } from '@angular/core';
import { HelpLinksComponent } from '@sagebionetworks/explorers/comparison-tools';
import { GeneExpressionComparisonToolService } from '../../services/gene-expression-comparison-tool.service';
import { GeneExpressionLegendComponent } from '../gene-expression-legend/gene-expression-legend.component';

@Component({
  selector: 'model-ad-gene-expression-help-links',
  imports: [GeneExpressionLegendComponent, HelpLinksComponent],
  templateUrl: './gene-expression-help-links.component.html',
  styleUrls: ['./gene-expression-help-links.component.scss'],
})
export class GeneExpressionHelpLinksComponent {
  private readonly legendService = inject(GeneExpressionComparisonToolService);

  toggleLegend() {
    this.legendService.toggleLegend();
  }

  toggleVisualizationOverview() {
    // TODO implement
  }
}
