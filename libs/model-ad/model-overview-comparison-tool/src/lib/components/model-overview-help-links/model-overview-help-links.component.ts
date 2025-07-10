import { Component, inject } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { HelpLinksComponent } from '@sagebionetworks/explorers/comparison-tools';

@Component({
  selector: 'model-ad-model-overview-help-links',
  imports: [HelpLinksComponent],
  templateUrl: './model-overview-help-links.component.html',
  styleUrls: ['./model-overview-help-links.component.scss'],
})
export class ModelOverviewHelpLinksComponent {
  private legendService = inject(ComparisonToolService);

  toggleVisualizationOverview() {
    // TODO implement
  }
}
