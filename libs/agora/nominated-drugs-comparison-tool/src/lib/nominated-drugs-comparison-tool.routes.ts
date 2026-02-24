import { Routes } from '@angular/router';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
  ComparisonToolUrlService,
} from '@sagebionetworks/explorers/services';
import { NominatedDrugsComparisonToolComponent } from './nominated-drugs-comparison-tool.component';
import { NominatedDrugsComparisonToolService } from './services/nominated-drugs-comparison-tool.service';

export const routes: Routes = [
  {
    path: '',
    component: NominatedDrugsComparisonToolComponent,
    providers: [
      { provide: ComparisonToolService, useExisting: NominatedDrugsComparisonToolService },
      NominatedDrugsComparisonToolService,
      ComparisonToolFilterService,
      ComparisonToolUrlService,
    ],
  },
];
