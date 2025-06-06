import { Component, OnInit } from '@angular/core';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolWrapperComponent } from '@sagebionetworks/model-ad/comparison-tool-wrapper';
import { ComparisonToolService } from '@sagebionetworks/model-ad/services';
import { ModelOverviewHelpLinksComponent } from './components/model-overview-help-links/model-overview-help-links.component';
@Component({
  selector: 'model-ad-model-overview-comparison-tool',
  imports: [BaseComparisonToolComponent, ModelOverviewHelpLinksComponent],
  templateUrl: './model-overview-comparison-tool.component.html',
  styleUrls: ['./model-overview-comparison-tool.component.scss'],
  providers: [ComparisonToolService],
})
export class ModelOverviewComparisonToolComponent
  extends ComparisonToolWrapperComponent
  implements OnInit
{
  override isLoading = true;
  override resultsCount = 50000;

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
