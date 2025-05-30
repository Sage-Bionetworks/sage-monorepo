import { Component, OnInit } from '@angular/core';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolWrapperComponent } from '@sagebionetworks/model-ad/comparison-tool-wrapper';

@Component({
  selector: 'model-ad-gene-expression-comparison-tool',
  imports: [BaseComparisonToolComponent],
  templateUrl: './gene-expression-comparison-tool.component.html',
  styleUrls: ['./gene-expression-comparison-tool.component.scss'],
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
    }, 3000);
  }
}
