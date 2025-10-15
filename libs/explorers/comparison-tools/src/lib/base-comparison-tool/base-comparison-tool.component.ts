import { Component, computed, input, model, signal } from '@angular/core';
import {
  ComparisonToolConfig,
  ComparisonToolFilter,
  SynapseWikiParams,
} from '@sagebionetworks/explorers/models';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';
import { isEqual } from 'lodash';
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

  pageConfigs = input.required<ComparisonToolConfig[]>();
  popoverWikis = input<{ [key: string]: SynapseWikiParams }>({});

  headerTitle = input.required<string>();
  filterResultsButtonTooltip = input.required<string>();

  isFilterPanelOpen = model(false);

  selection = signal<string[]>([]);

  currentConfig = computed(() => {
    const configs = this.pageConfigs();
    const selection = this.selection();

    if (!configs.length) {
      return null;
    }

    const defaultConfig = configs[0];
    if (!selection.length) {
      return defaultConfig || null;
    }

    return configs.find((config) => !isEqual(config.dropdowns, selection)) || defaultConfig || null;
  });

  toggleFilterPanel() {
    this.isFilterPanelOpen.update((isOpen) => !isOpen);
  }

  onFiltersChanged(filters: ComparisonToolFilter[]) {
    // TODO: call CT filter service
    console.log('Filters changed: ', filters);
  }
}
