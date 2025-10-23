import { Provider } from '@angular/core';
import { ComparisonToolConfig, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { ComparisonToolService } from './comparison-tool.service';

export type ComparisonToolServiceOptions = {
  configs?: ComparisonToolConfig[];
  selection?: string[];
  totalResultsCount?: number;
  legendVisible?: boolean;
  selectorsWikiParams?: Record<string, SynapseWikiParams>;
  maxPinnedItems?: number;
  pinnedItems?: string[];
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
          service.initialize(options.configs, options.selection, options.selectorsWikiParams);
        } else if (options.selection) {
          service.setDropdownSelection(options.selection);
        }

        if (!options.configs && options.selectorsWikiParams) {
          service.setSelectorsWikiParams(options.selectorsWikiParams);
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
        }

        return service;
      },
    },
  ];
};
