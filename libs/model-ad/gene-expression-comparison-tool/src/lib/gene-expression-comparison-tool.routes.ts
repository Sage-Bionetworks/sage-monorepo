import { Routes } from '@angular/router';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { GeneExpressionComparisonToolComponent } from './gene-expression-comparison-tool.component';

export const routes: Routes = [
  {
    path: '',
    component: GeneExpressionComparisonToolComponent,
    providers: [ComparisonToolService, ComparisonToolFilterService],
  },
];
