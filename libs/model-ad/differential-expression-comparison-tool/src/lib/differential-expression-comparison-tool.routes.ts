import { Routes } from '@angular/router';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
  ComparisonToolUrlService,
} from '@sagebionetworks/explorers/services';
import { DifferentialExpressionComparisonToolComponent } from './differential-expression-comparison-tool.component';
import { DifferentialExpressionComparisonToolService } from './services/differential-expression-comparison-tool.service';

export const routes: Routes = [
  {
    path: '',
    component: DifferentialExpressionComparisonToolComponent,
    providers: [
      { provide: ComparisonToolService, useExisting: DifferentialExpressionComparisonToolService },
      DifferentialExpressionComparisonToolService,
      ComparisonToolFilterService,
      ComparisonToolUrlService,
    ],
  },
];
