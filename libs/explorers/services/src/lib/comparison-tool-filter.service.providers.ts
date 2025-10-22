import { Provider } from '@angular/core';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';
import { ComparisonToolFilterService } from './comparison-tool-filter.service';

export type ComparisonToolFilterServiceOptions = {
  filters?: ComparisonToolFilter[];
  searchTerm?: string;
  significanceThreshold?: number;
  significanceThresholdActive?: boolean;
};

export const provideComparisonToolFilterService = (
  options?: ComparisonToolFilterServiceOptions,
): Provider[] => {
  if (!options) {
    return [ComparisonToolFilterService];
  }

  return [
    {
      provide: ComparisonToolFilterService,
      useFactory: () => {
        const service = new ComparisonToolFilterService();

        if (options.filters !== undefined) {
          service.setFilters(options.filters);
        }

        if (options.searchTerm !== undefined) {
          service.updateSearchTerm(options.searchTerm);
        }

        if (options.significanceThreshold !== undefined) {
          service.setSignificanceThreshold(options.significanceThreshold);
        }

        if (options.significanceThresholdActive !== undefined) {
          service.setSignificanceThresholdActive(options.significanceThresholdActive);
        }

        return service;
      },
    },
  ];
};
