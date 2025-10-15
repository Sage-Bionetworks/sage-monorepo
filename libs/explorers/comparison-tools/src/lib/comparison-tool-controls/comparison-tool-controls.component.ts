import { Component, input, model, ViewEncapsulation } from '@angular/core';
import { ComparisonToolConfig, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { ComparisonToolSearchInputComponent } from '../comparison-tool-search-input/comparison-tool-search-input.component';
import { DisplayedResultsComponent } from '../displayed-results/displayed-results.component';
import { ComparisonToolSelectorsComponent } from './comparison-tool-selectors/comparison-tool-selectors.component';

@Component({
  selector: 'explorers-comparison-tool-controls',
  imports: [
    DisplayedResultsComponent,
    ComparisonToolSelectorsComponent,
    ComparisonToolSearchInputComponent,
  ],
  templateUrl: './comparison-tool-controls.component.html',
  styleUrls: ['./comparison-tool-controls.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolControlsComponent {
  pageConfigs = input<ComparisonToolConfig[]>([]);
  popoverWikis = input<{ [key: string]: SynapseWikiParams }>({});
  dropdownsSelection = model<string[]>([]);
}
