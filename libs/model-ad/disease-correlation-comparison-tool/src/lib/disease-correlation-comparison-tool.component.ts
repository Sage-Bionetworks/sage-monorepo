import { Component } from '@angular/core';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolWrapperComponent } from '@sagebionetworks/model-ad/comparison-tool-wrapper';

@Component({
  selector: 'model-ad-disease-correlation-comparison-tool',
  imports: [BaseComparisonToolComponent],
  templateUrl: './disease-correlation-comparison-tool.component.html',
  styleUrls: ['./disease-correlation-comparison-tool.component.scss'],
})
export class DiseaseCorrelationComparisonToolComponent extends ComparisonToolWrapperComponent {
  override isLoading = true;
  override resultsCount = 40000;

  constructor() {
    super();
  }
}
