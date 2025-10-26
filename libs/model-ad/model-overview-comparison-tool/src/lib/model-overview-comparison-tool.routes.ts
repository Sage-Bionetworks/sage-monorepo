import { Routes } from '@angular/router';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { ModelOverviewComparisonToolComponent } from './model-overview-comparison-tool.component';

export const routes: Routes = [
  {
    path: '',
    component: ModelOverviewComparisonToolComponent,
    providers: [ComparisonToolService, ComparisonToolFilterService],
  },
];
