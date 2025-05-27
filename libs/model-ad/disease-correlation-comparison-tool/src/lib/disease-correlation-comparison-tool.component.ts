import { Component, OnInit } from '@angular/core';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/util';

@Component({
  selector: 'model-ad-disease-correlation-comparison-tool',
  imports: [BaseComparisonToolComponent],
  templateUrl: './disease-correlation-comparison-tool.component.html',
  styleUrls: ['./disease-correlation-comparison-tool.component.scss'],
})
export class DiseaseCorrelationComparisonToolComponent
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
