import { Component, input } from '@angular/core';
import {
  ComparisonToolFilterResultsButtonComponent,
  ComparisonToolShareURLButtonComponent,
} from '@sagebionetworks/explorers/comparison-tools';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';

@Component({
  selector: 'model-ad-model-overview-header',
  imports: [ComparisonToolFilterResultsButtonComponent, ComparisonToolShareURLButtonComponent],
  templateUrl: './model-overview-header.component.html',
  styleUrls: ['./model-overview-header.component.scss'],
})
export class ModelOverviewHeaderComponent {
  headerTitle = input.required<string>();
  filters = input.required<ComparisonToolFilter[]>();
  filterResultsButtonTooltip = input.required<string>();
  shareUrlButtonTooltip = input.required<string>();
}
