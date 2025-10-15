import { Provider } from '@angular/core';
import { ComparisonToolService } from './comparison-tool.service';

export const provideComparisonToolService = (): Provider[] => {
  return [ComparisonToolService];
};
