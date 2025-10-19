import { Component, input, ViewEncapsulation } from '@angular/core';
import { ComparisonToolSearchInputComponent } from '../comparison-tool-search-input/comparison-tool-search-input.component';
import { DisplayedResultsComponent } from '../displayed-results/displayed-results.component';
import { ComparisonToolSelectorsComponent } from './comparison-tool-selectors/comparison-tool-selectors.component';
import { SignificanceControlsComponent } from '../significance-controls/significance-controls.component';

@Component({
  selector: 'explorers-comparison-tool-controls',
  imports: [
    DisplayedResultsComponent,
    ComparisonToolSelectorsComponent,
    ComparisonToolSearchInputComponent,
    SignificanceControlsComponent,
  ],
  templateUrl: './comparison-tool-controls.component.html',
  styleUrls: ['./comparison-tool-controls.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolControlsComponent {
  showSignificanceControls = input(true);
}
