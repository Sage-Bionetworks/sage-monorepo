import { Component, OnInit } from '@angular/core';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/util';

@Component({
  selector: 'model-ad-gene-expression-comparison-tool',
  imports: [BaseComparisonToolComponent],
  templateUrl: './gene-expression-comparison-tool.component.html',
  styleUrls: ['./gene-expression-comparison-tool.component.scss'],
})
export class GeneExpressionComparisonToolComponent
  extends BaseComparisonToolComponent
  implements OnInit
{
  override isLoading = true;
  override resultsCount = 30000;
  LOADING_ICON_COLORS = LOADING_ICON_COLORS;

  constructor() {
    super();
  }

  ngOnInit(): void {
    console.log('Method not implemented.');
  }
}
