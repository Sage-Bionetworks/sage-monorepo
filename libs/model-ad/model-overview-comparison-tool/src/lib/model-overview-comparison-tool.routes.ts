import { Routes } from '@angular/router';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { ModelOverviewComparisonToolComponent } from './model-overview-comparison-tool.component';
import { ModelOverviewComparisonToolService } from './services/model-overview-comparison-tool.service';

export const routes: Routes = [
  {
    path: '',
    component: ModelOverviewComparisonToolComponent,
    providers: [
      { provide: ComparisonToolService, useExisting: ModelOverviewComparisonToolService },
      ModelOverviewComparisonToolService,
      ComparisonToolFilterService,
    ],
  },
];
