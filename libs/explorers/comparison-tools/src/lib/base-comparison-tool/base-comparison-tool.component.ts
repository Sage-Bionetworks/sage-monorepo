import { Component, computed, effect, inject, input, model } from '@angular/core';
import {
  ComparisonToolConfig,
  ComparisonToolFilter,
  SynapseWikiParams,
} from '@sagebionetworks/explorers/models';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { LoadingContainerComponent } from '@sagebionetworks/explorers/util';
import { isEqual } from 'lodash';
import { ComparisonToolColumnsComponent } from '../comparison-tool-columns/comparison-tool-columns.component';
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
    ComparisonToolColumnsComponent,
  ],
  templateUrl: './base-comparison-tool.component.html',
  styleUrls: ['./base-comparison-tool.component.scss'],
})
export class BaseComparisonToolComponent {
  private readonly comparisonToolService = inject(ComparisonToolService);
  private readonly comparisonToolFilterService = inject(ComparisonToolFilterService);

  isLoading = input(true);
  resultsCount = input(0);

  pageConfigs = input.required<ComparisonToolConfig[]>();
  popoverWikis = input<{ [key: string]: SynapseWikiParams }>({});

  headerTitle = input.required<string>();
  filterResultsButtonTooltip = input.required<string>();

  isFilterPanelOpen = model(false);

  dropdownsSelection = model<string[]>([]);

  currentConfig = computed(() => {
    const configs = this.pageConfigs();
    const dropdownsSelection = this.dropdownsSelection();
    if (!configs.length) {
      return null;
    }

    const defaultConfig = configs[0];
    if (!dropdownsSelection.length) {
      return defaultConfig;
    }

    return (
      configs.find((config) => isEqual(config.dropdowns, dropdownsSelection)) ||
      defaultConfig ||
      null
    );
  });

  constructor() {
    effect(() => {
      const config = this.currentConfig();
      if (config) {
        this.comparisonToolService.columns.set(this.getColumnsFromConfig(config));
      }
    });
  }

  toggleFilterPanel() {
    this.isFilterPanelOpen.update((isOpen) => !isOpen);
  }

  onFiltersChanged(filters: ComparisonToolFilter[]) {
    this.comparisonToolFilterService.setFilters(filters);
  }

  getColumnsFromConfig(config: ComparisonToolConfig) {
    let columns: string[] = [];
    if (config?.columns) {
      columns = config.columns.map((column) => column.name);
      return columns;
    } else {
      throw new Error(
        'Missing comparison tool config data: "columns" property is undefined or empty.',
      );
    }
  }
}
