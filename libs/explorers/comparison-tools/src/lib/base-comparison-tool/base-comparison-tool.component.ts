import { Component, input } from '@angular/core';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';
import { LoadingIconColors } from '@sagebionetworks/explorers/models';

@Component({
  selector: 'explorers-base-comparison-tool',
  imports: [LoadingContainerComponent],
  templateUrl: './base-comparison-tool.component.html',
  styleUrls: ['./base-comparison-tool.component.scss'],
})
export class BaseComparisonToolComponent {
  defaultLoadingIconColors: LoadingIconColors = {
    colorOutermost: '#8B8AD1',
    colorCentral: '#8B8AD1',
    colorInnermost: '#8B8AD1',
  };

  isLoading = input(true);
  loadingIconColors = input(this.defaultLoadingIconColors);
  resultsCount = input(0);
}
