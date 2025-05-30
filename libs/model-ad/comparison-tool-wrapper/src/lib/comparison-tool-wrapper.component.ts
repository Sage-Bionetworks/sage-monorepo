import { Component } from '@angular/core';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/util';

@Component({
  selector: 'model-ad-comparison-tool-wrapper',
  imports: [BaseComparisonToolComponent],
  templateUrl: './comparison-tool-wrapper.component.html',
  styleUrls: ['./comparison-tool-wrapper.component.scss'],
})
export class ComparisonToolWrapperComponent extends BaseComparisonToolComponent {
  LOADING_ICON_COLORS = LOADING_ICON_COLORS;

  constructor() {
    super();
    this.loadingIconColors = this.LOADING_ICON_COLORS;
  }
}
