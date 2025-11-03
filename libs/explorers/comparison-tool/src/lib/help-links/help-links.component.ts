import { Component, inject } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';

@Component({
  selector: 'explorers-help-links',
  templateUrl: './help-links.component.html',
  styleUrls: ['./help-links.component.scss'],
})
export class HelpLinksComponent {
  comparisonToolService = inject(ComparisonToolService);

  viewConfig = this.comparisonToolService.viewConfig;
}
