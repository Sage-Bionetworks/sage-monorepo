import { Component, ViewEncapsulation } from '@angular/core';
import { DisplayedResultsComponent } from '../displayed-results/displayed-results.component';
import { SearchInputComponent } from '../search-input/search-input.component';

@Component({
  selector: 'explorers-comparison-tool-controls',
  imports: [DisplayedResultsComponent, SearchInputComponent],
  templateUrl: './comparison-tool-controls.component.html',
  styleUrls: ['./comparison-tool-controls.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolControlsComponent {}
