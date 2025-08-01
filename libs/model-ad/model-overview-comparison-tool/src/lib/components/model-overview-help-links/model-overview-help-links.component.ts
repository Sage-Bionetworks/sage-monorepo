import { Component, inject } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { HelpLinksComponent } from '@sagebionetworks/explorers/comparison-tools';
import { AppError } from '@sagebionetworks/explorers/models';

@Component({
  selector: 'model-ad-model-overview-help-links',
  imports: [HelpLinksComponent],
  templateUrl: './model-overview-help-links.component.html',
  styleUrls: ['./model-overview-help-links.component.scss'],
})
export class ModelOverviewHelpLinksComponent {
  private legendService = inject(ComparisonToolService);

  toggleVisualizationOverview() {
    // TODO implement this method.  For now, throw a new Error
    throw new AppError('not implemented', true);
  }
}
