import { Component, input } from '@angular/core';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-base-comparison-tool',
  imports: [LoadingContainerComponent],
  templateUrl: './base-comparison-tool.component.html',
  styleUrls: ['./base-comparison-tool.component.scss'],
})
export class BaseComparisonToolComponent {
  isLoading = input(true);
  resultsCount = input(0);
}
