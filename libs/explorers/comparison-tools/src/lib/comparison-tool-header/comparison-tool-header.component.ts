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
  filterResultsButtonTooltip = input.required<string>();
  headerTitle = input.required<string>();

  toggleFilterPanel() {
    // TODO: Replace this alert with proper filter panel toggle behavior in a future update (MG-246)
    alert('Filter panel toggled');
  }
}
