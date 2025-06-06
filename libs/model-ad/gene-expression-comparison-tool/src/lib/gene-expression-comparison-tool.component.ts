import { Component, OnInit } from '@angular/core';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolWrapperComponent } from '@sagebionetworks/model-ad/comparison-tool-wrapper';
import { ComparisonToolService } from '@sagebionetworks/model-ad/services';
import { GeneExpressionHelpLinksComponent } from './components/gene-expression-help-links/gene-expression-help-links.component';

@Component({
  selector: 'model-ad-gene-expression-comparison-tool',
  imports: [BaseComparisonToolComponent, GeneExpressionHelpLinksComponent],
  templateUrl: './gene-expression-comparison-tool.component.html',
  styleUrls: ['./gene-expression-comparison-tool.component.scss'],
  providers: [ComparisonToolService],
})
export class GeneExpressionComparisonToolComponent
  extends ComparisonToolWrapperComponent
  implements OnInit
{
  override isLoading = true;
  override resultsCount = 30000;

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
