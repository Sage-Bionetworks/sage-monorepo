import { Provider } from '@angular/core';
import { ComparisonToolConfig, ComparisonToolViewConfig } from '@sagebionetworks/explorers/models';
import { ComparisonToolService } from './comparison-tool.service';

export type ComparisonToolServiceOptions = {
  configs?: ComparisonToolConfig[];
  selection?: string[];
  totalResultsCount?: number;
  legendVisible?: boolean;
  viewConfig?: Partial<ComparisonToolViewConfig>;
  maxPinnedItems?: number;
  pinnedItems?: string[];
  unpinnedData?: Record<string, unknown>[];
  pinnedData?: Record<string, unknown>[];
};

export const provideComparisonToolService = (
  options?: ComparisonToolServiceOptions,
): Provider[] => {
  if (!options) {
    return [ComparisonToolService];
  }

  return [
    {
      provide: ComparisonToolService,
      useFactory: () => {
        const service = new ComparisonToolService();

        if (options.configs) {
          service.initialize(options.configs, options.selection);
        } else if (options.selection) {
          service.setDropdownSelection(options.selection);
        }

        if (options.viewConfig) {
          service.setViewConfig(options.viewConfig);
        }

        if (options.totalResultsCount !== undefined) {
          service.totalResultsCount.set(options.totalResultsCount);
        }

        if (options.legendVisible !== undefined) {
          service.setLegendVisibility(options.legendVisible);
        }

        if (options.maxPinnedItems !== undefined) {
          service.setMaxPinnedItems(options.maxPinnedItems);
        }

        if (options.pinnedItems !== undefined) {
          service.setPinnedItems(options.pinnedItems);
          service.pinnedResultsCount.set(options.pinnedItems.length);
        }

        if (options.unpinnedData !== undefined) {
          service.setUnpinnedData(options.unpinnedData);
        }

        if (options.pinnedData !== undefined) {
          service.setPinnedData(options.pinnedData);
        }

        return service;
      },
    },
  ];
};
