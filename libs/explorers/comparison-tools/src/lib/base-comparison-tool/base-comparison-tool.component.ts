import { Component, Input } from '@angular/core';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';
import { LoadingIconColors } from '@sagebionetworks/explorers/models';

@Component({
  selector: 'explorers-base-comparison-tool',
  imports: [LoadingContainerComponent],
  templateUrl: './base-comparison-tool.component.html',
  styleUrls: ['./base-comparison-tool.component.scss'],
})
export class BaseComparisonToolComponent {
  @Input() isLoading = true;
  @Input() loadingIconColors!: LoadingIconColors;
  @Input() resultsCount = 0;
}
