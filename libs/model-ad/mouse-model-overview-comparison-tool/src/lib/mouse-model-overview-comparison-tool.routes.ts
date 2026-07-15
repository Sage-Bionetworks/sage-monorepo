import { Routes } from '@angular/router';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
  ComparisonToolUrlService,
} from '@sagebionetworks/explorers/services';
import { MouseModelOverviewComparisonToolComponent } from './mouse-model-overview-comparison-tool.component';
import { MouseModelOverviewComparisonToolService } from './services/mouse-model-overview-comparison-tool.service';

export const routes: Routes = [
  {
    path: '',
    component: MouseModelOverviewComparisonToolComponent,
    providers: [
      { provide: ComparisonToolService, useExisting: MouseModelOverviewComparisonToolService },
      MouseModelOverviewComparisonToolService,
      ComparisonToolFilterService,
      ComparisonToolUrlService,
    ],
  },
];
