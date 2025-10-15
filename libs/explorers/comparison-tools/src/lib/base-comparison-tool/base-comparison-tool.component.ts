import { Component, input, model, output } from '@angular/core';
import {
  ComparisonToolConfig,
  ComparisonToolConfigFilter,
  ComparisonToolFilter,
  SynapseWikiParams,
} from '@sagebionetworks/explorers/models';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';
import { ComparisonToolControlsComponent } from '../comparison-tool-controls/comparison-tool-controls.component';
import { ComparisonToolFilterPanelComponent } from '../comparison-tool-filter-panel/comparison-tool-filter-panel.component';
import { ComparisonToolHeaderComponent } from '../comparison-tool-header/comparison-tool-header.component';

@Component({
  selector: 'explorers-base-comparison-tool',
  imports: [
    LoadingContainerComponent,
    ComparisonToolHeaderComponent,
    ComparisonToolFilterPanelComponent,
    ComparisonToolControlsComponent,
  ],
  templateUrl: './base-comparison-tool.component.html',
  styleUrls: ['./base-comparison-tool.component.scss'],
})
export class BaseComparisonToolComponent {
  isLoading = input(true);
  resultsCount = input(0);

  pageConfigs = input<ComparisonToolConfig[]>([]);
  popoverWikis = input<{ [key: string]: SynapseWikiParams }>({});
  selection = model<string[]>([]);

  headerTitle = input.required<string>();
  filterResultsButtonTooltip = input.required<string>();
  filterConfigs = input.required<ComparisonToolConfigFilter[]>();
  filtersChanged = output<ComparisonToolFilter[]>();
  isFilterPanelOpen = model(false);

  toggleFilterPanel() {
    this.isFilterPanelOpen.update((isOpen) => !isOpen);
  }
}
