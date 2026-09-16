import { inject, Provider } from '@angular/core';
import { ComparisonToolFilter } from '@sagebionetworks/explorers/models';
import { ComparisonToolFilterService } from './comparison-tool-filter.service';
import { ComparisonToolService } from './comparison-tool.service';

export type ComparisonToolFilterServiceOptions = {
  filters?: ComparisonToolFilter[];
  searchTerm?: string | null;
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
        const comparisonToolService = inject(ComparisonToolService);

        if (options.filters !== undefined) {
          comparisonToolService.updateQuery({
            filters: options.filters,
            pageNumber: comparisonToolService.FIRST_PAGE_NUMBER,
          });
        }

        if (options.searchTerm !== undefined && options.searchTerm !== null) {
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
