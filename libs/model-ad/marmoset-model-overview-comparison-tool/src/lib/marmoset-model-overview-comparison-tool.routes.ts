import { Routes } from '@angular/router';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
  ComparisonToolUrlService,
} from '@sagebionetworks/explorers/services';
import { MarmosetModelOverviewComparisonToolComponent } from './marmoset-model-overview-comparison-tool.component';
import { MarmosetModelOverviewComparisonToolService } from './services/marmoset-model-overview-comparison-tool.service';

export const routes: Routes = [
  {
    path: '',
    component: MarmosetModelOverviewComparisonToolComponent,
    providers: [
      { provide: ComparisonToolService, useExisting: MarmosetModelOverviewComparisonToolService },
      MarmosetModelOverviewComparisonToolService,
      ComparisonToolFilterService,
      ComparisonToolUrlService,
    ],
  },
];
