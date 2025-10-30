import { Routes } from '@angular/router';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { GeneExpressionComparisonToolComponent } from './gene-expression-comparison-tool.component';
import { GeneExpressionComparisonToolService } from './services/gene-expression-comparison-tool.service';

export const routes: Routes = [
  {
    path: '',
    component: GeneExpressionComparisonToolComponent,
    providers: [
      { provide: ComparisonToolService, useExisting: GeneExpressionComparisonToolService },
      GeneExpressionComparisonToolService,
      ComparisonToolFilterService,
    ],
  },
];
