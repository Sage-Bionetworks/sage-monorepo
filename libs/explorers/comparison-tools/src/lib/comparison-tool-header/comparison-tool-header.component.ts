import { Component, input } from '@angular/core';
import { ComparisonToolFilterResultsButtonComponent } from './comparison-tool-filter-results-button/comparison-tool-filter-results-button.component';
import { ComparisonToolShareURLButtonComponent } from './comparison-tool-share-url-button/comparison-tool-share-url-button.component';

@Component({
  selector: 'explorers-comparison-tool-header',
  imports: [ComparisonToolFilterResultsButtonComponent, ComparisonToolShareURLButtonComponent],
  templateUrl: './comparison-tool-header.component.html',
  styleUrls: ['./comparison-tool-header.component.scss'],
})
export class ComparisonToolHeaderComponent {
  headerTitle = input.required<string>();
  filterResultsButtonTooltip = input.required<string>();
  shareUrlButtonTooltip = input.required<string>();
}
