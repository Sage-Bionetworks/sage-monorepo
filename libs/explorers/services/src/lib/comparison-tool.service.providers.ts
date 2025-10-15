import { Provider } from '@angular/core';
import { ComparisonToolFilterService } from './comparison-tool-filter.service';
import { ComparisonToolService } from './comparison-tool.service';

export const provideComparisonToolService = (): Provider[] => {
  return [ComparisonToolService];
};

export const provideComparisonToolFilterService = (): Provider[] => {
  return [ComparisonToolFilterService];
};
