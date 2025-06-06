import { Component, OnInit } from '@angular/core';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolWrapperComponent } from '@sagebionetworks/model-ad/comparison-tool-wrapper';
import { ComparisonToolService } from '@sagebionetworks/model-ad/services';
import { DiseaseCorrelationHelpLinksComponent } from './components/disease-correlation-help-links/disease-correlation-help-links.component';

@Component({
  selector: 'model-ad-disease-correlation-comparison-tool',
  imports: [BaseComparisonToolComponent, DiseaseCorrelationHelpLinksComponent],
  templateUrl: './disease-correlation-comparison-tool.component.html',
  styleUrls: ['./disease-correlation-comparison-tool.component.scss'],
  providers: [ComparisonToolService],
})
export class DiseaseCorrelationComparisonToolComponent
  extends ComparisonToolWrapperComponent
  implements OnInit
{
  override isLoading = true;
  override resultsCount = 40000;

  constructor() {
    super();
  }

  ngOnInit() {
    // TODO - Replace with actual data fetching logic
    setTimeout(() => {
      this.isLoading = false;
    }, 300);
  }
}
