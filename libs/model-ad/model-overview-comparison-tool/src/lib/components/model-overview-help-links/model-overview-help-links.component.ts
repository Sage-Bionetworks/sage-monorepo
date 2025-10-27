import { Component, inject } from '@angular/core';
import { HelpLinksComponent } from '@sagebionetworks/explorers/comparison-tools';
import { AppError } from '@sagebionetworks/explorers/models';
import { ModelOverviewComparisonToolService } from '../../services/model-overview-comparison-tool.service';

@Component({
  selector: 'model-ad-model-overview-help-links',
  imports: [HelpLinksComponent],
  templateUrl: './model-overview-help-links.component.html',
  styleUrls: ['./model-overview-help-links.component.scss'],
})
export class ModelOverviewHelpLinksComponent {
  private readonly legendService = inject(ModelOverviewComparisonToolService);

  toggleVisualizationOverview() {
    // TODO implement this method.  For now, throw a new Error
    throw new AppError('not implemented', true);
  }
}
