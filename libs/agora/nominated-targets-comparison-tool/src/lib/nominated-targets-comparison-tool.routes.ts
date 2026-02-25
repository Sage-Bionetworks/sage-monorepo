import { Routes } from '@angular/router';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
  ComparisonToolUrlService,
} from '@sagebionetworks/explorers/services';
import { NominatedTargetsComparisonToolComponent } from './nominated-targets-comparison-tool.component';
import { NominatedTargetsComparisonToolService } from './services/nominated-targets-comparison-tool.service';

export const routes: Routes = [
  {
    path: '',
    component: NominatedTargetsComparisonToolComponent,
    providers: [
      { provide: ComparisonToolService, useExisting: NominatedTargetsComparisonToolService },
      NominatedTargetsComparisonToolService,
      ComparisonToolFilterService,
      ComparisonToolUrlService,
    ],
  },
];
