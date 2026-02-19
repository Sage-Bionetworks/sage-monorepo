import { Component, inject } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { PopoverLinkComponent } from '@sagebionetworks/explorers/util';
import { ComparisonToolFilterResultsButtonComponent } from './comparison-tool-filter-results-button/comparison-tool-filter-results-button.component';
import { ComparisonToolShareURLButtonComponent } from './comparison-tool-share-url-button/comparison-tool-share-url-button.component';

@Component({
  selector: 'explorers-comparison-tool-header',
  imports: [
    ComparisonToolFilterResultsButtonComponent,
    ComparisonToolShareURLButtonComponent,
    PopoverLinkComponent,
  ],
  templateUrl: './comparison-tool-header.component.html',
  styleUrls: ['./comparison-tool-header.component.scss'],
})
export class ComparisonToolHeaderComponent {
  private readonly comparisonToolService = inject(ComparisonToolService);

  viewConfig = this.comparisonToolService.viewConfig;
}
