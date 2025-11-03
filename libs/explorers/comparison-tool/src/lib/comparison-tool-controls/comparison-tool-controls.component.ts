import { Component, inject, ViewEncapsulation } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { ComparisonToolCategorySelectorsComponent } from './comparison-tool-category-selectors/comparison-tool-category-selectors.component';
import { ComparisonToolColumnSelectorComponent } from './comparison-tool-column-selector/comparison-tool-column-selector.component';
import { ComparisonToolSearchInputComponent } from './comparison-tool-search-input/comparison-tool-search-input.component';
import { DisplayedResultsComponent } from './displayed-results/displayed-results.component';
import { SignificanceControlsComponent } from './significance-controls/significance-controls.component';

@Component({
  selector: 'explorers-comparison-tool-controls',
  imports: [
    DisplayedResultsComponent,
    ComparisonToolCategorySelectorsComponent,
    ComparisonToolSearchInputComponent,
    SignificanceControlsComponent,
    ComparisonToolColumnSelectorComponent,
  ],
  templateUrl: './comparison-tool-controls.component.html',
  styleUrls: ['./comparison-tool-controls.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolControlsComponent {
  comparisonToolService = inject(ComparisonToolService);
  viewConfig = this.comparisonToolService.viewConfig;
}
