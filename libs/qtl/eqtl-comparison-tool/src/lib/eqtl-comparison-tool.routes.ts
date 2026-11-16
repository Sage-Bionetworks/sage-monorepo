import { Routes } from '@angular/router';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
  ComparisonToolUrlService,
} from '@sagebionetworks/explorers/services';
import { EqtlComparisonToolComponent } from './eqtl-comparison-tool.component';
import { EqtlComparisonToolService } from './services/eqtl-comparison-tool.service';

export const routes: Routes = [
  {
    path: '',
    component: EqtlComparisonToolComponent,
    providers: [
      { provide: ComparisonToolService, useExisting: EqtlComparisonToolService },
      EqtlComparisonToolService,
      ComparisonToolFilterService,
      ComparisonToolUrlService,
    ],
  },
];
