import { Provider } from '@angular/core';
import { ComparisonToolConfig } from '@sagebionetworks/explorers/models';
import { ComparisonToolFilterService } from './comparison-tool-filter.service';
import { ComparisonToolService } from './comparison-tool.service';

export type ComparisonToolServiceOptions = {
  configs?: ComparisonToolConfig[];
  selection?: string[];
  totalResultsCount?: number;
  pinnedResultsCount?: number;
  legendVisible?: boolean;
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

        if (options.totalResultsCount !== undefined) {
          service.totalResultsCount.set(options.totalResultsCount);
        }

        if (options.pinnedResultsCount !== undefined) {
          service.pinnedResultsCount.set(options.pinnedResultsCount);
        }

        if (options.legendVisible !== undefined) {
          service.setLegendVisibility(options.legendVisible);
        }

        return service;
      },
    },
  ];
};

export const provideComparisonToolFilterService = (): Provider[] => {
  return [ComparisonToolFilterService];
};
